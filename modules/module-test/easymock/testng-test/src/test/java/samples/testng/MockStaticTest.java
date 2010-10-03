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
package samples.testng;

import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import samples.singleton.StaticHelper;
import samples.singleton.StaticService;

/**
 * Test class to demonstrate static, static+final, static+native and
 * static+final+native methods mocking.
 * 
 * @author Johan Haleby
 * @author Jan Kronquist
 */
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
		Assert.assertEquals(expected, actual);

		// Singleton still be mocked by now.
		try {
			StaticService.say("world");
			Assert.fail("Should throw AssertionError!");
		} catch (AssertionError e) {
			Assert.assertEquals("\n  Unexpected method call say(\"world\"):", e.getMessage());
		}
	}

	@Test
	public void testMockStaticFinal() throws Exception {
		mockStatic(StaticService.class);
		String expected = "Hello altered World";
		expect(StaticService.sayFinal("hello")).andReturn("Hello altered World");
		replay(StaticService.class);

		String actual = StaticService.sayFinal("hello");

		verify(StaticService.class);
		Assert.assertEquals(expected, actual);

		// Singleton still be mocked by now.
		try {
			StaticService.sayFinal("world");
			Assert.fail("Should throw AssertionError!");
		} catch (AssertionError e) {
			Assert.assertEquals("\n  Unexpected method call sayFinal(\"world\"):", e.getMessage());
		}
	}
}
