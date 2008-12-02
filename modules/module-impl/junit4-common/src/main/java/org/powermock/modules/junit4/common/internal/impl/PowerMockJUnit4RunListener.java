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
package org.powermock.modules.junit4.common.internal.impl;

import java.lang.reflect.Method;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.powermock.core.MockRepository;
import org.powermock.core.spi.PowerMockTestListener;
import org.powermock.reflect.Whitebox;
import org.powermock.tests.result.Result;
import org.powermock.tests.result.TestMethodResult;
import org.powermock.tests.result.impl.TestMethodResultImpl;
import org.powermock.tests.utils.Keys;

public class PowerMockJUnit4RunListener extends RunListener {

	private final ClassLoader mockClassLoader;

	private final PowerMockTestListener[] powerMockTestListeners;

	private int failureCount;

	private int ignoreCount;

	private boolean currentTestSuccessful = true;

	public PowerMockJUnit4RunListener(ClassLoader mockClassLoader, PowerMockTestListener[] powerMockTestListeners) {
		this.mockClassLoader = mockClassLoader;
		this.powerMockTestListeners = powerMockTestListeners;
	}

	/**
	 * Performs clean up after each test. The {@link MockRepository#clear()}
	 * methods has to be called by the correct class loader for the state to be
	 * cleared. Therefore it is invoked using reflection when the class is
	 * loaded from the correct class loader.
	 */
	@Override
	public void testFinished(Description description1) throws Exception {
		Class<?> mockRepositoryClass = mockClassLoader.loadClass(MockRepository.class.getName());
		try {
			final Object test = Whitebox.invokeMethod(mockRepositoryClass, "getAdditionalState", Keys.CURRENT_TEST_INSTANCE);
			final Method testMethod = (Method) Whitebox.invokeMethod(mockRepositoryClass, "getAdditionalState", Keys.CURRENT_TEST_METHOD);

			notifyListenersOfTestResult(test, testMethod);
		} finally {
			// Clear state
			Whitebox.invokeMethod(mockRepositoryClass, "clear");
		}
	}

	/**
	 * @return The number of failed tests.
	 */
	public int getFailureCount() {
		return failureCount;
	}

	/**
	 * @return The number of successful tests.
	 */
	public int getIgnoreCount() {
		return ignoreCount;
	}

	@Override
	public void testFailure(Failure failure) throws Exception {
		currentTestSuccessful = false;
		failureCount++;
	}

	@Override
	public void testIgnored(Description description) throws Exception {
		ignoreCount++;
	}

	private void notifyListenersOfTestResult(Object test, Method testMethod) throws Exception {
		try {
			final TestMethodResult testMethodResultImpl = new TestMethodResultImpl((currentTestSuccessful ? Result.SUCCESSFUL : Result.FAILED));

			for (PowerMockTestListener testListener : powerMockTestListeners) {
				testListener.afterTestMethod(test, testMethod, new Object[0], testMethodResultImpl);
			}
		} finally {
			currentTestSuccessful = true;
		}
	}
}
