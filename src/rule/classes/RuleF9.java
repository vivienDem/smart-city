package rule.classes;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RejectedExecutionException;

import components.interfaces.CorrelatorI;
import event.classes.FireEvent;
import event.classes.FireGeneralAlarm;
import event.classes.FirstFireAlarm;
import event.interfaces.EventBaseI;
import event.interfaces.EventI;
import components.interfaces.FIRECorrelatorI;
import rule.interfaces.RuleI;

/**
 * La classe <code> RuleF9 </code> implemente la regle F9 contenue dans le
 * cahier des charges
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class RuleF9 implements RuleI {
	private FireGeneralAlarm fg = null;

	/**
	 * @see rule.interfaces.RuleI#match(event.interfaces.EventBaseI)
	 */
	public ArrayList<EventI> match(EventBaseI eb) {
		EventI he = null;
		EventI s = null;

		for (int i = 0; i < eb.numberOfEvents() && (he == null || s == null); i++) {
			EventI e = eb.getEvent(i);
			if (e instanceof FirstFireAlarm && e.hasProperty("type")
					&& ((String) e.getPropertyValue("type")).equals("building")) {
				he = e;
			}
			if (e instanceof FireEvent && e.hasProperty("type")
					&& ((String) e.getPropertyValue("type")).equals("building")) {
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
				&& matchedEvents.get(0).getTimeStamp().plus(Duration.of(15, ChronoUnit.MINUTES))
						.isAfter(matchedEvents.get(1).getTimeStamp());
	}

	/**
	 * @see rule.interfaces.RuleI#filter(java.util.ArrayList,
	 *      components.interfaces.CorrelatorI)
	 */
	@Override
	public boolean filter(ArrayList<EventI> matchedEvents, CorrelatorI c)
			throws RejectedExecutionException, AssertionError, InterruptedException, ExecutionException, Exception {
		FIRECorrelatorI fireState = (FIRECorrelatorI) c;
		EventI e = matchedEvents.get(0);
		return e.hasProperty("area") && fireState.isInArea(e.getPropertyValue("area"), e.getPropertyValue("centerId"));

	}

	/**
	 * @see rule.interfaces.RuleI#act(java.util.ArrayList,
	 *      components.interfaces.CorrelatorI)
	 */
	@Override
	public void act(ArrayList<EventI> matchedEvents, CorrelatorI c) throws Exception {
		FIRECorrelatorI fireState = (FIRECorrelatorI) c;
		fireState.triggerGeneralAlarm(matchedEvents.get(0).getPropertyValue("area"),
				matchedEvents.get(0).getPropertyValue("centerId"));

		fg = new FireGeneralAlarm(matchedEvents.get(0), matchedEvents.get(1));
		fireState.redirectGeneralAlarmToAll(fg, matchedEvents.get(0).getPropertyValue("centerId"));

	}

	/**
	 * @see rule.interfaces.RuleI#update(java.util.ArrayList,
	 *      event.interfaces.EventBaseI)
	 */
	@Override
	public void update(ArrayList<EventI> matchedEvents, EventBaseI eb) {
		EventI e = matchedEvents.get(0);
		eb.removeEvent(e);
		eb.addEvent(fg);
	}

}
