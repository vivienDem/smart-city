package rule.classes;

import java.util.ArrayList;

import components.interfaces.CorrelatorI;
import event.interfaces.EventI;
import components.interfaces.FIRECorrelatorI;

/**
 * La classe <code> RuleF8Bis </code> implemente la regle F8Bis contenue dans le
 * cahier des charges
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class RuleF8Bis extends RuleF8 {
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
		return !fc.standardTruckAvailable(matchedEvents.get(0).getPropertyValue("centerId")) && otherStation == null;
	}

	/**
	 * @see rule.interfaces.RuleI#act(java.util.ArrayList,
	 *      components.interfaces.CorrelatorI)
	 */
	@Override
	public void act(ArrayList<EventI> matchedEvents, CorrelatorI c) {
	}

}
