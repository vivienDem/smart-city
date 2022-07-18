package connections;

import components.interfaces.EventEmissionCI;
import event.interfaces.EventI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

/**
 * La classe <code> BusEmissionOutboundPort </code> correspond au port entrant du
 * composant CEPBus. Elle offre l'interface EventEmissionCI
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class BusEmissionOutboundPort extends AbstractOutboundPort implements EventEmissionCI {
	private static final long serialVersionUID = 576613591821806144L;

	public BusEmissionOutboundPort(ComponentI owner) throws Exception {
		super(EventEmissionCI.class, owner);
	}

	public BusEmissionOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, EventEmissionCI.class, owner);
	}

	@Override
	public void sendEvent(String emitterURI, EventI event) throws Exception {
		((EventEmissionCI) this.getConnector()).sendEvent(emitterURI, event);

	}

	@Override
	public void sendEvents(String emitterURI, EventI[] events) throws Exception {
		((EventEmissionCI) this.getConnector()).sendEvents(emitterURI, events);

	}

}
