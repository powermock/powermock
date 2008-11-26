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
import org.junit.runner.notification.RunListener;
import org.powermock.core.MockRepository;

public class PowerMockRunListener extends RunListener {

	private ClassLoader mockClassLoader;

	public PowerMockRunListener(ClassLoader mockClassLoader) {
		this.mockClassLoader = mockClassLoader;
	}

	/**
	 * Performs clean up after each test. The {@link MockRepository#clear()}
	 * methods has to be called by the correct class loader for the state to be
	 * cleared. Therefore it is invoked using reflection when the class is
	 * loaded from the correct class loader.
	 */
	@Override
	public void testFinished(Description description1) throws Exception {
		Class<?> powerMockClass = mockClassLoader.loadClass(MockRepository.class.getName());

		Method method = powerMockClass.getDeclaredMethod("clear");
		if (method == null) {
			throw new IllegalStateException("Method clearState was not found in " + MockRepository.class);
		}
		method.setAccessible(true);
		method.invoke(powerMockClass);
	}
}
