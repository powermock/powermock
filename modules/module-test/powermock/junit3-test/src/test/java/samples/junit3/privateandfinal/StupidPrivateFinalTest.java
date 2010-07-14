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
package samples.junit3.privateandfinal;

import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.powermock.api.easymock.PowerMock.createPartialMock;
import static org.powermock.api.easymock.PowerMock.expectPrivate;
import junit.framework.TestCase;

import org.powermock.core.classloader.annotations.PrepareForTest;

import samples.privateandfinal.PrivateFinal;

/**
 * Test class to demonstrate private+final method mocking. No suite is added in
 * this class to demonstrate that you can run the test from an external test
 * suite.
 * 
 * @author Johan Haleby
 */
@PrepareForTest(PrivateFinal.class)
public class StupidPrivateFinalTest extends TestCase {

	public void testSay() throws Exception {

		PrivateFinal tested = createPartialMock(PrivateFinal.class,
				"sayIt");
		String expected = "Hello altered World";
		expectPrivate(tested, "sayIt", "name").andReturn(expected);
		replay(tested);

		String actual = tested.say("name");

		verify(tested);
		assertEquals("Expected and actual did not match", expected, actual);
	}

	public void testMultiMock() throws Exception {
		PrivateFinal tested1 = createPartialMock(PrivateFinal.class,
				"sayIt");
		String expected1 = "Hello altered World";
		expectPrivate(tested1, "sayIt", "name").andReturn(expected1);
		replay(tested1);
		PrivateFinal tested2 = createPartialMock(PrivateFinal.class,
				"sayIt");
		String expected2 = "Hello qweqweqwe";
		expectPrivate(tested2, "sayIt", "name").andReturn(expected2);
		replay(tested2);

		String actual1 = tested1.say("name");
		verify(tested1);
		assertEquals("Expected and actual did not match", expected1, actual1);
		String actual2 = tested2.say("name");
		verify(tested2);
		assertEquals("Expected and actual did not match", expected2, actual2);
	}
}
