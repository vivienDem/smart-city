package cvm;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import components.classes.CEPBus;
import components.classes.FIREStation;
import components.classes.FIRECorrelator;
import components.classes.SAMUCorrelator;
import components.classes.SAMUCenter;
import components.classes.TRAFFICCorrelator;
import components.classes.TRAFFICLights;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.cps.smartcity.AbstractSmartCityCVM;
import fr.sorbonne_u.cps.smartcity.SmartCityDescriptor;
import fr.sorbonne_u.cps.smartcity.grid.IntersectionPosition;
import fr.sorbonne_u.cps.smartcity.utils.TimeManager;
import rule.classes.RuleBase;
import rule.classes.*;

/**
 * La classe <code>CVM</code> permet de lancer le systeme de ville intelligente
 * avec une unique JVM
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class CVM extends AbstractSmartCityCVM {
	protected static long START_DELAY = 8000L;

	private static int nbBus = 0;
	private static int nbHealthCorrelators = 0;
	private static int nbFireCorrelators = 0;
	private static int nbTrafficCorrelators = 0;
	private String cvmIdentifier;

	public CVM() throws Exception {
		super();
		this.cvmIdentifier = AbstractCVM.getThisJVMURI();
	}

	/**
	 * @see fr.sorbonne_u.components.cvm.AbstractCVM#deploy()
	 */
	@Override
	public void deploy() throws Exception {
		String busManagementURI = this.createBus();
		List<String> samuCentersURI = this.createSAMUCenters(busManagementURI);
		List<String> fireStationsURI = this.createFIREStations(busManagementURI);
		List<String> trafficLightsURI = this.createTRAFFICLights(busManagementURI);

		List<List<String>> correlatorsExecutorsSAMU = new ArrayList<List<String>>();
		correlatorsExecutorsSAMU.add(samuCentersURI);
		// Les emetteurs sont aussi les executeurs
		this.createHealthCorrelators(1, busManagementURI, correlatorsExecutorsSAMU, correlatorsExecutorsSAMU);

		List<List<String>> correlatorsExecutorsFIRE = new ArrayList<List<String>>();
		correlatorsExecutorsFIRE.add(fireStationsURI);
		this.createFireCorrelators(1, busManagementURI, correlatorsExecutorsFIRE, correlatorsExecutorsFIRE);

		List<List<String>> correlatorsExecutorsTRAFFIC = new ArrayList<List<String>>();
		List<List<String>> correlatorsEmittersTRAFFIC = new ArrayList<List<String>>();
		List<String> copy = new ArrayList<String>(trafficLightsURI);
		correlatorsExecutorsTRAFFIC.add(copy);
		correlatorsEmittersTRAFFIC.add(trafficLightsURI);
		correlatorsEmittersTRAFFIC.get(0).addAll(samuCentersURI);
		correlatorsEmittersTRAFFIC.get(0).addAll(fireStationsURI);

		this.createTrafficLightsCorrelators(1, busManagementURI, correlatorsExecutorsTRAFFIC,
				correlatorsEmittersTRAFFIC);

		super.deploy();
	}

	/**
	 * Cree une base de regles contenant les regles de sante
	 * 
	 * @return la base de regles de sante
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
	 * Cree une base de regles contenant les regles de feu
	 * 
	 * @return la base de regles de feu
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
	 * Cree une base de regles contenant les regles de traffic
	 * 
	 * @return la base de regles de traffic
	 */
	private RuleBase createTrafficRuleBase() {
		RuleBase rb = new RuleBase();
		rb.addRule(new RuleC4());
		rb.addRule(new RuleC5());
		rb.addRule(new RuleC2());
		rb.addRule(new RuleC3());
		rb.addRule(new RuleC1());
		return rb;
	}

	/**
	 * 
	 * @return Cree le composant bus
	 * @throws URI pour communiquer avec le bus
	 */
	private String createBus() throws Exception {
		String emissionURI = AbstractPort.generatePortURI();
		String managementURI = AbstractPort.generatePortURI();

		String uri = "Bus-" + nbBus + "-" + cvmIdentifier;
		String internalRoutingPool = uri + "-internal-routing";
		String regAndSubManagement = uri + "-reg-and-sub-management";
		AbstractComponent.createComponent(CEPBus.class.getCanonicalName(),
				new Object[] { uri, emissionURI, managementURI, internalRoutingPool,
						regAndSubManagement, new ArrayList<>(), new ArrayList<>() });
		return managementURI;
	}

	/**
	 * Cree les composants centres de SAMU
	 * 
	 * @param busURI URI du bus
	 * @return la liste des URI des centres
	 * @throws Exception
	 */
	private List<String> createSAMUCenters(String busURI) throws Exception {
		List<String> res = new ArrayList<String>();
		String notificationInboundPort;
		String asset;
		Iterator<String> SAMUCenterIdsIterator = SmartCityDescriptor.createSAMUStationIdIterator();
		while (SAMUCenterIdsIterator.hasNext()) {
			asset = SAMUCenterIdsIterator.next();
			notificationInboundPort = AbstractPort.generatePortURI();
			AbstractComponent.createComponent(SAMUCenter.class.getCanonicalName(), new Object[] { asset, busURI,
					SmartCityDescriptor.getActionInboundPortURI(asset), notificationInboundPort });
			res.add(asset);
			this.register(asset, notificationInboundPort);
		}
		return res;
	}

	/**
	 * Cree les composants casernes de pompiers
	 * 
	 * @param busURI URI du bus
	 * @return la liste des URI des stations
	 * @throws Exception
	 */
	private List<String> createFIREStations(String busURI)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, Exception {
		List<String> res = new ArrayList<String>();
		String notificationInboundPort;
		String asset;
		Iterator<String> fireStationIdsIterator = SmartCityDescriptor.createFireStationIdIterator();
		while (fireStationIdsIterator.hasNext()) {
			asset = fireStationIdsIterator.next();
			notificationInboundPort = AbstractPort.generatePortURI();
			AbstractComponent.createComponent(FIREStation.class.getCanonicalName(), new Object[] { asset, busURI,
					SmartCityDescriptor.getActionInboundPortURI(asset), notificationInboundPort });
			res.add(asset);
			this.register(asset, notificationInboundPort);
		}
		return res;
	}

	/**
	 * Cree les composants feux de circulations
	 * 
	 * @param busURI URI du bus
	 * @return la liste des URI des stations
	 * @throws Exception
	 */
	private List<String> createTRAFFICLights(String busURI)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, Exception {
		List<String> res = new ArrayList<String>();
		String notificationInboundPort;
		IntersectionPosition asset;
		Iterator<IntersectionPosition> trafficLightsIterator = SmartCityDescriptor.createTrafficLightPositionIterator();
		while (trafficLightsIterator.hasNext()) {
			asset = trafficLightsIterator.next();
			notificationInboundPort = AbstractPort.generatePortURI();
			AbstractComponent.createComponent(TRAFFICLights.class.getCanonicalName(), new Object[] { asset.toString(),
					busURI, SmartCityDescriptor.getActionInboundPortURI(asset), notificationInboundPort });
			res.add(asset.toString());
			this.register(asset.toString(), notificationInboundPort);
		}
		return res;
	}

	/**
	 * 
	 * @param number        le nombre de correlateurs de sante qu'il faut creer
	 * @param busURI        URI pour communiquer avec le bus
	 * @param executorsURIs Une liste contenant pour chaque correlateur cree une
	 *                      liste des executeurs d'actions
	 * @param emittersURIs  Une liste contenant pour chaque correlateur cree une
	 *                      liste des emetteurs d'actions
	 * @return URI des ports entrants des correlateurs
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
	 * 
	 * @param number        le nombre de correlateurs de feu qu'il faut creer
	 * @param busURI        URI pour communiquer avec le bus
	 * @param executorsURIs Une liste contenant pour chaque correlateur cree une
	 *                      liste des executeurs d'actions
	 * @param emittersURIs  Une liste contenant pour chaque correlateur cree une
	 *                      liste des emetteurs d'actions
	 * @return URI des ports entrants des correlateurs
	 * @throws Exception
	 */
	private String[] createFireCorrelators(int number, String busURI, List<List<String>> executorsURIs,
			List<List<String>> emittersURIs)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, Exception {
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
	 * 
	 * @param number        le nombre de correlateurs de traffic qu'il faut creer
	 * @param busURI        URI pour communiquer avec le bus
	 * @param executorsURIs Une liste contenant pour chaque correlateur cree une
	 *                      liste des executeurs d'actions
	 * @param emittersURIs  Une liste contenant pour chaque correlateur cree une
	 *                      liste des emetteurs d'actions
	 * @return URI des ports entrants des correlateurs
	 * @throws Exception
	 */
	private String[] createTrafficLightsCorrelators(int number, String busURI, List<List<String>> executorsURIs,
			List<List<String>> emittersURIs)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, Exception {
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

	public static void main(String[] args) {
		try {
			simulatedStartTime = LocalTime.of(12, 0);
			simulatedEndTime = LocalTime.of(12, 0).plusMinutes(30);
			CVM c = new CVM();
			c.startStandardLifeCycle(TimeManager.get().computeExecutionDuration() + START_DELAY);
			Thread.sleep(10000L);
			System.exit(0);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
