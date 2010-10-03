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
package samples.junit4.expectnew;

import java.lang.reflect.Method;
import java.util.Date;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.easymock.internal.MocksControl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import samples.expectnew.ExpectNewDemo;

@RunWith(PowerMockRunner.class)
@PrepareForTest( { ExpectNewDemo.class })
public class MockDateTest {

	@Test
	public void testMockDate() throws Exception {
		Date someDate = new Date();
		Date date = PowerMock.createMock(Date.class);
		EasyMock.expect(date.after(someDate)).andReturn(false);

		PowerMock.replay(date);

		date.after(someDate);

		PowerMock.verify(date);
	}

	@Test
	public void testMockDateWithEasyMock() throws Exception {
		Date someDate = new Date();
		MocksControl c = (MocksControl) org.easymock.EasyMock.createControl();
		Date date = c.createMock(Date.class);
		EasyMock.expect(date.after(someDate)).andReturn(false);

		PowerMock.replay(date);

		date.after(someDate);

		PowerMock.verify(date);
	}

	@Test(expected = IllegalStateException.class)
	public void testMockDateWithEasyMockFails() {
		Date someDate = new Date();
		MocksControl c = (MocksControl) org.easymock.EasyMock.createControl();
		Date date = c.createMock(Date.class, new Method[0]);
		EasyMock.expect(date.after(someDate)).andReturn(false);
		Assert.fail("EasyMock with no methods mocked should not be possible to mock");
	}

	@Test
	public void testExpectNewDate() throws Exception {
		Date someDate = new Date();
		long time = someDate.getTime();
		PowerMock.expectNew(Date.class).andReturn(someDate);

		PowerMock.replay(Date.class);

		Assert.assertEquals(time, new ExpectNewDemo().makeDate().getTime());

		PowerMock.verify(Date.class);
	}
}
