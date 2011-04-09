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

package samples.junit4.resetmock;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.expectnew.ExpectNewDemo;
import samples.newmocking.MyClass;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.powermock.api.easymock.PowerMock.*;

/**
 * Tests to verify that the reset functionality works.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(ExpectNewDemo.class)
public class ResetMockTest {

	@Test
	public void assertManualResetWorks() throws Exception {
		ExpectNewDemo tested = new ExpectNewDemo();

		MyClass myClassMock = createMock(MyClass.class);
		expectNew(MyClass.class).andReturn(myClassMock);
		expect(myClassMock.getMessage()).andReturn("message");

		replayAll();

		String message = tested.getMessage();

		verifyAll();
		assertEquals("message", message);

		reset(myClassMock);
		reset(MyClass.class);

		expectNew(MyClass.class).andReturn(myClassMock);
		expect(myClassMock.getMessage()).andReturn("message");

		replayAll();

		message = tested.getMessage();

		verifyAll();
		assertEquals("message", message);
	}

	@Test
	public void assertManualResetWorksWhenMixingInstanceAndClassMocks() throws Exception {
		ExpectNewDemo tested = new ExpectNewDemo();

		MyClass myClassMock = createMock(MyClass.class);
		expectNew(MyClass.class).andReturn(myClassMock);
		expect(myClassMock.getMessage()).andReturn("message");

		replayAll();

		String message = tested.getMessage();

		verifyAll();
		assertEquals("message", message);

		reset(myClassMock, MyClass.class);

		expectNew(MyClass.class).andReturn(myClassMock);
		expect(myClassMock.getMessage()).andReturn("message");

		replayAll();

		message = tested.getMessage();

		verifyAll();
		assertEquals("message", message);
	}

	@Test
	public void assertResetAllWorks() throws Exception {
		ExpectNewDemo tested = new ExpectNewDemo();

		MyClass myClassMock = createMock(MyClass.class);
		expectNew(MyClass.class).andReturn(myClassMock);
		expect(myClassMock.getMessage()).andReturn("message");

		replayAll();

		String message = tested.getMessage();

		verifyAll();
		assertEquals("message", message);

		resetAll();

		expectNew(MyClass.class).andReturn(myClassMock);
		expect(myClassMock.getMessage()).andReturn("message");

		replayAll();

		message = tested.getMessage();

		verifyAll();
		assertEquals("message", message);
	}

}
