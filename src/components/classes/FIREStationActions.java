package components.classes;

import components.interfaces.ActionI;
/**
 * <code>FIREStationActions</code> liste les actions que peut effectuer une caserne de pompier
 *
 * @author Adan Bougherara et Vivien Demeulenaere
 */
public enum FIREStationActions implements ActionI {
	CheckArea, CheckHighLadder, CheckStandardTruck,
	GeneralAlarm, FirstAlarm, SecondAlarm, EndOfFire, StandardTruckAvailable, HighLadderAvailable,
	HighLadderNonAvailable, StandardTruckNonAvailable

}
