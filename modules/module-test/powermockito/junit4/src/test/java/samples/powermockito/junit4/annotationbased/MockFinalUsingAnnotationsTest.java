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
package samples.powermockito.junit4.annotationbased;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import samples.finalmocking.FinalDemo;

/**
 * Test class to demonstrate non-static final mocking with Mockito and PowerMock
 * annotations.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(FinalDemo.class)
public class MockFinalUsingAnnotationsTest {

	@Mock
	private FinalDemo usingMockitoMockAnnotation;

	@SuppressWarnings("deprecation")
	@org.mockito.MockitoAnnotations.Mock
	private FinalDemo usingDeprecatedMockitoMockAnnotation;

	@SuppressWarnings("deprecation")
	@org.powermock.core.classloader.annotations.Mock
	private FinalDemo usingPowerMockMockAnnotation;

	@Test
	public void assertMockFinalWithMockitoMockAnnotationWorks() throws Exception {
		final String argument = "hello";

		assertNull(usingMockitoMockAnnotation.say(argument));

		verify(usingMockitoMockAnnotation).say(argument);
	}

	@Test
	public void assertMockFinalWithDeprecatedMockitoMockAnnotationWorks() throws Exception {
		final String argument = "hello";

		assertNull(usingDeprecatedMockitoMockAnnotation.say(argument));

		verify(usingDeprecatedMockitoMockAnnotation).say(argument);
	}

	@Test
	public void assertMockFinalWhenUsingPowerMockMockAnnotationWorks() throws Exception {
		final String argument = "hello";

		assertNull(usingPowerMockMockAnnotation.say(argument));

		verify(usingPowerMockMockAnnotation).say(argument);
	}
}
