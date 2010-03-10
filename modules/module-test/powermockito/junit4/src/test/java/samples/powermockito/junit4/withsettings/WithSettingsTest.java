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
package samples.powermockito.junit4.withsettings;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.RETURNS_MOCKS;
import static org.mockito.Mockito.withSettings;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import samples.finalmocking.FinalDemo;
import samples.finalmocking.HoldingFinalDemo;
import samples.finalmocking.StaticHoldingFinalDemo;

@RunWith(PowerMockRunner.class)
@PrepareForTest( { StaticHoldingFinalDemo.class, HoldingFinalDemo.class, FinalDemo.class })
public class WithSettingsTest {

	@Test
	public void powermockitoSupportsAnswersForInstanceMethods() {
		HoldingFinalDemo tested = mock(HoldingFinalDemo.class, RETURNS_MOCKS);
		assertNotNull(tested.getFinalDemo());
	}

	@Test
	public void powermockitoSupportsWithSettingsForInstanceMethods() {
		HoldingFinalDemo tested = mock(HoldingFinalDemo.class, withSettings().defaultAnswer(RETURNS_MOCKS));
		assertNotNull(tested.getFinalDemo());
	}

	@Test
	public void powermockitoSupportsAnswersForStaticMethods() {
		mockStatic(StaticHoldingFinalDemo.class, RETURNS_MOCKS);
		assertNotNull(StaticHoldingFinalDemo.getFinalDemo());
	}

	@Test
	public void powermockitoSupportsWithSettingsForStaticMethods() {
		mockStatic(StaticHoldingFinalDemo.class, withSettings().defaultAnswer(RETURNS_MOCKS));
		assertNotNull(StaticHoldingFinalDemo.getFinalDemo());
	}
}
