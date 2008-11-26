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
import static org.junit.Assert.assertEquals;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import samples.finalmocking.FinalDemo;

/**
 * This test demonstrates that the issue at
 * http://code.google.com/p/powertest/issues/detail?id=63 is solved.
 * 
 * <p>
 * The bug was that if there were only one test case and the
 * <code>@PrepareForTest</code> annotation was placed at the test method instead
 * of the class the PowerMock JUnit runner would detect an extra test case.
 * 
 */
@RunWith(PowerMockRunner.class)
public class NoDuplicateTest {

	@Test
	@PrepareForTest(FinalDemo.class)
	@Ignore("The current solution doesn't work because other tests fail because of it. We need to look into it more.")
	public void testSay() throws Exception {
		FinalDemo tested = createMock(FinalDemo.class);
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
}
