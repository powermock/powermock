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
package org.powermock.modules.junit3.suppressconstructor;

import static org.powermock.PowerMock.suppressConstructorCodeHierarchy;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit3.PowerMockSuite;

import samples.suppressconstructor.SuppressConstructorHierarchyDemo;

@PrepareForTest( { SuppressConstructorHierarchyDemo.class })
public class SuppressConstructorHierarchyDemoTest extends TestCase {

	@SuppressWarnings("unchecked")
	public static TestSuite suite() throws Exception {
		return new PowerMockSuite(SuppressConstructorHierarchyDemoTest.class);
	}

	public void testSuppressConstructor() throws Exception {
		suppressConstructorCodeHierarchy(SuppressConstructorHierarchyDemo.class);
		SuppressConstructorHierarchyDemo tested = new SuppressConstructorHierarchyDemo(
				"message");

		final String message = tested.getMessage();
		assertNull(
				"Message should have been null since we're skipping the execution of the constructor code. Message was \""
						+ message + "\".", message);
	}

	@PrepareForTest
	public void testNotSuppressConstructor() throws Exception {
		try {
			new SuppressConstructorHierarchyDemo("message");
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
	public void testGetNumber() throws Exception {
		suppressConstructorCodeHierarchy(SuppressConstructorHierarchyDemo.class);
		SuppressConstructorHierarchyDemo tested = new SuppressConstructorHierarchyDemo(
				"message");
		assertEquals(42, tested.getNumber());
	}
}
