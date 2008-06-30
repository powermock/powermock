package demo.org.powermock.examples.tutorial.domainmocking;

import demo.org.powermock.examples.tutorial.domainmocking.domain.BusinessMessages;
import demo.org.powermock.examples.tutorial.domainmocking.domain.Person;

/**
 * A simple interface that manages events.
 */
public interface EventService {

	/**
	 * Sends a new error event to the interested parties in the system.
	 * 
	 * @param person
	 *            The person to object associated with this event.
	 * @param messages
	 *            The business messages object that may contain errors or
	 *            warnings.
	 */
	void sendErrorEvent(Person person, BusinessMessages messages);

}
