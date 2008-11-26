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
package powermock.examples.finalmocking;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Unit tests of the {@link StateFormatter} class.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(StateHolder.class)
public class StateFormatterTest {

	@Test
	public void testGetFormattedState_actualStateExists() throws Exception {
		final String expectedState = "state";

		StateHolder stateHolderMock = createMock(StateHolder.class);
		StateFormatter tested = new StateFormatter(stateHolderMock);

		expect(stateHolderMock.getState()).andReturn(expectedState);

		replay(stateHolderMock);

		final String actualState = tested.getFormattedState();

		verify(stateHolderMock);

		assertEquals(expectedState, actualState);
	}

	@Test
	public void testGetFormattedState_noStateExists() throws Exception {
		final String expectedState = "State information is missing";

		StateHolder stateHolderMock = createMock(StateHolder.class);
		StateFormatter tested = new StateFormatter(stateHolderMock);

		expect(stateHolderMock.getState()).andReturn(null);

		replay(stateHolderMock);

		final String actualState = tested.getFormattedState();

		verify(stateHolderMock);

		assertEquals(expectedState, actualState);
	}
}
