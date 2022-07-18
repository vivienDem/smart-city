package connections;

import java.io.Serializable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RejectedExecutionException;

import components.classes.Responses;
import components.classes.SAMUCenterActions;
import components.interfaces.ActionExecutionCI;
import components.interfaces.ActionI;
import components.interfaces.ResponseI;
import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition;
import fr.sorbonne_u.cps.smartcity.interfaces.SAMUActionImplI;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfSAMURessources;

/**
 * La classe<code>SAMUproxyActionConnector</code> implemente le connecteur pour
 * l'interface {@code ActionExecutionCI}. Elle connecte un centre de SAMU avec
 * son proxy
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class SAMUproxyActionConnector extends AbstractConnector implements ActionExecutionCI {

	/**
	 * @see components.interfaces.ActionExecutionCI#executeAction(components.interfaces.ActionI,
	 *      java.io.Serializable[])
	 */
	@Override
	public ResponseI executeAction(ActionI a, Serializable[] params)
			throws RejectedExecutionException, AssertionError, InterruptedException, ExecutionException, Exception {
		assert a instanceof SAMUCenterActions;
		ResponseI result = Responses.FAILURE;
		switch ((SAMUCenterActions) a) {
		case SendMedic:
			result = sendMedic(params);
			break;
		case SendAmbulance:
			result = sendAmbulance(params);
			break;
		case MedicCall:
			result = medicCall(params);
			break;
		default:
			break;
		}
		return result;
	}

	/**
	 * Appelle la methode triggerIntervention sur le proxy avec la ressource MEDIC
	 * 
	 * @param params les parametres de la methode
	 * @return la reponse
	 * @throws Exception
	 */
	private ResponseI sendMedic(Serializable[] params) throws Exception {
		AbsolutePosition p = (AbsolutePosition) params[0];
		((SAMUActionImplI) this.offering).triggerIntervention(p, null, TypeOfSAMURessources.MEDIC);
		return Responses.SUCCESS;
	}

	/**
	 * Appelle la methode triggerIntervention sur le proxy avec la ressource
	 * AMBULANCE
	 * 
	 * @param params les parametres de la methode
	 * @return la reponse
	 * @throws Exception
	 */
	private ResponseI sendAmbulance(Serializable[] params) throws Exception {
		AbsolutePosition p = (AbsolutePosition) params[0];
		((SAMUActionImplI) this.offering).triggerIntervention(p, null, TypeOfSAMURessources.AMBULANCE);
		return Responses.SUCCESS;
	}

	/**
	 * Appelle la methode triggerIntervention sur le proxy avec la ressource
	 * TELEMEDIC
	 * 
	 * @param params les parametres de la methode
	 * @return la reponse
	 * @throws Exception
	 */
	private ResponseI medicCall(Serializable[] params) throws Exception {
		AbsolutePosition p = (AbsolutePosition) params[0];
		String personID = (String) params[1];
		((SAMUActionImplI) this.offering).triggerIntervention(p, personID, TypeOfSAMURessources.TELEMEDIC);
		return Responses.SUCCESS;
	}

}
