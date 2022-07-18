package connections;

import components.interfaces.EventReceptionCI;
import event.interfaces.EventI;
import fr.sorbonne_u.components.connectors.AbstractConnector;

/**
 * La classe<code>ReceptionConnector</code> implemente le connecteur pour
 * l'interface {@code EventReceptionCI}. Elle connecte un correlateur et un bus
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class ReceptionConnector extends AbstractConnector implements EventReceptionCI {

	/**
	 * @see components.interfaces.EventReceptionCI#receiveEvent(java.lang.String,
	 *      event.interfaces.EventI)
	 */
	@Override
	public void receiveEvent(String emitterURI, EventI e) throws Exception {
		((EventReceptionCI) this.offering).receiveEvent(emitterURI, e);
	}

	/**
	 * @see components.interfaces.EventReceptionCI#receiveEvents(java.lang.String,
	 *      event.interfaces.EventI[])
	 */
	@Override
	public void receiveEvents(String emitterURI, EventI[] events) throws Exception {
		((EventReceptionCI) this.offering).receiveEvents(emitterURI, events);
	}

}
