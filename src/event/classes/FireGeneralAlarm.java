package event.classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map.Entry;

import event.interfaces.ComplexEventI;
import event.interfaces.EventI;

/**
 * La classe <code> FireGeneralAlarm </code> instancie un evenement complexe
 * d'alarme generale d'incendie
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class FireGeneralAlarm extends AbstractEvent implements ComplexEventI {
	private static final long serialVersionUID = 1L;
	private ArrayList<EventI> correlated;
	EventI firstAlarm;
	EventI fireAlarm;

	/**
	 * Cree l'evenement complexe
	 * 
	 * @param e1 le premier evenement
	 * @param e2 le second evenement
	 */
	public FireGeneralAlarm(EventI e1, EventI e2) {
		super();
		firstAlarm = e1;
		fireAlarm = e2;
		correlated = new ArrayList<>();
		correlated.add(firstAlarm);
		correlated.add(fireAlarm);

	}

	public Serializable getPosition1() {
		assert firstAlarm.hasProperty("area");
		return firstAlarm.getPropertyValue("area");
	}

	public Serializable getPosition2() {
		assert fireAlarm.hasProperty("area");
		return fireAlarm.getPropertyValue("area");
	}

	public void putProperty(String key, Serializable value) {
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

	@Override
	public Object clone() {
		FireGeneralAlarm res = new FireGeneralAlarm(firstAlarm, fireAlarm);
		for (Entry<String, Serializable> entry : properties.entrySet()) {
			res.putProperty(entry.getKey(), entry.getValue());
		}
		return res;

	}

}
