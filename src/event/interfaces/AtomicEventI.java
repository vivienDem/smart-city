package event.interfaces;

import java.io.Serializable;

/**
 * L'interface <code> AtomicEventI </code> definit un evenement atomique (tel
 * qu'emis par un emetteur)
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public interface AtomicEventI extends EventI {
	/**
	 * Ajoute une propriete a l'evenement
	 * 
	 * @param name  le nom de la propriete
	 * @param value la valeur de la propriete
	 * @return
	 */
	Serializable putProperty(String name, Serializable value);

	/**
	 * Enleve une propriete a l'evenement
	 * 
	 * @param name le nom de la propriete
	 */
	void removeProperty(String name);

}
