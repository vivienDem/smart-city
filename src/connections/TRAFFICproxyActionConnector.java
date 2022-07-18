package connections;

import java.io.Serializable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RejectedExecutionException;

import components.classes.Responses;
import components.classes.TRAFFICLightsActions;
import components.interfaces.ActionExecutionCI;
import components.interfaces.ActionI;
import components.interfaces.ResponseI;
import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.cps.smartcity.interfaces.TrafficLightActionImplI;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfTrafficLightPriority;

/**
 * La classe<code>TRAFFICproxyActionConnector</code> implemente le connecteur
 * pour l'interface {@code ActionExecutionCI}. Elle connecte un feu de
 * circulation avec son proxy
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class TRAFFICproxyActionConnector extends AbstractConnector implements ActionExecutionCI {

	/**
	 * @see components.interfaces.ActionExecutionCI#executeAction(components.interfaces.ActionI,
	 *      java.io.Serializable[])
	 */
	@Override
	public ResponseI executeAction(ActionI a, Serializable[] params)
			throws RejectedExecutionException, AssertionError, InterruptedException, ExecutionException, Exception {
		assert a instanceof TRAFFICLightsActions;
		ResponseI result = Responses.FAILURE;
		switch ((TRAFFICLightsActions) a) {
		case RequestPriority:
			result = requestPriority(params);
			break;
		case ReturnToNormal:
			result = returnToNormal(params);
			break;
		default:
			break;
		}
		return result;
	}

	/**
	 * Appelle la methode changePriority sur le proxy
	 * 
	 * @param params les parametres de la methode
	 * @return la reponse
	 * @throws Exception
	 */
	private ResponseI requestPriority(Serializable[] params) throws Exception {
		TypeOfTrafficLightPriority priority = (TypeOfTrafficLightPriority) params[0];
		((TrafficLightActionImplI) this.offering).changePriority(priority);
		return Responses.SUCCESS;
	}

	/**
	 * Appelle la methode returnToNormalMode sur le proxy
	 * 
	 * @param params les parametres de la methode
	 * @return la reponse
	 * @throws Exception
	 */
	private ResponseI returnToNormal(Serializable[] params) throws Exception {
		((TrafficLightActionImplI) this.offering).returnToNormalMode();
		return Responses.SUCCESS;
	}

}
