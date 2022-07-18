package connections;

import components.interfaces.EventEmissionCI;
import event.interfaces.EventI;
import fr.sorbonne_u.components.connectors.AbstractConnector;

/**
 * La classe<code>BusEmissionConnector</code> implemente le connecteur pour
 * l'interface {@code EventEmissionCI}. Elle connecte un emetteur (ou correlateur) et un
 * bus
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class BusEmissionConnector extends AbstractConnector implements EventEmissionCI {

	/**
	 * @see components.interfaces.EventEmissionCI#sendEvent(java.lang.String, event.interfaces.EventI)
	 */
	@Override
	public void sendEvent(String emitterURI, EventI event) throws Exception {
		((EventEmissionCI)this.offering).sendEvent(emitterURI, event);
	}

	/**
	 * @see components.interfaces.EventEmissionCI#sendEvents(java.lang.String, event.interfaces.EventI[])
	 */
	@Override
	public void sendEvents(String emitterURI, EventI[] events) throws Exception {
		((EventEmissionCI)this.offering).sendEvents(emitterURI, events);
	}

}
