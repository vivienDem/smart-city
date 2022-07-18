package rule.classes;

import java.io.Serializable;
import java.util.ArrayList;

import components.interfaces.CorrelatorI;
import event.classes.VehicleCrossing;
import event.classes.WaitingForVehicle;
import event.interfaces.EventBaseI;
import event.interfaces.EventI;
import fr.sorbonne_u.cps.smartcity.grid.IntersectionPosition;
import rule.interfaces.RuleI;
import components.interfaces.TRAFFICCorrelatorI;

/**
 * La classe <code> RuleC2 </code> implemente la regle C2 contenue dans le
 * cahier des charges
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class RuleC2 implements RuleI {
	private IntersectionPosition next;

	/**
	 * @see rule.interfaces.RuleI#match(event.interfaces.EventBaseI)
	 */
	@Override
	public ArrayList<EventI> match(EventBaseI eb) {
		EventI wv = null;
		EventI vc = null;

		for (int i = 0; i < eb.numberOfEvents() && (wv == null || vc == null); i++) {
			EventI e = eb.getEvent(i);
			if (e instanceof WaitingForVehicle && e.hasProperty("priority") && e.hasProperty("vehicle")
					&& e.hasProperty("finalDestination")) {
				wv = e;
			}
			if (e instanceof VehicleCrossing && e.hasProperty("direction")) {
				vc = e;
			}
		}

		if (wv != null && vc != null) {
			ArrayList<EventI> matchedEvents = new ArrayList<>();
			matchedEvents.add(wv);
			matchedEvents.add(vc);
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
		return matchedEvents.get(0).getPropertyValue("vehicle")
				.equals(matchedEvents.get(1).getPropertyValue("vehicle"));
	}

	/**
	 * @see rule.interfaces.RuleI#filter(java.util.ArrayList,
	 *      components.interfaces.CorrelatorI)
	 */
	@Override
	public boolean filter(ArrayList<EventI> matchedEvents, CorrelatorI c) throws Exception {
		TRAFFICCorrelatorI tc = (TRAFFICCorrelatorI) c;
		EventI e = matchedEvents.get(0);
		next = tc.isBefore(e.getPropertyValue("area"), matchedEvents.get(1).getPropertyValue("direction"),
				e.getPropertyValue("finalDestination"));
		return next != null;
	}

	/**
	 * @see rule.interfaces.RuleI#act(java.util.ArrayList,
	 *      components.interfaces.CorrelatorI)
	 */
	@Override
	public void act(ArrayList<EventI> matchedEvents, CorrelatorI c) throws Exception {
		TRAFFICCorrelatorI tc = (TRAFFICCorrelatorI) c;
		EventI e = matchedEvents.get(0);
		tc.switchIntersectionPriorityToNormal(e.getPropertyValue("area"));
		tc.redirectTrafficPriorityRequest((Serializable) next, matchedEvents.get(1).getPropertyValue("direction"),
				e.getPropertyValue("vehicle"), e.getPropertyValue("priority"), e.getPropertyValue("finalDestination"));

	}

	/**
	 * @see rule.interfaces.RuleI#update(java.util.ArrayList,
	 *      event.interfaces.EventBaseI)
	 */
	@Override
	public void update(ArrayList<EventI> matchedEvents, EventBaseI eb) {
		eb.removeEvent(matchedEvents.get(0));
		eb.removeEvent(matchedEvents.get(1));

	}

}
