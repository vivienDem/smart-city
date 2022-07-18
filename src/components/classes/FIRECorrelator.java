package components.classes;

import java.io.Serializable;
import java.util.List;

import components.interfaces.CEPBusManagementCI;
import components.interfaces.EventReceptionImplI;
import components.interfaces.FIRECorrelatorI;
import components.interfaces.ResponseI;
import connections.BusManagementConnector;
import connections.CorrelatorActionOutboundPort;
import connections.CorrelatorBusManagementOutboundPort;
import event.classes.EndOfFire;
import event.classes.EventBase;
import event.classes.FireGeneralAlarm;
import event.classes.FireInterventionAsk;
import event.classes.SecondFireAlarm;
import event.interfaces.EventI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.cps.smartcity.SmartCityDescriptor;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfFirefightingResource;
import plugins.CorrelatorPlugin;
import rule.classes.RuleBase;

/**
 * La classe <code> FIRECorrelator </code> implemente un correlateur relatif a
 * une caserne de pompier
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */

@RequiredInterfaces(required = { CEPBusManagementCI.class })
public class FIRECorrelator extends AbstractComponent implements FIRECorrelatorI, EventReceptionImplI {
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
	 * Cree un correlateur de pompier
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
	protected FIRECorrelator(String id, RuleBase rb, String busManagementURI, List<String> executorsURI,
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
		this.getTracer().setRelativePosition(3, 1);
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
	 * @see components.interfaces.EventReceptionImplI#receiveEvent(java.lang.String,
	 *      event.interfaces.EventI)
	 */
	@Override
	public void receiveEvent(String emitterURI, EventI e) throws Exception {
		this.traceMessage("Event received " + e.getClass().getSimpleName() + "\n");
		eb.addEvent(e);
		rb.fireAllOn(eb, this);
	}

	/**
	 * @see components.interfaces.EventReceptionImplI#receiveEvents(java.lang.String,
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
	 * @see components.interfaces.FIRECorrelatorI#isInArea(java.io.Serializable,
	 *      java.io.Serializable)
	 */
	@Override
	public boolean isInArea(Serializable position, Serializable centerId) throws Exception {
		CorrelatorActionOutboundPort correlatorActionOutboundPort = plugin.getCorrelatorActionOutboundPorts()
				.get(executorsURI.indexOf(centerId));
		if (correlatorActionOutboundPort.executeAction(FIREStationActions.CheckArea,
				new Serializable[] { position }) == Responses.SUCCESS)
			return true;
		return false;
	}

	/**
	 * @see components.interfaces.FIRECorrelatorI#otherFireStation(java.io.Serializable,
	 *      java.io.Serializable)
	 */
	@Override
	public String otherFireStation(Serializable stationId, Serializable type) throws Exception {
		String station = null;
		double distanceMin = Double.MAX_VALUE;
		double distance;

		if (((String) type).equals("house")) {
			for (String s : this.executorsURI) {
				if (!s.equals(stationId)) {
					CorrelatorActionOutboundPort correlatorActionOutboundPort = plugin
							.getCorrelatorActionOutboundPorts().get(executorsURI.indexOf(s));
					ResponseI res = correlatorActionOutboundPort.executeAction(FIREStationActions.CheckStandardTruck,
							new Serializable[0]);
					distance = SmartCityDescriptor.distance((String) stationId, s);
					if (res == Responses.SUCCESS && distance < distanceMin) {
						distanceMin = distance;
						station = s;
					}
				}
			}
			return station;
		} else {
			for (String s : this.executorsURI) {
				if (!s.equals(stationId)) {
					CorrelatorActionOutboundPort correlatorActionOutboundPort = plugin
							.getCorrelatorActionOutboundPorts().get(executorsURI.indexOf(s));
					ResponseI res = correlatorActionOutboundPort.executeAction(FIREStationActions.CheckHighLadder,
							new Serializable[0]);
					distance = SmartCityDescriptor.distance((String) stationId, s);
					if (res == Responses.SUCCESS && distance < distanceMin) {
						distanceMin = distance;
						station = s;
					}
				}
			}
		}
		return station;
	}

