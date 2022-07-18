package connections;

import java.io.Serializable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RejectedExecutionException;

import components.interfaces.ActionExecutionCI;
import components.interfaces.ActionI;
import components.interfaces.ResponseI;
import fr.sorbonne_u.components.connectors.AbstractConnector;

/**
 * La classe<code>ActionConnector</code> implemente le connecteur pour
 * l'interface {@code ActionExecutionCI}. Elle connecte un correlateur et un
 * executeur
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class ActionConnector extends AbstractConnector implements ActionExecutionCI {

	/**
	 * @see components.interfaces.ActionExecutionCI#executeAction(components.interfaces.ActionI,
	 *      java.io.Serializable[])
	 */
	@Override
	public ResponseI executeAction(ActionI a, Serializable[] params)
			throws RejectedExecutionException, AssertionError, InterruptedException, ExecutionException, Exception {
		return ((ActionExecutionCI) this.offering).executeAction(a, params);
	}

}
