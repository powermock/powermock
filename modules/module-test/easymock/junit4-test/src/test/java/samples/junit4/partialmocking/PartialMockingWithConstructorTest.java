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
package samples.junit4.partialmocking;

import org.junit.Ignore;
import org.junit.Test;
import samples.partialmocking.PartialMockingWithConstructor;

import static org.powermock.api.easymock.PowerMock.*;

public class PartialMockingWithConstructorTest {

	@Ignore("The initialize method is never invoked but is caught by the proxy. This is a possibly a bug in EasyMock class extensions?")
	@Test
	public void testPartialMock() throws Exception {

		/*
		 * In the original test case PartialMockingWithConstructor had
		 * constructor arguments which I removed to slim down the test case,
		 * originally I was using the following method to create a partial mock.
		 * Regardless the same problem still occurs.
		 */
		PartialMockingWithConstructor nationPartialMock = createPartialMockAndInvokeDefaultConstructor(PartialMockingWithConstructor.class, "touch");

		/*
		 * The following method also causes the same problem.
		 */

		// Nation nationPartialMock =
		// createPartialMockAndInvokeDefaultConstructor(Nation.class,"touch");
		replay(nationPartialMock);

		// Uncommenting the following line has no effect on the test result.
		// nationPartialMock.initialize();

		verify(nationPartialMock);
	}
}
