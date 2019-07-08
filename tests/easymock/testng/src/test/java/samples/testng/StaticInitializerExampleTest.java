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

import org.testng.annotations.Test;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.reflect.Whitebox;
import samples.staticinitializer.StaticInitializerExample;

import java.util.HashSet;

import static org.testng.Assert.*;
import org.powermock.modules.testng.PowerMockTestCase;

@SuppressStaticInitializationFor("samples.staticinitializer.StaticInitializerExample")
public class StaticInitializerExampleTest extends PowerMockTestCase {

	@Test
	public void testSuppressStaticInitializer() throws Exception {
		assertNull(StaticInitializerExample.getMySet(), "Should be null because the static initializer should be suppressed");
	}

	@Test
	public void testSuppressStaticInitializerAndSetFinalField() throws Exception {
		assertNull(StaticInitializerExample.getMySet(), "Should be null because the static initializer should be suppressed");
		final HashSet<String> hashSet = new HashSet<String>();
		Whitebox.setInternalState(StaticInitializerExample.class, "mySet", hashSet);
		assertSame(hashSet, Whitebox.getInternalState(StaticInitializerExample.class, "mySet"));
	}
}
