package event.classes;

import java.io.Serializable;
import java.util.ArrayList;

import event.interfaces.ComplexEventI;
import event.interfaces.EventI;

/**
 * Evenement : Chute consciente
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class ConsciousFall extends AbstractEvent implements ComplexEventI {
	private static final long serialVersionUID = 1L;
	private ArrayList<EventI> correlated;

	/**
	 * Cree un evenement complexe de chute complexe
	 * 
	 * @param c1 le premier evenement
	 * @param c2 le deuxieme evenement
	 */
	public ConsciousFall(EventI c1, EventI c2) {
		super();
		correlated = new ArrayList<EventI>();
		correlated.add(c1);
		correlated.add(c2);
	}

	public void putProperty(String key, String value) {
		properties.put(key, value);
	}

	/**
	 * @see event.interfaces.ComplexEventI#getCorrelatedEvents()
	 */
	@Override
	public ArrayList<EventI> getCorrelatedEvents() {
		return correlated;
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
