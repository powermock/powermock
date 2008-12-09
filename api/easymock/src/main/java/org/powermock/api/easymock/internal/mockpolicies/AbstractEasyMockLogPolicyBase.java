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
package org.powermock.api.easymock.internal.mockpolicies;

import static org.easymock.classextension.EasyMock.makeThreadSafe;
import static org.powermock.api.easymock.PowerMock.createNiceMock;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.powermock.mockpolicies.AbstractLogPolicyBase;

/**
 * A base class for EasyMock log policies.
 */
public abstract class AbstractEasyMockLogPolicyBase extends AbstractLogPolicyBase {

	/**
	 * Get the substitute return values.
	 */
	public final Map<Method, Object> getSubtituteReturnValues() {
		final Map<Method, Object> subtitutes = new HashMap<Method, Object>();

		Method[] loggerFactoryMethods = getLoggerMethods(getLoggerFactoryClassName(), getLoggerMethodName(), getLogFrameworkName());

		Class<?> loggerType = null;
		try {
			loggerType = getType(getLoggerClassToMock(), getLogFrameworkName());
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		final Object loggerMock = createNiceMock(loggerType);
		// Allow the mock to be used in a multi-threaded environment
		makeThreadSafe(loggerMock, true);

		for (Method method : loggerFactoryMethods) {
			subtitutes.put(method, loggerMock);
		}

		return Collections.unmodifiableMap(subtitutes);
	}

	/**
	 * @return The name of the methods in the Logger Factory that should return
	 *         a mock upon invocation.
	 */
	protected abstract String getLoggerMethodName();

	/**
	 * @return The fully-qualified class name of the Logger Factory that
	 *         contains the methods that should return a mock upon invocation.
	 */
	protected abstract String getLoggerFactoryClassName();

	/**
	 * @return The fully-qualified class name of the class that should be
	 *         mocked. The mock instance of this class will then be returned
	 *         each time a specified method in the Logger Factory is invoked.
	 */
	protected abstract String getLoggerClassToMock();

	/**
	 * @return The name of the log framework. Used in error messages, for
	 *         example if the {@link #getLoggerFactoryClassName()} cannot be
	 *         found in the classpath.
	 */
	protected abstract String getLogFrameworkName();
}
