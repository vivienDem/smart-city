package event.classes;

import java.io.Serializable;
import java.util.ArrayList;

import event.interfaces.ComplexEventI;
import event.interfaces.EventI;

/**
 * Evenement : Attente d'un vehicule
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class WaitingForVehicle extends AbstractEvent implements ComplexEventI {
	private static final long serialVersionUID = 6613430573769459837L;
	private ArrayList<EventI> correlated;

	public WaitingForVehicle(EventI e) {
		correlated = new ArrayList<EventI>();
		correlated.add(e);
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
		for (EventI e : correlated) {
			if (e.hasProperty(name))
				return e.getPropertyValue(name);
		}
		return null;

	}

}
