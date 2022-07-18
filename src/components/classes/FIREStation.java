package components.classes;

import java.io.Serializable;
import java.time.LocalTime;
import components.interfaces.ActionExecutionCI;
import components.interfaces.ActionExecutionImplI;
import components.interfaces.ActionI;
import components.interfaces.ResponseI;
import connections.ActionExecutionOutboundPort;
import connections.FIREproxyActionConnector;
import event.classes.ArrivedAtDestination;
import event.classes.ArrivedAtStation;
import event.classes.EndOfFire;
import event.classes.FireEvent;
import event.classes.HighLaddersAvailable;
import event.classes.NoHighLadderAvailable;
import event.classes.NoStandardTruckAvailable;
import event.classes.StandardTrucksAvailable;
import event.classes.TrafficPriorityRequest;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.cps.smartcity.SmartCityDescriptor;
import fr.sorbonne_u.cps.smartcity.connections.FireStationNotificationInboundPort;
import fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition;
import fr.sorbonne_u.cps.smartcity.grid.IntersectionPosition;
import fr.sorbonne_u.cps.smartcity.interfaces.FireStationNotificationCI;
import fr.sorbonne_u.cps.smartcity.interfaces.FireStationNotificationImplI;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfFire;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfFirefightingResource;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfTrafficLightPriority;
import plugins.EmitterExecutorPlugin;

/**
 * La classe <code>FIREStation</code> implemente un composant caserne de pompier
 * qui est un emetteur d'evenements ainsi qu'un executeur d'actions
 *
 * @author Adan Bougherara et Vivien Demeulenaere
 * 
 */
@OfferedInterfaces(offered = { FireStationNotificationCI.class })
@RequiredInterfaces(required = { ActionExecutionCI.class })
public class FIREStation extends AbstractComponent implements ActionExecutionImplI, FireStationNotificationImplI {
	private final String id;
	private final String pluginURI;
	private ActionExecutionOutboundPort actionExecutionOutboundPort;
	private FireStationNotificationInboundPort notificationInboundPort;
	private String uriBus;
	private String proxyActionInboundPortURI;
	private boolean standardTruckAvailable;
	private boolean highLadderAvailable;
	private EmitterExecutorPlugin plugin;

