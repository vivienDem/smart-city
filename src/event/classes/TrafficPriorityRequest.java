package event.classes;

import java.io.Serializable;

import event.interfaces.AtomicEventI;
import fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition;
import fr.sorbonne_u.cps.smartcity.grid.IntersectionPosition;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfTrafficLightPriority;

/**
 * Evenement : Requete de priorite sur un feu
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class TrafficPriorityRequest extends AbstractEvent implements AtomicEventI {
	private static final long serialVersionUID = 8880408892542990053L;

	/**
	 * Cree l'evenement de demande de priorite sur un feu de circulation
	 * 
	 * @param position         l'intersection concernee
	 * @param p                la priorit�
	 * @param vehicleId        URI du vehicule
	 * @param finalDestination la destination finale
	 */
	public TrafficPriorityRequest(IntersectionPosition position, TypeOfTrafficLightPriority p, String vehicleId,
			AbsolutePosition finalDestination) {
		properties.put("area", position);
		properties.put("priority", p);
		properties.put("vehicle", vehicleId);
		properties.put("finalDestination", finalDestination);
	}

	/**
	 * Cree l'evenement de demande de priorite sur un feu de circulation
	 * 
	 * @param position  l'intersection concernee
	 * @param p         la priorit�
	 * @param vehicleId URI du vehicule
	 */
	public TrafficPriorityRequest(IntersectionPosition position, TypeOfTrafficLightPriority p, String vehicleId) {
		properties.put("area", position);
		properties.put("priority", p);
		properties.put("vehicle", vehicleId);
	}

	/**
	 * Cree l'evenement de demande de priorite sur un feu de circulation
	 * 
	 * @param position         l'intersection concernee
	 * @param p                la priorit�
	 * @param vehicleId        URI du vehicule
	 * @param finalDestination la destination finale
	 */
	public TrafficPriorityRequest(Serializable position, Serializable p, Serializable vehicleId,
			Serializable finalDestination) {
		this((IntersectionPosition) position, (TypeOfTrafficLightPriority) p, (String) vehicleId,
				(AbsolutePosition) finalDestination);
	}

	/**
	 * Cree l'evenement de demande de priorite sur un feu de circulation
	 * 
	 * @param position  l'intersection concernee
	 * @param p         la priorit�
	 * @param vehicleId URI du vehicule
	 */
	public TrafficPriorityRequest(Serializable position, Serializable p, Serializable vehicleId) {
		this((IntersectionPosition) position, (TypeOfTrafficLightPriority) p, (String) vehicleId);
	}

	/**
	 * @see event.interfaces.AtomicEventI#putProperty(java.lang.String,
	 *      java.io.Serializable)
	 */
	@Override
	public Serializable putProperty(String name, Serializable value) {
		properties.put(name, value);
		return value;
	}

	@Override
	public void removeProperty(String name) {
		properties.remove(name);

	}

}
