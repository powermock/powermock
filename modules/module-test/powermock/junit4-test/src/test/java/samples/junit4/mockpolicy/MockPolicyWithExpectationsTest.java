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

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verifyAll;

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
import samples.mockpolicy.SimpleClassWithADependency;

@RunWith(PowerMockRunner.class)
@MockPolicy(MockPolicyExpectationsExample.class)
public class MockPolicyWithExpectationsTest {

	@Test
	public void mockPolicyWithExpectationsWorks() throws Exception {
		final SimpleClassWithADependency tested = new SimpleClassWithADependency();
		Whitebox.setInternalState(tested, new ResultCalculator(5));
		
		assertEquals(2.0, tested.getResult(), 0.0);
		
		verifyAll();
	}
}

class MockPolicyExpectationsExample implements PowerMockPolicy {
	public void applyClassLoadingPolicy(MockPolicyClassLoadingSettings settings) {
		settings.addFullyQualifiedNamesOfClassesToLoadByMockClassloader(ResultCalculator.class.getName());
	}

	public void applyInterceptionPolicy(MockPolicyInterceptionSettings settings) {
		final ResultCalculator calculatorMock = createMock(ResultCalculator.class);
		expect(calculatorMock.calculate()).andReturn(2.0);

		replay(calculatorMock);

		Method calculateMethod = Whitebox.getMethod(ResultCalculator.class, "calculate");
		settings.addSubtituteReturnValue(calculateMethod, calculatorMock.calculate());
	}
}
