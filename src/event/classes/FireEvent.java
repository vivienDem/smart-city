package event.classes;

import java.io.Serializable;
import java.util.Map;

import event.interfaces.AtomicEventI;
import fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition;

/**
 * La classe <code> FireEvent </code> instancie l'evenement incendie
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class FireEvent extends AbstractEvent implements AtomicEventI {

	private static final long serialVersionUID = 1L;

	/**
	 * Cree l'evenement d'incendie
	 * 
	 * @param value
	 */
	public FireEvent(String value) {
		super();
		properties.put("type", value);
	}

	public FireEvent(String value, AbsolutePosition p) {
		super();
		properties.put("type", value);
		properties.put("area", p);
	}

	/**
	 * Cree l'evenement d'incendie
	 * 
	 * @param properties
	 */
	public FireEvent(Map<String, Serializable> properties) {
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
