package rule.classes;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RejectedExecutionException;

import components.interfaces.CorrelatorI;
import event.classes.ConsciousFall;
import event.interfaces.EventBaseI;
import event.interfaces.EventI;
import components.interfaces.SAMUCorrelatorI;
import rule.interfaces.RuleI;

/**
 * La classe <code> RuleS13 </code> implemente la regle S13 contenue dans le
 * cahier des charges
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class RuleS13 implements RuleI {

	/**
	 * @see rule.interfaces.RuleI#match(event.interfaces.EventBaseI)
	 */
	@Override
	public ArrayList<EventI> match(EventBaseI eb) {
		EventI cf = null;
		for (int i = 0; i < eb.numberOfEvents() && (cf == null); i++) {
			EventI e = eb.getEvent(i);
			if (e instanceof ConsciousFall) {
				cf = e;
			}
		}
		if (cf != null) {
			ArrayList<EventI> matchedEvents = new ArrayList<>();
			matchedEvents.add(cf);
			return matchedEvents;
		} else {
			return null;
		}
	}

	/**
	 * @see rule.interfaces.RuleI#correlate(java.util.ArrayList)
	 */
	@Override
	public boolean correlate(ArrayList<EventI> matchedEvents) {
		return true;
	}

	/**
	 * @see rule.interfaces.RuleI#filter(java.util.ArrayList,
	 *      components.interfaces.CorrelatorI)
	 */
	@Override
	public boolean filter(ArrayList<EventI> matchedEvents, CorrelatorI c)
			throws RejectedExecutionException, AssertionError, InterruptedException, ExecutionException, Exception {
		SAMUCorrelatorI samuState = (SAMUCorrelatorI) c;
		return samuState.medicAvailable(matchedEvents.get(0).getPropertyValue("centerId"));
	}

	/**
	 * @see rule.interfaces.RuleI#act(java.util.ArrayList,
	 *      components.interfaces.CorrelatorI)
	 */
	@Override
	public void act(ArrayList<EventI> matchedEvents, CorrelatorI c)
			throws RejectedExecutionException, AssertionError, InterruptedException, ExecutionException, Exception {
		SAMUCorrelatorI samuState = (SAMUCorrelatorI) c;
		samuState.triggerMedicCall(matchedEvents.get(0).getPropertyValue("area"),
				matchedEvents.get(0).getPropertyValue("personId"), matchedEvents.get(0).getPropertyValue("centerId"));
	}

	/**
	 * @see rule.interfaces.RuleI#update(java.util.ArrayList,
	 *      event.interfaces.EventBaseI)
	 */
	@Override
	public void update(ArrayList<EventI> matchedEvents, EventBaseI eb) {
		eb.removeEvent(matchedEvents.get(0));

	}

}
