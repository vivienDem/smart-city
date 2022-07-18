package connections;

import components.interfaces.CEPBusManagementCI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

/**
 * La classe <code>CorrelatorBusManagementOutboundPort</code> implemente le port
 * sortant pour l'interface {@code CEPBusManagementCI}.
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class CorrelatorBusManagementOutboundPort extends AbstractOutboundPort implements CEPBusManagementCI {
	private static final long serialVersionUID = -850101104839824873L;

	public CorrelatorBusManagementOutboundPort(ComponentI owner) throws Exception {
		super(CEPBusManagementCI.class, owner);
	}

	/**
	 * @see components.interfaces.CEPBusManagementCI#registerEmitter(java.lang.String)
	 */
	@Override
	public String registerEmitter(String uri) throws Exception {
		return ((CEPBusManagementCI) this.getConnector()).registerEmitter(uri);
	}

	/**
	 * @see components.interfaces.CEPBusManagementCI#unregisterEmitter(java.lang.String)
	 */
	@Override
	public void unregisterEmitter(String uri) throws Exception {
		((CEPBusManagementCI) this.getConnector()).unregisterEmitter(uri);
	}

	/**
	 * @see components.interfaces.CEPBusManagementCI#registerCorrelator(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public String registerCorrelator(String uri, String inboundPortURI) throws Exception {
		return ((CEPBusManagementCI) this.getConnector()).registerCorrelator(uri, inboundPortURI);
	}

	/**
	 * @see components.interfaces.CEPBusManagementCI#unregisterCorrelator(java.lang.String)
	 */
	@Override
	public void unregisterCorrelator(String uri) throws Exception {
		((CEPBusManagementCI) this.getConnector()).unregisterCorrelator(uri);
	}

	/**
	 * @see components.interfaces.CEPBusManagementCI#registerExecutor(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public void registerExecutor(String uri, String inboundPortURI) throws Exception {
		((CEPBusManagementCI) this.getConnector()).registerExecutor(uri, inboundPortURI);
	}

	/**
	 * @see components.interfaces.CEPBusManagementCI#unregisterExecutor(java.lang.String)
	 */
	@Override
	public void unregisterExecutor(String uri) throws Exception {
		((CEPBusManagementCI) this.getConnector()).unregisterExecutor(uri);
	}

	/**
	 * @see components.interfaces.CEPBusManagementCI#subscribe(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public void subscribe(String subscriberURI, String emitterURI) throws Exception {
		((CEPBusManagementCI) this.getConnector()).subscribe(subscriberURI, emitterURI);
	}

	/**
	 * @see components.interfaces.CEPBusManagementCI#unsubscribe(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public void unsubscribe(String subscriberURI, String emitterURI) throws Exception {
		((CEPBusManagementCI) this.getConnector()).unsubscribe(subscriberURI, emitterURI);
	}

	/**
	 * @see components.interfaces.CEPBusManagementCI#getExecutorInboundPortURI(java.lang.String)
	 */
	@Override
	public String getExecutorInboundPortURI(String uri) throws Exception {
		return ((CEPBusManagementCI) this.getConnector()).getExecutorInboundPortURI(uri);
	}

}
