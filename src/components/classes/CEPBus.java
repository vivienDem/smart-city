package components.classes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

import components.interfaces.CEPBusManagementImplI;
import components.interfaces.EventEmissionCI;
import components.interfaces.CEPBusManagementCI;
import components.interfaces.EventEmissionImplI;
import components.interfaces.EventReceptionCI;
import connections.BusEmissionInboundPort;
import connections.BusManagementInboundPort;
import connections.BusManagementOutboundPort;
import connections.BusReceptionOutboundPort;
import connections.ReceptionConnector;
import event.interfaces.EventI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;

/**
 * La classe <code>CEPBus</code> implemente le composant bus de l'architecture
 *
 * @author Adan Bougherara et Vivien Demeulenaere
 */
@OfferedInterfaces(offered = { EventEmissionCI.class, CEPBusManagementCI.class })
@RequiredInterfaces(required = { EventReceptionCI.class, CEPBusManagementCI.class })
public class CEPBus extends AbstractComponent implements CEPBusManagementImplI, EventEmissionImplI {
	private Map<String, Set<String>> subscribers; // map for emitters -> uri : subscribers' uri
	private Map<String, BusReceptionOutboundPort> correlators; // map for correlators -> uri : port
	private Map<String, String> executors;
	private List<BusReceptionOutboundPort> busReceptionOutboundPorts;
	private List<BusManagementOutboundPort> busManagementOutboundPorts;
	private BusEmissionInboundPort busEmissionInboundPort;
	private BusManagementInboundPort busManagementInboundPort;
	private String id;
	private Semaphore sem;
	private int nbManagementThreads = 10;
	private int nbRoutingThreads = 10;