	/**
	 * @see components.interfaces.FIRECorrelatorI#triggerFirstAlarmOnBuilding(java.io.Serializable,
	 *      java.io.Serializable)
	 */
	@Override
	public void triggerFirstAlarmOnBuilding(Serializable position, Serializable centerId) throws Exception {
		CorrelatorActionOutboundPort correlatorActionOutboundPort = plugin.getCorrelatorActionOutboundPorts()
				.get(executorsURI.indexOf(centerId));
		correlatorActionOutboundPort.executeAction(FIREStationActions.FirstAlarm,
				new Serializable[] { position, TypeOfFirefightingResource.HighLadderTruck });

	}

	/**
	 * @see components.interfaces.FIRECorrelatorI#triggerFirstAlarmOnHouse(java.io.Serializable,
	 *      java.io.Serializable)
	 */
	@Override
	public void triggerFirstAlarmOnHouse(Serializable position, Serializable centerId) throws Exception {
		CorrelatorActionOutboundPort correlatorActionOutboundPort = plugin.getCorrelatorActionOutboundPorts()
				.get(executorsURI.indexOf(centerId));
		correlatorActionOutboundPort.executeAction(FIREStationActions.FirstAlarm,
				new Serializable[] { position, TypeOfFirefightingResource.StandardTruck });

	}

	/**
	 * @see components.interfaces.FIRECorrelatorI#triggerGeneralAlarm(java.io.Serializable,
	 *      java.io.Serializable)
	 */
	@Override
	public void triggerGeneralAlarm(Serializable position, Serializable centerId) throws Exception {
		CorrelatorActionOutboundPort correlatorActionOutboundPort = plugin.getCorrelatorActionOutboundPorts()
				.get(executorsURI.indexOf(centerId));
		correlatorActionOutboundPort.executeAction(FIREStationActions.GeneralAlarm, new Serializable[] { position });
	}

	/**
	 * @see components.interfaces.FIRECorrelatorI#triggerSecondAlarm(java.io.Serializable,
	 *      java.io.Serializable)
	 */
	@Override
	public void triggerSecondAlarm(Serializable position, Serializable centerId) throws Exception {
		CorrelatorActionOutboundPort correlatorActionOutboundPort = plugin.getCorrelatorActionOutboundPorts()
				.get(executorsURI.indexOf(centerId));
		correlatorActionOutboundPort.executeAction(FIREStationActions.SecondAlarm, new Serializable[] { position });
	}

	/**
	 * @see components.interfaces.FIRECorrelatorI#redirectSecondFireAlarm(event.classes.SecondFireAlarm)
	 */
	@Override
	public void redirectSecondFireAlarm(SecondFireAlarm sfa) throws Exception {
		this.eb.addEvent(sfa);
		// rb.fireAllOn(eb, this);

	}

	/**
	 * @see components.interfaces.FIRECorrelatorI#highLadderAvailable(java.io.Serializable)
	 */
	@Override
	public boolean highLadderAvailable(Serializable centerId) throws Exception {
		CorrelatorActionOutboundPort correlatorActionOutboundPort = plugin.getCorrelatorActionOutboundPorts()
				.get(this.executorsURI.indexOf(centerId));
		ResponseI res = correlatorActionOutboundPort.executeAction(FIREStationActions.CheckHighLadder,
				new Serializable[0]);
		return res == Responses.SUCCESS;

	}

	/**
	 * @see components.interfaces.FIRECorrelatorI#standardTruckAvailable(java.io.Serializable)
	 */
	@Override
	public boolean standardTruckAvailable(Serializable centerId) throws Exception {
		CorrelatorActionOutboundPort correlatorActionOutboundPort = plugin.getCorrelatorActionOutboundPorts()
				.get(this.executorsURI.indexOf(centerId));
		ResponseI res = correlatorActionOutboundPort.executeAction(FIREStationActions.CheckStandardTruck,
				new Serializable[0]);
		return res == Responses.SUCCESS;

	}

