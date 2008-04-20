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
package org.powermock.modules.junit4.suppressconstructor;

import static org.powermock.PowerMock.suppressSpecificConstructor;
import static org.junit.Assert.fail;

import org.powermock.modules.junit4.PowerMockRunner;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import samples.suppressconstructor.SuppressSpecificConstructorDemo;

@RunWith(PowerMockRunner.class)
// @PrepareForTest( { SuppressSpecificConstructorDemo.class,
// SuppressSpecificConstructorDemoTest.class })
public class SuppressSpecificConstructorDemoTest {

	@Test
	@Ignore
	public void testMockStringConstructor() throws Exception {
		suppressSpecificConstructor(SuppressSpecificConstructorDemo.class,
				String.class);

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
