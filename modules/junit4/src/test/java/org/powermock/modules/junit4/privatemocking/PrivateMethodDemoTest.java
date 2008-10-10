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
package org.powermock.modules.junit4.privatemocking;

import static org.powermock.PowerMock.expectPrivate;
import static org.powermock.PowerMock.createPartialMock;
import static org.powermock.PowerMock.replay;
import static org.powermock.PowerMock.verify;
import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;

import org.powermock.Whitebox;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.easymock.EasyMock;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import samples.privatemocking.PrivateMethodDemo;


/**
 * Test class to demonstrate private method mocking.
 * 
 * @author Johan Haleby
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(PrivateMethodDemo.class)
public class PrivateMethodDemoTest {

	@Test
	public void testSay() throws Exception {
		PrivateMethodDemo tested = createPartialMock(PrivateMethodDemo.class,
				"sayIt", String.class);
		String expected = "Hello altered World";
		expectPrivate(tested, "sayIt", "name").andReturn(expected);
		replay(tested);

		String actual = tested.say("name");

		verify(tested);
		assertEquals("Expected and actual did not match", expected, actual);
	}

	@Test
	public void testSayIt_name() throws Exception {
		PrivateMethodDemo tested = new PrivateMethodDemo();
		String expected = "Hello altered World";

		String actual = (String) Whitebox.invokeMethod(tested, "sayIt",
				"altered World");

		assertEquals("Expected and actual did not match", expected, actual);
	}

	@Test
	public void testSayIt() throws Exception {

		PrivateMethodDemo tested = new PrivateMethodDemo();
		String expected = "Hello world";

		String actual = (String) Whitebox.invokeMethod(tested, "sayIt");

		assertEquals("Expected and actual did not match", expected, actual);
	}

	@Test
	public void testMethodCallingPrimitiveTestMethod() throws Exception {
		PrivateMethodDemo tested = createPartialMock(PrivateMethodDemo.class,
				"aTestMethod", int.class);

		final int expected = 42;
		expectPrivate(tested, "aTestMethod", new Class<?>[] { int.class }, 10)
				.andReturn(expected);

		replay(tested);

		final int actual = tested.methodCallingPrimitiveTestMethod();

		verify(tested);

		assertEquals("Expected and actual did not match", expected, actual);
	}

	@Test
	public void testMethodCallingWrappedTestMethod() throws Exception {
		PrivateMethodDemo tested = createPartialMock(PrivateMethodDemo.class,
				"aTestMethod", Integer.class);

		final int expected = 42;
		expectPrivate(tested, "aTestMethod", new Class<?>[] { Integer.class },
				new Integer(15)).andReturn(expected);

		replay(tested);

		final int actual = tested.methodCallingWrappedTestMethod();

		verify(tested);

		assertEquals("Expected and actual did not match", expected, actual);
	}

	@Test
	public void testMethodCallingWrappedTestMethod_reflectiveMethodLookup()
			throws Exception {
		PrivateMethodDemo tested = createPartialMock(PrivateMethodDemo.class,
				"aTestMethod", Integer.class);

		final Method methodToExpect = PrivateMethodDemo.class
				.getDeclaredMethod("aTestMethod", Integer.class);

		final int expected = 42;
		expectPrivate(tested, methodToExpect, 15).andReturn(expected);

		replay(tested);

		final int actual = tested.methodCallingWrappedTestMethod();

		verify(tested);

		assertEquals("Expected and actual did not match", expected, actual);
	}

	@Test
	@Ignore
	public void testExpectPrivateWithArrayMatcher()
			throws Exception {
		PrivateMethodDemo tested = createPartialMock(PrivateMethodDemo.class,
				"doArrayInternal");

		expectPrivate(tested, "doArrayInternal", EasyMock.aryEq(new String[] {"hello"}));

		replay(tested);

		tested.doArrayStuff("hello");

		verify(tested);
	}

	@Test
	public void testExpectPrivateWithObjectMatcher()
			throws Exception {
		PrivateMethodDemo tested = createPartialMock(PrivateMethodDemo.class,
				"doObjectInternal");

		expectPrivate(tested, "doObjectInternal", EasyMock.isA(CharSequence.class));

		replay(tested);

		tested.doObjectStuff("hello");

		verify(tested);
	}

}
