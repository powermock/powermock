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
package org.powermock.modules.junit4.staticandinstance;

import static org.easymock.EasyMock.expect;
import static org.powermock.PowerMock.expectPrivate;
import static org.powermock.PowerMock.mockMethod;
import static org.powermock.PowerMock.mockStaticMethod;
import static org.powermock.PowerMock.replay;
import static org.powermock.PowerMock.verify;
import static org.junit.Assert.assertEquals;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import samples.staticandinstance.StaticAndInstanceDemo;


@RunWith(PowerMockRunner.class)
@PrepareForTest(StaticAndInstanceDemo.class)
public class StaticAndInstanceDemoTest {

	@Test
	public void testGetMessage() throws Exception {
		mockStaticMethod(StaticAndInstanceDemo.class, "getStaticMessage");

		StaticAndInstanceDemo tested = mockMethod(StaticAndInstanceDemo.class,
				"getPrivateMessage");

		final String staticExpected = "a static message";
		expect(StaticAndInstanceDemo.getStaticMessage()).andReturn(
				staticExpected);
		final String privateExpected = "A private message ";
		expectPrivate(tested, "getPrivateMessage").andReturn(privateExpected);

		replay(tested);
		replay(StaticAndInstanceDemo.class);

		String actual = tested.getMessage();

		verify(tested);
		verify(StaticAndInstanceDemo.class);

		assertEquals(privateExpected + staticExpected, actual);
	}

	@Test
	public void testGetMessage_onlyMockPrivate() throws Exception {
		StaticAndInstanceDemo tested = mockMethod(StaticAndInstanceDemo.class,
				"getPrivateMessage");

		final String privateExpected = "A private message ";
		expectPrivate(tested, "getPrivateMessage").andReturn(privateExpected);

		replay(tested);

		String actual = tested.getMessage();

		verify(tested);

		assertEquals(privateExpected + "hello world!", actual);
	}

	@Test
	public void testGetMessage_onlyMockStatic() throws Exception {
		StaticAndInstanceDemo tested = new StaticAndInstanceDemo();

		mockStaticMethod(StaticAndInstanceDemo.class, "getStaticMessage");

		final String staticExpected = "static message";
		expect(StaticAndInstanceDemo.getStaticMessage()).andReturn(
				staticExpected);

		replay(StaticAndInstanceDemo.class);

		String actual = tested.getMessage();

		verify(StaticAndInstanceDemo.class);

		assertEquals("Private " + staticExpected, actual);
	}
}
