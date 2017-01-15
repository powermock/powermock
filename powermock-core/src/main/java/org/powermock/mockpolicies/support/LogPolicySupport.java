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
package org.powermock.mockpolicies.support;

import org.powermock.reflect.Whitebox;

import java.lang.reflect.Method;

/**
 * A support class for mock policies dealing with logging frameworks.
 */
public class LogPolicySupport {
	/**
	 * Get the methods that should be mocked.
	 * 
	 * @param fullyQualifiedClassName
	 *            The fully-qualified name to the class that contains the
	 *            method.
	 * @param methodName
	 *            The name of the method that should be mocked.
	 * @param logFramework
	 *            The log framework that should be printed if the class
	 *            {@code fullyQualifiedClassName} cannot be found.
	 * @return The array of {@link Method}'s that should be mocked.
	 */
	public Method[] getLoggerMethods(String fullyQualifiedClassName, String methodName, String logFramework) {
		try {
			return Whitebox.getMethods(getType(fullyQualifiedClassName, logFramework), methodName);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Get the class type representing the fully-qualified name.
	 * 
	 * @param name
	 *            The fully-qualified name of a class to get.
	 * @param logFramework
	 *            The log framework that should be printed if the class cannot
	 *            be found.
	 * @return The class representing the fully-qualified name.
	 * @throws Exception
	 *             If something unexpected goes wrong, for example if the class
	 *             cannot be found.
	 */
	public Class<?> getType(String name, String logFramework) throws Exception {
		final Class<?> loggerType;
		try {
			loggerType = Class.forName(name);
		} catch (ClassNotFoundException e) {
			final String message = String.format("Cannot find %s in the classpath which the %s policy requires.", logFramework, getClass()
					.getSimpleName());
			throw new RuntimeException(message, e);
		}
		return loggerType;
	}
}
