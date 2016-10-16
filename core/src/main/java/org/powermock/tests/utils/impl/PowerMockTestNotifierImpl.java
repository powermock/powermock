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
package org.powermock.tests.utils.impl;

import org.powermock.core.MockRepository;
import org.powermock.core.spi.PowerMockTestListener;
import org.powermock.core.spi.testresult.Result;
import org.powermock.core.spi.testresult.TestMethodResult;
import org.powermock.core.spi.testresult.TestSuiteResult;
import org.powermock.core.spi.testresult.impl.TestMethodResultImpl;
import org.powermock.tests.utils.Keys;
import org.powermock.tests.utils.PowerMockTestNotifier;

import java.lang.reflect.Method;

/**
 * Utility class that may be used by PowerMock test runners to notify listeners.
 * Uses the {@link MockRepository} to set and get state.
 */
public class PowerMockTestNotifierImpl implements PowerMockTestNotifier {

	private static final String ERROR_MESSAGE_TEMPLATE = "Invoking the %s method on PowerMock test listener %s failed.";

	private final PowerMockTestListener[] powerMockTestListeners;

	/**
	 * Create a new instance with the following parameters.
	 * 
	 * @param powerMockTestListeners
	 *            The PowerMock listeners that will be notified.
	 */
	public PowerMockTestNotifierImpl(PowerMockTestListener[] powerMockTestListeners) {
		if (powerMockTestListeners == null) {
			this.powerMockTestListeners = new PowerMockTestListener[0];
		} else {
			this.powerMockTestListeners = powerMockTestListeners;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void notifyAfterTestMethod(Object testInstance, Method method, Object[] arguments, TestMethodResult testResult) {
		for (int i = 0; i < powerMockTestListeners.length; i++) {
			final PowerMockTestListener testListener = powerMockTestListeners[i];
			try {
				testListener.afterTestMethod(testInstance, method, arguments, testResult);
			} catch (Exception e) {
				throw new RuntimeException(String.format(ERROR_MESSAGE_TEMPLATE, "afterTestMethod", testListener), e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void notifyAfterTestSuiteEnded(Class<?> testClass, Method[] methods, TestSuiteResult testResult) {
		for (PowerMockTestListener powerMockTestListener : powerMockTestListeners) {
			try {
				powerMockTestListener.afterTestSuiteEnded(testClass, methods, testResult);
			} catch (Exception e) {
				throw new RuntimeException(String.format(ERROR_MESSAGE_TEMPLATE, "afterTestSuiteEnded", powerMockTestListener), e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void notifyBeforeTestMethod(Object testInstance, Method testMethod, Object[] arguments) {
		MockRepository.putAdditionalState(Keys.CURRENT_TEST_INSTANCE, testInstance);
		MockRepository.putAdditionalState(Keys.CURRENT_TEST_METHOD, testMethod);
		MockRepository.putAdditionalState(Keys.CURRENT_TEST_METHOD_ARGUMENTS, arguments);
		for (int i = 0; i < powerMockTestListeners.length; i++) {
			final PowerMockTestListener testListener = powerMockTestListeners[i];
			try {
				testListener.beforeTestMethod(testInstance, testMethod, arguments);
			} catch (Exception e) {
				throw new RuntimeException(String.format(ERROR_MESSAGE_TEMPLATE, "beforeTestMethod", testListener), e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void notifyBeforeTestSuiteStarted(Class<?> testClass, Method[] testMethods) {
		for (PowerMockTestListener powerMockTestListener : powerMockTestListeners) {
			try {
				powerMockTestListener.beforeTestSuiteStarted(testClass, testMethods);
			} catch (Exception e) {
				throw new RuntimeException(String.format(ERROR_MESSAGE_TEMPLATE, "beforeTestSuiteStarted", powerMockTestListener), e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void notifyAfterTestMethod(boolean successful) {
		final Object test = MockRepository.getAdditionalState(Keys.CURRENT_TEST_INSTANCE);
		final Method testMethod = MockRepository.getAdditionalState(Keys.CURRENT_TEST_METHOD);
		final Object[] testArguments = MockRepository.getAdditionalState(Keys.CURRENT_TEST_METHOD_ARGUMENTS);
		final TestMethodResult testResult = new TestMethodResultImpl((successful ? Result.SUCCESSFUL : Result.FAILED));
		notifyAfterTestMethod(test, testMethod, testArguments, testResult);
	}
}
