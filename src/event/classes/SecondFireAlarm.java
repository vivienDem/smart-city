package event.classes;

import java.util.ArrayList;

import event.interfaces.ComplexEventI;
import event.interfaces.EventI;

/**
 * Evenement : Seconde alarme de feu
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class SecondFireAlarm extends AbstractEvent implements ComplexEventI {
	private static final long serialVersionUID = -9213666664194850058L;
	private ArrayList<EventI> correlated;

	public SecondFireAlarm(EventI e1, EventI e2) {
		super();
		correlated = new ArrayList<EventI>();
		correlated.add(e1);
		correlated.add(e2);
		properties.put("second", "true");
	}

	/**
	 * @see event.interfaces.ComplexEventI#getCorrelatedEvents()
	 */
	@Override
	public ArrayList<EventI> getCorrelatedEvents() {
		return correlated;
	}

}
