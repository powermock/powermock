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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.powermocklistener.AnnotationEnabler;
import org.powermock.core.classloader.annotations.Mock;
import org.powermock.core.classloader.annotations.PowerMockListener;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import samples.partialmocking.PartialMockingExample;

/**
 * Asserts that partial mocking with Mockito works for non-final methods.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(PartialMockingExample.class)
@PowerMockListener(AnnotationEnabler.class)
public class PartialMockingExampleDefect {

	@Mock("methodToMock")
	private PartialMockingExample underTest;

	@Test
	public void partialMockitoMockingUsingAnnotaion() throws Exception {
		final String expected = "TEST VALUE";
		when(underTest.methodToMock()).thenReturn(expected);
		assertEquals(expected, underTest.methodToTest());

		verify(underTest).methodToMock();
	}

	@Test
	public void partialMockitoMockingUsingStandardMock() throws Exception {
		final String expected = "TEST VALUE";
		underTest = mock(PartialMockingExample.class, Whitebox.getMethods(PartialMockingExample.class, "methodToMock"));
		when(underTest.methodToMock()).thenReturn(expected);

		assertEquals(expected, underTest.methodToTest());

		verify(underTest).methodToMock();
	}
}
