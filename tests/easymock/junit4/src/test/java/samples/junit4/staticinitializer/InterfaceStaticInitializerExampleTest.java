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
package samples.junit4.staticinitializer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.staticinitializer.InterfaceComputation;

import static org.junit.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
@SuppressStaticInitializationFor( { "samples.staticinitializer.InterfaceA", "samples.staticinitializer.InterfaceB",
		"samples.staticinitializer.InterfaceC" })
public class InterfaceStaticInitializerExampleTest {

	@Test
	public void testSuppressStaticInitializer() throws Exception {
		assertEquals(0, InterfaceComputation.calculateWithinHierarchy());
	}

	@Test
	public void testSuppressStaticInitializer2() throws Exception {
		assertEquals(0, InterfaceComputation.calculateWithReference());
	}
}
