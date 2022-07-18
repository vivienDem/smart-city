package rule.classes;

import java.util.ArrayList;

import components.interfaces.CorrelatorI;
import event.interfaces.EventBaseI;
import event.interfaces.EventI;
import rule.interfaces.RuleI;

/**
 * La classe <code>AbstractRuleBase</code> implante partiellement une RuleBase
 * abstraite
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class RuleBase {
	/** Liste des RuleI contenues dans la RuleBase */
	private ArrayList<RuleI> rules = new ArrayList<RuleI>();

	/**
	 * Ajoute une regle a la base
	 * 
	 * @param r la regle a ajouter
	 */
	public void addRule(RuleI r) {
		rules.add(r);
	}

	/**
	 * Tente de declencher les regles. S'arrete lorqu'une regle est declenchee
	 * 
	 * @param eb une base d'evenements
	 * @param c  un correlateur
	 * @return true si un evenement a ete declenche, false sinon
	 * @throws Exception
	 */
	public boolean fireFirstOn(EventBaseI eb, CorrelatorI c) throws Exception {
		for (RuleI r : rules) {
			ArrayList<EventI> events = r.match(eb);
			if (events != null && r.correlate(events) && r.filter(events, c)) {
				if (r instanceof RuleC1 || r instanceof RuleC2 || r instanceof RuleC3 || r instanceof RuleC4
						|| r instanceof RuleC5)
					System.out.println(r.getClass().getSimpleName() + " trigerred by vehicle "
							+ events.get(0).getPropertyValue("vehicle"));
				else
					System.out.println(r.getClass().getSimpleName() + " trigerred by "
							+ events.get(0).getPropertyValue("centerId"));
				r.act(events, c);
				r.update(events, eb);
				return true;
			}
		}
		return false;
	}

	/**
	 * Tente de declencher les regles. S'arrete lorsqu'aucune regle ne peut etre
	 * declenchee
	 * 
	 * @param eb une base d'evenements
	 * @param c  un correlateur
	 * @return true si au moins un evenement a ete declenche, false sinon
	 * @throws Exception
	 */
	public boolean fireAllOn(EventBaseI eb, CorrelatorI c) throws Exception {
		boolean trigger = false;
		while (fireFirstOn(eb, c)) {
			trigger = true;
		}
		return trigger;
	}
}
