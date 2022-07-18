package event.classes;

import java.time.Duration;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Iterator;

import event.interfaces.EventBaseI;
import event.interfaces.EventI;

/**
 * La classe <code> EventBase </code> instancie une base d'evenement
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class EventBase implements EventBaseI {
	private ArrayList<EventI> events = new ArrayList<EventI>();

	/**
	 * @see event.interfaces.EventBaseI#addEvent(event.interfaces.EventI)
	 */
	@Override
	public void addEvent(EventI e) {
		int index = 0;
		LocalTime time = e.getTimeStamp();
		for (index = 0; index < events.size(); index++) {
			if (time.isAfter(events.get(index).getTimeStamp())) {
				break;
			}
		}
		events.add(index, e);
	}

	/**
	 * @see event.interfaces.EventBaseI#removeEvent(event.interfaces.EventI)
	 */
	@Override
	public void removeEvent(EventI e) {
		events.remove(e);
	}

	/**
	 * @see event.interfaces.EventBaseI#getEvent(int)
	 */
	@Override
	public EventI getEvent(int i) {
		return events.get(i);
	}

	/**
	 * @see event.interfaces.EventBaseI#numberOfEvents()
	 */
	@Override
	public int numberOfEvents() {
		return events.size();
	}

	/**
	 * @see event.interfaces.EventBaseI#appearsIn(event.interfaces.EventI)
	 */
	@Override
	public boolean appearsIn(EventI e) {
		return events.contains(e);
	}

	/**
	 * @see event.interfaces.EventBaseI#clearEvents(java.time.Duration)
	 */
	@Override
	public void clearEvents(Duration d) {
		if (d == null) {
			events.clear();
			return;
		}
		ZoneId z = ZoneId.of("ECT");
		LocalTime time = LocalTime.now(z);
		EventI e;
		Iterator<EventI> it = events.iterator();
		while (it.hasNext()) {
			e = it.next();
			long duration = d.getSeconds();
			if (e.getTimeStamp().plusSeconds(duration).compareTo(time) >= 0) {
				it.remove();
			}
		}
		return;
	}

}
