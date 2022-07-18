package plugins;

import java.util.List;
import java.util.Vector;

import components.interfaces.ActionExecutionCI;
import components.interfaces.EventEmissionCI;
import components.interfaces.EventReceptionCI;
import connections.ActionConnector;
import connections.BusEmissionConnector;
import connections.CorrelatorActionOutboundPort;
import connections.CorrelatorBusManagementOutboundPort;
import connections.CorrelatorEmissionOutboundPort;
import connections.CorrelatorReceptionInboundPort;
import fr.sorbonne_u.components.AbstractPlugin;
import fr.sorbonne_u.components.ComponentI;

/**
 * Plugin relatif aux correlateurs
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class CorrelatorPlugin extends AbstractPlugin {
	private static final long serialVersionUID = 5391568017815530315L;
	private String ownerId;

	private CorrelatorEmissionOutboundPort correlatorEmissionOutboundPort;
	private CorrelatorBusManagementOutboundPort correlatorBusManagementOutboundPort;
	private List<String> executorsURI;
	private List<String> emittersURI;
	private Vector<CorrelatorActionOutboundPort> correlatorActionOutboundPorts;
	private String receptionURI;
	private CorrelatorReceptionInboundPort correlatorReceptionInboundPort;
	private int indexOfeventDeliveryPool;

	public CorrelatorPlugin(String ownerId, CorrelatorBusManagementOutboundPort correlatorBusManagementOutboundPort,
			String receptionURI, int indexOfeventDeliveryPool, List<String> executorsURI, List<String> emittersURI) {
		this.correlatorBusManagementOutboundPort = correlatorBusManagementOutboundPort; // doPortConnection deja faite
																						// dans start() du correlateur
		this.ownerId = ownerId;
		this.receptionURI = receptionURI;
		this.executorsURI = executorsURI;
		this.emittersURI = emittersURI;
		this.indexOfeventDeliveryPool = indexOfeventDeliveryPool;

		this.correlatorActionOutboundPorts = new Vector<CorrelatorActionOutboundPort>();
	}

	/**
	 * @see fr.sorbonne_u.components.PluginI#installOn(fr.sorbonne_u.components.ComponentI)
	 */
	@Override
	public void installOn(ComponentI owner) throws Exception {
		super.installOn(owner);
		this.addOfferedInterface(EventReceptionCI.class);
		this.addRequiredInterface(EventEmissionCI.class);
		this.addRequiredInterface(ActionExecutionCI.class);
	}

	/**
	 * @see fr.sorbonne_u.components.PluginI#initialise()
	 */
	@Override
	public void initialise() throws Exception {

		this.correlatorReceptionInboundPort = new CorrelatorReceptionInboundPort(receptionURI, this.getOwner(),
				indexOfeventDeliveryPool);
		this.correlatorReceptionInboundPort.publishPort();

		String busEmissionInboundPort = correlatorBusManagementOutboundPort.registerCorrelator(ownerId,
				correlatorReceptionInboundPort.getPortURI());

		this.correlatorEmissionOutboundPort = new CorrelatorEmissionOutboundPort(this.getOwner());
		this.correlatorEmissionOutboundPort.publishPort();

		this.getOwner().doPortConnection(correlatorEmissionOutboundPort.getPortURI(), busEmissionInboundPort,
				BusEmissionConnector.class.getCanonicalName());

		String inboundURI;
		for (String executor : executorsURI) {
			while ((inboundURI = correlatorBusManagementOutboundPort.getExecutorInboundPortURI(executor)) == null) {
				Thread.sleep(100);
			}
			CorrelatorActionOutboundPort correlatorActionOutboundPort = new CorrelatorActionOutboundPort(
					this.getOwner());
			correlatorActionOutboundPort.publishPort();
			this.getOwner().doPortConnection(correlatorActionOutboundPort.getPortURI(), inboundURI,
					ActionConnector.class.getCanonicalName());
			this.correlatorActionOutboundPorts.add(correlatorActionOutboundPort);
		}

		for (String emitterURI : emittersURI) {
			this.correlatorBusManagementOutboundPort.subscribe(ownerId, emitterURI);
		}

		super.initialise();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 */
	@Override
	public void finalise() throws Exception {
		for (String emitter : this.emittersURI) {
			this.correlatorBusManagementOutboundPort.unsubscribe(ownerId, emitter);
		}
		this.getOwner().doPortDisconnection(this.correlatorBusManagementOutboundPort.getPortURI());
		this.getOwner().doPortDisconnection(this.correlatorEmissionOutboundPort.getPortURI());
		for (CorrelatorActionOutboundPort correlatorActionOutboundPort : correlatorActionOutboundPorts) {
			this.getOwner().doPortDisconnection(correlatorActionOutboundPort.getPortURI());
		}
	}

	/**
	 * @see fr.sorbonne_u.components.PluginI#uninstall()
	 */
	@Override
	public void uninstall() throws Exception {
		this.correlatorBusManagementOutboundPort.unpublishPort();
		this.correlatorEmissionOutboundPort.unpublishPort();
		for (CorrelatorActionOutboundPort correlatorActionOutboundPort : correlatorActionOutboundPorts) {
			correlatorActionOutboundPort.unpublishPort();
		}
		this.correlatorReceptionInboundPort.unpublishPort();
		this.removeOfferedInterface(EventReceptionCI.class);
		this.removeRequiredInterface(EventEmissionCI.class);
		this.removeRequiredInterface(ActionExecutionCI.class);

	}

	/**
	 * Accesseur sur la liste des ports sortants d'actions pour communiquer avec les
	 * executeurs
	 * 
	 * @return la liste des ports sortants d'actions
	 */
	public Vector<CorrelatorActionOutboundPort> getCorrelatorActionOutboundPorts() {
		return correlatorActionOutboundPorts;
	}

}
