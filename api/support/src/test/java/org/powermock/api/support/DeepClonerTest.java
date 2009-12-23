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
package org.powermock.api.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import java.net.URL;

import org.junit.Test;

public class DeepClonerTest {

	@Test
	public void clonesJavaInstances() throws Exception {
		final URL original = new URL("http://www.powermock.org");
		URL clone = DeepCloner.clone(original);
		assertEquals(clone, original);
		assertNotSame(clone, original);
	}
}
