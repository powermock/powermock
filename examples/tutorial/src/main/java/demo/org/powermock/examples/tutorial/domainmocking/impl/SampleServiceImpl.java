package demo.org.powermock.examples.tutorial.domainmocking.impl;

import demo.org.powermock.examples.tutorial.domainmocking.EventService;
import demo.org.powermock.examples.tutorial.domainmocking.PersonService;
import demo.org.powermock.examples.tutorial.domainmocking.SampleService;
import demo.org.powermock.examples.tutorial.domainmocking.domain.BusinessMessages;
import demo.org.powermock.examples.tutorial.domainmocking.domain.Person;
import demo.org.powermock.examples.tutorial.domainmocking.domain.SampleServiceException;

/**
 * This is a simple service that delegates calls to two stub services. The
 * purpose of this service is to demonstrate that need to mock instantiation of
 * domain objects.
 */
public class SampleServiceImpl implements SampleService {

	private final PersonService personService;

	private final EventService eventService;

	/**
	 * Creates a new instance of the SampleServiceImpl with the following
	 * collaborators.
	 * 
	 * @param personService
	 *            The person service to use.
	 * @param eventService
	 *            The event service to use.
	 */
	public SampleServiceImpl(PersonService personService, EventService eventService) {
		this.personService = personService;
		this.eventService = eventService;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean createPerson(String firstName, String lastName) {
		BusinessMessages messages = new BusinessMessages();
		Person person = null;
		try {
			person = new Person(firstName, lastName);
		} catch (IllegalArgumentException e) {
			throw new SampleServiceException(e.getMessage(), e);
		}

		personService.create(person, messages);

		final boolean hasErrors = messages.hasErrors();
		if (hasErrors) {
			eventService.sendErrorEvent(person, messages);
		}

		return !hasErrors;
	}
}
