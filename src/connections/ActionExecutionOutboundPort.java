package connections;

import java.io.Serializable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RejectedExecutionException;

import components.interfaces.ActionExecutionCI;
import components.interfaces.ActionI;
import components.interfaces.ResponseI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

/**
 * La classe <code>ActionExecutionOutboundPort</code> implemente le port sortant
 * pour l'interface {@code ActionExecutionCI}.
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class ActionExecutionOutboundPort extends AbstractOutboundPort implements ActionExecutionCI {
	private static final long serialVersionUID = -873293255993365690L;

	public ActionExecutionOutboundPort(ComponentI owner) throws Exception {
		super(ActionExecutionCI.class, owner);
	}

	/**
	 * @see components.interfaces.ActionExecutionCI#executeAction(components.interfaces.ActionI,
	 *      java.io.Serializable[])
	 */
	@Override
	public ResponseI executeAction(ActionI a, Serializable[] params)
			throws RejectedExecutionException, AssertionError, InterruptedException, ExecutionException, Exception {
		return ((ActionExecutionCI) this.getConnector()).executeAction(a, params);
	}

}
