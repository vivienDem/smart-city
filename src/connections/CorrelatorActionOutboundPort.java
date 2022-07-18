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
 * La classe <code>CorrelatorActionOutboundPort</code> implemente le port
 * sortant pour l'interface {@code ActionExecutionCI}.
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class CorrelatorActionOutboundPort extends AbstractOutboundPort implements ActionExecutionCI {
	private static final long serialVersionUID = 6389134041680490656L;

	public CorrelatorActionOutboundPort(ComponentI owner) throws Exception {
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