	/**
	 * Cree un composant bus
	 * 
	 * @param id                            l'identifiant du Bus
	 * @param emissionURI                   URI du port entrant d'emission du bus
	 * @param managementURI                 URI du port entrant de management du bus
	 * @param internalRoutingPool           URI du pool de thread dedie au routage
	 *                                      interne
	 * @param regAndSubManagement           URI du pool de thread dedie au
	 *                                      enregistrements/desenregistrements sur
	 *                                      le bus
	 * @param otherBusURIs                  URIs des autres bus (execution repartie)
	 * @param busEmissionOutboundPortsURI   URIs des ports d'emissions vers les
	 *                                      autres bus (execution repartie)
	 * @param busManagementOutboundPortsURI URIs des ports de management vers les
	 *                                      autres bus (execution repartie)
	 * @throws Exception
	 */
	protected CEPBus(String id, String emissionURI, String managementURI, String internalRoutingPool,
			String regAndSubManagement, List<String> otherBusURIs, List<String> busManagementOutboundPortsURI)
			throws Exception {
		super(id, 10, 0);

		this.createNewExecutorService(internalRoutingPool, nbRoutingThreads, false);
		this.createNewExecutorService(regAndSubManagement, nbManagementThreads, false);

		/*
		 * Tests valable pour l'execution repartie assert
		 * busEmissionOutboundPortsURI.size() == 3; assert
		 * busManagementOutboundPortsURI.size() == 3;
		 */

		busManagementOutboundPorts = new ArrayList<BusManagementOutboundPort>();
		BusManagementOutboundPort managementPort;
		for (String uri : busManagementOutboundPortsURI) {
			managementPort = new BusManagementOutboundPort(uri, this);
			busManagementOutboundPorts.add(managementPort);
			managementPort.publishPort();

		}

		subscribers = new ConcurrentHashMap<String, Set<String>>();
		correlators = new ConcurrentHashMap<String, BusReceptionOutboundPort>();
		executors = new ConcurrentHashMap<String, String>();

		this.busEmissionInboundPort = new BusEmissionInboundPort(emissionURI, this,
				this.getExecutorServiceIndex(internalRoutingPool));
		this.busEmissionInboundPort.publishPort();

		this.busManagementInboundPort = new BusManagementInboundPort(managementURI, this,
				this.getExecutorServiceIndex(regAndSubManagement));
		this.busManagementInboundPort.publishPort();

		this.id = id;
		this.getTracer().setTitle(id);
		this.getTracer().setRelativePosition(0, 0);
		this.toggleTracing();

		busReceptionOutboundPorts = new Vector<BusReceptionOutboundPort>();

		sem = new Semaphore(1);

	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#start()
	 */
	@Override
	public synchronized void start() throws ComponentStartException {
		super.start();
	}

	@Override
	public synchronized void execute() throws Exception {
		super.execute();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 */
	@Override
	public synchronized void finalise() throws Exception {
		BusReceptionOutboundPort p;
		for (Entry<String, Set<String>> entry : subscribers.entrySet()) {
			for (String correlator : entry.getValue()) {
				p = correlators.get(correlator);
				if (p != null)
					this.doPortDisconnection(p.getPortURI());
				correlators.remove(correlator);
			}
		}

		for (BusManagementOutboundPort port : busManagementOutboundPorts) {
			this.doPortDisconnection(port.getPortURI());

		}

		super.finalise();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			this.busEmissionInboundPort.unpublishPort();
			this.busManagementInboundPort.unpublishPort();
			for (BusReceptionOutboundPort port : this.busReceptionOutboundPorts) {
				port.unpublishPort();
			}
			for (BusManagementOutboundPort port : busManagementOutboundPorts) {
				port.unpublishPort();
			}

		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}

	/**
	 * @see components.interfaces.EventEmissionImplI#sendEvent(java.lang.String,
	 *      event.interfaces.EventI)
	 */
	@Override
	public void sendEvent(String emitterURI, EventI event) throws Exception {
		sem.acquire(1);
		Set<String> correlatorURIs = subscribers.get(emitterURI);
		sem.release(1);
		assert correlatorURIs != null;
		this.traceMessage("Sending an event " + event.getClass().getSimpleName() + "\n");
		for (String correlatorURI : correlatorURIs) {
			this.runTask(o -> {
				try {
					EventReceptionCI er = correlators.get(correlatorURI);
					er.receiveEvent(emitterURI, event);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		}
	}

	/**
	 * @see components.interfaces.EventEmissionImplI#sendEvents(java.lang.String,
	 *      event.interfaces.EventI[])
	 */
	@Override
	public void sendEvents(String emitterURI, EventI[] events) throws Exception {
		sem.acquire(1);
		Set<String> correlatorURIs = subscribers.get(emitterURI);
		sem.release(1);
		assert correlatorURIs != null;
		this.traceMessage("Sending " + events.length + " event\n");
		for (String correlatorURI : correlatorURIs) {
			this.runTask(o -> {
				try {
					EventReceptionCI er = correlators.get(correlatorURI);
					er.receiveEvents(emitterURI, events);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		}
	}

	/**
	 * @see components.interfaces.CEPBusManagementImplI#registerEmitter(java.lang.String)
	 */
	@Override
	public String registerEmitter(String uri) throws Exception {
		Set<String> hs = new HashSet<String>();
		hs = Collections.synchronizedSet(hs);
		this.subscribers.put(uri, hs);
		return this.busEmissionInboundPort.getPortURI();
	}

	/**
	 * @see components.interfaces.CEPBusManagementImplI#unregisterEmitter(java.lang.String)
	 */
	@Override
	public void unregisterEmitter(String uri) throws Exception {
		sem.acquire(1);
		subscribers.remove(uri);
		sem.release(1);

	}

	/**
	 * @see components.interfaces.CEPBusManagementImplI#registerCorrelator(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public String registerCorrelator(String uri, String inboundPortURI) throws Exception {
		boolean spread = correlators.get(uri) == null;
		BusReceptionOutboundPort busReceptionPort = new BusReceptionOutboundPort(this);
		busReceptionPort.publishPort();
		this.doPortConnection(busReceptionPort.getPortURI(), inboundPortURI,
				ReceptionConnector.class.getCanonicalName());

		correlators.put(uri, busReceptionPort);
		this.busReceptionOutboundPorts.add(busReceptionPort);

		if (spread) {
			for (BusManagementOutboundPort port : busManagementOutboundPorts) {
				port.registerCorrelator(uri, inboundPortURI);
			}
		}

		return this.busEmissionInboundPort.getPortURI();
	}

	/**
	 * @see components.interfaces.CEPBusManagementImplI#unregisterCorrelator(java.lang.String)
	 */
	@Override
	public void unregisterCorrelator(String uri) throws Exception {
		for (Entry<String, Set<String>> entry : subscribers.entrySet()) {
			if (entry.getValue().contains(uri)) {
				entry.getValue().remove(uri);
				BusReceptionOutboundPort p = correlators.get(uri);
				this.doPortDisconnection(p.getPortURI());
			}
		}
		correlators.remove(uri);

	}

	/**
	 * @see components.interfaces.CEPBusManagementImplI#registerExecutor(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public void registerExecutor(String uri, String inboundPortURI) {
		executors.put(uri, inboundPortURI);
	}

	/**
	 * @see components.interfaces.CEPBusManagementImplI#unregisterExecutor(java.lang.String)
	 */
	@Override
	public void unregisterExecutor(String uri) {
		executors.remove(uri);
	}

	/**
	 * @see components.interfaces.CEPBusManagementImplI#subscribe(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public void subscribe(String subscriberURI, String emitterURI) throws Exception {
		if (!subscribers.containsKey(emitterURI)) {
			this.registerEmitter(emitterURI);
		}
		if (!subscribers.get(emitterURI).contains(subscriberURI)) {
			subscribers.get(emitterURI).add(subscriberURI);
			// On abonne subscriberURI a l'emetteur aupres des autres bus
			for (BusManagementOutboundPort port : busManagementOutboundPorts) {
				port.subscribe(subscriberURI, emitterURI);
			}
		}

	}

	/**
	 * @see components.interfaces.CEPBusManagementImplI#unsubscribe(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public void unsubscribe(String subscriberURI, String emitterURI) throws Exception {
		Set<String> subscribersURI = subscribers.get(emitterURI);
		if (subscribersURI != null) {
			subscribersURI.remove(subscriberURI);
			// On se desenregistre aupres des autres bus
			for (BusManagementOutboundPort port : busManagementOutboundPorts) {
				port.unsubscribe(id, emitterURI);
			}
		}
	}

	/**
	 * @see components.interfaces.CEPBusManagementImplI#getExecutorInboundPortURI(java.lang.String)
	 */
	@Override
	public String getExecutorInboundPortURI(String uri) throws Exception {
		String executorURI = executors.get(uri);
		return executorURI;
	}

}
