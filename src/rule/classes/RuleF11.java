package rule.classes;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import components.interfaces.CorrelatorI;
import event.classes.FireEvent;
import event.classes.FirstFireAlarm;
import event.classes.SecondFireAlarm;
import event.interfaces.EventBaseI;
import event.interfaces.EventI;
import components.interfaces.FIRECorrelatorI;
import rule.interfaces.RuleI;

/**
 * La classe <code> RuleF11 </code> implemente la regle F11 contenue dans le
 * cahier des charges
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class RuleF11 implements RuleI {
	EventI fa = null;
	EventI sa = null;

	/**
	 * @see rule.interfaces.RuleI#match(event.interfaces.EventBaseI)
	 */
	@Override
	public ArrayList<EventI> match(EventBaseI eb) {

		for (int i = 0; i < eb.numberOfEvents() && (fa == null || sa == null); i++) {
			EventI e = eb.getEvent(i);
			if (e instanceof FirstFireAlarm && e.hasProperty("area") && e.hasProperty("type")
					&& ((String) e.getPropertyValue("type")).equals("house")) {
				fa = e;
			}
			if (e instanceof FireEvent && e.hasProperty("area") && e.hasProperty("type")
					&& ((String) e.getPropertyValue("type")).equals("house")) {
				sa = e;
			}
		}

		if (fa != null && sa != null) {
			ArrayList<EventI> matchedEvents = new ArrayList<>();
			matchedEvents.add(fa);
			matchedEvents.add(sa);
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
		return matchedEvents.get(0).getPropertyValue("area").equals(matchedEvents.get(1).getPropertyValue("area"))
				&& matchedEvents.get(0).getTimeStamp().isBefore(matchedEvents.get(1).getTimeStamp())
				&& matchedEvents.get(0).getTimeStamp().plus(Duration.of(15, ChronoUnit.MINUTES))
						.isAfter(matchedEvents.get(1).getTimeStamp());
	}

	/**
	 * @see rule.interfaces.RuleI#filter(java.util.ArrayList,
	 *      components.interfaces.CorrelatorI)
	 */
	@Override
	public boolean filter(ArrayList<EventI> matchedEvents, CorrelatorI c) throws Exception {
		FIRECorrelatorI fc = (FIRECorrelatorI) c;
		return fc.standardTruckAvailable(matchedEvents.get(0).getPropertyValue("centerId"));
	}

	/**
	 * @see rule.interfaces.RuleI#act(java.util.ArrayList,
	 *      components.interfaces.CorrelatorI)
	 */
	@Override
	public void act(ArrayList<EventI> matchedEvents, CorrelatorI c) throws Exception {
		FIRECorrelatorI fc = (FIRECorrelatorI) c;
		fc.triggerSecondAlarm(matchedEvents.get(0).getPropertyValue("area"),
				matchedEvents.get(0).getPropertyValue("centerId"));

	}

	/**
	 * @see rule.interfaces.RuleI#update(java.util.ArrayList,
	 *      event.interfaces.EventBaseI)
	 */
	@Override
	public void update(ArrayList<EventI> matchedEvents, EventBaseI eb) {
		eb.removeEvent(matchedEvents.get(0));
		SecondFireAlarm sfa = new SecondFireAlarm(fa, sa);
		sfa.setTimeStamp(sa.getTimeStamp());
		eb.addEvent(sfa);
	}

}
