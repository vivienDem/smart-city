package rule.classes;

import java.util.ArrayList;

import components.interfaces.CorrelatorI;
import event.classes.HealthEvent;
import event.interfaces.EventI;
import components.interfaces.SAMUCorrelatorI;

/**
 * La classe <code> RuleS2 </code> implemente la regle S2 contenue dans le
 * cahier des charges
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class RuleS2 extends RuleS1 {
	private String otherCenter;

	/**
	 * @see rule.interfaces.RuleI#filter(java.util.ArrayList,
	 *      components.interfaces.CorrelatorI)
	 */
	@Override
	public boolean filter(ArrayList<EventI> matchedEvents, CorrelatorI c) throws Exception {
		SAMUCorrelatorI samuState = (SAMUCorrelatorI) c;
		otherCenter = samuState.otherSamuCenter(matchedEvents.get(0).getPropertyValue("centerId"), "emergency");
		return samuState.isInArea(matchedEvents.get(0).getPropertyValue("area"),
				matchedEvents.get(0).getPropertyValue("centerId"))
				&& !samuState.ambulanceAvailable(matchedEvents.get(0).getPropertyValue("centerId"))
				&& otherCenter != null;
	}

	/**
	 * @see rule.interfaces.RuleI#act(java.util.ArrayList,
	 *      components.interfaces.CorrelatorI)
	 */
	@Override
	public void act(ArrayList<EventI> matchedEvents, CorrelatorI c) throws Exception {
		SAMUCorrelatorI samuState = (SAMUCorrelatorI) c;
		HealthEvent e = (HealthEvent) matchedEvents.get(0);
		e.putProperty("centerId", otherCenter);
		samuState.redirectAmbulanceIntervention(e);
	}

}
