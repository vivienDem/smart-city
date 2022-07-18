package rule.classes;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import components.interfaces.CorrelatorI;
import event.interfaces.EventBaseI;
import event.interfaces.EventI;
import components.interfaces.SAMUCorrelatorI;

/**
 * La classe <code> RuleS6 </code> implemente la regle S6 contenue dans le
 * cahier des charges
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class RuleS6 extends RuleS5 {
	private String otherCenter;

	/**
	 * @see rule.interfaces.RuleI#filter(java.util.ArrayList,
	 *      components.interfaces.CorrelatorI)
	 */
	@Override
	public boolean filter(ArrayList<EventI> matchedEvents, CorrelatorI c) throws Exception {
		EventI event = matchedEvents.get(0);
		SAMUCorrelatorI samuState = (SAMUCorrelatorI) c;
		otherCenter = samuState.otherSamuCenter(matchedEvents.get(0).getPropertyValue("centerId"),
				matchedEvents.get(0).getPropertyValue("type"));
		return event.getTimeStamp().plusMinutes(10).isAfter(LocalTime.now(ZoneId.of(ZoneId.SHORT_IDS.get("ECT"))))
				&& !samuState.medicAvailable(matchedEvents.get(0).getPropertyValue("centerId")) && otherCenter != null;
	}

	/**
	 * @see rule.interfaces.RuleI#act(java.util.ArrayList,
	 *      components.interfaces.CorrelatorI)
	 */
	@Override
	public void act(ArrayList<EventI> matchedEvents, CorrelatorI c) throws Exception {
		SAMUCorrelatorI samuState = (SAMUCorrelatorI) c;
		samuState.redirectTrackingEvent(matchedEvents.get(0).getPropertyValue("area"), otherCenter);
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
