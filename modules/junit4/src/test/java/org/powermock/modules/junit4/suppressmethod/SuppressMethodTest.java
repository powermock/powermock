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

import static org.powermock.PowerMock.suppressMethodCode;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import samples.suppressmethod.SuppressMethod;


@RunWith(PowerMockRunner.class)
@PrepareForTest(SuppressMethod.class)
public class SuppressMethodTest {

	@Test
	public void testGetObject() throws Exception {
		suppressMethodCode(SuppressMethod.class, "getObject");

		SuppressMethod tested = new SuppressMethod();
		assertNull(
				"A method returning Object should return null after suppressing method code.",
				tested.getObject());
	}

	@Test
	public void testGetByte() throws Exception {
		suppressMethodCode(SuppressMethod.class, "getByte");

		SuppressMethod tested = new SuppressMethod();
		assertEquals(
				"A method returning a byte should return 0 after suppressing method code.",
				0, tested.getByte());
	}

	@Test
	public void testGetShort() throws Exception {
		suppressMethodCode(SuppressMethod.class, "getShort");

		SuppressMethod tested = new SuppressMethod();
		assertEquals(
				"A method returning a short should return 0 after suppressing method code.",
				0, tested.getShort());
	}

	@Test
	public void testGetInt() throws Exception {
		suppressMethodCode(SuppressMethod.class, "getInt");

		SuppressMethod tested = new SuppressMethod();
		assertEquals(
				"A method returning an int should return 0 after suppressing method code.",
				0, tested.getInt());
	}

	@Test
	public void testGetLong() throws Exception {
		suppressMethodCode(SuppressMethod.class, "getLong");

		SuppressMethod tested = new SuppressMethod();
		assertEquals(
				"A method returning a long should return 0 after suppressing method code.",
				0, tested.getLong());
	}

	@Test
	public void testGetBoolean() throws Exception {
		suppressMethodCode(SuppressMethod.class, "getBoolean");

		SuppressMethod tested = new SuppressMethod();
		assertFalse(
				"A method returning a boolean should return false after suppressing method code.",
				tested.getBoolean());
	}

	@Test
	public void testGetFloat() throws Exception {
		suppressMethodCode(SuppressMethod.class, "getFloat");

		SuppressMethod tested = new SuppressMethod();
		assertEquals(
				"A method returning a float should return 0.0f after suppressing method code.",
				0.0f, tested.getFloat(), 0);
	}

	@Test
	public void testGetDouble() throws Exception {
		suppressMethodCode(SuppressMethod.class, "getDouble");

		SuppressMethod tested = new SuppressMethod();
		assertEquals(
				"A method returning a double should return 0.0d after suppressing method code.",
				0.0d, tested.getDouble(), 0);
	}

	@Test
	public void testGetDouble_parameter() throws Exception {
		suppressMethodCode(SuppressMethod.class, "getDouble", double.class);

		SuppressMethod tested = new SuppressMethod();
		assertEquals(
				"A method returning a double should return 0.0d after suppressing method code.",
				0.0d, tested.getDouble(8.7d), 0);
	}

	@Test
	public void testInvokeVoid() throws Exception {
		suppressMethodCode(SuppressMethod.class, "invokeVoid",
				StringBuilder.class);

		SuppressMethod tested = new SuppressMethod();
		// Should not cause an NPE when suppressing code.
		tested.invokeVoid(null);
	}

	@Test
	public void testInvokeVoid_noParameterTypeSupplied() throws Exception {
		suppressMethodCode(SuppressMethod.class, "invokeVoid");

		SuppressMethod tested = new SuppressMethod();
		// Should not cause an NPE when suppressing code.
		tested.invokeVoid(null);
	}
}
