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
package samples.junit3.singleton;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.powermock.api.easymock.PowerMock.expectPrivate;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.mockStaticPartial;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit3.PowerMockSuite;
import org.powermock.reflect.Whitebox;

import samples.singleton.StaticHelper;
import samples.singleton.StaticService;

@PrepareForTest( { StaticService.class, StaticHelper.class })
public class MockStaticTest extends TestCase {

	public static TestSuite suite() throws Exception {
		// This is not the best way to add a test suite, but we do this here
		// for testing purposes.
		PowerMockSuite powerMockJunit3Suite = new PowerMockSuite("Unit tests for " + MockStaticTest.class.getSimpleName());
		final MockStaticTest stupidSingletonTest = new MockStaticTest();
		stupidSingletonTest.setName("testSay");
		TestSuite suite = new TestSuite();
		suite.addTest(stupidSingletonTest);
		powerMockJunit3Suite.addTest(suite);
		return powerMockJunit3Suite;
	}

	public void testSay() throws Exception {
		mockStatic(StaticService.class);
		String expected = "Hello altered World";
		expect(StaticService.say("hello")).andReturn("Hello altered World");
		replay(StaticService.class);

		String actual = StaticService.say("hello");

		verify(StaticService.class);
		assertEquals("Expected and actual did not match", expected, actual);

		// Singleton still be mocked by now.
		try {
			StaticService.say("world");
			fail("Should throw AssertionError!");
		} catch (AssertionError e) {
			assertEquals("\n  Unexpected method call say(\"world\"):", e.getMessage());
		}
	}

	public void testSayFinal() throws Exception {
		mockStatic(StaticService.class);
		String expected = "Hello altered World";
		expect(StaticService.sayFinal("hello")).andReturn("Hello altered World");
		replay(StaticService.class);

		String actual = StaticService.sayFinal("hello");

		verify(StaticService.class);
		assertEquals("Expected and actual did not match", expected, actual);

		// Singleton still be mocked by now.
		try {
			StaticService.sayFinal("world");
			fail("Should throw AssertionError!");
		} catch (AssertionError e) {
			assertEquals("\n  Unexpected method call sayFinal(\"world\"):", e.getMessage());
		}
	}

	public void testSayNative() throws Exception {
		mockStatic(StaticService.class);
		String expected = "Hello altered World";
		expect(StaticService.sayNative("hello")).andReturn("Hello altered World");
		replay(StaticService.class);

		String actual = StaticService.sayNative("hello");

		verify(StaticService.class);
		assertEquals("Expected and actual did not match", expected, actual);
	}

	public void testSayFinalNative() throws Exception {
		mockStatic(StaticService.class);
		String expected = "Hello altered World";
		expect(StaticService.sayFinalNative("hello")).andReturn("Hello altered World");
		replay(StaticService.class);

		String actual = StaticService.sayFinalNative("hello");

		verify(StaticService.class);
		assertEquals("Expected and actual did not match", expected, actual);
	}

	public void testMockAStaticMethod() throws Exception {
		mockStatic(StaticService.class);
		String expected = "qwe";
		expect(StaticService.doStatic(5)).andReturn(expected);
		replay(StaticService.class);

		String actual = StaticService.doStatic(5);
		assertEquals(expected, actual);
		verify(StaticService.class);
	}

	public void testMockSayHello() throws Exception {
		mockStatic(StaticHelper.class);
		StaticHelper.sayHelloHelper();
		expectLastCall().times(2);
		replay(StaticHelper.class);

		StaticService.sayHello();

		verify(StaticHelper.class);
	}

	public void testMockSayHelloAgain() throws Exception {
		mockStatic(StaticHelper.class);
		StaticHelper.sayHelloAgain();
		expectLastCall().times(2);
		replay(StaticHelper.class);

		StaticService.sayHelloAgain();

		verify(StaticHelper.class);
	}

	public void testSayPrivateStatic() throws Exception {
		mockStaticPartial(StaticService.class, "sayPrivateStatic", String.class);

		final String expected = "Hello world";
		expectPrivate(StaticService.class, "sayPrivateStatic", "name").andReturn(expected);

		replay(StaticService.class);

		String actual = (String) Whitebox.invokeMethod(StaticService.class, "sayPrivateStatic", "name");

		verify(StaticService.class);
		assertEquals(expected, actual);
	}

	public void testSayPrivateFinalStatic() throws Exception {
		mockStaticPartial(StaticService.class, "sayPrivateFinalStatic", String.class);

		final String expected = "Hello world";
		expectPrivate(StaticService.class, "sayPrivateFinalStatic", "name").andReturn(expected);

		replay(StaticService.class);

		String actual = (String) Whitebox.invokeMethod(StaticService.class, "sayPrivateFinalStatic", "name");

		verify(StaticService.class);
		assertEquals(expected, actual);
	}

	public void testInnerClassesWork() throws Exception {
		assertEquals(17, StaticService.getNumberFromInner());
	}

	public void testInnerInstanceClassesWork() throws Exception {
		assertEquals(23, StaticService.getNumberFromInnerInstance());
	}
}
