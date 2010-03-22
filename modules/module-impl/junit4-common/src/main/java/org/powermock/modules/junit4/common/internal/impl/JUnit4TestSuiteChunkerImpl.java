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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.manipulation.Sortable;
import org.junit.runner.manipulation.Sorter;
import org.junit.runner.notification.RunNotifier;
import org.powermock.core.spi.PowerMockTestListener;
import org.powermock.core.spi.testresult.TestSuiteResult;
import org.powermock.core.spi.testresult.impl.TestSuiteResultImpl;
import org.powermock.modules.junit4.common.internal.JUnit4TestSuiteChunker;
import org.powermock.modules.junit4.common.internal.PowerMockJUnitRunnerDelegate;
import org.powermock.tests.utils.PowerMockTestNotifier;
import org.powermock.tests.utils.TestChunk;
import org.powermock.tests.utils.impl.AbstractTestSuiteChunkerImpl;
import org.powermock.tests.utils.impl.PowerMockTestNotifierImpl;

public class JUnit4TestSuiteChunkerImpl extends AbstractTestSuiteChunkerImpl<PowerMockJUnitRunnerDelegate> implements
		JUnit4TestSuiteChunker, Filterable, Sortable {

	private Description description;
	private final Class<? extends PowerMockJUnitRunnerDelegate> runnerDelegateImplementationType;

	public JUnit4TestSuiteChunkerImpl(Class<?> testClass,
			Class<? extends PowerMockJUnitRunnerDelegate> runnerDelegateImplementationType) throws Exception {
		super(testClass);
		if (testClass == null) {
			throw new IllegalArgumentException("You must supply a test class");
		}

		if (runnerDelegateImplementationType == null) {
			throw new IllegalArgumentException("Runner delegate type cannot be null.");
		}

		this.runnerDelegateImplementationType = runnerDelegateImplementationType;

		try {
			createTestDelegators(testClass, getTestChunksEntries(testClass));
		} catch (InvocationTargetException e) {
			final Throwable cause = e.getCause();
			if (cause instanceof Exception) {
				throw (Exception) cause;
			} else if (cause instanceof Error) {
				throw (Error) cause;
			} else {
				throw new RuntimeException(cause);
			}
		}
	}

	public void run(RunNotifier notifier) {
		List<TestChunk> chunkEntries = getTestChunks();
		Iterator<TestChunk> iterator = chunkEntries.iterator();

		if (delegates.size() != getChunkSize()) {
			throw new IllegalStateException("Internal error: There must be an equal number of suites and delegates.");
		}

		final Class<?> testClass = getTestClasses()[0];
		final PowerMockTestListener[] powerMockTestListeners = (PowerMockTestListener[]) getPowerMockTestListenersLoadedByASpecificClassLoader(
				testClass, this.getClass().getClassLoader());
		final Set<Method> allMethods = new LinkedHashSet<Method>();
		for (TestChunk testChunk : getTestChunks()) {
			allMethods.addAll(testChunk.getTestMethodsToBeExecutedByThisClassloader());
		}

		final Method[] allMethodsAsArray = allMethods.toArray(new Method[0]);
		final PowerMockTestNotifier powerMockTestNotifier = new PowerMockTestNotifierImpl(powerMockTestListeners);
		powerMockTestNotifier.notifyBeforeTestSuiteStarted(testClass, allMethodsAsArray);

		int failureCount = 0;
		int successCount = 0;
		int ignoreCount = 0;

		for (int i = 0; i < delegates.size(); i++) {
			TestChunk next = iterator.next();
			final ClassLoader key = next.getClassLoader();
			PowerMockJUnit4RunListener powerMockListener = new PowerMockJUnit4RunListener(key, powerMockTestNotifier);
			notifier.addListener(powerMockListener);
			final PowerMockJUnitRunnerDelegate delegate = delegates.get(i);
			delegate.run(notifier);
			final int failureCountForThisPowerMockListener = powerMockListener.getFailureCount();
			final int ignoreCountForThisPowerMockListener = powerMockListener.getIgnoreCount();
			failureCount += failureCountForThisPowerMockListener;
			ignoreCount += ignoreCountForThisPowerMockListener;
			successCount += delegate.getTestCount() - failureCountForThisPowerMockListener
					- ignoreCountForThisPowerMockListener;
			notifier.removeListener(powerMockListener);
		}

		final TestSuiteResult testSuiteResult = new TestSuiteResultImpl(failureCount, successCount, getTestCount(),
				ignoreCount);
		powerMockTestNotifier.notifyAfterTestSuiteEnded(testClass, allMethodsAsArray, testSuiteResult);
	}

	public boolean shouldExecuteTestForMethod(Class<?> testClass, Method potentialTestMethod) {
		return (potentialTestMethod.getName().startsWith("test")
				&& Modifier.isPublic(potentialTestMethod.getModifiers())
				&& potentialTestMethod.getReturnType().equals(Void.TYPE) && TestCase.class.isAssignableFrom(testClass) || potentialTestMethod
				.isAnnotationPresent(Test.class));
	}

	@Override
	protected PowerMockJUnitRunnerDelegate createDelegatorFromClassloader(ClassLoader classLoader, Class<?> testClass,
			final List<Method> methodsToTest) throws Exception {

		Set<String> methodNames = new HashSet<String>();
		for (Method method : methodsToTest) {
			methodNames.add(method.getName());
		}

		final Class<?> testClassLoadedByMockedClassLoader = Class.forName(testClass.getName(), false, classLoader);

		/*
		 * Array classes cannot be loaded be classloader.loadClass(..) in JDK 6.
		 * See http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6500212.
		 */
		final Class<?> powerMockTestListenerArrayType = Class.forName(PowerMockTestListener[].class.getName(), false,
				classLoader);
		final Class<?> delegateClass = Class.forName(runnerDelegateImplementationType.getName(), false, classLoader);
		Constructor<?> con = delegateClass.getConstructor(new Class[] { Class.class, String[].class,
				powerMockTestListenerArrayType });
		final PowerMockJUnitRunnerDelegate newInstance = (PowerMockJUnitRunnerDelegate) con.newInstance(new Object[] {
				testClassLoadedByMockedClassLoader, methodNames.toArray(new String[0]),
				getPowerMockTestListenersLoadedByASpecificClassLoader(testClass, classLoader) });
		return newInstance;
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
				/*
				 * This happens if Test A extends Test B and B uses the @RunWith
				 * annotation and there are no tests defined in class B.
				 */
				return Description.createTestDescription(this.getClass(), "no tests in this class");
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

	public void filter(Filter filter) throws NoTestsRemainException {
		for (Object delegate : delegates) {
			if (delegate instanceof Filterable) {
				((Filterable) delegate).filter(filter);
			}
		}
	}

	public void sort(Sorter sorter) {
		for (Object delegate : delegates) {
			if (delegate instanceof Sortable) {
				((Sortable) delegate).sort(sorter);
			}
		}
	}
}
