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

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import samples.privateandfinal.PrivateFinal;

import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.powermock.api.easymock.PowerMock.createPartialMock;
import static org.powermock.api.easymock.PowerMock.expectPrivate;

/**
 * Test class to demonstrate private+final method mocking.
 */
@PrepareForTest(PrivateFinal.class)
public class PartialMockingWithBeforeClassTest extends PowerMockTestCase {
	private PrivateFinal tested;

	@BeforeClass
	public void setup() {
		tested = createPartialMock(PrivateFinal.class, "sayIt");
	}

	@Test
	public void partialMockingWithMockCreatedInBeforeClassMethod() throws Exception {
		String expected = "Hello altered World";
		expectPrivate(tested, "sayIt", "name").andReturn(expected);
		replay(tested);

		String actual = tested.say("name");

		verify(tested);
		Assert.assertEquals(expected, actual);
	}
}
