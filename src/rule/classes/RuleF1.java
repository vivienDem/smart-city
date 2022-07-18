package rule.classes;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RejectedExecutionException;

import components.interfaces.CorrelatorI;
import event.classes.FireEvent;
import event.classes.FirstFireAlarm;
import event.interfaces.EventBaseI;
import event.interfaces.EventI;
import components.interfaces.FIRECorrelatorI;
import rule.interfaces.RuleI;
/**
 * La classe <code> RuleF1 </code> implemente la regle F1 contenue dans le
 * cahier des charges
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class RuleF1 implements RuleI {

	/**
	 * @see rule.interfaces.RuleI#match(event.interfaces.EventBaseI)
	 */
	@Override
	public ArrayList<EventI> match(EventBaseI eb) {
		EventI he = null;
		for (int i = 0; i < eb.numberOfEvents() && (he == null); i++) {
			EventI e = eb.getEvent(i);
			if (e instanceof FireEvent && e.hasProperty("type") && 
					((String) e.getPropertyValue("type")).equals("building") && e.hasProperty("area")) {
				he = e;
			}
		}
		if (he != null) {
			ArrayList<EventI> matchedEvents = new ArrayList<>();
			matchedEvents.add(he);
			return matchedEvents;
		}
		else {
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
	 * @see rule.interfaces.RuleI#filter(java.util.ArrayList, components.interfaces.CorrelatorI)
	 */
	@Override
	public boolean filter(ArrayList<EventI> matchedEvents, CorrelatorI c) 
			throws RejectedExecutionException, AssertionError, InterruptedException, ExecutionException, Exception {
		FIRECorrelatorI fireState = (FIRECorrelatorI) c;
		return fireState.isInArea(matchedEvents.get(0).getPropertyValue("area"), matchedEvents.get(0).getPropertyValue("centerId")) && 
				fireState.highLadderAvailable(matchedEvents.get(0).getPropertyValue("centerId"));
	}

	/**
	 * @see rule.interfaces.RuleI#act(java.util.ArrayList, components.interfaces.CorrelatorI)
	 */
	@Override
	public void act(ArrayList<EventI> matchedEvents, CorrelatorI c) 
			throws RejectedExecutionException, AssertionError, InterruptedException, ExecutionException, Exception {
		FIRECorrelatorI fireState = (FIRECorrelatorI) c;
		fireState.triggerFirstAlarmOnBuilding(matchedEvents.get(0).getPropertyValue("area") ,matchedEvents.get(0).getPropertyValue("centerId"));

	}

	/**
	 * @see rule.interfaces.RuleI#update(java.util.ArrayList, event.interfaces.EventBaseI)
	 */
	@Override
	public void update(ArrayList<EventI> matchedEvents, EventBaseI eb) {
		EventI e = matchedEvents.get(0);
		eb.removeEvent(e);
		FirstFireAlarm ffa = new FirstFireAlarm(e);
		ffa.setTimeStamp(e.getTimeStamp());
		eb.addEvent(ffa);

	}

}
