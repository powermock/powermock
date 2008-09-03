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
package org.powermock.modules.junit4.suppressconstructor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;

import org.junit.Ignore;
import org.junit.Test;
import org.powermock.PowerMock;

import samples.suppressconstructor.SuppressConstructorDemo;
import samples.suppressconstructor.SuppressSpecificConstructorDemo;

public class CreateUnmockedTest {

	@Test
	@Ignore("We need to change how to create unmocked")
	public void testUnmockedWithNoConstructor() throws Exception {
		SuppressSpecificConstructorDemo object = PowerMock.createMock(SuppressSpecificConstructorDemo.class, new Method[0]);
		PowerMock.replay(object);
		
		assertEquals("Hello", object.getHello());
		
		PowerMock.verify(object);
	}

	@Test
	public void testUnmockedWithConstructorAndAllowReplay() throws Exception {
		PowerMock.niceReplayAndVerify();
		SuppressConstructorDemo object = new SuppressConstructorDemo("Hello");
		PowerMock.replay(object);
		
		assertEquals("Hello", object.getMessage());
		
		PowerMock.verify(object);
	}

	@Test
	public void testUnmockedWithReplayCausesException() throws Exception {
		SuppressConstructorDemo object = new SuppressConstructorDemo("Hello");
		try {
			PowerMock.replay(object);
			fail("Replay should only work on mocks");
		} catch (RuntimeException e) {
			// ignore
		}
	}
}
