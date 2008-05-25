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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.powermock.core.classloader.MockClassLoader;
import org.powermock.modules.junit4.common.internal.JUnit4TestSuiteChunker;
import org.powermock.modules.junit4.common.internal.PowerMockJUnitRunnerDelegate;
import org.powermock.tests.utils.impl.AbstractTestSuiteChunkerImpl;

public class JUnit4TestSuiteChunkerImpl extends AbstractTestSuiteChunkerImpl<PowerMockJUnitRunnerDelegate> implements JUnit4TestSuiteChunker {

	private Description description;
	private final Class<? extends PowerMockJUnitRunnerDelegate> runnerDelegateImplementationType;

	public JUnit4TestSuiteChunkerImpl(Class<?> testClass, Class<? extends PowerMockJUnitRunnerDelegate> runnerDelegateImplementationType) {
		super(testClass);
		if (testClass == null) {
			throw new IllegalArgumentException("You must supply a test class");
		}

		if (runnerDelegateImplementationType == null) {
			throw new IllegalArgumentException("Runner delegate type cannot be null.");
		}

		this.runnerDelegateImplementationType = runnerDelegateImplementationType;

		try {
			createTestDelegators(testClass, getChunkEntries(testClass));
			// createTestChunks(testClass, getAllChunkEntries());
		} catch (Exception e) {
			throw new RuntimeException("Internal error: Failed to chunk the test suite.", e);
		}
	}

	public void run(RunNotifier notifier) {
		Set<Entry<MockClassLoader, List<Method>>> entrySet = getAllChunkEntries();
		Iterator<Entry<MockClassLoader, List<Method>>> iterator = entrySet.iterator();

		if (delegates.size() != getChunkSize()) {
			throw new IllegalStateException("Internal error: There must be an equal number of suites and delegates.");
		}

		for (PowerMockJUnitRunnerDelegate delegate : delegates) {
			Entry<MockClassLoader, List<Method>> next = iterator.next();
			PowerMockRunListener powerMockListener = new PowerMockRunListener(next.getKey());
			notifier.addListener(powerMockListener);
			delegate.run(notifier);
		}
	}

	public boolean shouldExecuteTestForMethod(Method potentialTestMethod) {
		return (potentialTestMethod.getName().startsWith("test") && Modifier.isPublic(potentialTestMethod.getModifiers())
				&& potentialTestMethod.getReturnType().equals(Void.TYPE) || potentialTestMethod.isAnnotationPresent(Test.class));
	}

	@Override
	protected PowerMockJUnitRunnerDelegate createDelegatorFromClassloader(MockClassLoader classLoader, Class<?> testClass,
			final List<Method> methodsToTest) throws Exception {

		Set<String> methodNames = new HashSet<String>();
		for (Method method : methodsToTest) {
			methodNames.add(method.getName());
		}

		final Class<?> testClassLoadedByMockedClassLoader = classLoader.loadClass(testClass.getName());
		Class<?> delegateClass = classLoader.loadClass(runnerDelegateImplementationType.getName());
		Constructor<?> con = delegateClass.getConstructor(new Class[] { Class.class, String[].class });
		return (PowerMockJUnitRunnerDelegate) con
				.newInstance(new Object[] { testClassLoadedByMockedClassLoader, methodNames.toArray(new String[0]) });
	}

	public synchronized int getTestCount() {
		if (testCount == NOT_INITIALIZED) {
			testCount = 0;
			for (PowerMockJUnitRunnerDelegate delegate : delegates) {
				testCount += delegate.getTestCount();
			}
		}
		return testCount;
	}

	public Description getDescription() {
		if (description == null) {
			if (delegates.size() == 0) {
				throw new IllegalStateException("Internal error: Run delegates were 0.");
			}

			// Use the first delegator as the base for the description.
			PowerMockJUnitRunnerDelegate delegate = delegates.get(0);
			description = delegate.getDescription();

			/*
			 * Add the remaining descriptions of all the chunked delegators. We
			 * do this to make sure that we avoid adding chunks as "Unrooted
			 * tests".
			 */
			for (int i = 1; i < delegates.size(); i++) {
				// Get the method-level descriptions
				ArrayList<Description> children = delegates.get(i).getDescription().getChildren();
				// Add all method-level descriptions to the main description.
				for (Description methodDescription : children) {
					description.addChild(methodDescription);
				}
			}
		}
		return description;
	}
}
