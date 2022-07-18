package connections;

import components.interfaces.CEPBusManagementCI;
import fr.sorbonne_u.components.connectors.AbstractConnector;

/**
 * La classe<code>BusManagementConnector</code> implemente le connecteur pour
 * l'interface {@code CEPBusManagementCI}. Elle connecte un composant
 * (emetteur/correlateur/executeur) et un bus
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class BusManagementConnector extends AbstractConnector implements CEPBusManagementCI {

	/**
	 * @see components.interfaces.CEPBusManagementCI#registerEmitter(java.lang.String)
	 */
	@Override
	public String registerEmitter(String uri) throws Exception {
		return ((CEPBusManagementCI) this.offering).registerEmitter(uri);
	}

	/**
	 * @see components.interfaces.CEPBusManagementCI#unregisterEmitter(java.lang.String)
	 */
	@Override
	public void unregisterEmitter(String uri) throws Exception {
		((CEPBusManagementCI) this.offering).unregisterEmitter(uri);
	}

	/**
	 * @see components.interfaces.CEPBusManagementCI#registerCorrelator(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public String registerCorrelator(String uri, String inboundPortURI) throws Exception {
		return ((CEPBusManagementCI) this.offering).registerCorrelator(uri, inboundPortURI);
	}

	/**
	 * @see components.interfaces.CEPBusManagementCI#unregisterCorrelator(java.lang.String)
	 */
	@Override
	public void unregisterCorrelator(String uri) throws Exception {
		((CEPBusManagementCI) this.offering).unregisterCorrelator(uri);
	}

	/**
	 * @see components.interfaces.CEPBusManagementCI#registerExecutor(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public void registerExecutor(String uri, String inboundPortURI) throws Exception {
		((CEPBusManagementCI) this.offering).registerExecutor(uri, inboundPortURI);
	}

	/**
	 * @see components.interfaces.CEPBusManagementCI#unregisterExecutor(java.lang.String)
	 */
	@Override
	public void unregisterExecutor(String uri) throws Exception {
		((CEPBusManagementCI) this.offering).unregisterExecutor(uri);
	}

	/**
	 * @see components.interfaces.CEPBusManagementCI#subscribe(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public void subscribe(String subscriberURI, String emitterURI) throws Exception {
		((CEPBusManagementCI) this.offering).subscribe(subscriberURI, emitterURI);
	}

	/**
	 * @see components.interfaces.CEPBusManagementCI#unsubscribe(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public void unsubscribe(String subscriberURI, String emitterURI) throws Exception {
		((CEPBusManagementCI) this.offering).unsubscribe(subscriberURI, emitterURI);
	}

	/**
	 * @see components.interfaces.CEPBusManagementCI#getExecutorInboundPortURI(java.lang.String)
	 */
	@Override
	public String getExecutorInboundPortURI(String uri) throws Exception {
		return ((CEPBusManagementCI) this.offering).getExecutorInboundPortURI(uri);
	}

}
