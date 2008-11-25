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
package org.powermock.modules.junit4.constructorargs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.powermock.PowerMock.createMock;
import static org.powermock.PowerMock.createPartialMock;
import static org.powermock.PowerMock.expectPrivate;
import static org.powermock.PowerMock.replay;
import static org.powermock.PowerMock.verify;

import java.lang.reflect.Constructor;

import org.easymock.classextension.ConstructorArgs;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.Whitebox;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import samples.constructorargs.ConstructorArgsDemo;

/**
 * This test demonstrates the ability to invoke a specific constructor after
 * creating the mock object.
 * 
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(ConstructorArgsDemo.class)
public class ConstructorArgsDemoTest {

	@Test
	public void testGetTheSecret_noConstructor() throws Exception {
		ConstructorArgsDemo tested = createMock(ConstructorArgsDemo.class);
		assertNull(Whitebox.getInternalState(tested, "secret", ConstructorArgsDemo.class));
	}

	@Test
	public void testGetTheSecret_defaultConstructor() throws Exception {
		final Constructor<ConstructorArgsDemo> constructor = ConstructorArgsDemo.class.getConstructor((Class<?>[]) null);
		ConstructorArgsDemo tested = createMock(ConstructorArgsDemo.class, new ConstructorArgs(constructor));
		assertEquals("default", Whitebox.getInternalState(tested, "secret", ConstructorArgsDemo.class));
	}

	@Test
	public void testGetTheSecret_stringConstructor() throws Exception {
		final String expected = "my own secret";
		ConstructorArgsDemo tested = createMock(ConstructorArgsDemo.class, expected);
		assertEquals(expected, Whitebox.getInternalState(tested, "secret", ConstructorArgsDemo.class));
	}

	@Test
	public void testGetTheSecret_stringConstructorAndMockedPrivateSecret() throws Exception {
		final String originalSecret = "my own secret";
		ConstructorArgsDemo tested = createPartialMock(ConstructorArgsDemo.class, new String[] { "theSecretIsPrivate" }, originalSecret);
		assertEquals(originalSecret, Whitebox.getInternalState(tested, "secret", ConstructorArgsDemo.class));

		final String myNewSecret = "my new secret";
		expectPrivate(tested, "theSecretIsPrivate").andReturn(myNewSecret);

		replay(tested);

		final String actual = tested.getTheSecret();

		verify(tested);

		assertEquals(myNewSecret, actual);
	}
}
