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
package samples.junit4.system;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import java.net.URLEncoder;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import samples.system.SystemClassUser;

/**
 * Demonstrates PowerMock's ability to mock non-final system classes. To mock a
 * system class you need to prepare the calling class and the unit test it self
 * for testing. I.e. let's say you're testing class A which interacts with
 * URLEncoder from a test named ATest then you would do:
 * 
 * <pre>
 * 
 * @PrepareForTest({A.class, ATest.class })
 * 
 * </pre>
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest( { SystemClassUser.class, SystemClassUserTest.class })
public class SystemClassUserTest {

	@Test
	public void assertThatMockingOfNonFinalSystemClassesWorks() throws Exception {
		mockStatic(URLEncoder.class);

		expect(URLEncoder.encode("string", "enc")).andReturn("something");
		replayAll();

		assertEquals("something", new SystemClassUser().getProperty());

		verifyAll();
	}
}
