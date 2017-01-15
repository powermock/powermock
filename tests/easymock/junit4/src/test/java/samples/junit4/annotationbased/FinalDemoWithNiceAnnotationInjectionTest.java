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
package samples.junit4.annotationbased;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.annotation.MockNice;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.finalmocking.FinalDemo;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

/**
 * Test class to demonstrate non-static final mocking with one listeners
 * injecting mocks to fields annotated with {@link MockNice}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(FinalDemo.class)
public class FinalDemoWithNiceAnnotationInjectionTest {

	@MockNice
	private FinalDemo tested;

	@Test
	public void testSay() throws Exception {
		String expected = null;
		replay(tested);

		String actual = tested.say("hello");

		verify(tested);
		assertEquals("Expected and actual did not match", expected, actual);

		// Should still be mocked by now.
		String actual2 = tested.say("world");
		assertEquals(expected, actual2);
	}
}
