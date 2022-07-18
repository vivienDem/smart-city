package event.classes;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import event.interfaces.EventI;
import fr.sorbonne_u.cps.smartcity.utils.TimeManager;

/**
 * La classe <code> AbstractEvent </code> permet de definir un evenement
 * generique
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public abstract class AbstractEvent implements EventI {
	private static final long serialVersionUID = 1L;
	private LocalTime birth;
	protected Map<String, Serializable> properties;

	/**
	 * Cree un evenement generique
	 */
	public AbstractEvent() {
		properties = new HashMap<String, Serializable>();
		TimeManager tm = TimeManager.get();
		birth = tm.getCurrentLocalTime();

	}

	/**
	 * @see event.interfaces.EventI#getTimeStamp()
	 */
	@Override
	public LocalTime getTimeStamp() {
		return birth;
	}

	@Override
	public void setTimeStamp(LocalTime birth) {
		this.birth = birth;
	}

	/**
	 * @see event.interfaces.EventI#hasProperty(java.lang.String)
	 */
	@Override
	public boolean hasProperty(String name) {
		return properties.get(name) != null;
	}

	/**
	 * @see event.interfaces.EventI#getPropertyValue(java.lang.String)
	 */
	@Override
	public Serializable getPropertyValue(String name) {
		return properties.get(name);
	}

}
