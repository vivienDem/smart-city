package rule.classes;

import java.util.ArrayList;

import components.interfaces.CorrelatorI;
import event.classes.HealthEvent;
import event.interfaces.EventI;
import components.interfaces.SAMUCorrelatorI;

/**
 * La classe <code> RuleS4 </code> implemente la regle S4 contenue dans le
 * cahier des charges
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class RuleS4 extends RuleS3 {
	private String otherCenter;

	/**
	 * @see rule.interfaces.RuleI#filter(java.util.ArrayList,
	 *      components.interfaces.CorrelatorI)
	 */
	@Override
	public boolean filter(ArrayList<EventI> matchedEvents, CorrelatorI c) throws Exception {
		SAMUCorrelatorI samuState = (SAMUCorrelatorI) c;
		otherCenter = samuState.otherSamuCenter(matchedEvents.get(0).getPropertyValue("centerId"), "medical");
		return samuState.isInArea(matchedEvents.get(0).getPropertyValue("area"),
				matchedEvents.get(0).getPropertyValue("centerId"))
				&& !samuState.medicAvailable(matchedEvents.get(0).getPropertyValue("centerId")) && otherCenter != null;
	}

	/**
	 * @see rule.interfaces.RuleI#act(java.util.ArrayList,
	 *      components.interfaces.CorrelatorI)
	 */
	@Override
	public void act(ArrayList<EventI> matchedEvents, CorrelatorI c) throws Exception {
		SAMUCorrelatorI samuState = (SAMUCorrelatorI) c;
		HealthEvent he = (HealthEvent) matchedEvents.get(0);
		he.putProperty("centerId", otherCenter);
		samuState.redirectMedicIntervention(he);
	}
}
