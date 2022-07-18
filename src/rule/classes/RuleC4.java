package rule.classes;

import java.util.ArrayList;

import components.interfaces.CorrelatorI;
import event.classes.PriorityVehicle;
import event.classes.TrafficPriorityRequest;
import event.classes.WaitingForVehicle;
import event.interfaces.EventBaseI;
import event.interfaces.EventI;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfTrafficLightPriority;
import rule.interfaces.RuleI;
import components.interfaces.TRAFFICCorrelatorI;

/**
 * La classe <code> RuleC4 </code> implemente la regle C4 contenue dans le
 * cahier des charges
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class RuleC4 implements RuleI {

	/**
	 * @see rule.interfaces.RuleI#match(event.interfaces.EventBaseI)
	 */
	@Override
	public ArrayList<EventI> match(EventBaseI eb) {
		EventI pr1 = null;
		EventI pr2 = null;

		for (int i = 0; i < eb.numberOfEvents() && (pr1 == null || pr2 == null); i++) {
			EventI e = eb.getEvent(i);
			if (e instanceof TrafficPriorityRequest && e.hasProperty("priority") && e.hasProperty("vehicle")
					&& e.getPropertyValue("priority") == PriorityVehicle.EMERGENCY) {
				pr1 = e;
			}
			if (e instanceof TrafficPriorityRequest && e.hasProperty("priority") && e.hasProperty("vehicle")
					&& e.getPropertyValue("priority") != PriorityVehicle.EMERGENCY) {
				pr2 = e;
			}
		}

		if (pr1 != null && pr2 != null) {
			ArrayList<EventI> matchedEvents = new ArrayList<>();
			matchedEvents.add(pr1);
			matchedEvents.add(pr2);
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
		return !matchedEvents.get(0).getPropertyValue("vehicle")
				.equals(matchedEvents.get(1).getPropertyValue("vehicle"));
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
		EventI e = matchedEvents.get(0);
		tc.changePriorityOfIntersection(e.getPropertyValue("area"), TypeOfTrafficLightPriority.EMERGENCY);

	}

	/**
	 * @see rule.interfaces.RuleI#update(java.util.ArrayList,
	 *      event.interfaces.EventBaseI)
	 */
	@Override
	public void update(ArrayList<EventI> matchedEvents, EventBaseI eb) {
		eb.removeEvent(matchedEvents.get(0));
		EventI complex = new WaitingForVehicle(matchedEvents.get(0));
		eb.addEvent(complex);

	}

}
