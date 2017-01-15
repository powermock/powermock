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
package samples.powermockito.junit4.annotationbased;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import samples.staticinitializer.SimpleStaticInitializerExample;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;

/**
 * Test class to demonstrate non-static final mocking with Mockito and PowerMock
 * annotations.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(SimpleStaticInitializerExample.class)
public class ChunkingAndStaticInitializerRemovalTest {

	@Mock
	private SimpleStaticInitializerExample tested;

	@Test
	public void testMockingWithNoChunking() throws Exception {
		final String argument = "hello";
		final String string = tested.getString();
		assertEquals(Whitebox.getInternalState(SimpleStaticInitializerExample.class, String.class), string);
		assertNull(tested.getConcatenatedString(argument));

		verify(tested).getConcatenatedString(argument);
	}

	@SuppressStaticInitializationFor("samples.staticinitializer.SimpleStaticInitializerExample")
	@Test
	public void testMockingWithChunking() throws Exception {
		final String argument = "hello";
		assertNull(tested.getString());
		assertNull(tested.getConcatenatedString(argument));

		verify(tested).getConcatenatedString(argument);
	}
}
