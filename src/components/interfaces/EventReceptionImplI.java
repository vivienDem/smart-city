package components.interfaces;

import event.interfaces.EventI;

/**
 * Interface definissant un composant qui recoit les evenements (Impl)
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public interface EventReceptionImplI {
	/**
	 * Receptionne un evenement
	 * 
	 * @param emitterURI URI de l'emetteur
	 * @param e          l'evenement
	 */
	void receiveEvent(String emitterURI, EventI e) throws Exception;

	/**
	 * Receptionne des evenements
	 * 
	 * @param emitterURI URI de l'emetteur
	 * @param events     les evenements
	 */
	void receiveEvents(String emitterURI, EventI[] events) throws Exception;
}
