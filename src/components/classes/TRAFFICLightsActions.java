package components.classes;

import components.interfaces.ActionI;
/**
 * L'enumeration <code> TRAFFICLightsActions </code> liste les actions que peut effectuer un feu de circulation
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public enum TRAFFICLightsActions implements ActionI{
	RequestPriority, ReturnToNormal;
}
