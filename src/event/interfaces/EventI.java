package event.interfaces;

import java.io.Serializable;
import java.time.LocalTime;

/**
 * Interface definissant un evenement
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public interface EventI extends Serializable {
	/**
	 * Accesseur sur l'heure d'apparition de l'evenement
	 * 
	 * @return l'heure d'apparition de l'evenement
	 */
	LocalTime getTimeStamp();

	/**
	 * Verifie si une propriete est bien definie dans l'evenement
	 * 
	 * @param name le nom de la propriete
	 * @return true si la propriete existe, false sinon
	 */
	boolean hasProperty(String name);

	/**
	 * Accesseur sur une propriete de l'evenement
	 * 
	 * @param name le nom de la propriete
	 * @return la valeur de la propriete
	 */
	Serializable getPropertyValue(String name);

	void setTimeStamp(LocalTime birth);
}
