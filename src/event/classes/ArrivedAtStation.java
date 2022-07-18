package event.classes;

import java.io.Serializable;

import event.interfaces.AtomicEventI;

/**
 * Evenement : Vehicule arrive a la station
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class ArrivedAtStation extends AbstractEvent implements AtomicEventI {
	private static final long serialVersionUID = 5230537350843135107L;

	public ArrivedAtStation(String v) {
		properties.put("vehicle", v);
	}

	/**
	 * @see event.interfaces.AtomicEventI#putProperty(java.lang.String,
	 *      java.io.Serializable)
	 */
	@Override
	public Serializable putProperty(String name, Serializable value) {
		return properties.put(name, value);
	}

	/**
	 * @see event.interfaces.AtomicEventI#removeProperty(java.lang.String)
	 */
	@Override
	public void removeProperty(String name) {
		properties.remove(name);

	}

}
