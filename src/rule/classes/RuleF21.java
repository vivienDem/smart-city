package rule.classes;

import java.util.ArrayList;

import components.interfaces.CorrelatorI;
import event.classes.EndOfFire;
import event.classes.FirstFireAlarm;
import event.interfaces.EventBaseI;
import event.interfaces.EventI;
import components.interfaces.FIRECorrelatorI;
import rule.interfaces.RuleI;

/**
 * La classe <code> RuleF21 </code> implemente la regle F21 contenue dans le
 * cahier des charges
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class RuleF21 implements RuleI {

	/**
	 * @see rule.interfaces.RuleI#match(event.interfaces.EventBaseI)
	 */
	@Override
	public ArrayList<EventI> match(EventBaseI eb) {
		EventI GeneralFireAlarm = null;
		EventI endFire = null;

		for (int i = 0; i < eb.numberOfEvents() && (GeneralFireAlarm == null || endFire == null); i++) {
			EventI e = eb.getEvent(i);
			if (e instanceof FirstFireAlarm && e.hasProperty("area")) {
				GeneralFireAlarm = e;
			}
			if (e instanceof EndOfFire && e.hasProperty("area")) {
				endFire = e;
			}
		}

		if (GeneralFireAlarm != null && endFire != null) {
			ArrayList<EventI> matchedEvents = new ArrayList<>();
			matchedEvents.add(GeneralFireAlarm);
			matchedEvents.add(endFire);
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
		return matchedEvents.get(0).getPropertyValue("area").equals(matchedEvents.get(1).getPropertyValue("area"));
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
		FIRECorrelatorI fc = (FIRECorrelatorI) c;
		fc.redirectEndOfFireToAll((EndOfFire) matchedEvents.get(1));

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
