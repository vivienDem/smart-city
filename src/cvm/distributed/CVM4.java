package cvm.distributed;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import components.classes.CEPBus;
import components.classes.TRAFFICCorrelator;
import components.classes.TRAFFICLights;
import connections.BusManagementConnector;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.cvm.AbstractDistributedCVM;
import fr.sorbonne_u.cps.smartcity.SmartCityDescriptor;
import fr.sorbonne_u.cps.smartcity.grid.IntersectionPosition;
import fr.sorbonne_u.cps.smartcity.utils.TimeManager;
import rule.classes.*;

/**
 * La classe <code>CVM4</code> permet de lancer la JVM4 dans le cadre du systeme
 * reparti de ville intelligente contenant 4 JVM au total
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class CVM4 extends AbstractDistributedCVM {
	protected static long START_DELAY = 20000L;
	/** the start time of the simulation as a Java {@code LocalTime}. */
	protected static LocalTime simulatedStartTime;
	/** the end time of the simulation as a Java {@code LocalTime}. */
	protected static LocalTime simulatedEndTime;
	private static int nbTrafficCorrelators = 0;
	private ArrayList<String> busManagementOutboundPortsURIs;
	private ArrayList<String> otherBusURIs;
	private String uriBUS;
	private String cvmIdentifier;

	public CVM4(String[] args, int xLayout, int yLayout) throws Exception {
		super(args, xLayout, yLayout);
		cvmIdentifier = AbstractCVM.getThisJVMURI();
		System.out.println(cvmIdentifier);

		SmartCityDescriptor.initialise();
		assert simulatedStartTime != null && simulatedEndTime != null && simulatedEndTime.isAfter(simulatedStartTime);
		long realTimeOfStart = System.currentTimeMillis() + START_DELAY;
		new TimeManager(realTimeOfStart, simulatedStartTime, simulatedEndTime);

		/* Utile pour la creation du bus */
		this.busManagementOutboundPortsURIs = new ArrayList<String>();
		this.otherBusURIs = new ArrayList<String>();
	}

	/**
	 * Cree le composant bus
	 * 
	 * @return URI du port entrant de management du bus
	 * @throws Exception
	 */
	private String createBus() throws Exception {
		String emissionURI = "URI-emission-inbound-port-of-" + cvmIdentifier;
		String managementURI = "URI-management-inbound-port-of-" + cvmIdentifier;
		String uri = "URI-bus-" + cvmIdentifier;
		String internalRoutingPool = uri + "-internal-routing";
		String regAndSubManagement = uri + "-reg-and-sub-management";

		for (int i = 1; i < 4; i++) {
			otherBusURIs.add("URI-bus-jvm" + i);
			busManagementOutboundPortsURIs.add(AbstractPort.generatePortURI());

		}
		uriBUS = AbstractComponent.createComponent(CEPBus.class.getCanonicalName(),
				new Object[] { uri, emissionURI, managementURI, internalRoutingPool, regAndSubManagement,
						otherBusURIs, busManagementOutboundPortsURIs });
		return managementURI;
	}

	/**
	 * Cree une base de regles contenant les regles de traffic
	 * 
	 * @return la base de regles de traffic
	 */
	private RuleBase createTrafficRuleBase() {
		RuleBase rb = new RuleBase();
		rb.addRule(new RuleC1());
		rb.addRule(new RuleC2());
		rb.addRule(new RuleC3());
		rb.addRule(new RuleC4());
		rb.addRule(new RuleC5());
		return rb;
	}

	/**
	 * Cree les composants feux de circulations
	 * 
	 * @param busURI URI du bus
	 * @return la liste des URI des stations
	 * @throws Exception
	 */
	private List<String> createTRAFFICLights(String busURI) throws Exception {
		List<String> res = new ArrayList<String>();
		String notificationInboundPort;
		IntersectionPosition asset;
		int cpt = 0;
		Iterator<IntersectionPosition> trafficLightsIterator = SmartCityDescriptor.createTrafficLightPositionIterator();
		while (trafficLightsIterator.hasNext()) {
			asset = trafficLightsIterator.next();
			notificationInboundPort = "inbound-port-traffic-lights-" + ++cpt;
			AbstractComponent.createComponent(TRAFFICLights.class.getCanonicalName(), new Object[] { asset.toString(),
					busURI, SmartCityDescriptor.getActionInboundPortURI(asset), notificationInboundPort });
			res.add(asset.toString());
		}
		return res;
	}

	/**
	 * Cree les correlateurs des feux de circulations
	 * 
	 * @param number        le nombre de correlateurs a creer
	 * @param busURI        URI du bus
	 * @param executorsURIs une liste contenant, pour chaque correlateur a creer,
	 *                      des executeurs d'actions qui relevent de lui
	 * @param emittersURIs  une liste contenant, pour chaque correlateur a creer,
	 *                      des emetteurs d'actions qui relevent de lui
	 * @return la liste des ports entrants de reception des correlateurs crees
	 * @throws Exception
	 */
	private String[] createTrafficLightsCorrelators(int number, String busURI, List<List<String>> executorsURIs,
			List<List<String>> emittersURIs) throws Exception {
		assert number > 0;
		assert number == executorsURIs.size();
		RuleBase rb = createTrafficRuleBase();
		String receptionInboundPort;
		String[] correlatorsInboundPort = new String[number];
		String uri;
		String eventDeliveryPool;
		for (int i = 0; i < number; i++) {
			uri = "Traffic Correlator " + ++nbTrafficCorrelators + "-" + cvmIdentifier;
			eventDeliveryPool = uri + "event-delivery";
			receptionInboundPort = AbstractPort.generatePortURI();
			AbstractComponent.createComponent(TRAFFICCorrelator.class.getCanonicalName(), new Object[] { uri, rb,
					busURI, executorsURIs.get(i), emittersURIs.get(i), receptionInboundPort, eventDeliveryPool });
			correlatorsInboundPort[i] = receptionInboundPort;
		}
		return correlatorsInboundPort;
	}

	/**
	 * @see fr.sorbonne_u.components.cvm.DistributedComponentVirtualMachineI#instantiateAndPublish()
	 */
	@Override
	public void instantiateAndPublish() throws Exception {

		String busManagementURI = this.createBus();
		List<String> samuCentersURI = new ArrayList<String>();
		List<String> fireStationsURI = new ArrayList<String>();
		List<String> trafficLightsURI = this.createTRAFFICLights(busManagementURI);
		Iterator<String> SAMUCenterIdsIterator = SmartCityDescriptor.createSAMUStationIdIterator();
		while (SAMUCenterIdsIterator.hasNext()) {
			samuCentersURI.add(SAMUCenterIdsIterator.next());
		}
		Iterator<String> fireStationIdsIterator = SmartCityDescriptor.createFireStationIdIterator();
		while (fireStationIdsIterator.hasNext()) {
			fireStationsURI.add(fireStationIdsIterator.next());
		}

		List<List<String>> correlatorsExecutorsTRAFFIC = new ArrayList<List<String>>();
		List<List<String>> correlatorsEmittersTRAFFIC = new ArrayList<List<String>>();
		List<String> copy = new ArrayList<String>(trafficLightsURI);
		correlatorsExecutorsTRAFFIC.add(copy);
		correlatorsEmittersTRAFFIC.add(trafficLightsURI);
		correlatorsEmittersTRAFFIC.get(0).addAll(samuCentersURI);
		correlatorsEmittersTRAFFIC.get(0).addAll(fireStationsURI);

		this.createTrafficLightsCorrelators(1, busManagementURI, correlatorsExecutorsTRAFFIC,
				correlatorsEmittersTRAFFIC);

		super.instantiateAndPublish();
	}

	/*
	 * @see
	 * fr.sorbonne_u.components.cvm.DistributedComponentVirtualMachineI#interconnect
	 * ()
	 */
	@Override
	public void interconnect() throws Exception {
		// liaison management -> s'abonner aupres des autres bus comme corrélateur

		// liaison emission -> envoyer les evenements sur les autres bus
		assert busManagementOutboundPortsURIs.size() == 3;
		int cpt = 0;
		for (int i = 0; i < busManagementOutboundPortsURIs.size(); i++) {
			cpt += 1;
			this.doPortConnection(uriBUS, busManagementOutboundPortsURIs.get(i),
					"URI-management-inbound-port-of-jvm" + cpt, BusManagementConnector.class.getCanonicalName());
		}
		super.interconnect();
	}

	public static void main(String[] args) {
		try {
			simulatedStartTime = LocalTime.of(12, 0);
			simulatedEndTime = LocalTime.of(12, 0).plusMinutes(30);
			CVM4 c = new CVM4(args, 2, 5);
			c.startStandardLifeCycle(TimeManager.get().computeExecutionDuration() + START_DELAY);
			Thread.sleep(10000L);
			System.exit(0);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
