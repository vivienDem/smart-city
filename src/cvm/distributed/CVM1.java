package cvm.distributed;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import components.classes.CEPBus;
import connections.BusManagementConnector;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.cvm.AbstractDistributedCVM;
import fr.sorbonne_u.cps.smartcity.SmartCityDescriptor;
import fr.sorbonne_u.cps.smartcity.components.FireStationProxy;
import fr.sorbonne_u.cps.smartcity.components.SAMUStationProxy;
import fr.sorbonne_u.cps.smartcity.components.TrafficLightProxy;
import fr.sorbonne_u.cps.smartcity.grid.IntersectionPosition;
import fr.sorbonne_u.cps.smartcity.traffic.components.TrafficLightsSimulator;
import fr.sorbonne_u.cps.smartcity.utils.TimeManager;

/**
 * La classe <code>CVM1</code> permet de lancer la JVM1 dans le cadre du systeme
 * reparti de ville intelligente contenant 4 JVM au total
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class CVM1 extends AbstractDistributedCVM {

	/**
	 * Delai avant le debut de la simulation de la ville intelligente apres le
	 * lancement du programme
	 */
	protected static long START_DELAY = 30000L;
	/** l'heure de debut de la simulation sous la forme d'un {@code LocalTime}. */
	protected static LocalTime simulatedStartTime;
	/** l'heure de fin de la simulation sous la forme d'un {@code LocalTime}. */
	protected static LocalTime simulatedEndTime;
	/**
	 * map qui contiendra l'URI des ports entrants d'action utilises dans les
	 * composants proxy pour offrir leurs services dans la ville intelligente et les
	 * URI des ports d'entree de notification utilises par les composants emetteurs
	 * d'evenements pour recevoir les notifications de la ville intelligente
	 */
	private Map<String, String> facadeNotificationInboundPortsURI;
	/**
	 * URI des ports entrants des casernes de pompiers et des centres SAMU utilises
	 * par le simulateur de feux tricolores pour les notifier des evenements les
	 * concernant.
	 */
	protected final Map<String, String> stationsEventIBPURI;
	protected final Map<IntersectionPosition, String> trafficLightsIBPURI;
	private String URIbus;
	private ArrayList<String> busManagementOutboundPortsURIs;
	private ArrayList<String> otherBusURIs;
	private String cvmIdentifier;

	public CVM1(String[] args, int xLayout, int yLayout) throws Exception {
		super(args, xLayout, yLayout);
		cvmIdentifier = AbstractCVM.getThisJVMURI();
		System.out.println(cvmIdentifier);
		SmartCityDescriptor.initialise();
		assert simulatedStartTime != null && simulatedEndTime != null && simulatedEndTime.isAfter(simulatedStartTime);
		long realTimeOfStart = System.currentTimeMillis() + START_DELAY;
		new TimeManager(realTimeOfStart, simulatedStartTime, simulatedEndTime);
		this.facadeNotificationInboundPortsURI = new HashMap<>();

		String id;
		this.stationsEventIBPURI = new HashMap<>();
		Iterator<String> iterStation = SmartCityDescriptor.createFireStationIdIterator();
		while (iterStation.hasNext()) {
			id = iterStation.next();
			this.stationsEventIBPURI.put(id, AbstractPort.generatePortURI());
			this.register(id, "inbound-port-fire-station-" + id);
		}
		iterStation = SmartCityDescriptor.createSAMUStationIdIterator();
		while (iterStation.hasNext()) {
			id = iterStation.next();
			stationsEventIBPURI.put(id, AbstractPort.generatePortURI());
			this.register(id, "inbound-port-samu-center-" + id);

		}

		this.trafficLightsIBPURI = new HashMap<>();
		Iterator<IntersectionPosition> iterTL = SmartCityDescriptor.createTrafficLightPositionIterator();
		IntersectionPosition ip;
		int cpt = 0;
		while (iterTL.hasNext()) {
			ip = iterTL.next();
			this.trafficLightsIBPURI.put(ip, AbstractPort.generatePortURI());
			this.register(ip.toString(), "inbound-port-traffic-lights-" + ++cpt);
		}

		/* Utile pour la creation du bus */
		this.busManagementOutboundPortsURIs = new ArrayList<String>();
		this.otherBusURIs = new ArrayList<String>();
	}

	/**
	 * return true if the asset has already a URI registered, false otherwise.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	{@code assetId != null && !assetId.isEmpty()}
	 * post	true		// no postcondition.
	 * </pre>
	 *
	 * @param assetId asset identifier as define the the smart city descriptor.
	 * @return true if the asset has already a URI registered, false otherwise.
	 */
	protected boolean registered(String assetId) {
		assert assetId != null && !assetId.isEmpty();
		return this.facadeNotificationInboundPortsURI.containsKey(assetId);
	}

	/**
	 * register the URI if the notification inbound port used in the events emitter
	 * component associated with the asset identifier {@code assetId}.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	{@code assetId != null && !assetId.isEmpty()}
	 * pre	{@code !registered(assetId)}
	 * pre	{@code uri != null && !uri.isEmpty()}
	 * post	{@code registered(assetId)}
	 * </pre>
	 *
	 * @param assetId asset identifier as define the the smart city descriptor.
	 * @param uri     URI of the notification inbound port of the corresponding
	 *                events emitter component.
	 */
	protected void register(String assetId, String uri) {
		assert assetId != null && !assetId.isEmpty();
		assert !this.registered(assetId);
		assert uri != null && !uri.isEmpty();
		this.facadeNotificationInboundPortsURI.put(assetId, uri);
	}

	/**
	 * Cree le composant bus
	 * 
	 * @return URI du bus
	 * @throws Exception
	 */
	private String createBus() throws Exception {
		String emissionURI = "URI-emission-inbound-port-of-" + cvmIdentifier;
		String managementURI = "URI-management-inbound-port-of-" + cvmIdentifier;
		String uri = "URI-bus-" + cvmIdentifier;
		String internalRoutingPool = uri + "-internal-routing";
		String regAndSubManagement = uri + "-reg-and-sub-management";

		for (int i = 2; i < 5; i++) {
			otherBusURIs.add("URI-bus-jvm" + i);
			busManagementOutboundPortsURIs.add(AbstractPort.generatePortURI());
		}
		return AbstractComponent.createComponent(CEPBus.class.getCanonicalName(),
				new Object[] { uri, emissionURI, managementURI, internalRoutingPool, regAndSubManagement,
						otherBusURIs, busManagementOutboundPortsURIs });
	}

	/**
	 * @see fr.sorbonne_u.components.cvm.DistributedComponentVirtualMachineI#instantiateAndPublish()
	 */
	@Override
	public void instantiateAndPublish() throws Exception {
		AbstractComponent.createComponent(TrafficLightsSimulator.class.getCanonicalName(),
				new Object[] { this.stationsEventIBPURI, this.trafficLightsIBPURI });

		this.URIbus = this.createBus();

		Iterator<String> iterStation = SmartCityDescriptor.createFireStationIdIterator();
		while (iterStation.hasNext()) {
			String id = iterStation.next();
			AbstractComponent.createComponent(FireStationProxy.class.getCanonicalName(),
					new Object[] { SmartCityDescriptor.getActionInboundPortURI(id),
							this.facadeNotificationInboundPortsURI.get(id), id, SmartCityDescriptor.getPosition(id),
							this.stationsEventIBPURI.get(id), 2, 2 });
		}

		iterStation = SmartCityDescriptor.createSAMUStationIdIterator();
		while (iterStation.hasNext()) {
			String id = iterStation.next();
			AbstractComponent.createComponent(SAMUStationProxy.class.getCanonicalName(),
					new Object[] { SmartCityDescriptor.getActionInboundPortURI(id),
							this.facadeNotificationInboundPortsURI.get(id), id, SmartCityDescriptor.getPosition(id),
							this.stationsEventIBPURI.get(id), 2, 2 });
		}

		Iterator<IntersectionPosition> trafficLightsIterator = SmartCityDescriptor.createTrafficLightPositionIterator();
		while (trafficLightsIterator.hasNext()) {
			IntersectionPosition p = trafficLightsIterator.next();
			AbstractComponent.createComponent(TrafficLightProxy.class.getCanonicalName(),
					new Object[] { p, SmartCityDescriptor.getActionInboundPortURI(p),
							this.facadeNotificationInboundPortsURI.get(p.toString()),
							this.trafficLightsIBPURI.get(p) });
		}

		super.instantiateAndPublish();
	}

	/*
	 * @see
	 * fr.sorbonne_u.components.cvm.DistributedComponentVirtualMachineI#interconnect
	 * ()
	 */
	@Override
	public void interconnect() throws Exception {
		assert busManagementOutboundPortsURIs.size() == 3;

		for (int i = 0; i < busManagementOutboundPortsURIs.size(); i++) {
			this.doPortConnection(URIbus, busManagementOutboundPortsURIs.get(i),
					"URI-management-inbound-port-of-jvm" + (i + 2), BusManagementConnector.class.getCanonicalName());
		}

		super.interconnect();
	}

	public static void main(String[] args) {
		try {
			simulatedStartTime = LocalTime.of(12, 0);
			simulatedEndTime = LocalTime.of(12, 0).plusMinutes(30);
			CVM1 c = new CVM1(args, 2, 5);
			c.startStandardLifeCycle(TimeManager.get().computeExecutionDuration() + START_DELAY);
			Thread.sleep(10000L);
			System.exit(0);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
