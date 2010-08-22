/*
 * Copyright 2010 the original author or authors.
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
package samples.junit4.console;

import static org.powermock.api.easymock.PowerMock.createMock;

import java.io.Console;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Asserts that PowerMock can mock the Console class. This failed in version 1.4
 * when the DefaultFieldValueGenerator was introduced. This tests makes sure
 * that the DefaultFieldValueGenerator can generate default values for field
 * that are interfaces and abstract with no inheritable constructor.
 */
@RunWith(PowerMockRunner.class)
public class ConsoleTest {

	@Test
	public void canMockConsole() throws Exception {
		createMock(Console.class);
	}
}
