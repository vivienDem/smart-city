package event.classes;

import java.io.Serializable;

import event.interfaces.AtomicEventI;

/**
 * Evenement : Camions echelles hautes disponibles
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class HighLaddersAvailable extends AbstractEvent implements AtomicEventI {
	private static final long serialVersionUID = 984416399512398269L;

	/**
	 * @see event.interfaces.AtomicEventI#putProperty(java.lang.String,
	 *      java.io.Serializable)
	 */
	@Override
	public Serializable putProperty(String name, Serializable value) {
		properties.put(name, value);
		return value;
	}

	/**
	 * @see event.interfaces.AtomicEventI#removeProperty(java.lang.String)
	 */
	@Override
	public void removeProperty(String name) {
		properties.remove(name);

	}

}
