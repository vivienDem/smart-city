package rule.classes;

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
 * La classe <code> RuleC3 </code> implemente la regle C3 contenue dans le
 * cahier des charges
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class RuleC3 implements RuleI {

	/**
	 * @see rule.interfaces.RuleI#match(event.interfaces.EventBaseI)
	 */
	@Override
	public ArrayList<EventI> match(EventBaseI eb) {
		EventI wv = null;
		EventI vc = null;

		for (int i = 0; i < eb.numberOfEvents() && (wv == null || vc == null); i++) {
			EventI e = eb.getEvent(i);
			if (e instanceof WaitingForVehicle && e.hasProperty("vehicle") && e.hasProperty("finalDestination")
					&& e.hasProperty("area")) {
				wv = e;
			}
			if (e instanceof VehicleCrossing && e.hasProperty("vehicle") && e.hasProperty("direction")) {
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
		EventI wv = matchedEvents.get(0);
		EventI vc = matchedEvents.get(1);
		IntersectionPosition next = tc.isBefore(wv.getPropertyValue("area"), vc.getPropertyValue("direction"),
				wv.getPropertyValue("finalDestination"));
		return next == null;
	}

	/**
	 * @see rule.interfaces.RuleI#act(java.util.ArrayList,
	 *      components.interfaces.CorrelatorI)
	 */
	@Override
	public void act(ArrayList<EventI> matchedEvents, CorrelatorI c) throws Exception {
		TRAFFICCorrelatorI tc = (TRAFFICCorrelatorI) c;
		tc.switchIntersectionPriorityToNormal(matchedEvents.get(0).getPropertyValue("area"));

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
