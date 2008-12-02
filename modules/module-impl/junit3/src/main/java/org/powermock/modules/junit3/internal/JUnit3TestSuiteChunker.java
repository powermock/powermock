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

import java.lang.reflect.Method;
import java.util.Enumeration;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;

import org.powermock.tests.utils.TestSuiteChunker;

public interface JUnit3TestSuiteChunker extends TestSuiteChunker {
	
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
	public void addTestClassToSuite(Class<?> clazz) throws Exception;

	public void run(TestResult result);

	public void addTest(Test test) throws Exception;

	public void runTest(Test test, TestResult result);

	public void addTestSuite(Class<? extends TestCase> testClass) throws Exception;

	public Test testAt(int index);

	public int countTestCases();

	public Enumeration<?> tests();
}
