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
package org.powermock.modules.junit4.expectnew;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.powermock.PowerMock.createMock;
import static org.powermock.PowerMock.expectNew;
import static org.powermock.PowerMock.replay;
import static org.powermock.PowerMock.verify;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.Whitebox;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import samples.expectnew.ExpectNewDemo;
import samples.newmocking.MyClass;

/**
 * Test class to demonstrate new instance mocking using expectNew(..).
 * 
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest( { MyClass.class, ExpectNewDemo.class, DataInputStream.class })
public class ExpectNewDemoTest {

	@Test
	public void testNewWithCheckedException() throws Exception {
		ExpectNewDemo tested = new ExpectNewDemo();

		final String expectedFailMessage = "testing checked exception";
		expectNew(MyClass.class).andThrow(new IOException(expectedFailMessage));

		replay(MyClass.class);

		try {
			tested.throwExceptionAndWrapInRunTimeWhenInvoction();
			fail("Should throw a checked Exception!");
		} catch (RuntimeException e) {
			assertTrue(e.getCause() instanceof IOException);
			assertEquals(expectedFailMessage, e.getMessage());
		}

		verify(MyClass.class);
	}

	@Test
	public void testGetMessage() throws Exception {
		ExpectNewDemo tested = new ExpectNewDemo();
		MyClass myClassMock = createMock(MyClass.class);

		expectNew(MyClass.class).andReturn(myClassMock);

		String expected = "Hello altered World";
		expect(myClassMock.getMessage()).andReturn("Hello altered World");

		replay(myClassMock, MyClass.class);

		String actual = tested.getMessage();

		verify(myClassMock, MyClass.class);
		assertEquals("Expected and actual did not match", expected, actual);
	}

	@Test
	public void testGetMessageWithArgument() throws Exception {
		ExpectNewDemo tested = new ExpectNewDemo();

		MyClass myClassMock = createMock(MyClass.class);
		expectNew(MyClass.class).andReturn(myClassMock);

		String expected = "Hello altered World";
		expect(myClassMock.getMessage("test")).andReturn("Hello altered World");
		replay(myClassMock, MyClass.class);

		String actual = tested.getMessageWithArgument();

		verify(myClassMock, MyClass.class);
		assertEquals("Expected and actual did not match", expected, actual);
	}

	@Test
	public void testInvokeVoidMethod() throws Exception {
		ExpectNewDemo tested = new ExpectNewDemo();

		MyClass myClassMock = createMock(MyClass.class);
		expectNew(MyClass.class).andReturn(myClassMock);

		myClassMock.voidMethod();
		expectLastCall().times(1);

		replay(myClassMock, MyClass.class);

		tested.invokeVoidMethod();

		verify(myClassMock, MyClass.class);
	}

	@Test
	public void testNewWithRuntimeException() throws Exception {
		ExpectNewDemo tested = new ExpectNewDemo();

		final String expectedFailMessage = "testing";
		expectNew(MyClass.class).andThrow(
				new RuntimeException(expectedFailMessage));

		replay(MyClass.class);

		try {
			tested.throwExceptionWhenInvoction();
			fail("Should throw RuntimeException!");
		} catch (RuntimeException e) {
			assertEquals(expectedFailMessage, e.getMessage());
		}

		verify(MyClass.class);
	}

	@Test
	public void testEasyMock() throws Exception {
		MyClass myClassMock1 = createMock(MyClass.class);
		expect(myClassMock1.getMessage()).andReturn("Hello");
		expect(myClassMock1.getMessage()).andReturn("World");
		replay(myClassMock1);
		assertEquals("Hello", myClassMock1.getMessage());
		assertEquals("World", myClassMock1.getMessage());
		verify(myClassMock1);
	}

	@Test
	public void testMultipleNew() throws Exception {
		ExpectNewDemo tested = new ExpectNewDemo();

		MyClass myClassMock1 = createMock(MyClass.class);
		MyClass myClassMock2 = createMock(MyClass.class);

		expectNew(MyClass.class).andReturn(myClassMock1);
		expectNew(MyClass.class).andReturn(myClassMock2);

		expect(myClassMock1.getMessage()).andReturn("Hello ");
		expect(myClassMock2.getMessage()).andReturn("World");

		replay(myClassMock1, myClassMock2, MyClass.class);

		final String actual = tested.multipleNew();

		verify(myClassMock1, myClassMock2, MyClass.class);

		assertEquals("Hello World", actual);
	}

	@Test
	public void testSimpleMultipleNew() throws Exception {
		ExpectNewDemo tested = new ExpectNewDemo();

		MyClass myClassMock1 = createMock(MyClass.class);

		expectNew(MyClass.class).andReturn(myClassMock1).times(3);

		replay(myClassMock1, MyClass.class);

		tested.simpleMultipleNew();

		verify(myClassMock1, MyClass.class);
	}

	@Test
	public void testSimpleMultipleNew_tooManyTimesExpected() throws Exception {
		ExpectNewDemo tested = new ExpectNewDemo();

		MyClass myClassMock1 = createMock(MyClass.class);

		expectNew(MyClass.class).andReturn(myClassMock1).times(4);

		replay(myClassMock1, MyClass.class);

		tested.simpleMultipleNew();

		try {
			verify(myClassMock1, MyClass.class);
			fail("Should throw AssertionError.");
		} catch (AssertionError e) {
			assertEquals(
					"\nExpectation failure on verify:\nExpected a new instance call on "
							+ "class samples.newmocking.MyClass 4 times but was actually 3 times.",
					e.getMessage());
		}
	}

	@Test
	public void testSimpleMultipleNew_tooFewTimesExpected() throws Exception {
		ExpectNewDemo tested = new ExpectNewDemo();

		MyClass myClassMock1 = createMock(MyClass.class);

		expectNew(MyClass.class).andReturn(myClassMock1).times(2);

		replay(myClassMock1, MyClass.class);
		try {
			tested.simpleMultipleNew();
			fail("Should throw AssertionError.");
		} catch (AssertionError e) {
			assertTrue(e.getMessage().contains(
					"Expected a new instance call on class "
							+ MyClass.class.getName()
							+ " 2 times but was actually 2 (+1) times."));
		}
	}

	/**
	 * Verifies that the issue
	 * http://code.google.com/p/powermock/issues/detail?id=10 is solved.
	 */
	@Test
	public void testSimpleMultipleNewPrivate_tooFewTimesExpected()
			throws Exception {
		ExpectNewDemo tested = new ExpectNewDemo();

		MyClass myClassMock1 = createMock(MyClass.class);

		expectNew(MyClass.class).andReturn(myClassMock1).times(2);

		replay(myClassMock1, MyClass.class);
		try {
			Whitebox.invokeMethod(tested, "simpleMultipleNewPrivate");
			fail("Should throw RuntimeException.");
		} catch (RuntimeException e) {
			assertTrue(e.getMessage().contains(
					"Expected a new instance call on class "
							+ MyClass.class.getName()
							+ " 2 times but was actually 2 (+1) times."));
		}
	}

	@Test
	public void testSimpleMultipleNewPrivate_ok() throws Exception {
		ExpectNewDemo tested = new ExpectNewDemo();

		MyClass myClassMock1 = createMock(MyClass.class);

		expectNew(MyClass.class).andReturn(myClassMock1).times(3);

		replay(myClassMock1, MyClass.class);
		Whitebox.invokeMethod(tested, "simpleMultipleNewPrivate");
	}

	@Test
	public void testSimpleSingleNew_withOnce() throws Exception {
		ExpectNewDemo tested = new ExpectNewDemo();

		MyClass myClassMock1 = createMock(MyClass.class);

		expectNew(MyClass.class).andReturn(myClassMock1).once();

		replay(myClassMock1, MyClass.class);
		tested.simpleSingleNew();
		verify(myClassMock1, MyClass.class);
	}

	@Test
	public void testSimpleSingleNew_withAtLeastOnce() throws Exception {
		ExpectNewDemo tested = new ExpectNewDemo();

		MyClass myClassMock1 = createMock(MyClass.class);

		expectNew(MyClass.class).andReturn(myClassMock1).atLeastOnce();

		replay(myClassMock1, MyClass.class);
		tested.simpleSingleNew();
		verify(myClassMock1, MyClass.class);
	}

	@Test
	public void testSimpleMultipleNew_withAtLeastOnce() throws Exception {
		ExpectNewDemo tested = new ExpectNewDemo();

		MyClass myClassMock1 = createMock(MyClass.class);

		expectNew(MyClass.class).andReturn(myClassMock1).atLeastOnce();

		replay(myClassMock1, MyClass.class);
		tested.simpleMultipleNew();
		verify(myClassMock1, MyClass.class);
	}

	@Test
	public void testSimpleMultipleNew_withRange_lowerBoundLessThan0()
			throws Exception {
		MyClass myClassMock1 = createMock(MyClass.class);

		try {

			expectNew(MyClass.class).andReturn(myClassMock1).times(-20, 2);
			fail("Should throw IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertEquals("minimum must be >= 0", e.getMessage());
		}
	}

	@Test
	public void testSimpleMultipleNew_withRange_upperBoundLessThan0()
			throws Exception {

		MyClass myClassMock1 = createMock(MyClass.class);
		try {
			expectNew(MyClass.class).andReturn(myClassMock1).times(-1, -2);
			fail("Should throw IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().contains("<="));
		}
	}

	@Test
	public void testSimpleMultipleNew_withRange_upperBoundLessThanLowerBound()
			throws Exception {

		MyClass myClassMock1 = createMock(MyClass.class);
		try {
			expectNew(MyClass.class).andReturn(myClassMock1).times(10, 2);
			fail("Should throw IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().contains("<="));
		}
	}

	@Test
	public void testSimpleMultipleNew_withRange_OK() throws Exception {
		ExpectNewDemo tested = new ExpectNewDemo();
		MyClass myClassMock1 = createMock(MyClass.class);

		expectNew(MyClass.class).andReturn(myClassMock1).times(1, 5);

		replay(myClassMock1, MyClass.class);

		tested.simpleMultipleNew();

		verify(myClassMock1, MyClass.class);
	}

	@Test
	public void testSimpleMultipleNew_anyTimes() throws Exception {
		ExpectNewDemo tested = new ExpectNewDemo();
		MyClass myClassMock1 = createMock(MyClass.class);

		expectNew(MyClass.class).andReturn(myClassMock1).anyTimes();

		replay(myClassMock1, MyClass.class);

		tested.simpleMultipleNew();

		verify(myClassMock1, MyClass.class);
	}

	@Test
	public void testSimpleMultipleNew_withRange_notWithinRange()
			throws Exception {
		ExpectNewDemo tested = new ExpectNewDemo();
		MyClass myClassMock1 = createMock(MyClass.class);

		expectNew(MyClass.class).andReturn(myClassMock1).times(5, 7);

		replay(myClassMock1, MyClass.class);

		tested.simpleMultipleNew();

		try {
			verify(myClassMock1, MyClass.class);
			fail("Should throw AssertionError.");
		} catch (AssertionError e) {
			assertEquals(
					"\nExpectation failure on verify:\nExpected a new instance call on "
							+ "class samples.newmocking.MyClass between 5 and 7 times but was actually 3 times.",
					e.getMessage());
		}
	}

	@Test
	public void testAlternativeFlow() throws Exception {
		ExpectNewDemo tested = new ExpectNewDemo();
		expectNew(DataInputStream.class)
				.andThrow(new RuntimeException("error"));

		replay(ExpectNewDemo.class, DataInputStream.class);

		InputStream stream = tested.alternativePath();

		verify(ExpectNewDemo.class, DataInputStream.class);

		assertNotNull("The returned inputstream should not be null.", stream);
		assertTrue(
				"The returned inputstream should be an instance of ByteArrayInputStream.",
				stream instanceof ByteArrayInputStream);
	}

	@Test
	public void testSimpleMultipleNewPrivate_tooManyTimesExpected()
			throws Exception {
		ExpectNewDemo tested = new ExpectNewDemo();

		MyClass myClassMock1 = createMock(MyClass.class);

		expectNew(MyClass.class).andReturn(myClassMock1).times(4);

		replay(myClassMock1, MyClass.class);
		try {
			Whitebox.invokeMethod(tested, "simpleMultipleNewPrivate");
			verify(myClassMock1, MyClass.class);
			fail("Should throw an exception!.");
		} catch (AssertionError e) {
			assertTrue(e.getMessage().contains(
					"4 times but was actually 3 times"));
		}
	}
}
