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
package org.powermock.modules.junit4.replayall;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.powermock.PowerMock.createPartialMock;
import static org.powermock.PowerMock.expectPrivate;
import static org.powermock.PowerMock.mockStaticPartial;
import static org.powermock.PowerMock.replayAll;
import static org.powermock.PowerMock.verifyAll;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import samples.staticandinstance.StaticAndInstanceDemo;

/**
 * The purpose of this test is to try-out the replay all functionality in
 * PowerMock in combination with static and instance mocking.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(StaticAndInstanceDemo.class)
public class ReplayAllForStaticMethodsTest {

	@Test
	public void testMockStaticMethodAndInstanceMethod() throws Exception {
		mockStaticPartial(StaticAndInstanceDemo.class, "getStaticMessage");

		StaticAndInstanceDemo tested = createPartialMock(StaticAndInstanceDemo.class, "getPrivateMessage");

		final String staticExpected = "a static message";
		expect(StaticAndInstanceDemo.getStaticMessage()).andReturn(staticExpected);
		final String privateExpected = "A private message ";
		expectPrivate(tested, "getPrivateMessage").andReturn(privateExpected);

		replayAll();

		String actual = tested.getMessage();

		verifyAll();

		assertEquals(privateExpected + staticExpected, actual);
	}
}
