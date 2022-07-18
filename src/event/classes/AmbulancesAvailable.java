package event.classes;

import java.io.Serializable;
import java.util.Map;

import event.interfaces.AtomicEventI;

/**
 * La classe <code>AmbulancesAvailable</code> instancie l'venement : Ambulances
 * disponibles
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class AmbulancesAvailable extends AbstractEvent implements AtomicEventI {
	private static final long serialVersionUID = 1L;

	/**
	 * Cree l'evenement indiquant que les ambulances sont disponibles
	 * 
	 * @param center le centre de SAMU concerne
	 */
	public AmbulancesAvailable(String center) {
		super();
		properties.put("centerId", center);
	}

	/**
	 * Cree l'evenement indiquant que les ambulances sont disponibles
	 */
	public AmbulancesAvailable(Map<String, Serializable> properties) {
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
