package components.classes;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RejectedExecutionException;

import components.interfaces.CEPBusManagementCI;
import components.interfaces.EventReceptionImplI;
import components.interfaces.ResponseI;
import components.interfaces.SAMUCorrelatorI;
import connections.BusManagementConnector;
import connections.CorrelatorActionOutboundPort;
import connections.CorrelatorBusManagementOutboundPort;
import event.classes.AmbulanceInterventionAsk;
import event.classes.ConsciousFall;
import event.classes.EventBase;
import event.classes.MedicInterventionAsk;
import event.interfaces.EventI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.cps.smartcity.SmartCityDescriptor;
import plugins.CorrelatorPlugin;
import rule.classes.RuleBase;

/**
 * La classe <code> SAMUCorrelator </code> implemente un correlateur relatif au
 * SAMU
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
@RequiredInterfaces(required = { CEPBusManagementCI.class })
public class SAMUCorrelator extends AbstractComponent implements SAMUCorrelatorI, EventReceptionImplI {
	private CorrelatorBusManagementOutboundPort correlatorBusManagementOutboundPort; // port transmis et gere par la suite dans le plugin
	private String busManagementURI;
	private RuleBase rb;
	private EventBase eb;
	private List<String> executorsURI;
	private List<String> emittersURI;
	private String id;
	private String pluginURI;
	private CorrelatorPlugin plugin;
	private String receptionURI;
	private int indexOfeventDeliveryPool;

	/**
	 * Cree un correlateur de sante
	 * 
	 * @param rb           une base de regles
	 * @param uriBus       URI du port entrant de management du bus, permettant de
	 *                     s'y enregistrer
	 * @param executorsURI URI des executeurs qui sont lies directement au
	 *                     correlateur
	 * @param emittersURI  URI des emetteurs que le correlateur veut suivre
	 * @param receptionURI URI du port entrant de reception
	 * @throws Exception
	 */
	protected SAMUCorrelator(String id, RuleBase rb, String busManagementURI, List<String> executorsURI,
			List<String> emittersURI, String receptionURI, String eventDeliveryPool) throws Exception {
		super(2, 0);
		this.id = id;

		this.busManagementURI = busManagementURI;
		this.rb = rb;
		this.eb = new EventBase();
		this.executorsURI = executorsURI;
		this.emittersURI = emittersURI;
		this.pluginURI = id + "-plugin-uri";
		this.createNewExecutorService(eventDeliveryPool, 1, false);
		this.receptionURI = receptionURI;
		this.indexOfeventDeliveryPool = this.getExecutorServiceIndex(eventDeliveryPool);

		this.correlatorBusManagementOutboundPort = new CorrelatorBusManagementOutboundPort(this);
		this.correlatorBusManagementOutboundPort.publishPort();

		this.getTracer().setTitle(id);
		this.getTracer().setRelativePosition(3, 0);
		this.toggleTracing();

	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#start()
	 */
	@Override
	public synchronized void start() throws ComponentStartException {
		try {
			this.doPortConnection(correlatorBusManagementOutboundPort.getPortURI(), this.busManagementURI,
					BusManagementConnector.class.getCanonicalName());

		} catch (Exception e) {
			throw new ComponentStartException(e);
		}
		super.start();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#execute()
	 */
	@Override
	public synchronized void execute() throws Exception {
		super.execute();
		plugin = new CorrelatorPlugin(id, correlatorBusManagementOutboundPort, receptionURI, indexOfeventDeliveryPool,
				executorsURI, emittersURI);
		plugin.setPluginURI(pluginURI);
		this.installPlugin(plugin);
	}

	/**
	 * @see components.interfaces.EventReceptionCI#receiveEvent(java.lang.String,
	 *      event.interfaces.EventI)
	 */
	@Override
	public void receiveEvent(String emitterURI, EventI e) throws Exception {
		this.traceMessage("Event received " + e.getClass().getSimpleName() + "\n");
		eb.addEvent(e);
		rb.fireAllOn(eb, this);
	}

	/**
	 * @see components.interfaces.EventReceptionCI#receiveEvents(java.lang.String,
	 *      event.interfaces.EventI[])
	 */
	@Override
	public void receiveEvents(String emitterURI, EventI[] events) throws Exception {
		for (EventI e : events) {
			this.traceMessage("Event received " + e.getClass().getSimpleName() + "\n");
			eb.addEvent(e);
		}
		rb.fireAllOn(eb, this);
	}

	/**
	 * @see components.interfaces.SAMUCorrelatorI#triggerAmbulanceIntervention(java.io.Serializable,
	 *      java.io.Serializable)
	 */
	@Override
	public void triggerAmbulanceIntervention(Serializable position, Serializable centerId)
			throws RejectedExecutionException, AssertionError, InterruptedException, ExecutionException, Exception {
		CorrelatorActionOutboundPort correlatorActionOutboundPort = plugin.getCorrelatorActionOutboundPorts()
				.get(executorsURI.indexOf(centerId));
		correlatorActionOutboundPort.executeAction(SAMUCenterActions.SendAmbulance, new Serializable[] { position });
	}

	/**
	 * @see components.interfaces.SAMUCorrelatorI#isInArea(java.io.Serializable,
	 *      java.io.Serializable)
	 */
	@Override
	public boolean isInArea(Serializable position, Serializable centerId)
			throws RejectedExecutionException, AssertionError, InterruptedException, ExecutionException, Exception {
		CorrelatorActionOutboundPort correlatorActionOutboundPort = plugin.getCorrelatorActionOutboundPorts()
				.get(executorsURI.indexOf(centerId));
		if (correlatorActionOutboundPort.executeAction(SAMUCenterActions.CheckArea,
				new Serializable[] { position }) == Responses.SUCCESS)
			return true;
		return false;
	}

	/**
	 * @see components.interfaces.SAMUCorrelatorI#ambulanceAvailable(java.io.Serializable)
	 */
	@Override
	public boolean ambulanceAvailable(Serializable centerId) throws Exception {
		CorrelatorActionOutboundPort correlatorActionOutboundPort = plugin.getCorrelatorActionOutboundPorts()
				.get(executorsURI.indexOf(centerId));
		if (correlatorActionOutboundPort.executeAction(SAMUCenterActions.CheckAmbulanceAvailable,
				new Serializable[0]) == Responses.SUCCESS)
			return true;
		return false;
	}

	/**
	 * @see components.interfaces.SAMUCorrelatorI#medicAvailable(java.io.Serializable)
	 */
	@Override
	public boolean medicAvailable(Serializable centerId) throws Exception {
		CorrelatorActionOutboundPort correlatorActionOutboundPort = plugin.getCorrelatorActionOutboundPorts()
				.get(executorsURI.indexOf(centerId));
		if (correlatorActionOutboundPort.executeAction(SAMUCenterActions.CheckMedicAvailable,
				new Serializable[0]) == Responses.SUCCESS)
			return true;
		return false;
	}

	/**
	 * @see components.interfaces.SAMUCorrelatorI#triggerMedicIntervention(java.io.Serializable,
	 *      java.io.Serializable)
	 */
	@Override
	public void triggerMedicIntervention(Serializable position, Serializable centerId) throws Exception {
		CorrelatorActionOutboundPort correlatorActionOutboundPort = plugin.getCorrelatorActionOutboundPorts()
				.get(executorsURI.indexOf(centerId));

		correlatorActionOutboundPort.executeAction(SAMUCenterActions.SendMedic, new Serializable[] { position });
		// centre sollicite remis a false lors de l'evenement AmbulanceAvailable
	}

	/**
	 * @see components.interfaces.SAMUCorrelatorI#redirectAmbulanceIntervention(java.io.Serializable)
	 */
	@Override
	public void redirectAmbulanceIntervention(Serializable event) throws Exception {
		AmbulanceInterventionAsk aia = new AmbulanceInterventionAsk(event);
		eb.addEvent(aia);
	}

	/**
	 * @see components.interfaces.SAMUCorrelatorI#redirectMedicIntervention(java.io.Serializable)
	 */
	@Override
	public void redirectMedicIntervention(Serializable event) throws Exception {
		MedicInterventionAsk mia = new MedicInterventionAsk(event);
		eb.addEvent(mia);
	}

	/**
	 * @see components.interfaces.SAMUCorrelatorI#redirectTrackingEvent(java.io.Serializable,
	 *      java.io.Serializable)
	 */
	@Override
	public void redirectTrackingEvent(Serializable position, Serializable centerId) throws Exception {
		CorrelatorActionOutboundPort correlatorActionOutboundPort = plugin.getCorrelatorActionOutboundPorts()
				.get(executorsURI.indexOf(centerId));
		correlatorActionOutboundPort.executeAction(SAMUCenterActions.SendMedic, new Serializable[] { position });
	}

	/**
	 * @see components.interfaces.SAMUCorrelatorI#triggerMedicCall(java.io.Serializable,
	 *      java.io.Serializable, java.io.Serializable)
	 */
	@Override
	public void triggerMedicCall(Serializable position, Serializable personId, Serializable centerId) throws Exception {
		CorrelatorActionOutboundPort correlatorActionOutboundPort = plugin.getCorrelatorActionOutboundPorts()
				.get(executorsURI.indexOf(centerId));
		correlatorActionOutboundPort.executeAction(SAMUCenterActions.MedicCall,
				new Serializable[] { position, personId });
		// centre sollicite remis a false lors de l'evenement AmbulanceAvailable
	}

	/**
	 * @see components.interfaces.SAMUCorrelatorI#redirectConsciousFall(event.classes.ConsciousFall)
	 */
	@Override
	public void redirectConsciousFall(ConsciousFall c) throws Exception {
		this.eb.addEvent(c);
	}

	/**
	 * @see components.interfaces.SAMUCorrelatorI#redirectMedicCall(java.io.Serializable,
	 *      java.io.Serializable, java.io.Serializable)
	 */
	@Override
	public void redirectMedicCall(Serializable position, Serializable personId, Serializable centerId)
			throws Exception {
		CorrelatorActionOutboundPort correlatorActionOutboundPort = plugin.getCorrelatorActionOutboundPorts()
				.get(executorsURI.indexOf(centerId));
		correlatorActionOutboundPort.executeAction(SAMUCenterActions.MedicCall,
				new Serializable[] { position, personId });
	}

	/**
	 * @see components.interfaces.SAMUCorrelatorI#otherSamuCenter(java.io.Serializable,
	 *      java.io.Serializable)
	 */
	public String otherSamuCenter(Serializable centerId, Serializable type) throws Exception {
		String center = null;
		double distanceMin = Double.MAX_VALUE;
		double distance;

		if (((String) type).equals("emergency")) {
			for (String c : this.executorsURI) {
				if (!c.equals(centerId)) {
					CorrelatorActionOutboundPort correlatorActionOutboundPort = plugin
							.getCorrelatorActionOutboundPorts().get(executorsURI.indexOf(c));
					ResponseI res = correlatorActionOutboundPort
							.executeAction(SAMUCenterActions.CheckAmbulanceAvailable, new Serializable[0]);

					distance = SmartCityDescriptor.distance((String) centerId, c);
					if (res == Responses.SUCCESS && distance < distanceMin) {
						distanceMin = distance;
						center = c;
					}
				}
			}
			return center;
		} else {
			for (String c : this.executorsURI) {
				if (!c.equals(centerId)) {
					CorrelatorActionOutboundPort correlatorActionOutboundPort = plugin
							.getCorrelatorActionOutboundPorts().get(executorsURI.indexOf(c));
					ResponseI res = correlatorActionOutboundPort.executeAction(SAMUCenterActions.CheckMedicAvailable,
							new Serializable[0]);

					distance = SmartCityDescriptor.distance((String) centerId, c);
					if (res == Responses.SUCCESS && distance < distanceMin) {
						distanceMin = distance;
						center = c;
					}
				}
			}
		}
		return center;
	}

	/**
	 * @see components.interfaces.SAMUCorrelatorI#setMedicsNonAvailable(java.lang.String)
	 */
	@Override
	public void setMedicsNonAvailable(String centerId)
			throws RejectedExecutionException, AssertionError, InterruptedException, ExecutionException, Exception {
		CorrelatorActionOutboundPort correlatorActionOutboundPort = plugin.getCorrelatorActionOutboundPorts()
				.get(this.executorsURI.indexOf(centerId));
		correlatorActionOutboundPort.executeAction(SAMUCenterActions.MedicsNonAvailable, new Serializable[0]);
	}

	/**
	 * @see components.interfaces.SAMUCorrelatorI#setAmbulancesAvailable(java.lang.String)
	 */
	@Override
	public void setAmbulancesAvailable(String centerId)
			throws RejectedExecutionException, AssertionError, InterruptedException, ExecutionException, Exception {
		CorrelatorActionOutboundPort correlatorActionOutboundPort = plugin.getCorrelatorActionOutboundPorts()
				.get(this.executorsURI.indexOf(centerId));
		correlatorActionOutboundPort.executeAction(SAMUCenterActions.AmbulancesAvailable, new Serializable[0]);
	}

	/**
	 * @see components.interfaces.SAMUCorrelatorI#setMedicsAvailable(java.lang.String)
	 */
	@Override
	public void setMedicsAvailable(String centerId)
			throws RejectedExecutionException, AssertionError, InterruptedException, ExecutionException, Exception {
		CorrelatorActionOutboundPort correlatorActionOutboundPort = plugin.getCorrelatorActionOutboundPorts()
				.get(this.executorsURI.indexOf(centerId));
		correlatorActionOutboundPort.executeAction(SAMUCenterActions.MedicsAvailable, new Serializable[0]);
	}

	/**
	 * @see components.interfaces.SAMUCorrelatorI#setAmbulancesNonAvailable(java.lang.String)
	 */
	@Override
	public void setAmbulancesNonAvailable(String centerId)
			throws RejectedExecutionException, AssertionError, InterruptedException, ExecutionException, Exception {
		CorrelatorActionOutboundPort correlatorActionOutboundPort = plugin.getCorrelatorActionOutboundPorts()
				.get(this.executorsURI.indexOf(centerId));
		correlatorActionOutboundPort.executeAction(SAMUCenterActions.AmbulancesNonAvailable, new Serializable[0]);
	}

}
