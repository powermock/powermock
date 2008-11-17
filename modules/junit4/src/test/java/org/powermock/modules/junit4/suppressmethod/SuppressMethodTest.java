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
package org.powermock.modules.junit4.suppressmethod;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.powermock.PowerMock.suppressMethod;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import samples.suppressmethod.SuppressMethod;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SuppressMethod.class)
public class SuppressMethodTest {

	@Test
	public void testGetObject() throws Exception {
		suppressMethod(SuppressMethod.class, "getObject");

		SuppressMethod tested = new SuppressMethod();
		assertNull("A method returning Object should return null after suppressing method code.", tested.getObject());
	}

	@Test
	public void testSuppressMultipleMethods() throws Exception {
		suppressMethod(SuppressMethod.class, "getObject", "getShort");

		SuppressMethod tested = new SuppressMethod();
		assertNull("A method returning Object should return null after suppressing method code.", tested.getObject());
		assertEquals("A method returning a short should return 0 after suppressing method code.", 0, tested.getShort());
	}

	@Test
	public void testGetObjectStatic() throws Exception {
		suppressMethod(SuppressMethod.class, "getObjectStatic");

		assertNull("A method returning Object should return null after suppressing method code.", SuppressMethod.getObjectStatic());
	}

	@Test
	public void testGetByte() throws Exception {
		suppressMethod(SuppressMethod.class, "getByte");

		SuppressMethod tested = new SuppressMethod();
		assertEquals("A method returning a byte should return 0 after suppressing method code.", 0, tested.getByte());
	}

	@Test
	public void testGetShort() throws Exception {
		suppressMethod(SuppressMethod.class, "getShort");

		SuppressMethod tested = new SuppressMethod();
		assertEquals("A method returning a short should return 0 after suppressing method code.", 0, tested.getShort());
	}

	@Test
	public void testGetInt() throws Exception {
		suppressMethod(SuppressMethod.class, "getInt");

		SuppressMethod tested = new SuppressMethod();
		assertEquals("A method returning an int should return 0 after suppressing method code.", 0, tested.getInt());
	}

	@Test
	public void testGetLong() throws Exception {
		suppressMethod(SuppressMethod.class, "getLong");

		SuppressMethod tested = new SuppressMethod();
		assertEquals("A method returning a long should return 0 after suppressing method code.", 0, tested.getLong());
	}

	@Test
	public void testGetBoolean() throws Exception {
		suppressMethod(SuppressMethod.class, "getBoolean");

		SuppressMethod tested = new SuppressMethod();
		assertFalse("A method returning a boolean should return false after suppressing method code.", tested.getBoolean());
	}

	@Test
	public void testGetFloat() throws Exception {
		suppressMethod(SuppressMethod.class, "getFloat");

		SuppressMethod tested = new SuppressMethod();
		assertEquals("A method returning a float should return 0.0f after suppressing method code.", 0.0f, tested.getFloat(), 0);
	}

	@Test
	public void testGetDouble() throws Exception {
		suppressMethod(SuppressMethod.class, "getDouble");

		SuppressMethod tested = new SuppressMethod();
		assertEquals("A method returning a double should return 0.0d after suppressing method code.", 0.0d, tested.getDouble(), 0);
	}

	@Test
	public void testGetDouble_parameter() throws Exception {
		suppressMethod(SuppressMethod.class, "getDouble", new Class<?>[] { double.class });

		SuppressMethod tested = new SuppressMethod();
		assertEquals("A method returning a double should return 0.0d after suppressing method code.", 0.0d, tested.getDouble(8.7d), 0);
	}

	@Test
	public void testInvokeVoid() throws Exception {
		suppressMethod(SuppressMethod.class, "invokeVoid", new Class<?>[] { StringBuilder.class });

		SuppressMethod tested = new SuppressMethod();
		// Should not cause an NPE when suppressing code.
		tested.invokeVoid(null);
	}

	@Test
	public void testInvokeVoid_noParameterTypeSupplied() throws Exception {
		suppressMethod(SuppressMethod.class, "invokeVoid");

		SuppressMethod tested = new SuppressMethod();
		// Should not cause an NPE when suppressing code.
		tested.invokeVoid(null);
	}
}
