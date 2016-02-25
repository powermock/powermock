/*
 * Copyright 2010 the original author or authors.
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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.finalmocking.FinalDemo;
import samples.injectmocks.DependencyHolder;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Asserts that {@link @InjectMocks} with PowerMock.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(FinalDemo.class)
public class InjectMocksAnnotationTest {

	@SuppressWarnings("unused")
	@Mock
	private FinalDemo finalDemo;

	@InjectMocks
	private DependencyHolder dependencyHolder = new DependencyHolder();

	@Test
	public void injectMocksWorks() {
		assertNotNull(dependencyHolder.getFinalDemo());
	}

	@Test
	public void testSay() throws Exception {

		FinalDemo tested = dependencyHolder.getFinalDemo();

		String expected = "Hello altered World";
		when(tested.say("hello")).thenReturn("Hello altered World");

		String actual = tested.say("hello");

		assertEquals("Expected and actual did not match", expected, actual);

		// Should still be mocked by now.
		try {
			verify(tested).say("world");
			fail("Should throw AssertionError!");
		} catch (AssertionError e) {
			assertThat(e.getMessage(), is(containsString("Argument(s) are different! Wanted")));
		}
	}
}
