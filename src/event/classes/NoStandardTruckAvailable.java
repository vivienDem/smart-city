package event.classes;

import java.io.Serializable;
import event.interfaces.AtomicEventI;

/**
 * Evenement : Aucun camions standards disponible
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class NoStandardTruckAvailable extends AbstractEvent implements AtomicEventI {
	private static final long serialVersionUID = 2047742631902686906L;

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
