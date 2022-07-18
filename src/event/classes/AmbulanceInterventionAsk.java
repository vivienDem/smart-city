package event.classes;

import java.io.Serializable;
import java.util.ArrayList;

import event.interfaces.ComplexEventI;
import event.interfaces.EventI;

/**
 * La classe <code> AmbulanceInterventionAsk </code> permet de definir un
 * evenement demande d'intervention d'ambulance
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class AmbulanceInterventionAsk extends AbstractEvent implements ComplexEventI {
	private static final long serialVersionUID = 6711043169801734378L;
	private ArrayList<EventI> correlatedEvents;

	public AmbulanceInterventionAsk(Serializable event) {
		super();
		assert event instanceof EventI;
		this.correlatedEvents = new ArrayList<EventI>();
		this.correlatedEvents.add((EventI) event);
	}

	/**
	 * @see event.interfaces.ComplexEventI#getCorrelatedEvents()
	 */
	@Override
	public ArrayList<EventI> getCorrelatedEvents() {
		return this.correlatedEvents;
	}

	/**
	 * Permet d'ajouter/modifier le centre associe a l'evenement
	 * 
	 * @param centerId
	 */
	public void setCenterId(Serializable centerId) {
		HealthEvent he = (HealthEvent) this.getCorrelatedEvents().get(0);
		he.putProperty("centerId", centerId);
	}

	/**
	 * @see event.interfaces.EventI#hasProperty(java.lang.String)
	 */
	@Override
	public boolean hasProperty(String name) {
		for (EventI e : correlatedEvents) {
			if (e.hasProperty(name))
				return true;
		}
		return false;

	}

	/**
	 * @see event.interfaces.EventI#getPropertyValue(java.lang.String)
	 */
	@Override
	public Serializable getPropertyValue(String name) {
		for (EventI e : correlatedEvents) {
			if (e.hasProperty(name))
				return e.getPropertyValue(name);
		}
		return null;
	}

}
