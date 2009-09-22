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
package samples.powermockito.junit4.partialmocking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;
import static org.powermock.api.mockito.PowerMockito.when;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import samples.singleton.StaticExample;

@RunWith(PowerMockRunner.class)
@PrepareForTest(StaticExample.class)
public class StaticPartialMockingTest {

	@Test
	@Ignore("Must work til version 1.3")
	public void partialMockingOfStaticMethodReturningObjectWorks() throws Exception {
		spy(StaticExample.class);

		assertTrue(Object.class.equals(StaticExample.objectMethod().getClass()));

		when(StaticExample.class, "privateObjectMethod").thenReturn("Hello static");

		assertEquals("Hello static", StaticExample.objectMethod());
		verifyPrivate(StaticExample.class).invocation("privateObjectMethod");
	}

	@Test
	@Ignore("Must work til version 1.3")
	public void partialMockingOfStaticFinalMethodReturningObjectWorks() throws Exception {
		spy(StaticExample.class);

		assertTrue(Object.class.equals(StaticExample.objectFinalMethod().getClass()));

		when(StaticExample.class, "privateObjectFinalMethod").thenReturn("Hello static");

		assertEquals("Hello static", StaticExample.objectFinalMethod());

		verifyPrivate(StaticExample.class).invocation("privateObjectFinalMethod");
	}

	@Test(expected = ArrayStoreException.class)
	public void partialMockingOfStaticVoidMethodReturningObjectWorks() throws Exception {
		spy(StaticExample.class);

		StaticExample.voidMethod();

		when(StaticExample.class, "privateVoidMethod").thenThrow(new ArrayStoreException());
		StaticExample.voidMethod();
	}

	@Test(expected = ArrayStoreException.class)
	public void partialMockingOfStaticFinalVoidMethodReturningObjectWorks() throws Exception {
		spy(StaticExample.class);

		StaticExample.voidFinalMethod();

		when(StaticExample.class, "privateVoidFinalMethod").thenThrow(new ArrayStoreException());
		StaticExample.voidFinalMethod();
	}
}
