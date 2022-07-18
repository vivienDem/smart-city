package connections;

import java.io.Serializable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RejectedExecutionException;

import components.classes.FIREStationActions;
import components.classes.Responses;
import components.interfaces.ActionExecutionCI;
import components.interfaces.ActionI;
import components.interfaces.ResponseI;
import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition;
import fr.sorbonne_u.cps.smartcity.interfaces.FireStationActionImplI;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfFirefightingResource;

/**
 * La classe<code>FIREproxyActionConnector</code> implemente le connecteur pour
 * l'interface {@code ActionExecutionCI}. Elle connecte une caserne de pompier
 * avec son proxy
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class FIREproxyActionConnector extends AbstractConnector implements ActionExecutionCI {

	/**
	 * @see components.interfaces.ActionExecutionCI#executeAction(components.interfaces.ActionI,
	 *      java.io.Serializable[])
	 */
	@Override
	public ResponseI executeAction(ActionI a, Serializable[] params)
			throws RejectedExecutionException, AssertionError, InterruptedException, ExecutionException, Exception {
		assert a instanceof FIREStationActions;
		ResponseI result = Responses.FAILURE;
		switch ((FIREStationActions) a) {

		case GeneralAlarm:
			result = generalAlarm(params);
			break;
		case FirstAlarm:
			result = firstAlarm(params);
			break;
		case SecondAlarm:
			result = secondAlarm(params);
			break;
		default:
			break;
		}
		return result;

	}

	/**
	 * Appelle la methode triggerSecondAlarm sur le proxy
	 * 
	 * @param params les parametres de la methode
	 * @return la reponse
	 * @throws Exception
	 */
	private ResponseI secondAlarm(Serializable[] params) throws Exception {
		((FireStationActionImplI) this.offering).triggerSecondAlarm((AbsolutePosition) params[0]);
		return Responses.SUCCESS;
	}

	/**
	 * Appelle la methode triggerFirstAlarm sur le proxy
	 * 
	 * @param params les parametres de la methode
	 * @return la reponse
	 * @throws Exception
	 */
	private ResponseI firstAlarm(Serializable[] params) throws Exception {

		((FireStationActionImplI) this.offering).triggerFirstAlarm((AbsolutePosition) params[0],
				(TypeOfFirefightingResource) params[1]);
		return Responses.SUCCESS;
	}

	/**
	 * Appelle la methode triggerGeneralAlarm sur le proxy
	 * 
	 * @param params les parametres de la methode
	 * @return la reponse
	 * @throws Exception
	 */
	private ResponseI generalAlarm(Serializable[] params) throws Exception {
		((FireStationActionImplI) this.offering).triggerGeneralAlarm((AbsolutePosition) params[0]);
		return Responses.SUCCESS;
	}

}
