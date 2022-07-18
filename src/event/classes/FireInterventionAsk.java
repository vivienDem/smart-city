package event.classes;

import java.io.Serializable;
import java.util.ArrayList;

import event.interfaces.ComplexEventI;
import event.interfaces.EventI;

/**
 * Evenement : Demande d'intervention de feu
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class FireInterventionAsk extends AbstractEvent implements ComplexEventI {

	private static final long serialVersionUID = 1L;
	private ArrayList<EventI> correlated;

	public FireInterventionAsk(EventI e, String type) {
		super();
		correlated = new ArrayList<>();
		correlated.add(e);
		properties.put("type", type);
	}

	/**
	 * @see event.interfaces.ComplexEventI#getCorrelatedEvents()
	 */
	@Override
	public ArrayList<EventI> getCorrelatedEvents() {
		return correlated;
	}

	public void putProperty(String key, String value) {
		properties.put(key, value);
	}

	/**
	 * @see event.interfaces.EventI#hasProperty(java.lang.String)
	 */
	@Override
	public boolean hasProperty(String name) {
		if (properties.containsKey(name))
			return true;

		for (EventI e : correlated) {
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
		if (properties.containsKey(name))
			return properties.get(name);

		for (EventI e : correlated) {
			if (e.hasProperty(name))
				return e.getPropertyValue(name);
		}
		return null;

	}

}
