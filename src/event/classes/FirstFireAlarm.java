package event.classes;

import java.util.ArrayList;

import event.interfaces.ComplexEventI;
import event.interfaces.EventI;

/**
 * Evenement : Premiere alarme de feu
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class FirstFireAlarm extends AbstractEvent implements ComplexEventI {
	private static final long serialVersionUID = 1L;
	private ArrayList<EventI> correlated;

	public FirstFireAlarm(EventI e) {
		super();
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

}
