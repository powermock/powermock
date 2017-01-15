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
package org.powermock.modules.junit3.internal;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import org.powermock.tests.utils.RunnerTestSuiteChunker;

import java.lang.reflect.Method;
import java.util.Enumeration;

public interface JUnit3TestSuiteChunker extends RunnerTestSuiteChunker {
	
	/**
	 * Add a class to the test suite. Methods in this class will be checked
	 * according to {@link #shouldExecuteTestForMethod(Class, Method)} to see if
	 * it should be executed.
	 * 
	 * @param clazz
	 *            The class that should contain test cases.
	 * @throws Exception
	 *             If something unexpected goes wrong.
	 */
	void addTestClassToSuite(Class<?> clazz) throws Exception;

	void run(TestResult result);

	void addTest(Test test) throws Exception;

	void runTest(Test test, TestResult result);

	void addTestSuite(Class<? extends TestCase> testClass) throws Exception;

	Test testAt(int index);

	int countTestCases();

	Enumeration<?> tests();
}
