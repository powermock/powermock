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
package powermock.examples.newmocking;

import java.io.File;

/**
 * Used to demonstrate PowerMock's ability to mock new instance calls.
 */
public class PersistenceManager {

	public boolean createDirectoryStructure(String directoryPath) {
		File directory = new File(directoryPath);

		if (directory.exists()) {
			throw new IllegalArgumentException("\"" + directoryPath + "\" already exists.");
		}

		return directory.mkdirs();
	}
}
