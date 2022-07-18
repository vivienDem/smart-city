package components.classes;

import components.interfaces.ActionI;
/**
 * L'enumeration <code> SAMUCenterActions </code> liste les actions que peut effectuer un centre de SAMU
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public enum SAMUCenterActions implements ActionI{
	SendMedic, SendAmbulance, CheckArea, CheckAmbulanceAvailable, CheckMedicAvailable,
	MedicCall, MedicsNonAvailable, AmbulancesNonAvailable, AmbulancesAvailable, MedicsAvailable
}
