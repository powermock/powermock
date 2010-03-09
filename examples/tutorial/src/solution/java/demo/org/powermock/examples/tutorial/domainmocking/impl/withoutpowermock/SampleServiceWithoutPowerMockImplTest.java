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

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.reportMatcher;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;

import org.easymock.classextension.ConstructorArgs;
import org.easymock.internal.matchers.Equals;
import org.easymock.internal.matchers.Same;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import demo.org.powermock.examples.tutorial.domainmocking.EventService;
import demo.org.powermock.examples.tutorial.domainmocking.PersonService;
import demo.org.powermock.examples.tutorial.domainmocking.domain.BusinessMessages;
import demo.org.powermock.examples.tutorial.domainmocking.domain.Person;
import demo.org.powermock.examples.tutorial.domainmocking.domain.SampleServiceException;

/**
 * This test demonstrates how to test the refactored SampleService without
 * PowerMock.
 */
public class SampleServiceWithoutPowerMockImplTest {

	private SampleServiceWithoutPowerMockImpl tested;
	private BusinessMessages businessMessagesMock;
	private PersonService personServiceMock;
	private EventService eventServiceMock;

	private ConstructorArgs constructorArgs;

	@SuppressWarnings("deprecation")
	@Before
	public void setUp() throws Exception {
		businessMessagesMock = createMock(BusinessMessages.class);
		personServiceMock = createMock(PersonService.class);
		eventServiceMock = createMock(EventService.class);
		constructorArgs = new ConstructorArgs(SampleServiceWithoutPowerMockImpl.class.getConstructor(PersonService.class, EventService.class),
				personServiceMock, eventServiceMock);
		tested = createMock(SampleServiceWithoutPowerMockImpl.class, constructorArgs, new Method[] {});
	}

	@After
	public void tearDown() {
		businessMessagesMock = null;
		personServiceMock = null;
		eventServiceMock = null;
		constructorArgs = null;
		tested = null;
	}

	protected void replayAll() {
		replay(businessMessagesMock, personServiceMock, eventServiceMock, tested);
	}

	protected void verifyAll() {
		verify(businessMessagesMock, personServiceMock, eventServiceMock, tested);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testCreatePerson() throws Exception {
		final String firstName = "firstName";
		final String lastName = "lastName";

		Method getNewBusinessMessagesInstanceMethod = SampleServiceWithoutPowerMockImpl.class.getDeclaredMethod("getNewBusinessMessagesInstance");
		tested = createMock(SampleServiceWithoutPowerMockImpl.class, constructorArgs, new Method[] { getNewBusinessMessagesInstanceMethod });

		expect(tested.getNewBusinessMessagesInstance()).andReturn(businessMessagesMock);

		personServiceMock.create(eq(new Person(firstName, lastName)), same(businessMessagesMock));
		expectLastCall().once();

		expect(businessMessagesMock.hasErrors()).andReturn(false);

		replayAll();

		assertTrue(tested.createPerson(firstName, lastName));

		verifyAll();
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testCreatePerson_error() throws Exception {
		final String firstName = "firstName";
		final String lastName = "lastName";

		Method getNewBusinessMessagesInstanceMethod = SampleServiceWithoutPowerMockImpl.class.getDeclaredMethod("getNewBusinessMessagesInstance");
		tested = createMock(SampleServiceWithoutPowerMockImpl.class, constructorArgs, new Method[] { getNewBusinessMessagesInstanceMethod });

		expect(tested.getNewBusinessMessagesInstance()).andReturn(businessMessagesMock);

		final Person person = new Person(firstName, lastName);
		personServiceMock.create(eq(person), same(businessMessagesMock));
		expectLastCall().once();

		expect(businessMessagesMock.hasErrors()).andReturn(true);

		eventServiceMock.sendErrorEvent(person, businessMessagesMock);
		expectLastCall().once();

		replayAll();

		assertFalse(tested.createPerson(firstName, lastName));

		verifyAll();
	}

	@SuppressWarnings("deprecation")
	@Test(expected = SampleServiceException.class)
	public void testCreatePerson_illegalName() throws Exception {
		final String firstName = null;
		final String lastName = "lastName";

		Method getNewBusinessMessagesInstanceMethod = SampleServiceWithoutPowerMockImpl.class.getDeclaredMethod("getNewBusinessMessagesInstance");
		tested = createMock(SampleServiceWithoutPowerMockImpl.class, constructorArgs, new Method[] { getNewBusinessMessagesInstanceMethod });

		expect(tested.getNewBusinessMessagesInstance()).andReturn(businessMessagesMock);

		replayAll();

		tested.createPerson(firstName, lastName);

		verifyAll();
	}

	private static <T> T eq(T object) {
		reportMatcher(new Equals(object));
		return object;
	}

	private static <T> T same(T object) {
		reportMatcher(new Same(object));
		return object;
	}
}
