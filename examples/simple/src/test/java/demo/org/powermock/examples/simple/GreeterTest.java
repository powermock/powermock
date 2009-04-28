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
package demo.org.powermock.examples.simple;

import static junit.framework.Assert.assertEquals;
import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.expectLastCall;
import static org.powermock.api.easymock.PowerMock.expectNew;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;
import static org.powermock.reflect.Whitebox.invokeMethod;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@SuppressStaticInitializationFor("demo.org.powermock.examples.simple.SimpleConfig")
@PrepareForTest( { Greeter.class, Logger.class })
public class GreeterTest {

	@Test
	public void testGetMessage() throws Exception {
		mockStatic(SimpleConfig.class);
		expect(SimpleConfig.getGreeting()).andReturn("Hi");
		expect(SimpleConfig.getTarget()).andReturn("All");
		replay(SimpleConfig.class);

		assertEquals("Hi All", invokeMethod(Greeter.class, "getMessage"));

		verify(SimpleConfig.class);
	}

	@Test
	public void testRun() throws Exception {
		Logger logger = createMock(Logger.class);
		
		expectNew(Logger.class).andReturn(logger);
		logger.log("Hello");
		expectLastCall().times(10);
		replay(logger, Logger.class);

		invokeMethod(new Greeter(), "run", 10, "Hello");

		verify(logger, Logger.class);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRunWhenLoggerThrowsUnexpectedRuntimeExeception() throws Exception {
		expectNew(Logger.class).andThrow(new IllegalArgumentException("Unexpected exeception"));
		replay(Logger.class);

		invokeMethod(new Greeter(), "run", 10, "Hello");

		verify(Logger.class);
	}

}