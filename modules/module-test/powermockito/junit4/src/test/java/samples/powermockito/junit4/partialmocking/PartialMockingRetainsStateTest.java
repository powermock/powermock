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
package samples.powermockito.junit4.partialmocking;

import static junit.framework.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

import samples.partialmocking.MockSelfDemo;

/**
 * Demonstrates that PowerMockito retains state when spying. This was previously
 * a bug (issue <a
 * href="http://code.google.com/p/powermock/issues/detail?id=263">263</a>).
 */
@RunWith(PowerMockRunner.class)
public class PartialMockingRetainsStateTest {

	@Test
	public void spyingOnAnObjectRetainsState() throws Exception {
		MockSelfDemo demo = new MockSelfDemo(4);
		MockSelfDemo spy = PowerMockito.spy(demo);
		assertEquals(4, spy.getConstructorValue());
	}
}