	protected FIREStation(String id, String uriBus, String proxyActionInboundPortURI, String notificationInboundPortURI)
			throws Exception {
		super(2, 0);
		this.id = id;
		this.pluginURI = id + "-plugin-uri";
		this.uriBus = uriBus;
		this.proxyActionInboundPortURI = proxyActionInboundPortURI;

		this.actionExecutionOutboundPort = new ActionExecutionOutboundPort(this);
		this.actionExecutionOutboundPort.publishPort();

		this.notificationInboundPort = new FireStationNotificationInboundPort(notificationInboundPortURI, this);
		this.notificationInboundPort.publishPort();

		this.getTracer().setTitle(id);
		this.getTracer().setRelativePosition(1, 1);
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
					FIREproxyActionConnector.class.getCanonicalName());
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
		assert a instanceof FIREStationActions;
		ResponseI result = Responses.FAILURE;
		switch ((FIREStationActions) a) {
		case FirstAlarm:
			result = firstFireAlarm(params);
			break;
		case SecondAlarm:
			result = secondFireAlarm(params);
			break;
		case GeneralAlarm:
			result = generalFireAlarm(params);
			break;
		case CheckArea:
			result = checkArea(params);
			break;
		case CheckHighLadder:
			result = checkHighLadderAvailable();
			break;
		case CheckStandardTruck:
			result = checkStandardTruckAvailable();
			break;
		case StandardTruckAvailable:
			result = standardTruckAvailable();
			break;
		case StandardTruckNonAvailable:
			result = standardTruckNonAvailable();
			break;
		case HighLadderAvailable:
			result = highLadderAvailable();
			break;
		case HighLadderNonAvailable:
			result = highLadderNonAvailable();
			break;
		case EndOfFire:
			break;
		default:
			break;
		}
		return result;
	}

	/**
	 * Declenche une premiere alarme de feu
	 * 
	 * @param params les parametres de l'evenement
	 * @return SUCCESS si l'intervention est declenchee, FAILURE sinon
	 * @throws Exception
	 */
	private ResponseI firstFireAlarm(Serializable[] params) throws Exception {
		assert params[0] instanceof AbsolutePosition;
		assert params[1] instanceof TypeOfFirefightingResource;
		if (params[1] == TypeOfFirefightingResource.HighLadderTruck) {
			if (!highLadderAvailable)
				return Responses.FAILURE;
			this.actionExecutionOutboundPort.executeAction(FIREStationActions.FirstAlarm, params);
			return Responses.SUCCESS;
		}
		if (!this.standardTruckAvailable)
			return Responses.FAILURE;
		this.actionExecutionOutboundPort.executeAction(FIREStationActions.FirstAlarm, params);
		return Responses.SUCCESS;

	}

	/**
	 * Declenche une seconde alarme de feu
	 * 
	 * @param params les parametres de l'evenement
	 * @return SUCCESS si l'intervention est declenchee, FAILURE sinon
	 * @throws Exception
	 */
	private ResponseI secondFireAlarm(Serializable[] params) throws Exception {
		assert params[0] instanceof AbsolutePosition;
		if (!this.standardTruckAvailable)
			return Responses.FAILURE;
		this.actionExecutionOutboundPort.executeAction(FIREStationActions.SecondAlarm, params);
		return Responses.SUCCESS;

	}

	/**
	 * Declenche une alarme generale de feu
	 * 
	 * @param params les parametres de l'evenement
	 * @return SUCCESS si l'intervention est declenchee, FAILURE sinon
	 * @throws Exception
	 */
	private ResponseI generalFireAlarm(Serializable[] params) throws Exception {
		assert params[0] instanceof AbsolutePosition;
		if (!this.highLadderAvailable)
			return Responses.FAILURE;
		this.actionExecutionOutboundPort.executeAction(FIREStationActions.GeneralAlarm, params);
		return Responses.SUCCESS;

	}

	/**
	 * Verifie si la station dispose de camions standards
	 * 
	 * @return SUCCESS si la station a un camion standard, FAILURE sinon
	 */
	private ResponseI checkStandardTruckAvailable() {
		if (standardTruckAvailable)
			return Responses.SUCCESS;
		return Responses.FAILURE;
	}

	/**
	 * Verifie si la station dispose de camions grande echelle
	 * 
	 * @return SUCCESS si la station a un camion grande echelle, FAILURE sinon
	 */
	private ResponseI checkHighLadderAvailable() {
		if (highLadderAvailable)
			return Responses.SUCCESS;
		return Responses.FAILURE;
	}

	/**
	 * Verifie si l'evenement est dans la zone d'action du centre
	 * 
	 * @param params les parametres de l'evenement
	 * @return SUCCESS si l'evenement est dans la zone d'action, FAILURE sinon
	 */
	private ResponseI checkArea(Serializable[] params) {
		assert params.length == 1;
		assert params[0] instanceof AbsolutePosition;
		AbsolutePosition p = (AbsolutePosition) params[0];
		if (SmartCityDescriptor.dependsUpon(p, id)) {
			return Responses.SUCCESS;
		}
		return Responses.FAILURE;
	}

	/**
	 * Camions grande echelle disponibles
	 * 
	 * @return SUCCESS
	 */
	private ResponseI highLadderAvailable() {
		highLadderAvailable = true;
		this.traceMessage("Every high ladders are available\n");
		return Responses.SUCCESS;
	}

	/**
	 * Camions standards disponibles
	 * 
	 * @return SUCCESS
	 */
	private ResponseI standardTruckAvailable() {
		standardTruckAvailable = true;
		this.traceMessage("Every standard trucks are available\n");
		return Responses.SUCCESS;
	}

	/**
	 * Camions grande echelle non disponibles
	 * 
	 * @return SUCCESS
	 */
	private ResponseI highLadderNonAvailable() {
		highLadderAvailable = false;
		this.traceMessage("Every high ladders are busy\n");
		return Responses.SUCCESS;
	}

	/**
	 * Camions standards non disponibles
	 * 
	 * @return SUCCESS
	 */
	private ResponseI standardTruckNonAvailable() {
		standardTruckAvailable = false;
		this.traceMessage("Every standard trucks are busy\n");
		return Responses.SUCCESS;
	}

	/*---------------------------------------------------------------------------------------*/
	/*---------------------------Partie Emetteur---------------------------------------------*/

	/**
	 * @see fr.sorbonne_u.cps.smartcity.interfaces.FireStationNotificationImplI#fireAlarm(fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition,
	 *      java.time.LocalTime, fr.sorbonne_u.cps.smartcity.interfaces.TypeOfFire)
	 */
	@Override
	public void fireAlarm(AbsolutePosition position, LocalTime occurrence, TypeOfFire type) throws Exception {
		String t = this.typeOfFireToString(type);
		assert t != null;
		FireEvent fe = new FireEvent(t, position);
		fe.putProperty("centerId", id);
		fe.setTimeStamp(occurrence);
		plugin.getEmissionPort().sendEvent(id, fe);
		this.traceMessage(
				"Fire alarm of type " + type + " received from position " + position + " at " + occurrence + "\n");

	}

	/**
	 * @see fr.sorbonne_u.cps.smartcity.interfaces.FireStationNotificationImplI#endOfFire(fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition,
	 *      java.time.LocalTime)
	 */
	@Override
	public void endOfFire(AbsolutePosition position, LocalTime occurrence) throws Exception {
		EndOfFire ef = new EndOfFire(position);
		ef.setTimeStamp(occurrence);
		ef.putProperty("centerId", id);
		plugin.getEmissionPort().sendEvent(id, ef);
		this.traceMessage("End of fire received from position " + position + " at " + occurrence + "\n");

	}

	/**
	 * @see fr.sorbonne_u.cps.smartcity.interfaces.FireStationNotificationImplI#requestPriority(fr.sorbonne_u.cps.smartcity.grid.IntersectionPosition,
	 *      fr.sorbonne_u.cps.smartcity.interfaces.TypeOfTrafficLightPriority,
	 *      java.lang.String, fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition,
	 *      java.time.LocalTime)
	 */
	@Override
	public void requestPriority(IntersectionPosition position, TypeOfTrafficLightPriority priority, String vehicleId,
			AbsolutePosition destination, LocalTime occurrence) throws Exception {
		TrafficPriorityRequest tpr = new TrafficPriorityRequest(position, priority, vehicleId, destination);
		tpr.setTimeStamp(occurrence);
		tpr.putProperty("centerId", id);
		plugin.getEmissionPort().sendEvent(id, tpr);
		this.traceMessage("priority " + priority + " requested for vehicle " + vehicleId + " at intersection "
				+ position + " towards " + destination + " at " + occurrence + "\n");

	}

	/**
	 * @see fr.sorbonne_u.cps.smartcity.interfaces.FireStationNotificationImplI#atDestination(java.lang.String,
	 *      java.time.LocalTime)
	 */
	@Override
	public void atDestination(String vehicleId, LocalTime occurrence) throws Exception {
		ArrivedAtDestination ad = new ArrivedAtDestination(vehicleId);
		ad.setTimeStamp(occurrence);
		ad.putProperty("centerId", id);
		plugin.getEmissionPort().sendEvent(id, ad);
		this.traceMessage("Vehicle " + vehicleId + " has arrived at destination\n");

	}

	/**
	 * @see fr.sorbonne_u.cps.smartcity.interfaces.FireStationNotificationImplI#atStation(java.lang.String,
	 *      java.time.LocalTime)
	 */
	@Override
	public void atStation(String vehicleId, LocalTime occurrence) throws Exception {
		ArrivedAtStation as = new ArrivedAtStation(vehicleId);
		as.setTimeStamp(occurrence);
		as.putProperty("centerId", id);
		plugin.getEmissionPort().sendEvent(id, as);
		this.traceMessage("Vehicle " + vehicleId + " has arrived at station\n");

	}

	/**
	 * @see fr.sorbonne_u.cps.smartcity.interfaces.FireStationNotificationImplI#notifyNoStandardTruckAvailable(java.time.LocalTime)
	 */
	@Override
	public void notifyNoStandardTruckAvailable(LocalTime occurrence) throws Exception {
		NoStandardTruckAvailable nsta = new NoStandardTruckAvailable();
		nsta.setTimeStamp(occurrence);
		nsta.putProperty("centerId", id);
		plugin.getEmissionPort().sendEvent(id, nsta);
		this.traceMessage("No standard truck available received at " + occurrence + "\n");

	}

	/**
	 * @see fr.sorbonne_u.cps.smartcity.interfaces.FireStationNotificationImplI#notifyStandardTrucksAvailable(java.time.LocalTime)
	 */
	@Override
	public void notifyStandardTrucksAvailable(LocalTime occurrence) throws Exception {
		StandardTrucksAvailable sta = new StandardTrucksAvailable();
		sta.setTimeStamp(occurrence);
		sta.putProperty("centerId", id);
		plugin.getEmissionPort().sendEvent(id, sta);
		this.traceMessage("Standard trucks available received at " + occurrence + "\n");

	}

	/**
	 * @see fr.sorbonne_u.cps.smartcity.interfaces.FireStationNotificationImplI#notifyNoHighLadderTruckAvailable(java.time.LocalTime)
	 */
	@Override
	public void notifyNoHighLadderTruckAvailable(LocalTime occurrence) throws Exception {
		NoHighLadderAvailable nhla = new NoHighLadderAvailable();
		nhla.setTimeStamp(occurrence);
		nhla.putProperty("centerId", id);
		plugin.getEmissionPort().sendEvent(id, nhla);
		this.traceMessage("No high ladder truck available received at " + occurrence + "\n");

	}

	/**
	 * @see fr.sorbonne_u.cps.smartcity.interfaces.FireStationNotificationImplI#notifyHighLadderTrucksAvailable(java.time.LocalTime)
	 */
	@Override
	public void notifyHighLadderTrucksAvailable(LocalTime occurrence) throws Exception {
		HighLaddersAvailable hla = new HighLaddersAvailable();
		hla.setTimeStamp(occurrence);
		hla.putProperty("centerId", id);
		plugin.getEmissionPort().sendEvent(id, hla);
		this.traceMessage("High ladder trucks available received at " + occurrence + "\n");

	}

	/**
	 * Traduit un type de feu en chaine de caractere
	 * 
	 * @param ha le type d'alarme
	 * @return
	 */
	private String typeOfFireToString(TypeOfFire f) {
		switch (f) {
		case Building:
			return "building";
		case House:
			return "house";
		default:
			return null;
		}
	}
}
