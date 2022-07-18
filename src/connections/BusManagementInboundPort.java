package connections;

import components.interfaces.CEPBusManagementCI;
import components.interfaces.CEPBusManagementImplI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

/**
 * La classe <code> BusManagementInboundPort </code> correspond au port entrant
 * du composant CEPBus permettant pour un composant d'interagir avec le bus
 * (s'enregistrer, s'abonner, ...) Il offre l'interface CEPBusManagementCI
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class BusManagementInboundPort extends AbstractInboundPort implements CEPBusManagementCI {
	private static final long serialVersionUID = 120319688041770445L;
	private int indexOfPool;

	public BusManagementInboundPort(String managementURI, ComponentI owner, int indexOfPool) throws Exception {
		super(managementURI, CEPBusManagementCI.class, owner);
		assert owner instanceof CEPBusManagementImplI;
		this.indexOfPool = indexOfPool;
	}

	/**
	 * @see components.interfaces.CEPBusManagementCI#registerEmitter(java.lang.String)
	 */
	@Override
	public String registerEmitter(String uri) throws Exception {
		String result = this.getOwner().handleRequest(indexOfPool, o -> {
			return ((CEPBusManagementImplI) o).registerEmitter(uri);
		});
		return result;
	}

	/**
	 * @see components.interfaces.CEPBusManagementCI#unregisterEmitter(java.lang.String)
	 */
	@Override
	public void unregisterEmitter(String uri) throws Exception {
		this.getOwner().runTask(indexOfPool, o -> {
			try {
				((CEPBusManagementImplI) o).unregisterEmitter(uri);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * @see components.interfaces.CEPBusManagementCI#registerCorrelator(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public String registerCorrelator(String uri, String inboundPortURI) throws Exception {
		String result = this.getOwner().handleRequest(indexOfPool, o -> {
			return ((CEPBusManagementImplI) o).registerCorrelator(uri, inboundPortURI);
		});
		return result;
	}

	/**
	 * @see components.interfaces.CEPBusManagementCI#unregisterCorrelator(java.lang.String)
	 */
	@Override
	public void unregisterCorrelator(String uri) throws Exception {
		this.getOwner().runTask(indexOfPool, o -> {
			try {
				((CEPBusManagementImplI) o).unregisterCorrelator(uri);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * @see components.interfaces.CEPBusManagementImplI#registerExecutor(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public void registerExecutor(String uri, String inboundPortURI) throws Exception {
		this.getOwner().runTask(indexOfPool, o -> {
			try {
				((CEPBusManagementImplI) o).registerExecutor(uri, inboundPortURI);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * @see components.interfaces.CEPBusManagementImplI#unregisterExecutor(java.lang.String)
	 */
	@Override
	public void unregisterExecutor(String uri) throws Exception {
		this.getOwner().runTask(indexOfPool, o -> {
			try {
				((CEPBusManagementImplI) o).unregisterExecutor(uri);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * @see components.interfaces.CEPBusManagementImplI#subscribe(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public void subscribe(String subscriberURI, String emitterURI) throws Exception {
		this.getOwner().runTask(indexOfPool, o -> {
			try {
				((CEPBusManagementImplI) o).subscribe(subscriberURI, emitterURI);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

	}

	/**
	 * @see components.interfaces.CEPBusManagementImplI#unsubscribe(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public void unsubscribe(String subscriberURI, String emitterURI) throws Exception {
		this.getOwner().runTask(indexOfPool, o -> {
			try {
				((CEPBusManagementImplI) o).unsubscribe(subscriberURI, emitterURI);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * @see components.interfaces.CEPBusManagementImplI#getExecutorInboundPortURI(java.lang.String)
	 */
	@Override
	public String getExecutorInboundPortURI(String uri) throws Exception {
		String result = this.getOwner().handleRequest(indexOfPool, o -> {
			return ((CEPBusManagementImplI) o).getExecutorInboundPortURI(uri);
		});
		return result;
	}

}
