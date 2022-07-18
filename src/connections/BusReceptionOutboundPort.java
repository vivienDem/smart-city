package connections;

import components.interfaces.EventReceptionCI;
import event.interfaces.EventI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

/**
 * La classe <code>BusReceptionOutboundPort</code> implemente le port sortant
 * pour l'interface {@code EventReceptionCI}.
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class BusReceptionOutboundPort extends AbstractOutboundPort implements EventReceptionCI {
	private static final long serialVersionUID = 7234896801323080952L;

	public BusReceptionOutboundPort(ComponentI owner) throws Exception {
		super(EventReceptionCI.class, owner);
	}

	public BusReceptionOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, EventReceptionCI.class, owner);
	}

	/**
	 * @see components.interfaces.EventReceptionCI#receiveEvent(java.lang.String,
	 *      event.interfaces.EventI)
	 */
	@Override
	public void receiveEvent(String emitterURI, EventI e) throws Exception {
		((EventReceptionCI) this.getConnector()).receiveEvent(emitterURI, e);
	}

	/**
	 * @see components.interfaces.EventReceptionCI#receiveEvents(java.lang.String,
	 *      event.interfaces.EventI[])
	 */
	@Override
	public void receiveEvents(String emitterURI, EventI[] events) throws Exception {
		((EventReceptionCI) this.getConnector()).receiveEvents(emitterURI, events);
	}

}
