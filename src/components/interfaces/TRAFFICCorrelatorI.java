package components.interfaces;

import java.io.Serializable;

import fr.sorbonne_u.cps.smartcity.grid.IntersectionPosition;

/**
 * Interface definissant un correlateur relatif au feu de circulation
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public interface TRAFFICCorrelatorI extends CorrelatorI {

	/**
	 * Change la priorite d'un feu de circulation
	 * 
	 * @param position le feu concerne
	 * @param priority la priorite demandee
	 * @throws Exception
	 */
	void changePriorityOfIntersection(Serializable position, Serializable priority) throws Exception;

	/**
	 * Test indiquant si un feu se trouve avant la destination finale d'un vehicule
	 * 
	 * @param position         la position du vehicule
	 * @param direction        la direction
	 * @param finalDestination la destination finale
	 * @return la position du feu s'il est trouve, null sinon
	 */
	IntersectionPosition isBefore(Serializable position, Serializable direction, Serializable finalDestination);

	/**
	 * Modifie la priorite de l'intersection au mode normal
	 * 
	 * @param position la position de l'intersection
	 * @throws Exception
	 */
	void switchIntersectionPriorityToNormal(Serializable position) throws Exception;

	/**
	 * Redirige une requete de priorite
	 * 
	 * @param position         la position
	 * @param direction        la direction
	 * @param vehicleId        URI du vehicule
	 * @param priority         la priorite demandee
	 * @param finalDestination la destination finale
	 * @throws Exception
	 */
	void redirectTrafficPriorityRequest(Serializable position, Serializable direction, Serializable vehicleId,
			Serializable priority, Serializable finalDestination) throws Exception;

}
