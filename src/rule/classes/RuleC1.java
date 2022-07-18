package rule.classes;

import java.util.ArrayList;

import components.interfaces.CorrelatorI;
import event.classes.TrafficPriorityRequest;
import event.classes.WaitingForVehicle;
import event.interfaces.EventBaseI;
import event.interfaces.EventI;
import rule.interfaces.RuleI;
import components.interfaces.TRAFFICCorrelatorI;

/**
 * La classe <code> RuleC1 </code> implemente la regle C1 contenue dans le
 * cahier des charges
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class RuleC1 implements RuleI {

	/**
	 * @see rule.interfaces.RuleI#match(event.interfaces.EventBaseI)
	 */
	@Override
	public ArrayList<EventI> match(EventBaseI eb) {
		EventI he = null;
		for (int i = 0; i < eb.numberOfEvents() && (he == null); i++) {
			EventI e = eb.getEvent(i);
			if (e instanceof TrafficPriorityRequest && e.hasProperty("priority") && e.hasProperty("vehicle")
					&& e.hasProperty("finalDestination")) {
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
	public boolean filter(ArrayList<EventI> matchedEvents, CorrelatorI c) throws Exception {
		return true;
	}

	/**
	 * @see rule.interfaces.RuleI#act(java.util.ArrayList,
	 *      components.interfaces.CorrelatorI)
	 */
	@Override
	public void act(ArrayList<EventI> matchedEvents, CorrelatorI c) throws Exception {
		TRAFFICCorrelatorI tc = (TRAFFICCorrelatorI) c;
		tc.changePriorityOfIntersection(matchedEvents.get(0).getPropertyValue("area"),
				matchedEvents.get(0).getPropertyValue("priority"));

	}

	/**
	 * @see rule.interfaces.RuleI#update(java.util.ArrayList,
	 *      event.interfaces.EventBaseI)
	 */
	@Override
	public void update(ArrayList<EventI> matchedEvents, EventBaseI eb) {
		EventI complex = new WaitingForVehicle(matchedEvents.get(0));
		eb.removeEvent(matchedEvents.get(0));
		eb.addEvent(complex);

	}

}
