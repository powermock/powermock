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
package powermock.examples.suppress.staticinitializer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * This test demonstrates the ability for PowerMock to remove static
 * initializers.
 */
@RunWith(PowerMockRunner.class)
@SuppressStaticInitializationFor("powermock.examples.suppress.staticinitializer.ExampleWithEvilStaticInitializer")
public class ExampleWithEvilStaticInitializerTest {

	@Test
	public void testSuppressStaticInitializer() throws Exception {
		final String message = "myMessage";
		ExampleWithEvilStaticInitializer tested = new ExampleWithEvilStaticInitializer(message);
		assertEquals(message, tested.getMessage());
	}
}