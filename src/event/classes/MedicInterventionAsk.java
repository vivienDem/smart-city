package event.classes;

import java.io.Serializable;
import java.util.ArrayList;
import event.interfaces.ComplexEventI;
import event.interfaces.EventI;

/**
 * Evenement : Demande d'intervention d'un medecin
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class MedicInterventionAsk extends AbstractEvent implements ComplexEventI {
	private static final long serialVersionUID = 8002368681363045580L;
	private ArrayList<EventI> correlatedEvents;

	public MedicInterventionAsk(Serializable event) {
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
