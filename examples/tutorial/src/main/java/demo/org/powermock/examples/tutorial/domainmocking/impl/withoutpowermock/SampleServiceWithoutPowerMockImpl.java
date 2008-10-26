/*
 * Copyright 2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
