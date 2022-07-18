package components.classes;

import java.io.Serializable;
import java.time.LocalTime;

import components.interfaces.ActionExecutionCI;
import components.interfaces.ActionExecutionImplI;
import components.interfaces.ActionI;
import components.interfaces.ResponseI;
import connections.ActionExecutionOutboundPort;
import connections.SAMUproxyActionConnector;
import event.classes.AmbulancesAvailable;
import event.classes.HealthEvent;
import event.classes.MedicsAvailable;
import event.classes.NoAmbulancesAvailable;
import event.classes.NoMedicsAvailable;
import event.classes.SignalOK;
import event.classes.TrafficPriorityRequest;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.cps.smartcity.SmartCityDescriptor;
import fr.sorbonne_u.cps.smartcity.connections.SAMUNotificationInboundPort;
import fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition;
import fr.sorbonne_u.cps.smartcity.grid.IntersectionPosition;
import fr.sorbonne_u.cps.smartcity.interfaces.SAMUNotificationImplI;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfHealthAlarm;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfTrafficLightPriority;
import plugins.EmitterExecutorPlugin;
import fr.sorbonne_u.cps.smartcity.interfaces.SAMUNotificationCI;

/**
 * La classe <code>SAMUCenter</code> implemente un composant centre de SAMU qui
 * est un emetteur d'evenements ainsi qu'un executeur d'actions
 *
 * @author Adan Bougherara et Vivien Demeulenaere
 * 
 */
@OfferedInterfaces(offered = { SAMUNotificationCI.class })
@RequiredInterfaces(required = { ActionExecutionCI.class })
public class SAMUCenter extends AbstractComponent implements SAMUNotificationImplI, ActionExecutionImplI {
	private SAMUNotificationInboundPort notificationInboundPort;
	private ActionExecutionOutboundPort actionExecutionOutboundPort;
	private String uriBus;
	private String proxyActionInboundPortURI;
	private final String pluginURI;
	private boolean ambulanceAvailable;
	private boolean medicAvailable;
	private EmitterExecutorPlugin plugin;
	private final String id;

	protected SAMUCenter(String id, String uriBus, String proxyActionInboundPortURI, String notificationInboundPortURI)
			throws Exception {
		super(2, 0);
		this.id = id;
		this.pluginURI = id + "-plugin-uri";
		this.uriBus = uriBus;
		this.proxyActionInboundPortURI = proxyActionInboundPortURI;

		this.actionExecutionOutboundPort = new ActionExecutionOutboundPort(this);
		this.actionExecutionOutboundPort.publishPort();

		this.notificationInboundPort = new SAMUNotificationInboundPort(notificationInboundPortURI, this);
		this.notificationInboundPort.publishPort();

		this.getTracer().setTitle(id);
		this.getTracer().setRelativePosition(1, 0);
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
					SAMUproxyActionConnector.class.getCanonicalName());

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
	/*---------------------------Partie Executeur--------------------------------------------*/

	/**
	 * @see components.interfaces.ActionExecutionImplI#executeAction(components.interfaces.ActionI,
	 *      java.io.Serializable[])
	 */
	@Override
	public ResponseI executeAction(ActionI a, Serializable[] params) throws Exception {
		assert a instanceof SAMUCenterActions;
		ResponseI result = Responses.FAILURE;
		switch ((SAMUCenterActions) a) {
		case SendMedic:
			result = sendMedic(params);
			break;
		case SendAmbulance:
			result = sendAmbulance(params);
			break;
		case CheckArea:
			result = checkArea(params);
			break;
		case CheckAmbulanceAvailable:
			result = checkAmbulanceAvailable();
			break;
		case CheckMedicAvailable:
			result = checkMedicAvailable();
			break;
		case MedicCall:
			result = medicCall(params);
			break;
		case MedicsNonAvailable:
			result = medicsNonAvailable();
			break;
		case AmbulancesNonAvailable:
			result = ambulancesNonAvailable();
			break;
		case AmbulancesAvailable:
			result = ambulancesAvailable();
			break;
		case MedicsAvailable:
			result = medicsAvailable();
		}
		return result;
	}

	/**
	 * Declenche une intervention de medecin
	 * 
	 * @param params les parametres de l'evenement
	 * @return SUCCESS si un medecin est disponible, FAILURE sinon
	 * @throws Exception
	 */
	private ResponseI sendMedic(Serializable[] params) throws Exception {
		assert params.length == 1;
		assert params[0] instanceof AbsolutePosition;
		if (!medicAvailable)
			return Responses.FAILURE;

		this.actionExecutionOutboundPort.executeAction(SAMUCenterActions.SendMedic, params);
		return Responses.SUCCESS;
	}

