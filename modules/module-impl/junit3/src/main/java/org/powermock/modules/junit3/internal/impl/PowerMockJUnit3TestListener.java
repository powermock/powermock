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
package org.powermock.modules.junit3.internal.impl;

import java.lang.reflect.Method;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestListener;
import junit.runner.TestRunListener;

import org.powermock.core.MockRepository;

/**
 * An implementation of the {@link TestRunListener} interface that performs
 * cleanup after each test so that no state is maintained in PowerMock between
 * test runs.
 * 
 */
public class PowerMockJUnit3TestListener implements TestListener {
	private ClassLoader mockClassLoader;

	public PowerMockJUnit3TestListener(ClassLoader mockClassLoader) {
		this.mockClassLoader = mockClassLoader;
	}

	/**
	 * Does nothing.
	 */
	public void addError(Test test, Throwable t) {
	}

	/**
	 * Does nothing.
	 */
	public void addFailure(Test test, AssertionFailedError t) {
	}

	public void endTest(Test test) {
		try {
			Class<?> powerMockClass = mockClassLoader.loadClass(MockRepository.class.getName());
			Method method = powerMockClass.getDeclaredMethod("clear");
			if (method == null) {
				throw new IllegalStateException("Method clearState was not found in " + MockRepository.class);
			}
			method.setAccessible(true);
			method.invoke(powerMockClass);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Does nothing.
	 */
	public void startTest(Test test) {
	}
}
