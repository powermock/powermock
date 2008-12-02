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
package org.powermock.modules.junit4.legacy.internal.impl;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.internal.runners.BeforeAndAfterRunner;
import org.junit.internal.runners.InitializationError;
import org.junit.internal.runners.TestClassRunner;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.powermock.core.spi.PowerMockTestListener;
import org.powermock.modules.junit4.common.internal.PowerMockJUnitRunnerDelegate;
import org.powermock.modules.junit4.legacy.internal.impl.testcaseworkaround.PowerMockJUnit4LegacyTestClassMethodsRunner;
import org.powermock.modules.junit4.legacy.internal.impl.testcaseworkaround.PowerMockJUnit4LegacyTestIntrospector;
import org.powermock.reflect.Whitebox;

/**
 * A JUnit4 legacy (i.e. v4.0-4.3) test runner that only runs a specified set of
 * test methods in a test class.
 * 
 * <p>
 * Most parts of this class is essentially a rip off from
 * {@link TestClassRunner} used in JUnit 4.3.
 * 
 * @see TestClassRunner
 * 
 */
public class PowerMockJUnit4LegacyRunnerDelegateImpl extends TestClassRunner implements PowerMockJUnitRunnerDelegate {

	private final int testCount;

	public PowerMockJUnit4LegacyRunnerDelegateImpl(Class<?> klass, String[] methodsToRun, PowerMockTestListener[] listeners)
			throws InitializationError, NoTestsRemainException {
		super(klass, new PowerMockJUnit4LegacyTestClassMethodsRunner(klass, listeners == null ? new PowerMockTestListener[0] : listeners));
		filter(new PowerMockJUnit4LegacyFilter(methodsToRun));

		testCount = methodsToRun.length;
	}

	public PowerMockJUnit4LegacyRunnerDelegateImpl(Class<?> klass, String[] methodsToRun) throws InitializationError, NoTestsRemainException {
		this(klass, methodsToRun, null);
	}

	@Override
	public void run(final RunNotifier notifier) {
		BeforeAndAfterRunner runner = new BeforeAndAfterRunner(getTestClass(), BeforeClass.class, AfterClass.class, null) {

			@Override
			protected void runUnprotected() {
				fEnclosedRunner.run(notifier);
			}

			@Override
			protected void addFailure(Throwable targetException) {
				notifier.fireTestFailure(new Failure(getDescription(), targetException));
			}
		};

		Whitebox.setInternalState(runner, "fTestIntrospector", new PowerMockJUnit4LegacyTestIntrospector(getTestClass()), BeforeAndAfterRunner.class);

		runner.runProtected();
	}

	/**
	 * {@inheritDoc}
	 */
	public int getTestCount() {
		return testCount;
	}

	@Override
	public Class<?> getTestClass() {
		return super.getTestClass();
	}
}
