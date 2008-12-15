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
import java.lang.reflect.Modifier;
import java.util.Vector;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.powermock.core.spi.PowerMockTestListener;
import org.powermock.modules.junit3.internal.PowerMockJUnit3RunnerDelegate;

@SuppressWarnings("unchecked")
public class PowerMockJUnit3RunnerDelegateImpl extends TestSuite implements PowerMockJUnit3RunnerDelegate {

	private final Method[] methodsToRun;

	public PowerMockJUnit3RunnerDelegateImpl(final Class<?> theClass, Method[] methodsToRun, String name, PowerMockTestListener[] powerListeners) {
		this(theClass, methodsToRun, powerListeners);
		setName(name);
	}

	/**
	 * Constructs a TestSuite from the given class. Adds all the methods
	 * starting with "test" as test cases to the suite. Parts of this method was
	 * cut'n'pasted on the train between Malmö and Stockholm.
	 */
	public PowerMockJUnit3RunnerDelegateImpl(final Class<?> theClass, Method[] methodsToRun, PowerMockTestListener[] powerMockTestListeners) {
		this.methodsToRun = methodsToRun;
		setName(theClass.getName());

		try {
			getTestConstructor(theClass); // Avoid generating multiple error
			// messages
		} catch (NoSuchMethodException e) {
			addTest(warning("Class " + theClass.getName() + " has no public constructor TestCase(String name) or TestCase()"));
			return;
		}

		if (!Modifier.isPublic(theClass.getModifiers())) {
			addTest(warning("Class " + theClass.getName() + " is not public"));
			return;
		}

		Class<?> superClass = theClass;
		Vector<?> names = new Vector<Object>();
		Method addTestMethod = null;
		Method[] declaredMethods = TestSuite.class.getDeclaredMethods();
		for (Method method : declaredMethods) {
			/*
			 * Since the TestSuite class is loaded by another classloader we
			 * look up the method this way (which is not fail-proof, but good
			 * enough).
			 */
			if (method.getName().equals("addTestMethod")) {
				addTestMethod = method;
			}
		}

		if (addTestMethod == null) {
			throw new RuntimeException("Internal error: Failed to get addTestMethod for JUnit3.");
		}

		addTestMethod.setAccessible(true);

		while (Test.class.isAssignableFrom(superClass)) {
			for (int i = 0; i < methodsToRun.length; i++) {
				try {
					addTestMethod.invoke(this, methodsToRun[i], names, theClass);
				} catch (Exception e) {
					throw new RuntimeException("Internal error: Failed to execute addTestMethod for JUnit3.");
				}
			}
			superClass = superClass.getSuperclass();
		}
		if (testCount() == 0) {
			addTest(warning("No tests found in " + theClass.getName()));
		}
	}

	/**
	 * Returns a test which will fail and log a warning message.
	 */
	private static Test warning(final String message) {
		return new TestCase("warning") {
			protected void runTest() {
				fail(message);
			}
		};
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Methods ran by this test runner delegate:\n");
		for (Method method : methodsToRun) {
			builder.append(method).append("\n");
		}
		return builder.toString();
	}
}
