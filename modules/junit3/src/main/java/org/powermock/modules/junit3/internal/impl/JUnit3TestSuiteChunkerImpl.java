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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.powermock.core.classloader.MockClassLoader;
import org.powermock.modules.junit3.internal.JUnit3TestSuiteChunker;
import org.powermock.modules.junit3.internal.PowerMockJUnit3RunnerDelegate;
import org.powermock.tests.utils.impl.AbstractTestSuiteChunkerImpl;

public class JUnit3TestSuiteChunkerImpl extends AbstractTestSuiteChunkerImpl<PowerMockJUnit3RunnerDelegate> implements JUnit3TestSuiteChunker {

	private String name;

	public JUnit3TestSuiteChunkerImpl(Class<? extends TestCase>... testClasses) throws Exception {
		super(testClasses);
		try {
			for (Class<? extends TestCase> testClass : testClasses) {
				createTestDelegators(testClass, getChunkEntries(testClass));
			}
		} catch (Exception e) {
			final Throwable cause = e.getCause();
			if (cause instanceof Exception) {
				throw (Exception) cause;
			} else {
				throw new RuntimeException(cause);
			}
		}
	}

	public JUnit3TestSuiteChunkerImpl(String name, Class<? extends TestCase>... testClasses) throws Exception {
		this(testClasses);
		this.name = name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected PowerMockJUnit3RunnerDelegate createDelegatorFromClassloader(MockClassLoader classLoader, Class<?> testClass,
			final List<Method> methodsToTest) throws Exception {

		final Class<?> testClassLoadedByMockedClassLoader = classLoader.loadClass(testClass.getName());
		Class<?> delegateClass = classLoader.loadClass(PowerMockJUnit3RunnerDelegateImpl.class.getName());
		Constructor<?> con = delegateClass.getConstructor(new Class[] { Class.class, Method[].class });
		final PowerMockJUnit3RunnerDelegate newDelegate = (PowerMockJUnit3RunnerDelegate) con.newInstance(new Object[] {
				testClassLoadedByMockedClassLoader, methodsToTest.toArray(new Method[0]) });
		newDelegate.setName(name);
		return newDelegate;
	}

	@Override
	protected void chunkClass(Class<?> testClass) {
		if (!TestCase.class.isAssignableFrom(testClass)) {
			throw new IllegalArgumentException(testClass.getName() + " must be a subtype of " + TestCase.class.getName());
		}
		super.chunkClass(testClass);
	}

	/**
	 * {@inheritDoc}
	 */
	public int getTestCount() {
		if (testCount == NOT_INITIALIZED) {
			testCount = 0;
			for (PowerMockJUnit3RunnerDelegate delegate : delegates) {
				testCount += delegate.testCount();
			}
		}
		return testCount;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean shouldExecuteTestForMethod(Class<?> testClass, Method potentialTestMethod) {
		return potentialTestMethod.getName().startsWith("test") && Modifier.isPublic(potentialTestMethod.getModifiers())
				&& potentialTestMethod.getReturnType().equals(Void.TYPE);
	}

	/**
	 * {@inheritDoc}
	 */
	public void addTest(Test test) {
		if (test == null) {
			throw new IllegalArgumentException("test cannot be null");
		}

		if (test instanceof TestCase) {
			// testSuiteDelegator.addTest(prepareTestCase((TestCase) test));
			super.addTestClassToSuite(test.getClass());
		} else if (test instanceof TestSuite) {
			final Enumeration<?> tests = ((TestSuite) test).tests();
			while (tests.hasMoreElements()) {
				addTest((Test) tests.nextElement());
			}
		} else {
			throw new IllegalArgumentException("The test type " + test.getClass().getName() + " is not supported. Only " + TestCase.class.getName()
					+ " and " + TestSuite.class.getName() + " are supported.");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void addTestSuite(Class<? extends TestCase> testClass) {
		super.addTestClassToSuite(testClass);
	}

	/**
	 * {@inheritDoc}
	 */
	public int countTestCases() {
		int count = 0;
		for (PowerMockJUnit3RunnerDelegate delegate : delegates) {
			count += delegate.countTestCases();
		}
		return count;
	}

	/**
	 * {@inheritDoc}
	 */
	public void run(TestResult result) {
		final Iterator<Entry<MockClassLoader, List<Method>>> iterator = getChunkIterator();
		for (PowerMockJUnit3RunnerDelegate delegate : delegates) {
			Entry<MockClassLoader, List<Method>> next = iterator.next();
			result.addListener(new PowerMockJUnit3TestListener(next.getKey()));
			delegate.run(result);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void runTest(Test test, TestResult result) {
		final Iterator<Entry<MockClassLoader, List<Method>>> iterator = getChunkIterator();
		for (PowerMockJUnit3RunnerDelegate delegate : delegates) {
			Entry<MockClassLoader, List<Method>> next = iterator.next();
			result.addListener(new PowerMockJUnit3TestListener(next.getKey()));
			delegate.runTest(test, result);
		}
	}

	private Iterator<Entry<MockClassLoader, List<Method>>> getChunkIterator() {
		Set<Entry<MockClassLoader, List<Method>>> entrySet = getAllChunkEntries();
		Iterator<Entry<MockClassLoader, List<Method>>> iterator = entrySet.iterator();

		if (delegates.size() != getChunkSize()) {
			throw new IllegalStateException("Internal error: There must be an equal number of suites and delegates.");
		}
		return iterator;
	}

	/**
	 * {@inheritDoc}
	 */
	public Test testAt(int index) {
		return delegates.get(getDelegatorIndex(index)).testAt(getInternalTestIndex(index));
	}

	/**
	 * {@inheritDoc}
	 */
	public Enumeration<?> tests() {
		final List<Object> tests = new LinkedList<Object>();
		for (PowerMockJUnit3RunnerDelegate delegate : delegates) {
			final Enumeration<?> delegateTests = delegate.tests();
			while (delegateTests.hasMoreElements()) {
				tests.add(delegateTests.nextElement());
			}
		}

		Enumeration<?> allTests = new Enumeration<Object>() {
			private volatile int count = 0;

			public boolean hasMoreElements() {
				return count != tests.size();
			}

			public Object nextElement() {
				return tests.get(count++);
			}
		};

		return allTests;
	}
}
