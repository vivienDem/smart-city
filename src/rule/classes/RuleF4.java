package rule.classes;

import java.util.ArrayList;

import components.interfaces.CorrelatorI;
import event.classes.FireEvent;
import event.classes.FireInterventionAsk;
import event.interfaces.EventBaseI;
import event.interfaces.EventI;
import rule.interfaces.RuleI;
import components.interfaces.FIRECorrelatorI;

/**
 * La classe <code> RuleF4 </code> implemente la regle F4 contenue dans le
 * cahier des charges
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class RuleF4 implements RuleI {
	String otherStation;

	/**
	 * @see rule.interfaces.RuleI#match(event.interfaces.EventBaseI)
	 */
	@Override
	public ArrayList<EventI> match(EventBaseI eb) {
		EventI he = null;
		for (int i = 0; i < eb.numberOfEvents() && (he == null); i++) {
			EventI e = eb.getEvent(i);
			if (e instanceof FireEvent && e.hasProperty("type") && ((String) e.getPropertyValue("type")).equals("house")
					&& e.hasProperty("area")) {
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
		FIRECorrelatorI fireState = (FIRECorrelatorI) c;
		otherStation = fireState.otherFireStation(matchedEvents.get(0).getPropertyValue("centerId"),
				matchedEvents.get(0).getPropertyValue("type"));
		return !fireState.standardTruckAvailable(matchedEvents.get(0).getPropertyValue("centerId"))
				&& otherStation != null; // pas de camion et C' le plus proche non sollicité
	}

	/**
	 * @see rule.interfaces.RuleI#act(java.util.ArrayList,
	 *      components.interfaces.CorrelatorI)
	 */
	@Override
	public void act(ArrayList<EventI> matchedEvents, CorrelatorI c) throws Exception {
		FIRECorrelatorI fireState = (FIRECorrelatorI) c;
		FireInterventionAsk f = new FireInterventionAsk(matchedEvents.get(0), "house");
		f.putProperty("centerId", otherStation);

		fireState.redirectFireInterventionAsk(f);

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
