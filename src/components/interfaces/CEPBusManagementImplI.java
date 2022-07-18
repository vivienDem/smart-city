package components.interfaces;
/**
 * Interface du composant bus (Impl)
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public interface CEPBusManagementImplI {
	/**
	 * Enregistre un emetteur d'evenements au bus
	 * 
	 * @param uri l'uri de l'emetteur souhaitant s'enregistrer
	 * @return l'uri du port entrant du bus offrant l'interface EventEmissionCI
	 * @throws Exception
	 */
	String registerEmitter(String uri) throws Exception;

	/**
	 * Desenregistre un emetteur d'evenements du bus
	 * 
	 * @param uri l'uri de l'emetteur souhaitant se desenregistrer du bus
	 * @throws Exception
	 */
	void unregisterEmitter(String uri) throws Exception;

	/**
	 * Enregistre un correlateur au bus
	 * 
	 * @param uri            l'uri du correlateur souhait s'enregistrer
	 * @param inboundPortURI l'uri de son port entrant offrant l'interface
	 *                       EventReceptionCI
	 * @return l'uri du port entrant du bus offrant l'interface EventEmissionCI
	 * @throws Exception
	 */
	String registerCorrelator(String uri, String inboundPortURI) throws Exception;

	/**
	 * Desenregistre un correlateur du bus
	 * 
	 * @param uri l'uri du correlateur
	 * @throws Exception
	 */
	void unregisterCorrelator(String uri) throws Exception;

	/**
	 * Enregistre un executeur d'actions au bus
	 * 
	 * @param uri            l'uri de l'executeur d'actions
	 * @param inboundPortURI l'uri du port entrant de l'executeur offrant
	 *                       l'interface ActionExecutionCI
	 * @throws Exception
	 */
	void registerExecutor(String uri, String inboundPortURI) throws Exception;

	/**
	 * Desenregistre un executeur d'actions du bus
	 * 
	 * @param uri l'uri de l'executeur d'actions
	 * @throws Exception
	 */
	void unregisterExecutor(String uri) throws Exception;

	/**
	 * Abonne un correlateur aux evenements emis par un emetteur
	 * 
	 * @param subscriberURI l'uri du correlateur
	 * @param emitterURI    l'uri de l'emetteur
	 * @throws Exception
	 */
	void subscribe(String subscriberURI, String emitterURI) throws Exception;

	/**
	 * Desabonne un correlateur aux evenements emis par un emetteur
	 * 
	 * @param subscriberURI l'uri du correlateur
	 * @param emitterURI    l'uri de l'emetteur
	 * @throws Exception
	 */
	void unsubscribe(String subscriberURI, String emitterURI) throws Exception;

	/**
	 * Accesseur sur l'uri du port entrant d'un executeur d'actions
	 * 
	 * @param uri l'uri de l'executeur d'actions
	 * @return l'uri du port entrant de l'executeur d'actions
	 * @throws Exception
	 */
	String getExecutorInboundPortURI(String uri) throws Exception;
}
