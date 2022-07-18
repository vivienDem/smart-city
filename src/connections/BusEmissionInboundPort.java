package connections;

import components.interfaces.EventEmissionCI;
import components.interfaces.EventEmissionImplI;
import event.interfaces.EventI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

/**
 * La classe <code> BusEmissionInboundPort </code> correspond au port entrant du
 * composant CEPBus. Elle offre l'interface EventEmissionCI
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class BusEmissionInboundPort extends AbstractInboundPort implements EventEmissionCI {
	private static final long serialVersionUID = 5684837901723419961L;
	private int indexOfPool;

	public BusEmissionInboundPort(String uri, ComponentI owner, int indexOfPool) throws Exception {
		super(uri, EventEmissionCI.class, owner);
		assert owner instanceof EventEmissionImplI;
		this.indexOfPool = indexOfPool;
	}

	/**
	 * @see components.interfaces.EventEmissionCI#sendEvent(java.lang.String,
	 *      event.interfaces.EventI)
	 */
	@Override
	public void sendEvent(String emitterURI, EventI event) throws Exception {
		this.getOwner().runTask(indexOfPool, o -> {
			try {
				((EventEmissionImplI) o).sendEvent(emitterURI, event);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * @see components.interfaces.EventEmissionCI#sendEvents(java.lang.String,
	 *      event.interfaces.EventI[])
	 */
	@Override
	public void sendEvents(String emitterURI, EventI[] events) throws Exception {
		this.getOwner().runTask(indexOfPool, o -> {
			try {
				((EventEmissionImplI) o).sendEvents(emitterURI, events);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

}
