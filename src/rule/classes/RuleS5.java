package rule.classes;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import components.interfaces.CorrelatorI;
import event.classes.HealthEvent;
import event.interfaces.EventBaseI;
import event.interfaces.EventI;
import fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition;
import components.interfaces.SAMUCorrelatorI;
import rule.interfaces.RuleI;

/**
 * La classe <code> RuleS5 </code> implemente la regle S5 contenue dans le
 * cahier des charges
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class RuleS5 implements RuleI {

	/**
	 * @see rule.interfaces.RuleI#match(event.interfaces.EventBaseI)
	 */
	@Override
	public ArrayList<EventI> match(EventBaseI eb) {
		EventI he = null;
		for (int i = 0; i < eb.numberOfEvents() && (he == null); i++) {
			EventI e = eb.getEvent(i);
			if (e instanceof HealthEvent && e.hasProperty("type")
					&& ((String) e.getPropertyValue("type")).equals("tracking") && e.hasProperty("area")) {
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
		EventI event = matchedEvents.get(0);
		SAMUCorrelatorI samuState = (SAMUCorrelatorI) c;
		return event.getTimeStamp().plusMinutes(10).isAfter(LocalTime.now(ZoneId.of(ZoneId.SHORT_IDS.get("ECT"))))
				&& samuState.medicAvailable(matchedEvents.get(0).getPropertyValue("centerId"));
	}

	/**
	 * @see rule.interfaces.RuleI#act(java.util.ArrayList,
	 *      components.interfaces.CorrelatorI)
	 */
	@Override
	public void act(ArrayList<EventI> matchedEvents, CorrelatorI c) throws Exception {
	}

	/**
	 * @see rule.interfaces.RuleI#update(java.util.ArrayList,
	 *      event.interfaces.EventBaseI)
	 */
	@Override
	public void update(ArrayList<EventI> matchedEvents, EventBaseI eb) {
		EventI old = matchedEvents.get(0);
		eb.removeEvent(matchedEvents.get(0));
		HealthEvent he = new HealthEvent("medical", (AbsolutePosition) old.getPropertyValue("position"));
		if (old.hasProperty("personId"))
			he.putProperty("personId", old.getPropertyValue("personId"));
		eb.addEvent(he);
	}

}
