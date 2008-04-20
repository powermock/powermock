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
package org.powermock.modules.junit4.newmocking;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.powermock.PowerMock.mockConstruction;
import static org.powermock.PowerMock.replay;
import static org.powermock.PowerMock.verify;
import static org.junit.Assert.assertEquals;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import samples.newmocking.MyClass;
import samples.newmocking.StupidNew;

/**
 * Test class to demonstrate new instance mocking.
 * 
 * @author Johan Haleby
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest( { MyClass.class, StupidNew.class })
public class StupidNewTest {

	@Test
	public void testGetMessage() throws Exception {
		StupidNew tested = new StupidNew();

		MyClass myClassMock = mockConstruction(MyClass.class);

		String expected = "Hello altered World";
		expect(myClassMock.getMessage()).andReturn("Hello altered World");
		replay(myClassMock);

		String actual = tested.getMessage();

		verify(myClassMock);
		assertEquals("Expected and actual did not match", expected, actual);
	}

	@Test
	public void testGetMessageWithArgument() throws Exception {
		StupidNew tested = new StupidNew();

		MyClass myClassMock = mockConstruction(MyClass.class);

		String expected = "Hello altered World";
		expect(myClassMock.getMessage("test")).andReturn("Hello altered World");
		replay(myClassMock);

		String actual = tested.getMessageWithArgument();

		verify(myClassMock);
		assertEquals("Expected and actual did not match", expected, actual);
	}

	@Test
	public void testInvokeVoidMethod() throws Exception {
		StupidNew tested = new StupidNew();

		MyClass myClassMock = mockConstruction(MyClass.class);
		myClassMock.voidMethod();
		expectLastCall().times(1);

		replay(myClassMock);

		tested.invokeVoidMethod();

		verify(myClassMock);
	}
}
