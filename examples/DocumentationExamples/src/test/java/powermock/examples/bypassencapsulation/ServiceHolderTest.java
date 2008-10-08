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
package powermock.examples.bypassencapsulation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.powermock.Whitebox.getInternalState;

import java.util.Set;

import org.junit.Test;

/**
 * Unit tests for the {@link ServiceHolder} class.
 */
@SuppressWarnings("unchecked")
public class ServiceHolderTest {

	@Test
	public void testAddService() throws Exception {
		ServiceHolder tested = new ServiceHolder();
		final Object service = new Object();

		tested.addService(service);

		Set<String> services = (Set<String>) getInternalState(tested,
				"services");

		assertEquals("Size of the \"services\" Set should be 1", 1, services
				.size());
		assertSame("The services Set should didn't contain the expect service",
				service, services.iterator().next());
	}

	@Test
	public void testRemoveService() throws Exception {
		ServiceHolder tested = new ServiceHolder();
		final Object service = new Object();

		// Get the hash set.
		Set<Object> servicesSet = (Set<Object>) getInternalState(tested,
				"services");
		servicesSet.add(service);

		tested.removeService(service);

		assertTrue("Set should be empty after removeal.", servicesSet.isEmpty());
	}
}
