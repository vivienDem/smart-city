package cvm.distributed;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import components.classes.CEPBus;
import components.classes.FIRECorrelator;
import components.classes.FIREStation;
import connections.BusManagementConnector;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.cvm.AbstractDistributedCVM;
import fr.sorbonne_u.cps.smartcity.SmartCityDescriptor;
import fr.sorbonne_u.cps.smartcity.utils.TimeManager;
import rule.classes.*;

/**
 * La classe <code>CVM3</code> permet de lancer la JVM3 dans le cadre du systeme
 * reparti de ville intelligente contenant 4 JVM au total
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class CVM3 extends AbstractDistributedCVM {
	protected static long START_DELAY = 10000L;
	/** the start time of the simulation as a Java {@code LocalTime}. */
	protected static LocalTime simulatedStartTime;
	/** the end time of the simulation as a Java {@code LocalTime}. */
	protected static LocalTime simulatedEndTime;
	private static int nbFireCorrelators = 0;
	private ArrayList<String> busManagementOutboundPortsURIs;
	private ArrayList<String> otherBusURIs;
	private String uriBUS;
	private String cvmIdentifier;

	public CVM3(String[] args, int xLayout, int yLayout) throws Exception {
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
	 * Cree une base de regles contenant les regles de pompiers
	 * 
	 * @return la base de regles de pompiers
	 */
	private RuleBase createFireRuleBase() {
		RuleBase rb = new RuleBase();
		// notifications
		rb.addRule(new RuleF17());
		rb.addRule(new RuleF18());
		rb.addRule(new RuleF15());
		rb.addRule(new RuleF16());
		// autres regles
		rb.addRule(new RuleF5());
		rb.addRule(new RuleF6());
		rb.addRule(new RuleF6Bis());
		rb.addRule(new RuleF7());
		rb.addRule(new RuleF8());
		rb.addRule(new RuleF8Bis());
		rb.addRule(new RuleF9());
		rb.addRule(new RuleF10());
		rb.addRule(new RuleF11());
		rb.addRule(new RuleF12());
		rb.addRule(new RuleF13());
		rb.addRule(new RuleF14());
		rb.addRule(new RuleF14Bis());
		rb.addRule(new RuleF19());
		rb.addRule(new RuleF20());
		rb.addRule(new RuleF21());
		rb.addRule(new RuleF22());
		rb.addRule(new RuleF1());
		rb.addRule(new RuleF2());
		rb.addRule(new RuleF3());
		rb.addRule(new RuleF4());

		return rb;
	}

	/**
	 * Cree les composants casernes de pompiers
	 * 
	 * @param busURI URI du bus
	 * @return la liste des URI des stations
	 * @throws Exception
	 */
	private List<String> createFIREStations(String busURI) throws Exception {
		List<String> res = new ArrayList<String>();
		String notificationInboundPort;
		String asset;
		Iterator<String> fireStationIdsIterator = SmartCityDescriptor.createFireStationIdIterator();
		while (fireStationIdsIterator.hasNext()) {
			asset = fireStationIdsIterator.next();
			notificationInboundPort = "inbound-port-fire-station-" + asset;
			AbstractComponent.createComponent(FIREStation.class.getCanonicalName(), new Object[] { asset, busURI,
					SmartCityDescriptor.getActionInboundPortURI(asset), notificationInboundPort });
			res.add(asset);
		}
		return res;
	}

	/**
	 * Cree les correlateurs de pompiers
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
	private String[] createFireCorrelators(int number, String busURI, List<List<String>> executorsURIs,
			List<List<String>> emittersURIs) throws Exception {
		assert number > 0;
		assert number == executorsURIs.size();
		RuleBase rb = createFireRuleBase();
		String receptionInboundPort;
		String[] correlatorsInboundPort = new String[number];
		String uri;
		String eventDeliveryPool;
		for (int i = 0; i < number; i++) {
			uri = "Fire Correlator " + ++nbFireCorrelators + "-" + cvmIdentifier;
			eventDeliveryPool = uri + "event-delivery";
			receptionInboundPort = AbstractPort.generatePortURI();
			AbstractComponent.createComponent(FIRECorrelator.class.getCanonicalName(), new Object[] { uri, rb, busURI,
					executorsURIs.get(i), emittersURIs.get(i), receptionInboundPort, eventDeliveryPool });
			correlatorsInboundPort[i] = receptionInboundPort;
		}
		return correlatorsInboundPort;
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
		uriBUS = "URI-bus-" + cvmIdentifier;
		String internalRoutingPool = uriBUS + "-internal-routing";
		String regAndSubManagement = uriBUS + "-reg-and-sub-management";

		for (int i = 1; i < 5; i++) {
			if (i != 3) {
				otherBusURIs.add("URI-bus-jvm" + i);
				busManagementOutboundPortsURIs.add(AbstractPort.generatePortURI());
			}

		}
		uriBUS = AbstractComponent.createComponent(CEPBus.class.getCanonicalName(),
				new Object[] { uriBUS, emissionURI, managementURI, internalRoutingPool,
						regAndSubManagement, otherBusURIs,
						busManagementOutboundPortsURIs });
		return managementURI;
	}

	/**
	 * @see fr.sorbonne_u.components.cvm.DistributedComponentVirtualMachineI#instantiateAndPublish()
	 */
	@Override
	public void instantiateAndPublish() throws Exception {

		String busManagementURI = this.createBus();
		List<String> fireStationsURI = this.createFIREStations(busManagementURI);

		List<List<String>> correlatorsExecutorsFIRE = new ArrayList<List<String>>();
		correlatorsExecutorsFIRE.add(fireStationsURI);
		// Les emetteurs sont aussi les executeurs
		this.createFireCorrelators(1, busManagementURI, correlatorsExecutorsFIRE, correlatorsExecutorsFIRE);

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
			if (i == 2)
				cpt += 2;
			else
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
			CVM3 c = new CVM3(args, 3, 5);
			c.startStandardLifeCycle(TimeManager.get().computeExecutionDuration() + START_DELAY);
			Thread.sleep(10000L);
			System.exit(0);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
