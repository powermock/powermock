/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.powermock.modules.test.mockito.junit4.agent;

import org.junit.Rule;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;
import samples.finalmocking.FinalDemo;
import samples.privateandfinal.PrivateFinal;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.powermock.api.support.membermodification.MemberMatcher.method;

/**
 * Test class to demonstrate non-static final mocking with Mockito.
 */
@PrepareForTest( { FinalDemo.class, PrivateFinal.class })
public class FinalDemoTest {

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

	@Test
	public void assertMockFinalWithNoExpectationsWorks() throws Exception {
        final String argument = "hello";

		FinalDemo tested = mock(FinalDemo.class);

		assertNull(tested.say(argument));

		verify(tested).say(argument);
	}

	@Test
	public void assertMockFinalWithExpectationsWorks() throws Exception {
		final String argument = "hello";
		final String expected = "Hello altered World";

		FinalDemo tested = mock(FinalDemo.class);

		when(tested.say(argument)).thenReturn(expected);

		final String actual = tested.say(argument);

		verify(tested).say(argument);

		assertEquals("Expected and actual did not match", expected, actual);
	}

	@Test
	public void assertFinalNativeWithExpectationsWorks() throws Exception {
		final String expected = "Hello altered World";
		final String argument = "hello";

		FinalDemo tested = mock(FinalDemo.class);

		when(tested.sayFinalNative(argument)).thenReturn("Hello altered World");

		String actual = tested.sayFinalNative(argument);

		verify(tested).sayFinalNative(argument);
		assertEquals("Expected and actual did not match", expected, actual);
	}

	@Test
	public void assertSpyingOnFinalInstanceMethodWorks() throws Exception {
		FinalDemo tested = new FinalDemo();
		FinalDemo spy = spy(tested);

		final String argument = "PowerMock";
		final String expected = "something";

		assertEquals("Hello " + argument, spy.say(argument));
		when(spy.say(argument)).thenReturn(expected);
		assertEquals(expected, spy.say(argument));
	}

	@Test(expected = ArrayStoreException.class)
	public void assertSpyingOnFinalVoidInstanceMethodWorks() throws Exception {
		FinalDemo tested = new FinalDemo();
		FinalDemo spy = spy(tested);

		doThrow(new ArrayStoreException()).when(spy).finalVoidCallee();

		spy.finalVoidCaller();
	}

	@Test
	public void assertSpyingOnPrivateFinalInstanceMethodWorks() throws Exception {
		PrivateFinal spy = spy(new PrivateFinal());

		final String expected = "test";
		assertEquals("Hello " + expected, spy.say(expected));

		when(spy, "sayIt", isA(String.class)).thenReturn(expected);

		assertEquals(expected, spy.say(expected));

		verifyPrivate(spy, times(2)).invoke("sayIt", expected);
	}

	@Test
	public void assertSpyingOnPrivateFinalInstanceMethodWorksWhenUsingJavaLangReflectMethod() throws Exception {
		PrivateFinal spy = spy(new PrivateFinal());

		final String expected = "test";
		assertEquals("Hello " + expected, spy.say(expected));
		
		final Method methodToExpect = method(PrivateFinal.class, "sayIt");
		when(spy, methodToExpect).withArguments(isA(String.class)).thenReturn(expected);

		assertEquals(expected, spy.say(expected));

		verifyPrivate(spy, times(2)).invoke(methodToExpect).withArguments(expected);
	}
}
