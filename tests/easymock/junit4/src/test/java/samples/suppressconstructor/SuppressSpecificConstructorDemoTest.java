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
package samples.suppressconstructor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.fail;
import static org.powermock.api.support.membermodification.MemberMatcher.constructor;
import static org.powermock.api.support.membermodification.MemberModifier.suppress;

@RunWith(PowerMockRunner.class)
@PrepareForTest( { SuppressSpecificConstructorDemo.class, SuppressSpecificConstructorDemoTest.class })
public class SuppressSpecificConstructorDemoTest {

	@Test
	public void testMockStringConstructor() throws Exception {
		suppress(constructor(SuppressSpecificConstructorDemo.class, String.class));

		// This should be fine
		new SuppressSpecificConstructorDemo("This expection should not occur");
		// This should not be fine!
		try {
			new SuppressSpecificConstructorDemo();
			fail("Should have thrown IllegalStateException!");
		} catch (IllegalStateException e) {
			// assertEquals("", e.getMessage());
		}
	}
}
