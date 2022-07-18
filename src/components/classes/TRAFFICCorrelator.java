package components.classes;

import java.io.Serializable;
import java.util.List;

import components.interfaces.CEPBusManagementCI;
import components.interfaces.EventReceptionImplI;
import components.interfaces.TRAFFICCorrelatorI;
import connections.BusManagementConnector;
import connections.CorrelatorActionOutboundPort;
import connections.CorrelatorBusManagementOutboundPort;
import event.classes.EventBase;
import event.classes.TrafficPriorityRequest;
import event.interfaces.EventI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.cps.smartcity.SmartCityDescriptor;
import fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition;
import fr.sorbonne_u.cps.smartcity.grid.Direction;
import fr.sorbonne_u.cps.smartcity.grid.IntersectionPosition;
import plugins.CorrelatorPlugin;
import rule.classes.RuleBase;

/**
 * La classe <code> TRAFFICCorrelator </code> implemente un correlateur relatif
 * au trafic.
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
@RequiredInterfaces(required = { CEPBusManagementCI.class })
public class TRAFFICCorrelator extends AbstractComponent implements TRAFFICCorrelatorI, EventReceptionImplI {
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
	 * Cree un correlateur de trafic
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
	protected TRAFFICCorrelator(String id, RuleBase rb, String busManagementURI, List<String> executorsURI,
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
		this.getTracer().setRelativePosition(3, 2);
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
	 * @see components.interfaces.TRAFFICCorrelatorI#changePriorityOfIntersection(java.io.Serializable,
	 *      java.io.Serializable)
	 */
	@Override
	public void changePriorityOfIntersection(Serializable position, Serializable priority) throws Exception {
		IntersectionPosition p = (IntersectionPosition) position;
		CorrelatorActionOutboundPort correlatorActionOutboundPort = plugin.getCorrelatorActionOutboundPorts()
				.get(executorsURI.indexOf(p.toString()));
		correlatorActionOutboundPort.executeAction(TRAFFICLightsActions.RequestPriority,
				new Serializable[] { priority });

	}

	/**
	 * @see components.interfaces.TRAFFICCorrelatorI#nextIntersection(fr.sorbonne_u.cps.smartcity.grid.IntersectionPosition)
	 */
	private IntersectionPosition nextIntersection(IntersectionPosition position, Direction direction) {
		double x = position.getX();
		double y = position.getY();
		boolean valid = false;
		IntersectionPosition next = null;
		switch (direction) {
		case N:
			next = new IntersectionPosition(x - 1, y);
			break;
		case S:
			next = new IntersectionPosition(x + 1, y);
			break;
		case E:
			next = new IntersectionPosition(x, y + 1);
			break;
		case W:
			next = new IntersectionPosition(x, y - 1);
			break;
		}
		valid = SmartCityDescriptor.isValidTrafficLight(next);
		if (valid)
			return next;
		return null;
	}

	/**
	 * @see components.interfaces.TRAFFICCorrelatorI#isBefore(java.io.Serializable,
	 *      java.io.Serializable, java.io.Serializable)
	 */
	@Override
	public IntersectionPosition isBefore(Serializable position, Serializable direction, Serializable finalDestination) {
		IntersectionPosition pos = (IntersectionPosition) position;
		AbsolutePosition finalPos = (AbsolutePosition) finalDestination;
		Direction d = (Direction) direction;

		IntersectionPosition next = nextIntersection(pos, d);
		if (next == null)
			return null;
		switch (d) {
		case N:
			if (next.getX() > finalPos.getX())
				return next;
			break;
		case S:
			if (next.getX() < finalPos.getX())
				return next;
			break;
		case W:
			if (next.getY() > finalPos.getY())
				return next;
			break;
		case E:
			if (next.getY() < finalPos.getY())
				return next;
			break;
		}
		return null;
	}

	/**
	 * @see components.interfaces.TRAFFICCorrelatorI#switchIntersectionPriorityToNormal(java.io.Serializable)
	 */
	@Override
	public void switchIntersectionPriorityToNormal(Serializable position) throws Exception {
		IntersectionPosition p = (IntersectionPosition) position;
		CorrelatorActionOutboundPort correlatorActionOutboundPort = plugin.getCorrelatorActionOutboundPorts()
				.get(executorsURI.indexOf(p.toString()));
		correlatorActionOutboundPort.executeAction(TRAFFICLightsActions.ReturnToNormal, new Serializable[0]);

	}

	/**
	 * @see components.interfaces.TRAFFICCorrelatorI#redirectTrafficPriorityRequest(java.io.Serializable,
	 *      java.io.Serializable, java.io.Serializable, java.io.Serializable, java.io.Serializable)
	 */
	@Override
	public void redirectTrafficPriorityRequest(Serializable position, Serializable direction, Serializable vehicleId,
			Serializable priority, Serializable finalDestination) throws Exception {
		TrafficPriorityRequest r = new TrafficPriorityRequest(position, priority, vehicleId, finalDestination);
		r.putProperty("direction", direction);
		eb.addEvent(r);
	}

}
