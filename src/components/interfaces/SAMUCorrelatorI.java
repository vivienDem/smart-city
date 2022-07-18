package components.interfaces;

import java.io.Serializable;
import event.classes.ConsciousFall;

/**
 * Interface definissant un correlateur relatif au SAMU
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public interface SAMUCorrelatorI extends CorrelatorI {

	/**
	 * Declenche l'intervention d'une ambulance
	 * 
	 * @param position la position de l'evenement
	 * @param centerId URI du centre de SAMU
	 * @throws Exception
	 */
	void triggerAmbulanceIntervention(Serializable position, Serializable centerId) throws Exception;

	/**
	 * Verifie si l'evenement est dans le rayon d'action des executeurs d'actions
	 * (centres de SAMU)
	 * 
	 * @param position la position de l'evenement
	 * @param centerId URI du centre de SAMU
	 * @return true si l'evenement est dans le rayon d'action du centre, false sinon
	 * @throws Exception
	 */
	boolean isInArea(Serializable position, Serializable centerId) throws Exception;

	/**
	 * Verifie si une ambulance est disponible
	 * 
	 * @return true si une ambulance est disponible, false sinon
	 * @throws Exception
	 */
	boolean ambulanceAvailable(Serializable centerId) throws Exception;

	/**
	 * Verifie si un medecin est disponible
	 * @param centerId l'identifiant du centre
	 * @return true si un medecin est disponible, false sinon
	 * @throws Exception
	 */
	boolean medicAvailable(Serializable centerId) throws Exception;

	/**
	 * Declenche l'intervention d'une d'un medecin
	 * 
	 * @param position la position de l'evenement
	 * @param centerId URI du centre de SAMU
	 * @throws Exception
	 */
	void triggerMedicIntervention(Serializable position, Serializable centerId) throws Exception;

	/**
	 * Redirige un evenement intervention d'ambulance
	 * 
	 * @param event l'evenement
	 * @throws Exception
	 */
	void redirectAmbulanceIntervention(Serializable event) throws Exception;

	/**
	 * Redirige un evenement intervention de medecin
	 * 
	 * @param event les proprietes de l'evenement
	 * @throws Exception
	 */
	void redirectMedicIntervention(Serializable event) throws Exception;

	/**
	 * Redirige un evenement de Tracking
	 * 
	 * @param position la position de l'evenement
	 * @param centerId URI du centre de SAMU
	 * @throws Exception
	 */
	void redirectTrackingEvent(Serializable position, Serializable centerId) throws Exception;

	/**
	 * Declenche l'intervention d'un appel de medecin
	 * 
	 * @param position la position de l'evenement
	 * @param personId l'identifiant de la personne
	 * @param centerId URI du centre de SAMU
	 * @throws Exception
	 */
	void triggerMedicCall(Serializable position, Serializable personId, Serializable centerId) throws Exception;

	/**
	 * Redirige un evenement appel de medecin
	 * 
	 * @param position la position de l'evenement
	 * @param personId le nom de la personne
	 * @param centerId URI du centre de SAMU
	 * @throws Exception
	 */
	void redirectMedicCall(Serializable position, Serializable personId, Serializable centerId) throws Exception;

	/**
	 * Rend les medecins non disponibles
	 * 
	 * @param centerId URI du centre de SAMU concerne
	 * @throws Exception
	 */
	void setMedicsNonAvailable(String centerId) throws Exception;

	/**
	 * Rend les ambulances disponibles
	 * 
	 * @param centerId URI du centre de SAMU concerne
	 * @throws Exception
	 */
	void setAmbulancesAvailable(String centerId) throws Exception;

	/**
	 * Rend les medecins disponibles
	 * 
	 * @param centerId URI du centre de SAMU concerne
	 * @throws Exception
	 */
	void setMedicsAvailable(String centerId) throws Exception;

	/**
	 * Rend les ambulances non disponibles
	 * 
	 * @param centerId URI du centre de SAMU concerne
	 * @throws Exception
	 */
	void setAmbulancesNonAvailable(String centerId) throws Exception;

	/**
	 * Cherche un autre centre de SAMU disponible vis a vis de la ressource
	 * manquante
	 * 
	 * @param centerId URI du centre de SAMU
	 * @param type     type de la ressource dont le centre manque
	 * @return URI du centre trouve, null sinon
	 * @throws Exception
	 */
	String otherSamuCenter(Serializable centerId, Serializable type) throws Exception;

	/**
	 * Redirige un evenement de chute consciente vers un autre correlateur
	 * 
	 * @param c l'evenement
	 * @throws Exception
	 */
	void redirectConsciousFall(ConsciousFall c) throws Exception;

}
