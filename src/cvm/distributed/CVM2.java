package cvm.distributed;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import components.classes.CEPBus;
import components.classes.SAMUCenter;
import components.classes.SAMUCorrelator;
import connections.BusManagementConnector;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.cvm.AbstractDistributedCVM;
import fr.sorbonne_u.cps.smartcity.SmartCityDescriptor;
import fr.sorbonne_u.cps.smartcity.utils.TimeManager;
import rule.classes.*;

/**
 * La classe <code>CVM2</code> permet de lancer la JVM2 dans le cadre du systeme
 * reparti de ville intelligente contenant 4 JVM au total
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class CVM2 extends AbstractDistributedCVM {
	protected static long START_DELAY = 10000L;
	/** the start time of the simulation as a Java {@code LocalTime}. */
	protected static LocalTime simulatedStartTime;
	/** the end time of the simulation as a Java {@code LocalTime}. */
	protected static LocalTime simulatedEndTime;
	private static int nbHealthCorrelators = 0;
	private ArrayList<String> busManagementOutboundPortsURIs;
	private ArrayList<String> otherBusURIs;
	private String busURI;
	private String cvmIdentifier;

	public CVM2(String[] args, int xLayout, int yLayout) throws Exception {
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
	 * Cree la base d'evenement pour traiter les regles relatives au SAMU
	 * 
	 * @return la base de regle de SAMU
	 */
	private RuleBase createHealthRuleBase() {
		RuleBase rb = new RuleBase();
		// notifications
		rb.addRule(new RuleS18());
		rb.addRule(new RuleS19());
		rb.addRule(new RuleS16());
		rb.addRule(new RuleS17());
		// autres regles
		rb.addRule(new RuleS5());
		rb.addRule(new RuleS6());
		rb.addRule(new RuleS7());
		rb.addRule(new RuleS8());
		rb.addRule(new RuleS13());
		rb.addRule(new RuleS14());
		rb.addRule(new RuleS15());
		rb.addRule(new RuleS1());
		rb.addRule(new RuleS9());
		rb.addRule(new RuleS2());
		rb.addRule(new RuleS10());
		rb.addRule(new RuleS10Bis());
		rb.addRule(new RuleS3());
		rb.addRule(new RuleS11());
		rb.addRule(new RuleS4());
		rb.addRule(new RuleS12());
		rb.addRule(new RuleS12Bis());
		return rb;
	}

	/**
	 * Cree les centres de SAMU
	 * 
	 * @param busURI URI du bus
	 * @return la liste contenant les URI des centres de SAMU crees
	 * @throws Exception
	 */
	private List<String> createSAMUCenters(String busURI) throws Exception {
		List<String> res = new ArrayList<String>();
		String notificationInboundPort;
		String asset;
		Iterator<String> SAMUCenterIdsIterator = SmartCityDescriptor.createSAMUStationIdIterator();
		while (SAMUCenterIdsIterator.hasNext()) {
			asset = SAMUCenterIdsIterator.next();
			notificationInboundPort = "inbound-port-samu-center-" + asset;
			AbstractComponent.createComponent(SAMUCenter.class.getCanonicalName(), new Object[] { asset, busURI,
					SmartCityDescriptor.getActionInboundPortURI(asset), notificationInboundPort });
			res.add(asset);
		}
		return res;
	}

	/**
	 * Cree les correlateurs de SAMU
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
	private String[] createHealthCorrelators(int number, String busURI, List<List<String>> executorsURIs,
			List<List<String>> emittersURIs) throws Exception {
		assert number > 0;
		assert number == executorsURIs.size();
		RuleBase rb = createHealthRuleBase();
		String receptionInboundPort;
		String[] correlatorsInboundPort = new String[number];
		String uri;
		String eventDeliveryPool;
		for (int i = 0; i < number; i++) {
			uri = "Health Correlator " + ++nbHealthCorrelators + "-" + cvmIdentifier;
			eventDeliveryPool = uri + "event-delivery";
			receptionInboundPort = AbstractPort.generatePortURI();
			AbstractComponent.createComponent(SAMUCorrelator.class.getCanonicalName(), new Object[] { uri, rb, busURI,
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
		busURI = "URI-bus-" + cvmIdentifier;
		String internalRoutingPool = busURI + "-internal-routing";
		String regAndSubManagement = busURI + "-reg-and-sub-management";

		for (int i = 1; i < 5; i++) {
			if (i != 2) {
				otherBusURIs.add("URI-bus-jvm" + i);
				busManagementOutboundPortsURIs.add(AbstractPort.generatePortURI());
			}

		}
		busURI = AbstractComponent.createComponent(CEPBus.class.getCanonicalName(),
				new Object[] { busURI, emissionURI, managementURI, internalRoutingPool,
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
		List<String> samuCentersURI = this.createSAMUCenters(busManagementURI);

		List<List<String>> correlatorsExecutorsSAMU = new ArrayList<List<String>>();
		correlatorsExecutorsSAMU.add(samuCentersURI);
		// Les emetteurs sont aussi les executeurs
		this.createHealthCorrelators(1, busManagementURI, correlatorsExecutorsSAMU, correlatorsExecutorsSAMU);

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
			if (i == 1)
				cpt += 2;
			else
				cpt += 1;
			this.doPortConnection(busURI, busManagementOutboundPortsURIs.get(i),
					"URI-management-inbound-port-of-jvm" + cpt, BusManagementConnector.class.getCanonicalName());
		}
		super.interconnect();
	}

	public static void main(String[] args) {
		try {
			simulatedStartTime = LocalTime.of(12, 0);
			simulatedEndTime = LocalTime.of(12, 0).plusMinutes(30);
			CVM2 c = new CVM2(args, 3, 5);
			c.startStandardLifeCycle(TimeManager.get().computeExecutionDuration() + START_DELAY);
			Thread.sleep(10000L);
			System.exit(0);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
