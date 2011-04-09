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

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.*;
import org.junit.runner.notification.RunNotifier;
import org.powermock.core.MockRepository;
import org.powermock.modules.junit4.common.internal.JUnit4TestSuiteChunker;
import org.powermock.modules.junit4.common.internal.PowerMockJUnitRunnerDelegate;

public abstract class AbstractCommonPowerMockRunner extends Runner implements Filterable, Sortable {

	private JUnit4TestSuiteChunker suiteChunker;

	public AbstractCommonPowerMockRunner(Class<?> klass,
			Class<? extends PowerMockJUnitRunnerDelegate> runnerDelegateImplClass) throws Exception {
		suiteChunker = new JUnit4TestSuiteChunkerImpl(klass, runnerDelegateImplClass);
		/*
		 * For extra safety clear the MockitoRepository on each new
		 * instantiation of the runner. This is good in cases where a previous
		 * test has used e.g. PowerMock#createMock(..) to create a mock without
		 * using this runner. That means that there's some state left in the
		 * MockRepository that hasn't been cleared. Currently clearing the
		 * MockRepository from any classloader will clear the previous state but
		 * it's not certain that this is always the case.
		 */
		MockRepository.clear();
	}

	@Override
	public Description getDescription() {
		return suiteChunker.getDescription();
	}

	@Override
	public void run(RunNotifier notifier) {
		try {
			suiteChunker.run(notifier);
		} finally {
			suiteChunker = null; // To avoid out of memory errors!
		}
	}

	@Override
	public synchronized int testCount() {
		return suiteChunker.getTestCount();
	}

	public void filter(Filter filter) throws NoTestsRemainException {
		suiteChunker.filter(filter);
	}

	public void sort(Sorter sorter) {
		suiteChunker.sort(sorter);
	}
}
