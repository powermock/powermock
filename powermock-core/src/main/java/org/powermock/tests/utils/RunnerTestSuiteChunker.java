/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.powermock.tests.utils;

import java.util.List;

/**
 * An interface that should be implemented by classes that performs test suite
 * chunking. Test suite chunking may be performed because certain classes may
 * need to be byte-code manipulated in tests without impacting on other tests.
 * 
 * @author Johan Haleby
 */
public interface RunnerTestSuiteChunker extends TestSuiteChunker {

	/**
	 * Create the test delegators needed for a whole class.
	 */
	void createTestDelegators(Class<?> testClass, List<TestChunk> chunks) throws Exception;


	/**
	 * Get the number of total tests defined in the suite (the sum of all tests
	 * defined in all chunks for this suite).
	 * 
	 * @return The number of tests in this suite.
	 */
	int getTestCount();

}
