package rule.classes;

import java.util.ArrayList;

import components.interfaces.CorrelatorI;
import event.classes.MedicInterventionAsk;
import event.interfaces.EventBaseI;
import event.interfaces.EventI;
import components.interfaces.SAMUCorrelatorI;

/**
 * La classe <code> RuleS11 </code> implemente la regle S11 contenue dans le
 * cahier des charges
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class RuleS11 extends RuleS3 {

	/**
	 * @see rule.interfaces.RuleI#match(event.interfaces.EventBaseI)
	 */
	@Override
	public ArrayList<EventI> match(EventBaseI eb) {
		EventI he = null;
		for (int i = 0; i < eb.numberOfEvents() && (he == null); i++) {
			EventI e = eb.getEvent(i);
			if (e instanceof MedicInterventionAsk && e.hasProperty("type")
					&& ((String) e.getPropertyValue("type")).equals("medical")) {
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
	 * @see rule.interfaces.RuleI#filter(java.util.ArrayList,
	 *      components.interfaces.CorrelatorI)
	 */
	@Override
	public boolean filter(ArrayList<EventI> matchedEvents, CorrelatorI c) throws Exception {
		SAMUCorrelatorI samuState = (SAMUCorrelatorI) c;
		return samuState.medicAvailable(matchedEvents.get(0).getPropertyValue("centerId"));
	}

}
