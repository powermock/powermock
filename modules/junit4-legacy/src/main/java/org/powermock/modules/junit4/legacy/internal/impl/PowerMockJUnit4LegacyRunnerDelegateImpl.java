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

import org.powermock.modules.junit4.common.internal.PowerMockJUnitRunnerDelegate;
import org.junit.internal.runners.InitializationError;
import org.junit.internal.runners.TestClassRunner;
import org.junit.runner.manipulation.NoTestsRemainException;

/**
 * A JUnit4 legacy (i.e. v4.0-4.3) test runner that only runs a specified set of
 * test methods in a test class.
 * 
 * <p>
 * Most parts of this class is essentially a rip off from
 * {@link TestClassRunner} used in JUnit 4.3.
 * 
 * @see TestClassRunner
 * @author Johan Haleby
 * 
 */
public class PowerMockJUnit4LegacyRunnerDelegateImpl extends TestClassRunner
		implements PowerMockJUnitRunnerDelegate {

	private final int testCount;

	public PowerMockJUnit4LegacyRunnerDelegateImpl(Class<?> klass,
			String[] methodsToRun) throws InitializationError {
		super(klass);
		try {
			filter(new PowerMockJUnit4LegacyFilter(methodsToRun));
		} catch (NoTestsRemainException e) {
			throw new RuntimeException(e);
		}

		testCount = methodsToRun.length;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getTestCount() {
		return testCount;
	}
}
