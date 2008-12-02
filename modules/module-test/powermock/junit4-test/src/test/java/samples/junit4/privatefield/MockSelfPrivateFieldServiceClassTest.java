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
package samples.junit4.privatefield;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.junit.Assert.assertEquals;
import static org.powermock.api.easymock.PowerMock.createPartialMock;
import static org.powermock.api.easymock.PowerMock.expectPrivate;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;
import static org.powermock.reflect.Whitebox.setInternalState;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import samples.Service;
import samples.privatefield.MockSelfPrivateFieldServiceClass;

/**
 * A test class that demonstrate how to test classes that uses a private field
 * for a service and has no corresponding setter and at the same time mocking a
 * method of the actual test class. This is approach is common in DI frameworks
 * like Guice and Wicket IoC.
 * 
 * @author Johan Haleby
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(MockSelfPrivateFieldServiceClass.class)
public class MockSelfPrivateFieldServiceClassTest {

	@Test
	public void testGetCompositeMessage() throws Exception {
		MockSelfPrivateFieldServiceClass tested = createPartialMock(MockSelfPrivateFieldServiceClass.class, "getOwnMessage");

		Service serviceMock = createMock(Service.class);
		setInternalState(tested, "service", serviceMock, MockSelfPrivateFieldServiceClass.class);

		final String expected = "Hello world";
		expectPrivate(tested, "getOwnMessage").andReturn("Hello");
		expect(serviceMock.getServiceMessage()).andReturn(" world");

		replay(serviceMock);
		replay(tested);

		final String actual = tested.getCompositeMessage();

		verify(serviceMock);
		verify(tested);

		assertEquals(expected, actual);
	}
}