	/**
	 * Envoi une ambulance
	 * 
	 * @param params les parametres de l'evenement
	 * @return SUCCESS si le centre dispose d'au moins une ambulance disponible,
	 *         FAILURE sinon
	 * @throws Exception
	 */
	private ResponseI sendAmbulance(Serializable[] params) throws Exception {
		assert params.length == 1;
		assert params[0] instanceof AbsolutePosition;
		if (!ambulanceAvailable)
			return Responses.FAILURE;
		this.actionExecutionOutboundPort.executeAction(SAMUCenterActions.SendAmbulance, params);
		return Responses.SUCCESS;
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
	 * Verifie si le centre dispose d'ambulance disponible
	 * 
	 * @return SUCCESS si le centre possede au moins une ambulance disponible,
	 *         FAILURE sinon
	 */
	private ResponseI checkAmbulanceAvailable() {
		if (ambulanceAvailable)
			return Responses.SUCCESS;
		return Responses.FAILURE;
	}

	/**
	 * Verifie si le centre dispose de medecin disponible
	 * 
	 * @return SUCCESS si le centre possede au moins un medecin disponible, FAILURE
	 *         sinon
	 */
	private ResponseI checkMedicAvailable() {
		if (medicAvailable)
			return Responses.SUCCESS;
		return Responses.FAILURE;
	}

	/**
	 * Appel d'un medecin
	 * 
	 * @param params les parametres de l'evenement
	 * @return SUCCESS si un medecin est disponible, FAILURE sinon
	 * @throws Exception
	 */
	private ResponseI medicCall(Serializable[] params) throws Exception {
		assert params.length == 2;
		assert params[0] instanceof AbsolutePosition;
		assert params[1] instanceof String;
		if (!medicAvailable)
			return Responses.FAILURE;
		this.actionExecutionOutboundPort.executeAction(SAMUCenterActions.MedicCall, params);
		return Responses.SUCCESS;
	}

	/**
	 * Medecins non disponibles
	 * 
	 * @return SUCCESS
	 */
	private ResponseI medicsNonAvailable() {
		medicAvailable = false;
		this.traceMessage("Every medics are busy\n");
		return Responses.SUCCESS;
	}

	/**
	 * Ambulances non disponibles
	 * 
	 * @return SUCCESS
	 */
	private ResponseI ambulancesNonAvailable() {
		ambulanceAvailable = false;
		this.traceMessage("Every ambulances are in intervention\n");
		return Responses.SUCCESS;
	}

	/**
	 * Medecins disponibles
	 * 
	 * @return SUCCESS
	 */
	private ResponseI medicsAvailable() {
		medicAvailable = true;
		this.traceMessage("Every medics are available\n");
		return Responses.SUCCESS;
	}

	/**
	 * Ambulances disponibles
	 * 
	 * @return SUCCESS
	 */
	private ResponseI ambulancesAvailable() {
		ambulanceAvailable = true;
		this.traceMessage("Every ambuances are available\n");
		return Responses.SUCCESS;
	}

	/*---------------------------------------------------------------------------------------*/
	/*---------------------------Partie Emetteur---------------------------------------------*/

	/**
	 * @see fr.sorbonne_u.cps.smartcity.interfaces.SAMUNotificationImplI#healthAlarm(fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition,
	 *      fr.sorbonne_u.cps.smartcity.interfaces.TypeOfHealthAlarm,
	 *      java.time.LocalTime)
	 */
	@Override
	public void healthAlarm(AbsolutePosition position, TypeOfHealthAlarm type, LocalTime occurrence) throws Exception {
		String t = typeOfHealthAlarmToString(type);
		assert t != null;
		HealthEvent e = new HealthEvent(t, position);
		e.putProperty("centerId", id);
		e.setTimeStamp(occurrence);
		plugin.getEmissionPort().sendEvent(id, e);

		this.traceMessage("Health notification of type " + type + " at position " + position + " received at "
				+ occurrence + "\n");
	}

	/**
	 * @see fr.sorbonne_u.cps.smartcity.interfaces.SAMUNotificationImplI#trackingAlarm(fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition,
	 *      java.lang.String, java.time.LocalTime)
	 */
	@Override
	public void trackingAlarm(AbsolutePosition position, String personId, LocalTime occurrence) throws Exception {
		HealthEvent e = new HealthEvent("tracking", position);
		e.putProperty("personId", personId);
		e.putProperty("centerId", id);
		e.setTimeStamp(occurrence);
		plugin.getEmissionPort().sendEvent(id, e);

		this.traceMessage("Health notification of type tracking for " + personId + " at position " + position
				+ " received at " + occurrence + "\n");

	}

	/**
	 * @see fr.sorbonne_u.cps.smartcity.interfaces.SAMUNotificationImplI#manualSignal(java.lang.String,
	 *      java.time.LocalTime)
	 */
	@Override
	public void manualSignal(String personId, LocalTime occurrence) throws Exception {
		SignalOK s = new SignalOK(personId);
		s.setTimeStamp(occurrence);
		s.putProperty("centerId", id);
		plugin.getEmissionPort().sendEvent(id, s);

		this.traceMessage("Manual signal emitted by " + personId + " received at " + occurrence + "\n");

	}

	/**
	 * @see fr.sorbonne_u.cps.smartcity.interfaces.SAMUNotificationImplI#notifyMedicsAvailable(java.time.LocalTime)
	 */
	@Override
	public void notifyMedicsAvailable(LocalTime occurrence) throws Exception {
		MedicsAvailable ma = new MedicsAvailable(id);
		ma.setTimeStamp(occurrence);
		ma.putProperty("centerId", id);
		plugin.getEmissionPort().sendEvent(id, ma);

		this.traceMessage("Notification that medics are available received at " + occurrence + "\n");
	}

	/**
	 * @see fr.sorbonne_u.cps.smartcity.interfaces.SAMUNotificationImplI#notifyNoMedicAvailable(java.time.LocalTime)
	 */
	@Override
	public void notifyNoMedicAvailable(LocalTime occurrence) throws Exception {
		NoMedicsAvailable ma = new NoMedicsAvailable(id);
		ma.setTimeStamp(occurrence);
		ma.putProperty("centerId", id);
		plugin.getEmissionPort().sendEvent(id, ma);

		this.traceMessage("Notification that no medic are available received at " + occurrence + "\n");

	}

	/**
	 * @see fr.sorbonne_u.cps.smartcity.interfaces.SAMUNotificationImplI#notifyAmbulancesAvailable(java.time.LocalTime)
	 */
	@Override
	public void notifyAmbulancesAvailable(LocalTime occurrence) throws Exception {
		AmbulancesAvailable aa = new AmbulancesAvailable(id);
		aa.setTimeStamp(occurrence);
		aa.putProperty("centerId", id);
		plugin.getEmissionPort().sendEvent(id, aa);

		this.traceMessage("Notification that ambulances are available received at " + occurrence + "\n");

	}

	/**
	 * @see fr.sorbonne_u.cps.smartcity.interfaces.SAMUNotificationImplI#notifyNoAmbulanceAvailable(java.time.LocalTime)
	 */
	@Override
	public void notifyNoAmbulanceAvailable(LocalTime occurrence) throws Exception {
		NoAmbulancesAvailable aa = new NoAmbulancesAvailable(id);
		aa.setTimeStamp(occurrence);
		aa.putProperty("centerId", id);
		plugin.getEmissionPort().sendEvent(id, aa);

		this.traceMessage("Notification that no ambulance are available received at " + occurrence + "\n");

	}

	/**
	 * @see fr.sorbonne_u.cps.smartcity.interfaces.SAMUNotificationImplI#requestPriority(fr.sorbonne_u.cps.smartcity.grid.IntersectionPosition,
	 *      fr.sorbonne_u.cps.smartcity.interfaces.TypeOfTrafficLightPriority,
	 *      java.lang.String, fr.sorbonne_u.cps.smartcity.grid.IntersectionPosition,
	 *      java.time.LocalTime)
	 */
	@Override
	public void requestPriority(IntersectionPosition intersection, TypeOfTrafficLightPriority priority,
			String vehicleId, AbsolutePosition destination, LocalTime occurrence) throws Exception {
		TrafficPriorityRequest r = new TrafficPriorityRequest(intersection, priority, vehicleId, destination);
		r.setTimeStamp(occurrence);
		r.putProperty("centerId", id);
		plugin.getEmissionPort().sendEvent(id, r);
		this.traceMessage("priority " + priority + " requested for vehicle " + vehicleId + " at intersection "
				+ intersection + " towards " + destination + " at " + occurrence + "\n");

	}

	/**
	 * @see fr.sorbonne_u.cps.smartcity.interfaces.SAMUNotificationImplI#atDestination(java.lang.String,
	 *      java.time.LocalTime)
	 */
	@Override
	public void atDestination(String vehicleId, LocalTime occurrence) throws Exception {
		this.traceMessage("Vehicle " + vehicleId + " has arrived at destination at " + occurrence + "\n");

	}

	/**
	 * @see fr.sorbonne_u.cps.smartcity.interfaces.SAMUNotificationImplI#atStation(java.lang.String,
	 *      java.time.LocalTime)
	 */
	@Override
	public void atStation(String vehicleId, LocalTime occurrence) throws Exception {
		this.traceMessage("Vehicle " + vehicleId + " has arrived at station at " + occurrence + "\n");

	}

	/**
	 * Traduit un type d'alarme de sante en chaine de caractere
	 * 
	 * @param ha le type d'alarme
	 * @return
	 */
	private String typeOfHealthAlarmToString(TypeOfHealthAlarm ha) {
		switch (ha) {
		case EMERGENCY:
			return "emergency";
		case TRACKING:
			return "tracking";
		case MEDICAL:
			return "medical";
		}
		return null;
	}

}
