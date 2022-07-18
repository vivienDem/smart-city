package components.interfaces;

import java.io.Serializable;

/**
 * Interface definissant un executeur d'action (Impl)
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public interface ActionExecutionImplI {
	/**
	 * Fonction generique. Execute l'action a a l'aide des parametres
	 * 
	 * @param a      une action
	 * @param params les parametres
	 * @return la reponse
	 * @throws Exception
	 */
	ResponseI executeAction(ActionI a, Serializable[] params) throws Exception;

}
