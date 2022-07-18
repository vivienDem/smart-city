package event.interfaces;

import java.time.Duration;

/**
 * L'interface <code> EventBaseI </code> definit une base d'evenements
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public interface EventBaseI {
	/**
	 * Permet d'ajouter un evenement a la base
	 * 
	 * @param e l'evenement a ajouter
	 */
	void addEvent(EventI e);

	/**
	 * Permet d'enlever un evenement de la base
	 * 
	 * @param e l'evenement a enlever
	 */
	void removeEvent(EventI e);

	/**
	 * Accesseur sur un evenement
	 * 
	 * @param i l'indice de l'evenement dans la base
	 * @return l'evenement recherche
	 */
	EventI getEvent(int i);

	/**
	 * Cherche le nombre d'evenements presents dans la base
	 * 
	 * @return le nombre d'evenements presents dans la base
	 */
	int numberOfEvents();

	/**
	 * Verifie si un evenement est present dans la base
	 * 
	 * @param e l'evenement
	 * @return true si e est present dans la base, false sinon
	 */
	boolean appearsIn(EventI e);

	/**
	 * Supprime les evenements de la base
	 * 
	 * @param d une duree
	 */
	void clearEvents(Duration d);
}
