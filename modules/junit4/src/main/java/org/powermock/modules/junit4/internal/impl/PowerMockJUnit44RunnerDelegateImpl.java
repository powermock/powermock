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
package org.powermock.modules.junit4.internal.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.junit.internal.runners.ClassRoadie;
import org.junit.internal.runners.InitializationError;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.internal.runners.MethodRoadie;
import org.junit.internal.runners.MethodValidator;
import org.junit.internal.runners.TestClass;
import org.junit.internal.runners.TestMethod;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.manipulation.Sortable;
import org.junit.runner.manipulation.Sorter;
import org.junit.runner.notification.RunNotifier;
import org.powermock.Whitebox;
import org.powermock.modules.junit4.common.internal.PowerMockJUnitRunnerDelegate;
import org.powermock.modules.junit4.internal.impl.testcaseworkaround.PowerMockJUnit4MethodValidator;

/**
 * A JUnit4 test runner that only runs a specified set of test methods in a test
 * class.
 * 
 * <p>
 * Most parts of this class is essentially a rip off from
 * {@link JUnit4ClassRunner} used in JUnit 4.4. It does however not extend this
 * class because we cannot let it perform the stuff it does in its constructor.
 * 
 * @see JUnit4ClassRunner
 * @author Johan Haleby
 * 
 */
public class PowerMockJUnit44RunnerDelegateImpl extends Runner implements Filterable, Sortable, PowerMockJUnitRunnerDelegate {
	private final List<Method> testMethods;
	private TestClass testClass;

	public PowerMockJUnit44RunnerDelegateImpl(Class<?> klass, String[] methodsToRun) throws InitializationError {
		testClass = new TestClass(klass);
		testMethods = getTestMethods(klass, methodsToRun);
		validate();
	}

	public PowerMockJUnit44RunnerDelegateImpl(Class<?> klass) throws InitializationError {
		this(klass, null);
	}

	@SuppressWarnings("unchecked")
	protected List<Method> getTestMethods(Class<?> klass, String[] methodsToRun) {
		if (methodsToRun == null || methodsToRun.length == 0) {
			// The getTestMethods of TestClass is not visible so we need to look
			// it invoke it using reflection.
			try {
				return (List<Method>) Whitebox.invokeMethod(testClass, "getTestMethods");
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		} else {
			List<Method> foundMethods = new LinkedList<Method>();
			Method[] methods = klass.getMethods();
			for (String methodName : methodsToRun) {
				for (Method method : methods) {
					if (method.getName().equals(methodName)) {
						foundMethods.add(method);
					}
				}
			}
			return foundMethods;
		}
	}

	protected void validate() throws InitializationError {
		MethodValidator methodValidator = new PowerMockJUnit4MethodValidator(testClass);
		methodValidator.validateMethodsForDefaultRunner();
		methodValidator.assertValid();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see se.jayway.powermock.junit.junit4.internal.impl.PowerMockJUnitRunnerDelegate#run(org.junit.runner.notification.RunNotifier)
	 */
	@Override
	public void run(final RunNotifier notifier) {
		new ClassRoadie(notifier, testClass, getDescription(), new Runnable() {
			public void run() {
				runMethods(notifier);
			}
		}).runProtected();
	}

	protected void runMethods(final RunNotifier notifier) {
		for (Method method : testMethods) {
			invokeTestMethod(method, notifier);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see se.jayway.powermock.junit.junit4.internal.impl.PowerMockJUnitRunnerDelegate#getDescription()
	 */
	@Override
	public Description getDescription() {
		Description spec = Description.createSuiteDescription(getName(), classAnnotations());
		List<Method> testMethods = this.testMethods;
		for (Method method : testMethods)
			spec.addChild(methodDescription(method));
		return spec;
	}

	protected Annotation[] classAnnotations() {
		return testClass.getJavaClass().getAnnotations();
	}

	protected String getName() {
		return getTestClass().getName();
	}

	protected Object createTest() throws Exception {
		return getTestClass().getConstructor().newInstance();
	}

	protected void invokeTestMethod(Method method, RunNotifier notifier) {
		Description description = methodDescription(method);
		Object test;
		try {
			test = createTest();
		} catch (InvocationTargetException e) {
			notifier.testAborted(description, e.getCause());
			return;
		} catch (Exception e) {
			notifier.testAborted(description, e);
			return;
		}
		TestMethod testMethod = wrapMethod(method);
		new MethodRoadie(test, testMethod, notifier, description).run();
	}

	protected TestMethod wrapMethod(Method method) {
		return new TestMethod(method, testClass);
	}

	protected String testName(Method method) {
		return method.getName();
	}

	protected Description methodDescription(Method method) {
		return Description.createTestDescription(getTestClass().getJavaClass(), testName(method), testAnnotations(method));
	}

	protected Annotation[] testAnnotations(Method method) {
		return method.getAnnotations();
	}

	public void filter(Filter filter) throws NoTestsRemainException {
		for (Iterator<Method> iter = testMethods.iterator(); iter.hasNext();) {
			Method method = iter.next();
			if (!filter.shouldRun(methodDescription(method)))
				iter.remove();
		}
		if (testMethods.isEmpty())
			throw new NoTestsRemainException();
	}

	public void sort(final Sorter sorter) {
		Collections.sort(testMethods, new Comparator<Method>() {
			public int compare(Method o1, Method o2) {
				return sorter.compare(methodDescription(o1), methodDescription(o2));
			}
		});
	}

	protected TestClass getTestClass() {
		return testClass;
	}

	public int getTestCount() {
		return testMethods.size();
	}
}