	/**
	 * @see components.interfaces.FIRECorrelatorI#redirectEndOfFireToAll(event.classes.EndOfFire)
	 */
	@Override
	public void redirectEndOfFireToAll(EndOfFire event) throws Exception {
		this.eb.addEvent(event);
		rb.fireAllOn(eb, this);

	}

	/**
	 * @see components.interfaces.FIRECorrelatorI#redirectFireInterventionAsk(event.classes.FireInterventionAsk)
	 */
	@Override
	public void redirectFireInterventionAsk(FireInterventionAsk fireIntervention) throws Exception {
		this.eb.addEvent(fireIntervention);
		// rb.fireAllOn(eb, this);

	}

	/**
	 * @see components.interfaces.FIRECorrelatorI#redirectGeneralAlarmToAll(event.classes.FireGeneralAlarm,
	 *      java.io.Serializable)
	 */
	@Override
	public void redirectGeneralAlarmToAll(FireGeneralAlarm fireGeneralAlarm, Serializable centerId) throws Exception {
		CorrelatorActionOutboundPort correlatorActionOutboundPort = plugin.getCorrelatorActionOutboundPorts()
				.get(executorsURI.indexOf(centerId));

		for (CorrelatorActionOutboundPort port : plugin.getCorrelatorActionOutboundPorts()) {
			if (port == correlatorActionOutboundPort)
				continue;
			FireGeneralAlarm fga = (FireGeneralAlarm) fireGeneralAlarm.clone();
			fga.setTimeStamp(fireGeneralAlarm.getTimeStamp());
			this.eb.addEvent(fga);
		}
		rb.fireAllOn(eb, this);

	}

	/**
	 * @see components.interfaces.FIRECorrelatorI#setStandardTrucksAvailable(java.io.Serializable)
	 */
	@Override
	public void setStandardTrucksAvailable(Serializable centerId) throws Exception {
		CorrelatorActionOutboundPort correlatorActionOutboundPort = plugin.getCorrelatorActionOutboundPorts()
				.get(this.executorsURI.indexOf(centerId));
		correlatorActionOutboundPort.executeAction(FIREStationActions.StandardTruckAvailable, new Serializable[0]);
	}

	/**
	 * @see components.interfaces.FIRECorrelatorI#setHighLaddersAvailable(java.io.Serializable)
	 */
	@Override
	public void setHighLaddersAvailable(Serializable centerId) throws Exception {
		CorrelatorActionOutboundPort correlatorActionOutboundPort = plugin.getCorrelatorActionOutboundPorts()
				.get(this.executorsURI.indexOf(centerId));
		correlatorActionOutboundPort.executeAction(FIREStationActions.HighLadderAvailable, new Serializable[0]);
	}

	/**
	 * @see components.interfaces.FIRECorrelatorI#setHighLaddersNonAvailable(java.io.Serializable)
	 */
	@Override
	public void setHighLaddersNonAvailable(Serializable centerId) throws Exception {
		CorrelatorActionOutboundPort correlatorActionOutboundPort = plugin.getCorrelatorActionOutboundPorts()
				.get(this.executorsURI.indexOf(centerId));
		correlatorActionOutboundPort.executeAction(FIREStationActions.HighLadderNonAvailable, new Serializable[0]);
	}

	/**
	 * @see components.interfaces.FIRECorrelatorI#setStandardTrucksNonAvailable(java.io.Serializable)
	 */
	@Override
	public void setStandardTrucksNonAvailable(Serializable centerId) throws Exception {
		CorrelatorActionOutboundPort correlatorActionOutboundPort = plugin.getCorrelatorActionOutboundPorts()
				.get(this.executorsURI.indexOf(centerId));
		correlatorActionOutboundPort.executeAction(FIREStationActions.StandardTruckNonAvailable, new Serializable[0]);

	}

	/**
	 * Lance fireAllOn sur la base de regles
	 * 
	 * @throws Exception
	 */
	public void fire() throws Exception {
		rb.fireAllOn(eb, this);
	}

}
