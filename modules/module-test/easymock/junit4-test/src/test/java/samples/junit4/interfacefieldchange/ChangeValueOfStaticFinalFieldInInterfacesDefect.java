/*
 * Copyright 2009 the original author or authors.
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

package samples.junit4.interfacefieldchange;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import samples.interfacefieldchange.InterfaceWithStaticFinalField;

/**
 * This test asserts that it's possible for PowerMock to modify static final
 * fields in Interfaces.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest( { InterfaceWithStaticFinalField.class })
public class ChangeValueOfStaticFinalFieldInInterfacesDefect {

	@Test
	public void assertThatStaticFinalFieldValuesInInterfacesAreChangable() throws Exception {
		final String value = "new value";
		Whitebox.setInternalState(InterfaceWithStaticFinalField.class, value);
		assertEquals(value, Whitebox.getInternalState(InterfaceWithStaticFinalField.class, String.class));
	}
}
