package event.classes;

import java.io.Serializable;

import event.interfaces.AtomicEventI;
import fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition;

/**
 * Evenement : Fin de feu
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class EndOfFire extends AbstractEvent implements AtomicEventI {
	private static final long serialVersionUID = -2504130866320653227L;

	public EndOfFire(AbsolutePosition p) {
		super();
		properties.put("area", p);
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
