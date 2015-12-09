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
package org.powermock.core.spi.support;

import org.powermock.core.spi.PowerMockTestListener;
import org.powermock.core.spi.testresult.TestMethodResult;
import org.powermock.core.spi.testresult.TestSuiteResult;

import java.lang.reflect.Method;

/**
 * An empty implementation of the {@link PowerMockTestListener} interface. May
 * be inherited by clients that wants to provide empty implementations of some
 * of the interface methods.
 */
public class AbstractPowerMockTestListenerBase implements PowerMockTestListener {

	/**
	 * Provides an empty implementation.
	 */
	@Override
	public void afterTestMethod(Object testInstance, Method method, Object[] arguments, TestMethodResult testResult) throws Exception {
	}

	/**
	 * Provides an empty implementation.
	 */
	@Override
	public void beforeTestMethod(Object testInstance, Method method, Object[] arguments) throws Exception {
	}

	/**
	 * Provides an empty implementation.
	 */
	@Override
	public void beforeTestSuiteStarted(Class<?> testClass, Method[] testMethods) throws Exception {
	}

	/**
	 * Provides an empty implementation.
	 */
	@Override
	public void afterTestSuiteEnded(Class<?> testClass, Method[] methods, TestSuiteResult testResult) throws Exception {
	}
}
