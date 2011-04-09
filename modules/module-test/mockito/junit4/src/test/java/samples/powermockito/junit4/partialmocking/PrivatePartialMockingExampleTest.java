/*
 * Copyright 2009 the original author or authors.
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
package samples.powermockito.junit4.partialmocking;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.partialmocking.PrivatePartialMockingExample;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.powermock.api.support.membermodification.MemberMatcher.method;

/**
 * Asserts that partial mocking (spying) with PowerMockito works for non-final
 * private methods.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(PrivatePartialMockingExample.class)
public class PrivatePartialMockingExampleTest {

	@Test
	public void spyingOnPrivateMethodsWorks() throws Exception {
		final String expected = "TEST VALUE";
		PrivatePartialMockingExample underTest = spy(new PrivatePartialMockingExample());
		final String nameOfMethodToMock = "methodToMock";
		final String input = "input";
		when(underTest, nameOfMethodToMock, input).thenReturn(expected);

		assertEquals(expected, underTest.methodToTest());

		verifyPrivate(underTest).invoke(nameOfMethodToMock, input);
	}

	@Test
	public void partialMockingOfPrivateMethodsWorks() throws Exception {
		final String expected = "TEST VALUE";
		PrivatePartialMockingExample underTest = spy(new PrivatePartialMockingExample());
		final String nameOfMethodToMock = "methodToMock";
		final String input = "input";
		doReturn(expected).when(underTest, nameOfMethodToMock, input);

		assertEquals(expected, underTest.methodToTest());

		verifyPrivate(underTest).invoke(nameOfMethodToMock, input);
	}

	@Test
	public void spyingOnPrivateMethodsWorksWithoutSpecifyingMethodName() throws Exception {
		final String expected = "TEST VALUE";
		PrivatePartialMockingExample underTest = spy(new PrivatePartialMockingExample());
		final String input = "input";
		final Method methodToMock = method(PrivatePartialMockingExample.class, String.class);
		when(underTest, methodToMock).withArguments(input).thenReturn(expected);

		assertEquals(expected, underTest.methodToTest());

		verifyPrivate(underTest).invoke(methodToMock).withArguments(input);
	}

	@Test
	public void partialMockingOfPrivateMethodsWorksWithoutSpecifyingMethodName() throws Exception {
		final String expected = "TEST VALUE";
		PrivatePartialMockingExample underTest = spy(new PrivatePartialMockingExample());
		final String input = "input";
		final Method methodToMock = method(PrivatePartialMockingExample.class, String.class);
		doReturn(expected).when(underTest, methodToMock).withArguments(input);

		assertEquals(expected, underTest.methodToTest());

		verifyPrivate(underTest).invoke(methodToMock).withArguments(input);
	}
}
