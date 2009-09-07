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
package samples.junit3.annotationbased;

import static org.easymock.EasyMock.expect;

import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import junit.framework.TestCase;

import org.powermock.api.easymock.annotation.Mock;
import org.powermock.api.easymock.powermocklistener.AnnotationEnabler;
import org.powermock.core.classloader.annotations.PowerMockListener;
import org.powermock.core.classloader.annotations.PrepareForTest;

import samples.privateandfinal.PrivateFinal;

/**
 * Test class will demonstrate annotation support for JUnit 3 when this feature
 * is available.
 */
@PrepareForTest(PrivateFinal.class)
@PowerMockListener(AnnotationEnabler.class)
public class PrivateFinalWithAnnotationTest extends TestCase {

//	@SuppressWarnings("unchecked")
//	public static TestSuite suite() throws Exception {
//		return new PowerMockSuite("Unit tests for " + PrivateFinalWithAnnotationTest.class.getSimpleName(), PrivateFinalWithAnnotationTest.class);
//	}

	@Mock
	private PrivateFinal tested;

	public void no_testAnnotationWorks() throws Exception {
		final String argument = "name";
		String expected = "Hello altered World";

		expect(tested.say(argument)).andReturn(expected);
		replay(tested);

		String actual = tested.say(argument);

		verify(tested);
		assertEquals("Expected and actual did not match", expected, actual);
	}
	
	public void testDummy() throws Exception {
	}
}
