package components.classes;

import java.io.Serializable;
import java.time.LocalTime;
import components.interfaces.ActionExecutionCI;
import components.interfaces.ActionExecutionImplI;
import components.interfaces.ActionI;
import components.interfaces.ResponseI;
import connections.ActionExecutionOutboundPort;
import connections.TRAFFICproxyActionConnector;
import event.classes.VehicleCrossing;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.cps.smartcity.connections.TrafficLightNotificationInboundPort;
import fr.sorbonne_u.cps.smartcity.grid.Direction;
import fr.sorbonne_u.cps.smartcity.interfaces.TrafficLightNotificationImplI;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfTrafficLightPriority;
import plugins.EmitterExecutorPlugin;
import fr.sorbonne_u.cps.smartcity.interfaces.TrafficLightNotificationCI;

/**
 * La classe <code>TRAFFICLights</code> implemente un composant feu de
 * circulation qui est un emetteur d'evenements ainsi qu'un executeur d'actions
 *
 * @author Adan Bougherara et Vivien Demeulenaere
 * 
 */
@OfferedInterfaces(offered = { TrafficLightNotificationCI.class })
@RequiredInterfaces(required = { ActionExecutionCI.class })
public class TRAFFICLights extends AbstractComponent implements TrafficLightNotificationImplI, ActionExecutionImplI {
	private final String id;
	private TrafficLightNotificationInboundPort notificationInboundPort;
	private ActionExecutionOutboundPort actionExecutionOutboundPort;
	private String uriBus;
	private String proxyActionInboundPortURI;
	private final String pluginURI;
	private EmitterExecutorPlugin plugin;

	protected TRAFFICLights(String id, String uriBus, String proxyActionInboundPortURI,
			String notificationInboundPortURI) throws Exception {
		super(2, 0);
		this.id = id;
		this.pluginURI = id + "-plugin-uri";
		this.uriBus = uriBus;
		this.proxyActionInboundPortURI = proxyActionInboundPortURI;

		this.actionExecutionOutboundPort = new ActionExecutionOutboundPort(this);
		this.actionExecutionOutboundPort.publishPort();
		this.notificationInboundPort = new TrafficLightNotificationInboundPort(notificationInboundPortURI, this);
		this.notificationInboundPort.publishPort();

		this.getTracer().setTitle(id);
		this.getTracer().setRelativePosition(1, 2);
		this.toggleTracing();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#start()
	 */
	@Override
	public synchronized void start() throws ComponentStartException {
		super.start();
		try {
			this.doPortConnection(this.actionExecutionOutboundPort.getPortURI(), this.proxyActionInboundPortURI,
					TRAFFICproxyActionConnector.class.getCanonicalName());
		} catch (Exception e) {
			throw new ComponentStartException(e);
		}
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#execute()
	 */
	@Override
	public synchronized void execute() throws Exception {
		super.execute();
		plugin = new EmitterExecutorPlugin(uriBus, id);
		plugin.setPluginURI(pluginURI);
		this.installPlugin(plugin);

	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 */
	@Override
	public synchronized void finalise() throws Exception {
		this.doPortDisconnection(this.actionExecutionOutboundPort.getPortURI());
		super.finalise();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			this.actionExecutionOutboundPort.unpublishPort();
			this.notificationInboundPort.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}

	/*---------------------------------------------------------------------------------------*/
	/*---------------------------Partie Executeur---------------------------------------------*/

	/**
	 * @see components.interfaces.ActionExecutionImplI#executeAction(components.interfaces.ActionI,
	 *      java.io.Serializable[])
	 */
	@Override
	public ResponseI executeAction(ActionI a, Serializable[] params) throws Exception {
		assert a instanceof TRAFFICLightsActions;
		ResponseI result = Responses.FAILURE;
		switch ((TRAFFICLightsActions) a) {
		case RequestPriority:
			result = requestPriority(params);
			break;
		case ReturnToNormal:
			result = returnToNormal(params);
		default:
			break;
		}
		return result;
	}

	/**
	 * Change la priorite du feu a normal
	 * 
	 * @param params les parametres
	 * @return
	 * @throws Exception
	 */
	private ResponseI returnToNormal(Serializable[] params) throws Exception {
		return actionExecutionOutboundPort.executeAction(TRAFFICLightsActions.ReturnToNormal, params);
	}

	/**
	 * 
	 * Change la priorite du feu selon la priorite demandee
	 * 
	 * @param params les parametres
	 * @return
	 * @throws Exception
	 */
	private ResponseI requestPriority(Serializable[] params) throws Exception {
		assert params[0] instanceof TypeOfTrafficLightPriority;
		return actionExecutionOutboundPort.executeAction(TRAFFICLightsActions.RequestPriority, params);
	}

	/*---------------------------------------------------------------------------------------*/
	/*---------------------------Partie Emetteur---------------------------------------------*/

	/**
	 * @see fr.sorbonne_u.cps.smartcity.interfaces.TrafficLightNotificationImplI#vehiclePassage(java.lang.String,
	 *      fr.sorbonne_u.cps.smartcity.grid.Direction, java.time.LocalTime)
	 */
	@Override
	public void vehiclePassage(String vehicleId, Direction d, LocalTime occurrence) throws Exception {
		VehicleCrossing vc = new VehicleCrossing(vehicleId, d);
		vc.setTimeStamp(occurrence);
		plugin.getEmissionPort().sendEvent(id, vc);
		this.traceMessage("Traffic light at " + id + " receives the notification of the passage of " + vehicleId
				+ (d != null ? " in the direction of " + d : "") + " at " + occurrence + "\n");
	}

}
