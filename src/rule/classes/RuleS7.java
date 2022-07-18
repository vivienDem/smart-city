package rule.classes;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import components.interfaces.CorrelatorI;
import event.classes.HealthEvent;
import event.classes.SignalOK;
import event.interfaces.EventBaseI;
import event.interfaces.EventI;
import components.interfaces.SAMUCorrelatorI;
import rule.interfaces.RuleI;

/**
 * La classe <code> RuleS7 </code> implemente la regle S7 contenue dans le
 * cahier des charges
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class RuleS7 implements RuleI {

	/**
	 * @see rule.interfaces.RuleI#match(event.interfaces.EventBaseI)
	 */
	@Override
	public ArrayList<EventI> match(EventBaseI eb) {
		EventI he = null;
		EventI s = null;

		for (int i = 0; i < eb.numberOfEvents() && (he == null || s == null); i++) {
			EventI e = eb.getEvent(i);
			if (e instanceof HealthEvent && e.hasProperty("type")
					&& ((String) e.getPropertyValue("type")).equals("tracking") && e.hasProperty("area")) {
				he = e;
			}
			if (e instanceof SignalOK && e.hasProperty("personId")) {
				s = e;
			}
		}

		if (he != null && s != null) {
			ArrayList<EventI> matchedEvents = new ArrayList<>();
			matchedEvents.add(he);
			matchedEvents.add(s);
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
		return matchedEvents.get(0).hasProperty("personId") && matchedEvents.get(1).hasProperty("personId")
				&& matchedEvents.get(0).getPropertyValue("personId")
						.equals(matchedEvents.get(1).getPropertyValue("personId"))
				&& matchedEvents.get(0).getTimeStamp().isBefore(matchedEvents.get(1).getTimeStamp())
				&& matchedEvents.get(0).getTimeStamp().plus(Duration.of(10, ChronoUnit.MINUTES))
						.isAfter(matchedEvents.get(1).getTimeStamp());
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

	/**
	 * @see rule.interfaces.RuleI#act(java.util.ArrayList,
	 *      components.interfaces.CorrelatorI)
	 */
	@Override
	public void act(ArrayList<EventI> matchedEvents, CorrelatorI c) throws Exception {
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
		eb.removeEvent(matchedEvents.get(1));
	}

}
