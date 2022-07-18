package event.classes;

import java.io.Serializable;
import java.util.Map;

import event.interfaces.AtomicEventI;
import fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition;

/**
 * La classe <code> HealthEvent </code> instancie un evenement de sante
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class HealthEvent extends AbstractEvent implements AtomicEventI {
	private static final long serialVersionUID = 1L;

	public HealthEvent(String type, AbsolutePosition position) {
		super();
		properties.put("area", position);
		properties.put("type", type);

	}

	/**
	 * Cree l'evenement
	 * 
	 * @param properties
	 */
	public HealthEvent(Map<String, Serializable> properties) {
		super();
		this.properties = properties;
	}

	/**
	 * @see event.interfaces.AtomicEventI#putProperty(java.lang.String,
	 *      java.io.Serializable)
	 */
	@Override
	public Serializable putProperty(String name, Serializable value) {
		return properties.put(name, (String) value);
	}

	/**
	 * @see event.interfaces.AtomicEventI#removeProperty(java.lang.String)
	 */
	@Override
	public void removeProperty(String name) {
		properties.remove(name);
	}

}
