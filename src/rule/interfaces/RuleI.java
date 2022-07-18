package rule.interfaces;

import java.util.ArrayList;

import components.interfaces.CorrelatorI;
import event.interfaces.EventBaseI;
import event.interfaces.EventI;

/**
 * L'interface <code> RuleI </code> definit une regle
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public interface RuleI {
	/**
	 * Apparie les evenements possedant les proprietes requises
	 * 
	 * @param eb une base d'evenements
	 * @return la liste des evenements apparies a la regle
	 */
	ArrayList<EventI> match(EventBaseI eb);

	/**
	 * Verifie les contraintes croisees entre les evenements
	 * 
	 * @param matchedEvents la liste des evenements apparies
	 * @return true s'il n'y a pas de telle contrainte, false sinon
	 */
	boolean correlate(ArrayList<EventI> matchedEvents);

	/**
	 * Verifie les conditions de declenchement de la regle
	 * 
	 * @param matchedEvents les evenements apparies
	 * @param c             un correlateur
	 * @return true si les conditions sont respectees, false sinon
	 * @throws Exception
	 */
	boolean filter(ArrayList<EventI> matchedEvents, CorrelatorI c) throws Exception;

	/**
	 * Action
	 * 
	 * @param matchedEvents les evenements apparies
	 * @param c             un correlateur
	 * @throws Exception
	 */
	void act(ArrayList<EventI> matchedEvents, CorrelatorI c) throws Exception;

	/**
	 * Mise a jour de la base d'evenements
	 * 
	 * @param matchedEvents les evenements apparies
	 * @param eb            la base d'evenements
	 */
	void update(ArrayList<EventI> matchedEvents, EventBaseI eb);
}
