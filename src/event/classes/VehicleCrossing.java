package event.classes;

import java.io.Serializable;

import event.interfaces.AtomicEventI;
import fr.sorbonne_u.cps.smartcity.grid.Direction;

/**
 * Evenement : Passage d'un vehicule
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class VehicleCrossing extends AbstractEvent implements AtomicEventI {
	private static final long serialVersionUID = 1502842674019971358L;

	/**
	 * Cr�� l'�v�nement indiquant qu'un v�hicule est en train de passer
	 * 
	 * @param v URI du v�hicule
	 * @param d la direction
	 */
	public VehicleCrossing(String v, Direction d) {
		properties.put("vehicle", v);
		properties.put("direction", d);
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
