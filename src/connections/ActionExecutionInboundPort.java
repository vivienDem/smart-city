package connections;

import java.io.Serializable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RejectedExecutionException;

import components.interfaces.ActionExecutionCI;
import components.interfaces.ActionExecutionImplI;
import components.interfaces.ActionI;
import components.interfaces.ResponseI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

/**
 * La classe <code>ActionExecutionInboundPort</code> implemente le port entrant
 * pour l'interface {@code ActionExecutionCI}.
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class ActionExecutionInboundPort extends AbstractInboundPort implements ActionExecutionCI {
	private static final long serialVersionUID = 1L;

	public ActionExecutionInboundPort(ComponentI owner) throws Exception {
		super(ActionExecutionCI.class, owner);
		assert owner instanceof ActionExecutionImplI;
	}

	/**
	 * @see components.interfaces.ActionExecutionCI#executeAction(components.interfaces.ActionI,
	 *      java.io.Serializable[])
	 */
	@Override
	public ResponseI executeAction(ActionI a, Serializable[] params)
			throws RejectedExecutionException, AssertionError, InterruptedException, ExecutionException, Exception {
		ResponseI result = this.getOwner().handleRequest(o -> {
			return ((ActionExecutionImplI) o).executeAction(a, params);
		});
		return result;
	}

}
