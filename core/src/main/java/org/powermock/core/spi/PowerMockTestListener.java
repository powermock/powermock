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
package org.powermock.core.spi;

import java.lang.reflect.Method;

import org.powermock.core.spi.testresult.TestMethodResult;
import org.powermock.core.spi.testresult.TestSuiteResult;

/**
 * This interface should be implemented by all PowerMock test listeners. The
 * listener will be notified on the events present in this interface. Please
 * note that a listener cannot hold state.
 */
public interface PowerMockTestListener {

	/**
	 * Invoked once before the test run has started.
	 * 
	 * @param testClass
	 *            The type of the test to be executed.
	 * @param testMethods
	 *            The test methods that will be executed during the test.
	 * @throws Exception
	 *             If something unexpected occurs.
	 */
	void beforeTestSuiteStarted(Class<?> testClass, Method[] testMethods) throws Exception;

	/**
	 * Invoked before each test method.
	 * 
	 * @param testInstance
	 *            The test case instance.
	 * @param method
	 *            The test method that is currently executed.
	 * @param arguments
	 *            The arguments passed to the test method if any. May be an
	 *            empty array but never <code>null</code>.
	 * @throws Exception
	 *             If something unexpected occurs.
	 */
	void beforeTestMethod(Object testInstance, Method method, Object[] arguments) throws Exception;

	/**
	 * Invoked after each test method.
	 * 
	 * * @param testInstance The test case instance.
	 * 
	 * @param method
	 *            The test method that is currently executed.
	 * @param arguments
	 *            The arguments passed to the test method if any. May be an
	 *            empty array but never <code>null</code>.
	 * @param testResult
	 *            The outcome of the test method.
	 * @throws Exception
	 *             If something unexpected occurs.
	 */
	void afterTestMethod(Object testInstance, Method method, Object[] arguments, TestMethodResult testResult) throws Exception;

	/**
	 * Invoked after a test suite has ended.
	 * 
	 * @param testClass
	 *            The type of the test to be executed.
	 * @param testMethods
	 *            The test methods that were executed during the test.
	 * @param testResult
	 *            The outcome of the test suite.
	 * @throws Exception
	 *             If something unexpected occurs.
	 */
	void afterTestSuiteEnded(Class<?> testClass, Method[] methods, TestSuiteResult testResult) throws Exception;
}
