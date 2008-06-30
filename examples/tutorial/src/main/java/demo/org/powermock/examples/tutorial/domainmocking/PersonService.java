package demo.org.powermock.examples.tutorial.domainmocking;

import demo.org.powermock.examples.tutorial.domainmocking.domain.BusinessMessages;
import demo.org.powermock.examples.tutorial.domainmocking.domain.Person;

/**
 * A simple interface that manages persons.
 */
public interface PersonService {

	/**
	 * Create a new person.
	 * 
	 * @param person
	 *            The person to create.
	 * @param messages
	 *            The business messages object to be filled with validation
	 *            errors.
	 */
	void create(Person person, BusinessMessages messages);

}
