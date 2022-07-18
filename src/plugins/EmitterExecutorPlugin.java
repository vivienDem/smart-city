package plugins;

import components.interfaces.ActionExecutionCI;
import components.interfaces.CEPBusManagementCI;
import components.interfaces.EventEmissionCI;
import connections.ActionExecutionInboundPort;
import connections.BusEmissionConnector;
import connections.BusManagementConnector;
import connections.BusManagementOutboundPort;
import connections.EmissionOutboundPort;
import fr.sorbonne_u.components.AbstractPlugin;
import fr.sorbonne_u.components.ComponentI;

/**
 * Plugin relatif aux emetteurs/executeurs d'actions
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class EmitterExecutorPlugin extends AbstractPlugin {
	private static final long serialVersionUID = -1456188270718671053L;
	private ActionExecutionInboundPort actionExecutionInboundPort;// utilise par le correlateur
	private EmissionOutboundPort eventEmissionOutboundPort; // utilise par nous pour rediriger vers le bus
	private BusManagementOutboundPort busManagementOutboundPort; // utilise par nous pour le bus
	private String busManagementURI;
	private String ownerId;

	public EmitterExecutorPlugin(String busManagementURI, String ownerId) {
		this.busManagementURI = busManagementURI;
		this.ownerId = ownerId;
	}

	/**
	 * @see fr.sorbonne_u.components.PluginI#installOn(fr.sorbonne_u.components.ComponentI)
	 */
	@Override
	public void installOn(ComponentI owner) throws Exception {
		super.installOn(owner);
		this.addRequiredInterface(EventEmissionCI.class);
		this.addRequiredInterface(CEPBusManagementCI.class);
		this.addOfferedInterface(ActionExecutionCI.class);
	}

	/**
	 * @see fr.sorbonne_u.components.PluginI#initialise()
	 */
	@Override
	public void initialise() throws Exception {
		this.actionExecutionInboundPort = new ActionExecutionInboundPort(this.getOwner());
		this.actionExecutionInboundPort.publishPort();

		this.eventEmissionOutboundPort = new EmissionOutboundPort(this.getOwner());
		this.eventEmissionOutboundPort.publishPort();

		this.busManagementOutboundPort = new BusManagementOutboundPort(this.getOwner());
		this.busManagementOutboundPort.publishPort();

		this.getOwner().doPortConnection(busManagementOutboundPort.getPortURI(), this.busManagementURI,
				BusManagementConnector.class.getCanonicalName());
		String busInboundPort = busManagementOutboundPort.registerEmitter(ownerId);
		busManagementOutboundPort.registerExecutor(ownerId, actionExecutionInboundPort.getPortURI());
		this.getOwner().doPortConnection(this.eventEmissionOutboundPort.getPortURI(), busInboundPort,
				BusEmissionConnector.class.getCanonicalName());
		super.initialise();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 */
	@Override
	public void finalise() throws Exception {
		busManagementOutboundPort.unregisterEmitter(ownerId);
		this.getOwner().doPortDisconnection(eventEmissionOutboundPort.getPortURI());
		this.getOwner().doPortDisconnection(busManagementOutboundPort.getPortURI());
	}

	/**
	 * @see fr.sorbonne_u.components.PluginI#uninstall()
	 */
	@Override
	public void uninstall() throws Exception {
		this.eventEmissionOutboundPort.unpublishPort();
		this.busManagementOutboundPort.unpublishPort();
		this.actionExecutionInboundPort.unpublishPort();
		this.removeRequiredInterface(ActionExecutionCI.class);
		this.removeRequiredInterface(EventEmissionCI.class);
		this.removeRequiredInterface(CEPBusManagementCI.class);
		this.removeOfferedInterface(ActionExecutionCI.class);

	}

	public EmissionOutboundPort getEmissionPort() {
		return this.eventEmissionOutboundPort;
	}

}
