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
package samples.junit4.privatemocking;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.easymock.PowerMock.createPartialMock;
import static org.powermock.api.easymock.PowerMock.expectPrivate;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

import java.lang.reflect.Method;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

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
	public void testMockPrivateMethod() throws Exception {
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
	public void testMockPrivateMethod_withArgument() throws Exception {
		PrivateMethodDemo tested = new PrivateMethodDemo();
		String expected = "Hello altered World";

		String actual = (String) Whitebox.invokeMethod(tested, "sayIt",
				"altered World");

		assertEquals("Expected and actual did not match", expected, actual);
	}

	@Test
	public void testInvokePrivateMethod() throws Exception {

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
	public void testExpectPrivateWithArrayMatcher() throws Exception {
		PrivateMethodDemo tested = createPartialMock(PrivateMethodDemo.class,
				"doArrayInternal");

		expectPrivate(tested, "doArrayInternal", EasyMock
				.aryEq((Object[]) new String[] { "hello" }));

		replay(tested);

		tested.doArrayStuff("hello");

		verify(tested);
	}

	@Test
	public void testExpectPrivateWithObjectMatcher() throws Exception {
		PrivateMethodDemo tested = createPartialMock(PrivateMethodDemo.class,
				"doObjectInternal");

		expectPrivate(tested, "doObjectInternal", EasyMock
				.isA(CharSequence.class));

		replay(tested);

		tested.doObjectStuff("hello");

		verify(tested);
	}

	@Test
	public void testExpectPrivateMethodWithVarArgsParameters() throws Exception {
		final String methodToExpect = "varArgsMethod";
		final int expected = 7;
		final int valueA = 2;
		final int valueB = 3;
		PrivateMethodDemo tested = createPartialMock(PrivateMethodDemo.class,
				methodToExpect);

		expectPrivate(tested, methodToExpect, valueA, valueB).andReturn(
				expected);

		replay(tested);

		assertEquals(expected, tested.invokeVarArgsMethod(valueA, valueB));

		verify(tested);
	}

	@Test
	public void testExpectPrivateMethodWithoutSpecifyingMethodName_firstArgumentIsOfStringType()
			throws Exception {
		final String expected = "Hello world";
		PrivateMethodDemo tested = createPartialMock(PrivateMethodDemo.class,
				"sayIt");

		expectPrivate(tested, (String) null, "firstName", " ", "lastName")
				.andReturn(expected);

		replay(tested);

		assertEquals(expected, tested.enhancedSay("firstName", "lastName"));

		verify(tested);
	}

	@Test
	public void testExpectPrivateMethodWithoutSpecifyingMethodName()
			throws Exception {
		final String expected = "Hello world";
		PrivateMethodDemo tested = createPartialMock(PrivateMethodDemo.class,
				"doSayYear");

		expectPrivate(tested, 22, "name").andReturn(expected);

		replay(tested);

		assertEquals(expected, tested.sayYear("name", 22));

		verify(tested);
	}
}
