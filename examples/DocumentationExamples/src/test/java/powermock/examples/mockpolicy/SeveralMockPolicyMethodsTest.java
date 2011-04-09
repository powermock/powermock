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
package powermock.examples.mockpolicy;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.MockPolicy;
import org.powermock.modules.junit4.PowerMockRunner;
import powermock.examples.mockpolicy.nontest.domain.DataObject;
import powermock.examples.mockpolicy.policy.MyCustomMockPolicy;

import static org.junit.Assert.assertEquals;

/**
 * Test that makes sure that {@link MyCustomMockPolicy} works when several test
 * methods are used. This was a bug fixed in PowerMock 1.3.
 */
@RunWith(PowerMockRunner.class)
@MockPolicy(MyCustomMockPolicy.class)
public class SeveralMockPolicyMethodsTest {

	/**
	 * Make sure that mock policies works in setup methods.
	 */
	@Before
	public void setUp() {
		DataObject dependencyData = new DependencyUser().getDependencyData();
		assertEquals("Policy generated data object", dependencyData.getData());
	}

	@Test
	public void assertThatMyFirstMockPolicyWork() throws Exception {
		DataObject dependencyData = new DependencyUser().getDependencyData();
		assertEquals("Policy generated data object", dependencyData.getData());
	}

	@Test
	public void assertThatMyFirstMockPolicyWorkFromAnotherTestMethodInTheSameTestClass() throws Exception {
		DataObject dependencyData = new DependencyUser().getDependencyData();
		assertEquals("Policy generated data object", dependencyData.getData());
	}
}
