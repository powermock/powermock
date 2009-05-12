/*
 * Copyright 2009 the original author or authors.
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
package samples.junit4.mockpolicy;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.MockPolicy;
import org.powermock.core.spi.PowerMockPolicy;
import org.powermock.mockpolicies.MockPolicyClassLoadingSettings;
import org.powermock.mockpolicies.MockPolicyInterceptionSettings;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import samples.mockpolicy.ResultCalculator;
import samples.mockpolicy.SomeClassWithAMethod;

/**
 * A simple example of a mock policy that stubs out a method call.
 */
@RunWith(PowerMockRunner.class)
@MockPolicy(MockPolicyExample.class)
public class MockPolicyUsageExampleTest {

	@Test
	public void exampleOfStubbingOutCallsInParentClass() throws Exception {
		SomeClassWithAMethod tested = new SomeClassWithAMethod();
		assertEquals(8.0d, tested.getResult(), 0.0d);
	}
}

class MockPolicyExample implements PowerMockPolicy {
	public void applyClassLoadingPolicy(MockPolicyClassLoadingSettings settings) {
		settings.addFullyQualifiedNamesOfClassesToLoadByMockClassloader(ResultCalculator.class.getName());
	}

	public void applyInterceptionPolicy(MockPolicyInterceptionSettings settings) {
		Method calculateMethod = Whitebox.getMethod(ResultCalculator.class);
		settings.addSubtituteReturnValue(calculateMethod, 4);
	}
}
