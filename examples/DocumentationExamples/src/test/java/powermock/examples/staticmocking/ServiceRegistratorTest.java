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
package powermock.examples.staticmocking;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.powermock.api.easymock.PowerMock.*;

/**
 * An example on how to mock the call to a static method.
 * 
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(IdGenerator.class)
public class ServiceRegistratorTest {

	@Test
	public void registersServiceToRepository() throws Exception {
		long expectedId = 42;

		// We create a new instance of the class under test as usually.
		ServiceRegistrator tested = new ServiceRegistrator();

		// This is the way to tell PowerMock to mock all static methods of a
		// given class
		mockStatic(IdGenerator.class);

		/*
		 * The static method call to IdGenerator.generateNewId() expectation.
		 * This is why we need PowerMock.
		 */
		expect(IdGenerator.generateNewId()).andReturn(expectedId);

		// Note how we replay the class, not the instance!
		replay(IdGenerator.class);

		long actualId = tested.registerService(new Object());

		// Note how we verify the class, not the instance!
		verify(IdGenerator.class);

		// Assert that the ID is correct
		assertEquals(expectedId, actualId);
	}
}
