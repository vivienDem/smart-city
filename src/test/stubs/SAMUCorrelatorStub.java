package test.stubs;

import java.io.Serializable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RejectedExecutionException;

import event.classes.ConsciousFall;
import components.interfaces.SAMUCorrelatorI;

/**
 * La classe <code> SAMUCorrelatorStub </code> implemente un bouchon d'un
 * correlateur SAMU
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class SAMUCorrelatorStub implements SAMUCorrelatorI {
	private String area = "Ve";
	private int testAmbulanceDispo = 0;
	private int testMedicDispo = 0;

	public SAMUCorrelatorStub(String area) {
		this.area = area;
	}

	public SAMUCorrelatorStub() {
	}

	/**
	 * @see components.interfaces.SAMUCorrelatorI#triggerAmbulanceIntervention(java.io.Serializable,
	 *      java.io.Serializable)
	 */
	@Override
	public void triggerAmbulanceIntervention(Serializable position, Serializable centerId) {
		System.out.println("An ambulance is coming from the area : " + area);

	}

	/**
	 * @see components.interfaces.SAMUCorrelatorI#isInArea(java.io.Serializable,
	 *      java.io.Serializable)
	 */
	@Override
	public boolean isInArea(Serializable position, Serializable centerId) {
		return true;
	}

	/**
	 * @see components.interfaces.SAMUCorrelatorI#ambulanceAvailable(java.io.Serializable)
	 */
	@Override
	public boolean ambulanceAvailable(Serializable centerId) {
		if (testAmbulanceDispo++ == 0)
			return true;
		return false;
	}

	/**
	 * @see components.interfaces.SAMUCorrelatorI#medicAvailable(java.io.Serializable)
	 */
	@Override
	public boolean medicAvailable(Serializable centerId) {
		if (testMedicDispo++ == 0) {
			return true;
		}
		return false;
	}

	/**
	 * @see components.interfaces.SAMUCorrelatorI#triggerMedicIntervention(java.io.Serializable,
	 *      java.io.Serializable)
	 */
	@Override
	public void triggerMedicIntervention(Serializable position, Serializable centerId) {
		System.out.println("A doctor is coming from the area : " + area);

	}

	/**
	 * @see components.interfaces.SAMUCorrelatorI#redirectTrackingEvent(java.io.Serializable,
	 *      java.io.Serializable)
	 */
	@Override
	public void redirectTrackingEvent(Serializable position, Serializable centerId) {
		System.out.println("redirectTrackingEvent");

	}

	/**
	 * @see components.interfaces.SAMUCorrelatorI#triggerMedicCall(java.io.Serializable,
	 *      java.io.Serializable, java.io.Serializable)
	 */
	@Override
	public void triggerMedicCall(Serializable position, Serializable personId, Serializable centerId) {
		System.out.println("A medic call has been triggered");

	}

	/**
	 * @see components.interfaces.SAMUCorrelatorI#redirectMedicCall(java.io.Serializable,
	 *      java.io.Serializable, java.io.Serializable)
	 */
	@Override
	public void redirectMedicCall(Serializable position, Serializable personId, Serializable centerId) {
		System.out.println("The medic call has been transfered");

	}

	/**
	 * @see components.interfaces.SAMUCorrelatorI#redirectConsciousFall(event.classes.ConsciousFall)
	 */
	@Override
	public void redirectConsciousFall(ConsciousFall c) {
		System.out.println("Conscious fall redirected");

	}

	/**
	 * @see components.interfaces.SAMUCorrelatorI#otherSamuCenter(java.io.Serializable,
	 *      java.io.Serializable)
	 */
	@Override
	public String otherSamuCenter(Serializable centerId, Serializable type) {
		return null;
	}

	/**
	 * @see components.interfaces.SAMUCorrelatorI#setMedicsNonAvailable(java.lang.String)
	 */
	@Override
	public void setMedicsNonAvailable(String centerId) throws Exception {
		System.out.println("No medics available");

	}

	/**
	 * @see components.interfaces.SAMUCorrelatorI#setAmbulancesAvailable(java.lang.String)
	 */
	@Override
	public void setAmbulancesAvailable(String centerId)
			throws RejectedExecutionException, AssertionError, InterruptedException, ExecutionException, Exception {
		System.out.println("Medics available");

	}

	/**
	 * @see components.interfaces.SAMUCorrelatorI#setMedicsAvailable(java.lang.String)
	 */
	@Override
	public void setMedicsAvailable(String centerId)
			throws RejectedExecutionException, AssertionError, InterruptedException, ExecutionException, Exception {
		System.out.println("Ambulances available");

	}

	/**
	 * @see components.interfaces.SAMUCorrelatorI#setAmbulancesNonAvailable(java.lang.String)
	 */
	@Override
	public void setAmbulancesNonAvailable(String centerId)
			throws RejectedExecutionException, AssertionError, InterruptedException, ExecutionException, Exception {
		System.out.println("No ambulances available");

	}

	/**
	 * @see components.interfaces.SAMUCorrelatorI#redirectAmbulanceIntervention(java.io.Serializable)
	 */
	@Override
	public void redirectAmbulanceIntervention(Serializable event) throws Exception {
		System.out.println("An ambulance is coming from another area");

	}

	/**
	 * @see components.interfaces.SAMUCorrelatorI#redirectMedicIntervention(java.io.Serializable)
	 */
	@Override
	public void redirectMedicIntervention(Serializable event) throws Exception {
		System.out.println("A medic is coming from another area");

	}

}
