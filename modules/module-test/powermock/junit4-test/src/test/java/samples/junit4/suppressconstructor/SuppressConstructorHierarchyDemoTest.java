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
package samples.junit4.suppressconstructor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.powermock.api.easymock.PowerMock.suppressConstructor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import samples.suppressconstructor.InvokeConstructor;
import samples.suppressconstructor.SuppressConstructorHierarchyChild;

@PrepareForTest(SuppressConstructorHierarchyChild.class)
@RunWith(PowerMockRunner.class)
public class SuppressConstructorHierarchyDemoTest {

	@Test
	public void testSuppressConstructorHierarchy() throws Exception {
		suppressConstructor(SuppressConstructorHierarchyChild.class);
		final String message = new InvokeConstructor().doStuff("qwe");
		assertNull("Message should have been null since we're skipping the execution of the constructor code. Message was \"" + message + "\".",
				message);
	}

	@Test
	@PrepareForTest
	public void testNotSuppressConstructorWithoutByteCodeManipulation() throws Exception {
		try {
			new SuppressConstructorHierarchyChild("message");
			fail("Should throw RuntimeException since we're running this test with a new class loader!");
		} catch (RuntimeException e) {
			assertEquals("This should be suppressed!!", e.getMessage());
		}
	}

	@Test
	public void testNotSuppressConstructorWithByteCodeManipulation() throws Exception {
		try {
			new SuppressConstructorHierarchyChild("message");
			fail("Should throw RuntimeException since we're running this test with a new class loader!");
		} catch (RuntimeException e) {
			assertEquals("This should be suppressed!!", e.getMessage());
		}
	}

	/**
	 * This simple test demonstrate that it's possible to continue execution
	 * with the default <code>PrepareForTest</code> settings (i.e. using a
	 * byte-code manipulated version of the SuppressConstructorHierarchyDemo
	 * class).
	 */
	@Test
	public void testSuppressConstructorHierarchyAgain() throws Exception {
		suppressConstructor(SuppressConstructorHierarchyChild.class);
		SuppressConstructorHierarchyChild tested = new SuppressConstructorHierarchyChild("message");
		assertEquals(42, tested.getNumber());
	}
}
