package components.interfaces;

import event.interfaces.EventI;
/**
 * Interface definissant un emetteur d'evenement (Impl)
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public interface EventEmissionImplI {
	/**
	 * Envoi un evenement vers le bus
	 * 
	 * @param emitterURI URI de l'emetteur
	 * @param event      l'evenement a propager vers le bus
	 * @throws Exception
	 */
	void sendEvent(String emitterURI, EventI event) throws Exception;

	/**
	 * Envoi des evenements vers le bus
	 * 
	 * @param emitterURI URI de l'emetteur
	 * @param events     les evenements a propager vers le bus
	 * @throws Exception
	 */
	void sendEvents(String emitterURI, EventI[] events) throws Exception;

}
