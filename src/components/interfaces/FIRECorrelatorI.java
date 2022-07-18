package components.interfaces;

import java.io.Serializable;
import event.classes.EndOfFire;
import event.classes.FireGeneralAlarm;
import event.classes.FireInterventionAsk;
import event.classes.SecondFireAlarm;

/**
 * Interface definissant un correlateur relatif au feu
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public interface FIRECorrelatorI extends CorrelatorI {
	/**
	 * Verifie si un evenement est dans la zone d'action du correlateur
	 * 
	 * @param position la position
	 * @param centerId l'identifiant du centre
	 * @return true si la position est dans la zone d'action, false sinon
	 * @throws Exception
	 */
	boolean isInArea(Serializable position, Serializable centerId) throws Exception;

	/**
	 * Verifie si une grande echelle est disponible
	 * 
	 * @return true si une grande echelle est disponible, false sinon
	 * @throws Exception
	 */
	boolean highLadderAvailable(Serializable centerId) throws Exception;

	/**
	 * Verifie si un camion standard est disponible
	 * 
	 * @return true si un camion standard est disponible, false sinon
	 * @throws Exception
	 */
	boolean standardTruckAvailable(Serializable centerId) throws Exception;

	/**
	 * Cherche une station disponible la plus proche de la station appelante
	 * 
	 * @param centerId URI de la station appelante
	 * @param type     le type de ressource dont la station appelante manque
	 * @return URI de la station trouvee, null sinon
	 * @throws Exception
	 */
	String otherFireStation(Serializable centerId, Serializable type) throws Exception;

	/**
	 * Redirige une intervention vers le correlateur le plus proche
	 * 
	 * @param fireIntervention l'evenement a rediriger
	 * @throws Exception
	 */
	void redirectFireInterventionAsk(FireInterventionAsk fireIntervention) throws Exception; // to the closest neighbour

	/**
	 * Declenche une premiere alarme sur batiment
	 * 
	 * @param position la position de l'evenement
	 * @param centerId URI de la station concernee
	 * @throws Exception
	 */
	void triggerFirstAlarmOnBuilding(Serializable position, Serializable centerId) throws Exception;

	/**
	 * Declenche une premiere alarme sur maison
	 * 
	 * @param position la position de l'evenement
	 * @param centerId URI de la station concernee
	 * @throws Exception
	 */
	void triggerFirstAlarmOnHouse(Serializable position, Serializable centerId) throws Exception;

	/**
	 * Declenche une alarme generale (batiment)
	 * 
	 * @param position la position de l'evenement
	 * @param centerId URI de la station concernee
	 * @throws Exception
	 */
	void triggerGeneralAlarm(Serializable position, Serializable centerId) throws Exception;

	/**
	 * Redirige une alarme generale a toutes les stations
	 * 
	 * @param fireGeneralAlarm l'evenement a rediriger
	 * @param centerId         URI de la station qui redirige
	 * @throws Exception
	 */
	void redirectGeneralAlarmToAll(FireGeneralAlarm fireGeneralAlarm, Serializable centerId) throws Exception;

	/**
	 * Redirige une seconde alarme a la nouvelle station (deja mis a jour dans les
	 * proprietes de l'evenement)
	 * 
	 * @param sfa l'evenement a rediriger
	 * @throws Exception
	 */
	void redirectSecondFireAlarm(SecondFireAlarm sfa) throws Exception;

	/**
	 * Redirige une fin de feu a toutes les stations
	 * 
	 * @param endOfFire l'evenement a rediriger
	 * @throws Exception
	 */
	void redirectEndOfFireToAll(EndOfFire endOfFire) throws Exception;

	/**
	 * Declenche une seconde alarme (maison)
	 * 
	 * @param position la position de l'evenement
	 * @param centerId URI de la station concernee
	 * @throws Exception
	 */
	void triggerSecondAlarm(Serializable position, Serializable centerId) throws Exception;

	/**
	 * Rend les camions standards disponibles
	 * 
	 * @param centerId
	 * @throws Exception
	 */
	void setStandardTrucksAvailable(Serializable centerId) throws Exception;

	/**
	 * Rend les camions grandes echelles disponibles
	 * 
	 * @param centerId
	 * @throws Exception
	 */
	void setHighLaddersAvailable(Serializable centerId) throws Exception;

	/**
	 * Rend les camions grandes echelles non disponibles
	 * 
	 * @param centerId
	 * @throws Exception
	 */
	void setHighLaddersNonAvailable(Serializable centerId) throws Exception;

	/**
	 * Rend les camions standards non disponibles
	 * 
	 * @param centerId
	 * @throws Exception
	 */
	void setStandardTrucksNonAvailable(Serializable centerId) throws Exception;

}
