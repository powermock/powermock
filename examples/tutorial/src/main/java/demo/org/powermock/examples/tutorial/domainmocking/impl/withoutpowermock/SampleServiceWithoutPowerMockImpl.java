package demo.org.powermock.examples.tutorial.domainmocking.impl.withoutpowermock;

import demo.org.powermock.examples.tutorial.domainmocking.EventService;
import demo.org.powermock.examples.tutorial.domainmocking.PersonService;
import demo.org.powermock.examples.tutorial.domainmocking.SampleService;
import demo.org.powermock.examples.tutorial.domainmocking.domain.BusinessMessages;
import demo.org.powermock.examples.tutorial.domainmocking.domain.Person;
import demo.org.powermock.examples.tutorial.domainmocking.domain.SampleServiceException;

/**
 * This is a simple service that delegates calls to two stub services. The
 * purpose of this service is to demonstrate that need to refactor the
 * production code in order to make it unit-testable if PowerMock is not used.
 * Note that there's no need to refactor the class if PowerMock had been used.
 */
public class SampleServiceWithoutPowerMockImpl implements SampleService {

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
	public SampleServiceWithoutPowerMockImpl(PersonService personService, EventService eventService) {
		this.personService = personService;
		this.eventService = eventService;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean createPerson(String firstName, String lastName) {
		BusinessMessages messages = getNewBusinessMessagesInstance();
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

	/**
	 * In order to test this class without PowerMock we need to create a new
	 * protected method whose only purpose is to create a new instance of a
	 * BusinessMessage. This means that we can utilize partial mocking to
	 * override this method in our test and have it return a mock.
	 * 
	 * @return A new instance of a {@link BusinessMessages} object.
	 */
	protected BusinessMessages getNewBusinessMessagesInstance() {
		return new BusinessMessages();
	}
}
