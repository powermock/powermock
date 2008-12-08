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
package org.powermock.api.easymock.mockpolicies;

import static org.easymock.classextension.EasyMock.makeThreadSafe;
import static org.powermock.api.easymock.PowerMock.createNiceMock;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.powermock.core.spi.PowerMockPolicy;
import org.powermock.reflect.Whitebox;

/**
 * A log4j mock policy. This mock policy deals with solving log4j related
 * mocking issues. It takes care of loading all concerned log4j classes through
 * the correct class-loader and automatically prepares and injects logger
 * instances. This policy does the following:
 * <ol>
 * <li>Prepares <tt>org.apache.log4j.Appender</tt> and
 * <tt>org.apache.log4j.Logger</tt> for testing.</li>
 * <li>All calls to the <tt>org.apache.log4j.Logger#getLogger(..)</tt> methods
 * are intercepted and returns a nice mock of type
 * <tt>org.apache.log4j.Logger</tt>.</li>
 * </ol>
 */
public class Log4jMockPolicy implements PowerMockPolicy {

	public String[] getFullyQualifiedNamesOfClassesToLoadByMockClassloader() {
		return new String[] { "org.apache.log4j.Appender", "org.apache.log4j.Logger" };
	}

	public String[] getFieldTypesToSuppress() {
		return null;
	}

	public Method[] getMethodsToSuppress() {
		return null;
	}

	public String[] getStaticInitializersToSuppress() {
		return null;
	}

	public Map<Method, Object> getSubtituteReturnValues() {
		final Map<Method, Object> subtitutes = new HashMap<Method, Object>();

		Method[] loggerFactoryMethods = getLoggerMethods();

		Class<?> loggerType = null;
		try {
			loggerType = getLog4jType("org.apache.log4j.Logger");
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

	private Method[] getLoggerMethods() {
		try {
			return Whitebox.getMethods(getLog4jType("org.apache.log4j.Logger"), "getLogger");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private Class<?> getLog4jType(String name) throws Exception {
		Class<?> loggerType = null;
		try {
			loggerType = Class.forName(name);
		} catch (ClassNotFoundException e) {
			final String message = "Cannot find log4j in the classpath which the " + Log4jMockPolicy.class.getSimpleName() + " policy requires.";
			throw new RuntimeException(message, e);
		}
		return loggerType;
	}

	public Field[] getFieldsSuppress() {
		return null;
	}
}