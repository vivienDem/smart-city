package connections;

import components.interfaces.EventReceptionImplI;
import components.interfaces.EventReceptionCI;
import event.interfaces.EventI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

/**
 * La classe <code>CorrelatorReceptionInboundPort</code> implemente le port
 * entrant pour l'interface {@code EventReceptionCI}.
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class CorrelatorReceptionInboundPort extends AbstractInboundPort implements EventReceptionCI {
	private static final long serialVersionUID = 2298290273272500458L;
	private int indexOfPool;

	public CorrelatorReceptionInboundPort(String receptionURI, ComponentI owner, int indexOfPool) throws Exception {
		super(receptionURI, EventReceptionCI.class, owner);
		assert owner instanceof EventReceptionImplI;
		this.indexOfPool = indexOfPool;
	}

	/**
	 * @see components.interfaces.EventReceptionCI#receiveEvent(java.lang.String,
	 *      event.interfaces.EventI)
	 */
	@Override
	public void receiveEvent(String emitterURI, EventI e) throws Exception {
		this.getOwner().runTask(indexOfPool, o -> {
			try {
				((EventReceptionImplI) o).receiveEvent(emitterURI, e);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
	}

	/**
	 * @see components.interfaces.EventReceptionCI#receiveEvents(java.lang.String,
	 *      event.interfaces.EventI[])
	 */
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
