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

import org.junit.Test;
import samples.Service;
import samples.privatefield.SimplePrivateFieldServiceClass;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;
import static org.powermock.reflect.Whitebox.setInternalState;

/**
 * A test class that demonstrate how to test class that uses a private field for
 * a Service and has no corresponding setter. This is approach is common in DI
 * frameworks like Guice and Wicket IoC.
 * 
 * @author Johan Haleby
 */
public class SimplePrivateFieldServiceClassTest {

	@Test
	public void testSimplePrivateFieldServiceClass() throws Exception {
		SimplePrivateFieldServiceClass tested = new SimplePrivateFieldServiceClass();
		Service serviceMock = createMock(Service.class);
		setInternalState(tested, "service", serviceMock, SimplePrivateFieldServiceClass.class);

		final String expected = "Hello world!";
		expect(serviceMock.getServiceMessage()).andReturn(expected);

		replay(serviceMock);
		final String actual = tested.useService();

		verify(serviceMock);

		assertEquals(expected, actual);
	}

	@Test
	public void testSimplePrivateFieldServiceClassTypeSafe() throws Exception {
		SimplePrivateFieldServiceClass tested = new SimplePrivateFieldServiceClass();
		Service serviceMock = createMock(Service.class);
		setInternalState(tested, serviceMock);

		final String expected = "Hello world!";
		expect(serviceMock.getServiceMessage()).andReturn(expected);

		replay(serviceMock);
		final String actual = tested.useService();

		verify(serviceMock);

		assertEquals(expected, actual);
	}
}
