package event.interfaces;

import java.util.ArrayList;

/**
 * Interface definissant un evenement complexe
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public interface ComplexEventI extends EventI {
	/**
	 * Accesseur sur les evenements correles a l'evenement complexe
	 * 
	 * @return la liste des evenements correles
	 */
	ArrayList<EventI> getCorrelatedEvents();
}
