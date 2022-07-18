package rule.classes;

import java.util.ArrayList;

import components.interfaces.CorrelatorI;
import event.classes.SecondFireAlarm;
import event.interfaces.EventBaseI;
import event.interfaces.EventI;
import components.interfaces.FIRECorrelatorI;

/**
 * La classe <code> RuleF14 </code> implemente la regle F14 contenue dans le
 * cahier des charges
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class RuleF14 extends RuleF13 {
	String otherStation;

	/**
	 * @see rule.interfaces.RuleI#filter(java.util.ArrayList,
	 *      components.interfaces.CorrelatorI)
	 */
	@Override
	public boolean filter(ArrayList<EventI> matchedEvents, CorrelatorI c) throws Exception {
		FIRECorrelatorI fc = (FIRECorrelatorI) c;
		otherStation = fc.otherFireStation(matchedEvents.get(0).getPropertyValue("centerId"),
				matchedEvents.get(0).getPropertyValue("type"));
		return !fc.standardTruckAvailable(matchedEvents.get(0).getPropertyValue("centerId")) && otherStation != null;
	}

	/**
	 * @see rule.interfaces.RuleI#act(java.util.ArrayList,
	 *      components.interfaces.CorrelatorI)
	 */
	@Override
	public void act(ArrayList<EventI> matchedEvents, CorrelatorI c) throws Exception {
		FIRECorrelatorI fc = (FIRECorrelatorI) c;
		fc.redirectSecondFireAlarm((SecondFireAlarm) matchedEvents.get(0));

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
