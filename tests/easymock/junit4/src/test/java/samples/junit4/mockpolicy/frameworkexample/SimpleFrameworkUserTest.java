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
package samples.junit4.mockpolicy.frameworkexample;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.MockPolicy;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.mockpolicy.frameworkexample.SimpleFrameworkUser;

import static org.junit.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
@MockPolicy(SimpleFrameworkMockPolicy.class)
public class SimpleFrameworkUserTest {

	@Test
	public void testPerformComplexOperation() {
		SimpleFrameworkUser tested = new SimpleFrameworkUser();
		assertEquals(SimpleFrameworkMockPolicy.NATIVE_RESULT_VALUE, tested.performComplexOperation("some complex stuff"));
	}

}
