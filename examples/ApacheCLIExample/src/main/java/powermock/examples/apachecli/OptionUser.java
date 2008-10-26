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

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

/**
 * Class that verifies that the
 * http://code.google.com/p/powermock/issues/detail?id=38 is fixed.
 */
public class OptionUser {

	private Options options;

	/**
	 * {@inheritDoc}
	 */
	public void printOptionDescription() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("caption", options);
	}
}
