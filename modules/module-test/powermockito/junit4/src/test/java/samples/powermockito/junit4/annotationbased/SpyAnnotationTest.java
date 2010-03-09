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
package samples.powermockito.junit4.annotationbased;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;
import static org.powermock.api.mockito.PowerMockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import samples.partialmocking.PrivatePartialMockingExample;

/**
 * Asserts that spying on private methods work with PowerMock when using
 * {@link Spy} annotation.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(PrivatePartialMockingExample.class)
public class SpyAnnotationTest {

	@Spy
	private PrivatePartialMockingExample underTest = new PrivatePartialMockingExample();

	@Test
	public void spyingOnPrivateMethodsWorksWithSpyAnnotation() throws Exception {
		final String expected = "TEST VALUE";
		final String nameOfMethodToMock = "methodToMock";
		final String input = "input";
		when(underTest, nameOfMethodToMock, input).thenReturn(expected);

		assertEquals(expected, underTest.methodToTest());

		verifyPrivate(underTest).invoke(nameOfMethodToMock, input);
	}

}
