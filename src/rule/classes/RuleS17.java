package rule.classes;

import java.util.ArrayList;

import components.interfaces.CorrelatorI;
import event.classes.NoMedicsAvailable;
import event.interfaces.EventBaseI;
import event.interfaces.EventI;
import components.interfaces.SAMUCorrelatorI;

/**
 * La classe <code> RuleS17 </code> implemente la regle S17 contenue dans le
 * cahier des charges
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class RuleS17 extends RuleS16 {

	/**
	 * @see rule.interfaces.RuleI#match(event.interfaces.EventBaseI)
	 */
	@Override
	public ArrayList<EventI> match(EventBaseI eb) {
		EventI he = null;
		for (int i = 0; i < eb.numberOfEvents() && (he == null); i++) {
			EventI e = eb.getEvent(i);
			if (e instanceof NoMedicsAvailable && e.hasProperty("centerId")) {
				he = e;
			}
		}
		if (he != null) {
			ArrayList<EventI> matchedEvents = new ArrayList<>();
			matchedEvents.add(he);
			return matchedEvents;
		} else {
			return null;
		}
	}

	/**
	 * @see rule.interfaces.RuleI#act(java.util.ArrayList,
	 *      components.interfaces.CorrelatorI)
	 */
	@Override
	public void act(ArrayList<EventI> matchedEvents, CorrelatorI c) throws Exception {
		SAMUCorrelatorI samuState = (SAMUCorrelatorI) c;
		samuState.setMedicsNonAvailable((String) matchedEvents.get(0).getPropertyValue("centerId"));

	}
}
