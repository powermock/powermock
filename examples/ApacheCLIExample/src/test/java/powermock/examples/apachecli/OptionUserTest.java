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
package powermock.examples.apachecli;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.powermock.PowerMock.replay;
import static org.powermock.PowerMock.verify;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.PowerMock;
import org.powermock.Whitebox;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Verifies that the http://code.google.com/p/powermock/issues/detail?id=38 is
 * fixed.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest( { OptionUser.class, HelpFormatter.class })
public class OptionUserTest {

	/**
	 * @throws Exception
	 *             If something unexpected goes wrong.
	 */
	@Test
	public void testPrintOptionDescription() throws Exception {
		HelpFormatter helpFormatterMock = PowerMock.createMockAndExpectNew(HelpFormatter.class);

		helpFormatterMock.printHelp(eq("caption"), isA(Options.class));
		expectLastCall().once();

		OptionUser tested = new OptionUser();
		Whitebox.setInternalState(tested, "options", new Options());

		replay(helpFormatterMock, HelpFormatter.class);

		tested.printOptionDescription();

		verify(helpFormatterMock, HelpFormatter.class);
	}
}
