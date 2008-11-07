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
package powermock.examples;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.powermock.PowerMock.createMock;
import static org.powermock.PowerMock.mockStatic;
import static org.powermock.PowerMock.replay;
import static org.powermock.PowerMock.verify;

import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import powermock.examples.dependencymanagement.DependencyManager;
import powermock.examples.domain.Person;
import powermock.examples.service.MyService;
import powermock.examples.service.impl.MyServiceImpl;

/**
 * This is an example unit test using JUnit 4.4 for the
 * {@link MyServiceImpl#getAllPersons()} method. The task for PowerMock is to
 * mock the call to {@link DependencyManager#getInstance()} which is not
 * possible without byte-code manipulation.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(DependencyManager.class)
public class MyServiceUserTest {

	private MyServiceUser tested;

	private DependencyManager dependencyManagerMock;
	private MyService myServiceMock;

	@Before
	public void setUp() {
		tested = new MyServiceUser();
		dependencyManagerMock = createMock(DependencyManager.class);
		myServiceMock = createMock(MyService.class);
	}

	@After
	public void tearDown() {
		tested = null;
		dependencyManagerMock = null;
	}

	public void replayAll() throws Exception {
		replay(DependencyManager.class, dependencyManagerMock, myServiceMock);
	}

	public void verifyAll() {
		verify(DependencyManager.class, dependencyManagerMock, myServiceMock);
	}

	/**
	 * Unit test for the {@link MyServiceImpl#getAllPersons()} method. This
	 * tests demonstrate how to mock the static call to
	 * {@link DependencyManager#getInstance()} and returning a mock of the
	 * <code>DependencyManager</code> instead of the real instance.
	 */
	@Test
	public void testGetNumberOfPersons() throws Exception {
		/*
		 * This is how to tell PowerMock to prepare the DependencyManager class
		 * for static mocking.
		 */
		mockStatic(DependencyManager.class);

		/*
		 * Expectations are performed the same for static methods as for
		 * instance methods.
		 */
		expect(DependencyManager.getInstance()).andReturn(dependencyManagerMock);

		expect(dependencyManagerMock.getMyService()).andReturn(myServiceMock);

		Set<Person> persons = new HashSet<Person>();
		persons.add(new Person("Johan", "Haleby", "MockStreet"));
		persons.add(new Person("Jan", "Kronquist", "MockStreet2"));

		expect(myServiceMock.getAllPersons()).andReturn(persons);

		replayAll();

		int numberOfPersons = tested.getNumberOfPersons();

		verifyAll();

		assertEquals(2, numberOfPersons);
	}
}
