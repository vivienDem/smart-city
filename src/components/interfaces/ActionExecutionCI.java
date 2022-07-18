package components.interfaces;

import java.io.Serializable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RejectedExecutionException;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

/**
 * Interface definissant un executeur d'action
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public interface ActionExecutionCI extends OfferedCI, RequiredCI, ActionExecutionImplI {
	
	/**
	 * Fonction generique. Execute l'action a a l'aide des parametres
	 * @param a une action
	 * @param params les parametres
	 * @return la reponse
	 * @throws Exception 
	 */
	ResponseI executeAction (ActionI a, Serializable [] params) 
			throws RejectedExecutionException, AssertionError, InterruptedException, ExecutionException, Exception;
}
