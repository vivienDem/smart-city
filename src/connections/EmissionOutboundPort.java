package connections;

import components.interfaces.EventEmissionCI;
import event.interfaces.EventI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

/**
 * La classe <code>SAMUEmissionOutboundPort</code> implemente le port sortant
 * pour l'interface {@code EventEmissionCI}.
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class EmissionOutboundPort extends AbstractOutboundPort implements EventEmissionCI {
	private static final long serialVersionUID = -6752902927741807930L;

	public EmissionOutboundPort(ComponentI owner) throws Exception {
		super(EventEmissionCI.class, owner);
	}

	/**
	 * @see components.interfaces.EventEmissionCI#sendEvent(java.lang.String,
	 *      event.interfaces.EventI)
	 */
	@Override
	public void sendEvent(String emitterURI, EventI event) throws Exception {
		((EventEmissionCI) this.getConnector()).sendEvent(emitterURI, event);
	}

	/**
	 * @see components.interfaces.EventEmissionCI#sendEvents(java.lang.String,
	 *      event.interfaces.EventI[])
	 */
	@Override
	public void sendEvents(String emitterURI, EventI[] events) throws Exception {
		((EventEmissionCI) this.getConnector()).sendEvents(emitterURI, events);
	}

}
