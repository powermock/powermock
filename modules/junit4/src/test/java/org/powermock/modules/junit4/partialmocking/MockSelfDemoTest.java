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
package org.powermock.modules.junit4.partialmocking;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.powermock.PowerMock.expectPrivate;
import static org.powermock.PowerMock.createPartialMockForAllMethodsExcept;
import static org.powermock.PowerMock.createPartialMock;
import static org.powermock.PowerMock.replay;
import static org.powermock.PowerMock.verify;
import static org.junit.Assert.assertEquals;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import samples.partialmocking.MockSelfDemo;


@RunWith(PowerMockRunner.class)
@PrepareForTest(MockSelfDemo.class)
public class MockSelfDemoTest {

	private MockSelfDemo tested;

	@Test
	public void testMockMultiple_ok() throws Exception {
		tested = createPartialMock(MockSelfDemo.class, "aMethod2", "getString");

		tested.aMethod2();
		expectLastCall().times(1);

		final String expected = "Hello altered world";
		expect(tested.getString("world")).andReturn(expected);

		replay(tested);

		String actual = tested.aMethod();

		verify(tested);

		assertEquals("Result ought to be \"Hello altered world\".", expected,
				actual);
	}

	@Test
	public void testMockMultiple_sameName() throws Exception {
		tested = createPartialMock(MockSelfDemo.class, "getString");

		final String firstString = "A message: ";
		expectPrivate(tested, "getString").andReturn(firstString);

		final String secondString = "altered world";
		expect(tested.getString("world2")).andReturn(secondString);
		final String expected = firstString + secondString;

		replay(tested);

		String actual = tested.getTwoStrings();

		verify(tested);

		assertEquals("Result ought to be \"A message:Hello altered world\".",
				expected, actual);
	}

	@Test
	public void testMockSingleMethod() throws Exception {
		tested = createPartialMock(MockSelfDemo.class, "timesTwo", int.class);

		final int expectedInt = 2;
		final int expectedInteger = 8;
		expect(tested.timesTwo(4)).andReturn(expectedInt);

		replay(tested);

		int actualInt = tested.timesTwo(4);
		int actualInteger = tested.timesTwo(new Integer(4));

		verify(tested);

		assertEquals(expectedInt, actualInt);
		assertEquals(expectedInteger, actualInteger);
	}

	@Test
	public void testMockAllExcept_parametersDefined() throws Exception {
		tested = createPartialMockForAllMethodsExcept(MockSelfDemo.class, "getString2", String.class);

		final String expected = "Hello altered world";
		expect(tested.getString2()).andReturn(expected);

		replay(tested);
		assertEquals(expected, tested.getString2());
		assertEquals("Hello string", tested.getString2("string"));
		verify(tested);
	}

	@Test
	public void testMockAllExcept_single() throws Exception {
		tested = createPartialMockForAllMethodsExcept(MockSelfDemo.class, "aMethod");
		tested.aMethod2();
		expectLastCall().times(1);

		final String expected = "Hello altered world";
		expect(tested.getString("world")).andReturn(expected);

		replay(tested);

		String actual = tested.aMethod();

		verify(tested);

		assertEquals("Result ought to be \"Hello altered world\".", expected,
				actual);
	}

	@Test
	public void testMockAllExcept_multiple() throws Exception {
		tested = createPartialMockForAllMethodsExcept(MockSelfDemo.class, "timesTwo", "timesThree");

		final String expected = "A new value";
		expect(tested.getString2()).andReturn(expected);

		replay(tested);

		assertEquals(4, tested.timesTwo(2));
		assertEquals(4, tested.timesTwo(new Integer(2)));
		assertEquals(6, tested.timesThree(2));
		assertEquals(expected, tested.getString2());

		verify(tested);

	}
}
