package rule.classes;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RejectedExecutionException;

import components.interfaces.CorrelatorI;
import event.interfaces.EventI;
import components.interfaces.SAMUCorrelatorI;

/**
 * La classe <code> RuleS12Bis </code> implemente la regle S12Bis contenue dans
 * le cahier des charges
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class RuleS12Bis extends RuleS12 {
	private String otherCenter;

	/**
	 * @see rule.interfaces.RuleI#filter(java.util.ArrayList,
	 *      components.interfaces.CorrelatorI)
	 */
	@Override
	public boolean filter(ArrayList<EventI> matchedEvents, CorrelatorI c)
			throws RejectedExecutionException, AssertionError, InterruptedException, ExecutionException, Exception {
		SAMUCorrelatorI samuState = (SAMUCorrelatorI) c;
		otherCenter = samuState.otherSamuCenter(matchedEvents.get(0).getPropertyValue("centerId"),
				matchedEvents.get(0).getPropertyValue("type"));
		return !samuState.medicAvailable(matchedEvents.get(0).getPropertyValue("centerId")) && otherCenter == null;
	}

	/**
	 * @see rule.interfaces.RuleI#act(java.util.ArrayList,
	 *      components.interfaces.CorrelatorI)
	 */
	@Override
	public void act(ArrayList<EventI> matchedEvents, CorrelatorI c) throws Exception {
	}
}
