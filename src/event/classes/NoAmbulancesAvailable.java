package event.classes;

import java.io.Serializable;
import java.util.Map;

import event.interfaces.AtomicEventI;

/**
 * Evenement : Aucune ambulance disponible
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class NoAmbulancesAvailable extends AbstractEvent implements AtomicEventI {
	private static final long serialVersionUID = 1L;

	public NoAmbulancesAvailable(String center) {
		super();
		properties.put("centerId", center);
	}

	public NoAmbulancesAvailable(Map<String, Serializable> properties) {
		super();
		this.properties = properties;
	}

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
