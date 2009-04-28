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
package demo.org.powermock.examples.tutorial.domainmocking.impl;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.createMockAndExpectNew;
import static org.powermock.api.easymock.PowerMock.expectLastCall;
import static org.powermock.api.easymock.PowerMock.expectNew;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import demo.org.powermock.examples.tutorial.domainmocking.EventService;
import demo.org.powermock.examples.tutorial.domainmocking.PersonService;
import demo.org.powermock.examples.tutorial.domainmocking.domain.BusinessMessages;
import demo.org.powermock.examples.tutorial.domainmocking.domain.Person;
import demo.org.powermock.examples.tutorial.domainmocking.domain.SampleServiceException;

@RunWith(PowerMockRunner.class)
@PrepareForTest( { SampleServiceImpl.class, BusinessMessages.class, Person.class })
public class SampleServiceImplTest {

	private SampleServiceImpl tested;
	private PersonService personServiceMock;
	private EventService eventService;

	@Before
	public void setUp() {
		personServiceMock = createMock(PersonService.class);
		eventService = createMock(EventService.class);

		tested = new SampleServiceImpl(personServiceMock, eventService);
	}

	@After
	public void tearDown() {
		personServiceMock = null;
		eventService = null;
		tested = null;
	}

	@Test
	public void testCreatePerson() throws Exception {
		// Mock the creation of person
		final String firstName = "firstName";
		final String lastName = "lastName";
		Person personMock = createMockAndExpectNew(Person.class, firstName, lastName);

		// Mock the creation of BusinessMessages
		BusinessMessages businessMessagesMock = createMockAndExpectNew(BusinessMessages.class);

		personServiceMock.create(personMock, businessMessagesMock);
		expectLastCall().times(1);

		expect(businessMessagesMock.hasErrors()).andReturn(false);

		replayAll();

		assertTrue(tested.createPerson(firstName, lastName));

		verifyAll();
	}

	@Test
	public void testCreatePerson_error() throws Exception {
		// Mock the creation of person
		final String firstName = "firstName";
		final String lastName = "lastName";
		Person personMock = createMockAndExpectNew(Person.class, firstName, lastName);

		// Mock the creation of BusinessMessages
		BusinessMessages businessMessagesMock = createMockAndExpectNew(BusinessMessages.class);

		personServiceMock.create(personMock, businessMessagesMock);
		expectLastCall().times(1);

		expect(businessMessagesMock.hasErrors()).andReturn(true);

		eventService.sendErrorEvent(personMock, businessMessagesMock);
		expectLastCall().times(1);

		replayAll();

		assertFalse(tested.createPerson(firstName, lastName));

		verifyAll();
	}

	@Test(expected = SampleServiceException.class)
	public void testCreatePerson_illegalName() throws Exception {
		// Mock the creation of person
		final String firstName = "firstName";
		final String lastName = "lastName";
		expectNew(Person.class, firstName, lastName).andThrow(new IllegalArgumentException("Illegal name"));

		replayAll();

		tested.createPerson(firstName, lastName);

		verifyAll();
	}
}
