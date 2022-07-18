package rule.classes;

import java.util.ArrayList;

import components.interfaces.CorrelatorI;
import event.classes.MedicInterventionAsk;
import event.interfaces.EventI;
import components.interfaces.SAMUCorrelatorI;

/**
 * La classe <code> RuleS12 </code> implemente la regle S12 contenue dans le
 * cahier des charges
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class RuleS12 extends RuleS11 {
	private String otherCenter;

	/**
	 * @see rule.interfaces.RuleI#filter(java.util.ArrayList,
	 *      components.interfaces.CorrelatorI)
	 */
	@Override
	public boolean filter(ArrayList<EventI> matchedEvents, CorrelatorI c) throws Exception {
		SAMUCorrelatorI samuState = (SAMUCorrelatorI) c;
		otherCenter = samuState.otherSamuCenter(matchedEvents.get(0).getPropertyValue("centerId"),
				matchedEvents.get(0).getPropertyValue("type"));
		return !samuState.medicAvailable(matchedEvents.get(0).getPropertyValue("centerId")) && otherCenter != null;
	}

	/**
	 * @see rule.interfaces.RuleI#act(java.util.ArrayList,
	 *      components.interfaces.CorrelatorI)
	 */
	@Override
	public void act(ArrayList<EventI> matchedEvents, CorrelatorI c) throws Exception {
		SAMUCorrelatorI samuState = (SAMUCorrelatorI) c;
		MedicInterventionAsk he = (MedicInterventionAsk) matchedEvents.get(0);
		he.setCenterId(otherCenter);
		samuState.redirectMedicIntervention(he);
	}
}
