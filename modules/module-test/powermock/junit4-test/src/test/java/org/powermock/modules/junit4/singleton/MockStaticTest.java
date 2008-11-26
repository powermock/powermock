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
package org.powermock.modules.junit4.singleton;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertEquals;
import static org.powermock.api.easymock.PowerMock.expectPrivate;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.mockStaticPartial;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import samples.singleton.StaticHelper;
import samples.singleton.StaticService;

/**
 * Test class to demonstrate static, static+final, static+native and
 * static+final+native methods mocking.
 * 
 * @author Johan Haleby
 * @author Jan Kronquist
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest( { StaticService.class, StaticHelper.class })
public class MockStaticTest {

	@Test
	public void testMockStatic() throws Exception {
		mockStatic(StaticService.class);
		String expected = "Hello altered World";
		expect(StaticService.say("hello")).andReturn("Hello altered World");
		replay(StaticService.class);

		String actual = StaticService.say("hello");

		verify(StaticService.class);
		assertEquals("Expected and actual did not match", expected, actual);

		// Singleton should no longer be mocked by now.
		String actual2 = StaticService.say("world");
		assertEquals("Hello world", actual2);
	}

	@Test
	public void testMockStaticFinal() throws Exception {
		mockStatic(StaticService.class);
		String expected = "Hello altered World";
		expect(StaticService.sayFinal("hello"))
				.andReturn("Hello altered World");
		replay(StaticService.class);

		String actual = StaticService.sayFinal("hello");

		verify(StaticService.class);
		assertEquals("Expected and actual did not match", expected, actual);

		// Singleton should no longer be mocked by now.
		String actual2 = StaticService.sayFinal("world");
		assertEquals("Hello world", actual2);
	}

	@Test
	public void testMockStaticNative() throws Exception {
		mockStatic(StaticService.class);
		String expected = "Hello altered World";
		expect(StaticService.sayNative("hello")).andReturn(
				"Hello altered World");
		replay(StaticService.class);

		String actual = StaticService.sayNative("hello");

		verify(StaticService.class);
		assertEquals("Expected and actual did not match", expected, actual);
	}

	@Test
	public void testMockStaticFinalNative() throws Exception {
		mockStatic(StaticService.class);
		String expected = "Hello altered World";
		expect(StaticService.sayFinalNative("hello")).andReturn(
				"Hello altered World");
		replay(StaticService.class);

		String actual = StaticService.sayFinalNative("hello");

		verify(StaticService.class);
		assertEquals("Expected and actual did not match", expected, actual);
	}

	@Test
	public void mockAStaticMethod() throws Exception {
		mockStatic(StaticService.class);
		String expected = "qwe";
		expect(StaticService.doStatic(5)).andReturn(expected);
		replay(StaticService.class);

		String actual = StaticService.doStatic(5);
		assertEquals(expected, actual);
		verify(StaticService.class);
	}

	@Test
	public void mockMockStatic_times2() throws Exception {
		mockStatic(StaticHelper.class);
		StaticHelper.sayHelloHelper();
		expectLastCall().times(2);
		replay(StaticHelper.class);

		StaticService.sayHello();

		verify(StaticHelper.class);
	}

	@Test
	public void mockStaticCallingOtherStatic() throws Exception {
		mockStatic(StaticHelper.class);
		StaticHelper.sayHelloAgain();
		expectLastCall().times(2);
		replay(StaticHelper.class);

		StaticService.sayHelloAgain();

		verify(StaticHelper.class);
	}

	@Test
	public void testMockPrivateStatic() throws Exception {
		mockStaticPartial(StaticService.class, "sayPrivateStatic", String.class);

		final String expected = "Hello world";
		expectPrivate(StaticService.class, "sayPrivateStatic", "name")
				.andReturn(expected);

		replay(StaticService.class);

		String actual = (String) Whitebox.invokeMethod(StaticService.class,
				"sayPrivateStatic", "name");

		verify(StaticService.class);
		assertEquals(expected, actual);
	}

	@Test
	public void testMockPrivateFinalStatic() throws Exception {
		mockStaticPartial(StaticService.class, "sayPrivateFinalStatic",
				String.class);

		final String expected = "Hello world";
		expectPrivate(StaticService.class, "sayPrivateFinalStatic", "name")
				.andReturn(expected);

		replay(StaticService.class);

		String actual = (String) Whitebox.invokeMethod(StaticService.class,
				"sayPrivateFinalStatic", "name");

		verify(StaticService.class);
		assertEquals(expected, actual);
	}

	@Test
	public void testMockPrivateNativeFinalStatic() throws Exception {
		mockStaticPartial(StaticService.class, "sayPrivateNativeFinalStatic",
				String.class);

		final String expected = "Hello world";
		expectPrivate(StaticService.class, "sayPrivateNativeFinalStatic",
				"name").andReturn(expected);

		replay(StaticService.class);

		String actual = (String) Whitebox.invokeMethod(StaticService.class,
				"sayPrivateNativeFinalStatic", "name");

		verify(StaticService.class);
		assertEquals(expected, actual);
	}

	@Test
	public void innerClassesWork() {
		assertEquals(17, StaticService.getNumberFromInner());
	}

	@Test
	public void innerInstanceClassesWork() {
		assertEquals(23, StaticService.getNumberFromInnerInstance());
	}
}
