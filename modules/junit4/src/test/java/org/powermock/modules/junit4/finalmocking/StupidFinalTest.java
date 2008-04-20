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
package org.powermock.modules.junit4.finalmocking;

import static org.easymock.EasyMock.expect;
import static org.powermock.PowerMock.createMock;
import static org.powermock.PowerMock.replay;
import static org.powermock.PowerMock.verify;
import static org.junit.Assert.assertEquals;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import samples.finalmocking.StupidFinal;


/**
 * Test class to demonstrate non-static final mocking.
 * 
 * @author Johan Haleby
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(StupidFinal.class)
public class StupidFinalTest {

	@Test
	public void testSay() throws Exception {
		StupidFinal tested = createMock(StupidFinal.class);
		String expected = "Hello altered World";
		expect(tested.say("hello")).andReturn("Hello altered World");
		replay(tested);

		String actual = tested.say("hello");

		verify(tested);
		assertEquals("Expected and actual did not match", expected, actual);

		// Should no longer be mocked by now.
		String actual2 = tested.say("world");
		assertEquals("Hello world", actual2);
	}

	@Test
	public void testSayFinalNative() throws Exception {
		StupidFinal tested = createMock(StupidFinal.class);
		String expected = "Hello altered World";
		expect(tested.sayFinalNative("hello")).andReturn("Hello altered World");
		replay(tested);

		String actual = tested.sayFinalNative("hello");

		verify(tested);
		assertEquals("Expected and actual did not match", expected, actual);

		// Should no longer be mocked by now.
		String actual2 = tested.say("world");
		assertEquals("Hello world", actual2);
	}
}
