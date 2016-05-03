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

import org.powermock.core.spi.testresult.TestMethodResult;
import org.powermock.core.spi.testresult.TestSuiteResult;

import java.lang.reflect.Method;

/**
 * Implementors of this interface that must provide the ability to notify
 * PowerMock test listeners with the events as specified by the methods declared
 * in this interface.
 */
public interface PowerMockTestNotifier {

	/**
	 * Notifies all listeners with the "before test method started" event.
	 */
	void notifyBeforeTestMethod(final Object testInstance, final Method testMethod, final Object[] arguments);

	/**
	 * Notifies all listeners with the "after test method ended" event.
	 */
	void notifyAfterTestMethod(Object testInstance, Method method, Object[] arguments, TestMethodResult testResult);

	/**
	 * Notifies all listeners with the "after test method ended" event. Uses
	 * some state-store to get the needed state. For this method to work
	 * {@link #notifyBeforeTestMethod(Object, Method, Object[])} must have been
	 * called before this method. Otherwise revert to using the
	 * {@link #notifyAfterTestMethod(Object, Method, Object[], TestMethodResult)}
	 * method.
	 * 
	 * @param successful
	 *            {@code true} if the test was successful,
	 *            {@code false} otherwise.
	 */
	void notifyAfterTestMethod(boolean successful);

	/**
	 * Notifies all listeners with the "before test suite started" event.
	 */
	void notifyBeforeTestSuiteStarted(final Class<?> testClass, final Method[] testMethods);

	/**
	 * Notifies all listeners with the "after test suite ended" event.
	 */
	void notifyAfterTestSuiteEnded(Class<?> testClass, Method[] methods, TestSuiteResult testResult);

}
