package connections;

import components.interfaces.EventEmissionImplI;
import components.interfaces.EventReceptionCI;
import components.interfaces.EventReceptionImplI;
import event.interfaces.EventI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
/**
 * La classe <code>BusReceptionInboundPort</code> implemente le port entrant
 * pour l'interface {@code EventReceptionCI}.
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class BusReceptionInboundPort extends AbstractInboundPort implements EventReceptionCI {
	private static final long serialVersionUID = -629785624646640913L;
	private int indexOfPool;

	public BusReceptionInboundPort(String uri, ComponentI owner, int indexOfPool) throws Exception {
		super(uri, EventReceptionCI.class, owner);
		assert owner instanceof EventEmissionImplI;
		this.indexOfPool = indexOfPool;
	}
	@Override
	public void receiveEvent(String emitterURI, EventI event) throws Exception {
		this.getOwner().runTask(indexOfPool, o -> {
			try {
				((EventReceptionImplI) o).receiveEvent(emitterURI, event);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

	}

	@Override
	public void receiveEvents(String emitterURI, EventI[] events) throws Exception {
		this.getOwner().runTask(indexOfPool, o -> {
			try {
				((EventReceptionImplI) o).receiveEvents(emitterURI, events);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

	}

}
