package rule.classes;

import java.util.ArrayList;
import components.interfaces.CorrelatorI;
import event.classes.FireInterventionAsk;
import event.interfaces.EventBaseI;
import event.interfaces.EventI;
import components.interfaces.FIRECorrelatorI;

/**
 * La classe <code> RuleF6 </code> implemente la regle F6 contenue dans le
 * cahier des charges
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class RuleF6 extends RuleF5 {
	String otherStation;

	/**
	 * @see rule.interfaces.RuleI#filter(java.util.ArrayList,
	 *      components.interfaces.CorrelatorI)
	 */
	@Override
	public boolean filter(ArrayList<EventI> matchedEvents, CorrelatorI c) throws Exception {
		FIRECorrelatorI fireState = (FIRECorrelatorI) c;
		otherStation = fireState.otherFireStation(matchedEvents.get(0).getPropertyValue("centerId"),
				matchedEvents.get(0).getPropertyValue("type"));
		return (!fireState.highLadderAvailable(matchedEvents.get(0).getPropertyValue("centerId")))
				&& otherStation != null;
	}

	/**
	 * @see rule.interfaces.RuleI#act(java.util.ArrayList,
	 *      components.interfaces.CorrelatorI)
	 */
	@Override
	public void act(ArrayList<EventI> matchedEvents, CorrelatorI c) throws Exception {
		FIRECorrelatorI fireState = (FIRECorrelatorI) c;
		FireInterventionAsk fia = (FireInterventionAsk) matchedEvents.get(0);
		fia.putProperty("centerId", otherStation);
		fireState.redirectFireInterventionAsk(fia);
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
