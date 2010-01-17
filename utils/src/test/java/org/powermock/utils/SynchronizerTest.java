/*
 * Copyright 2010 the original author or authors.
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
package org.powermock.utils;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.powermock.utils.Synchronizer.await;
import static org.powermock.utils.model.synchronizer.SynchronizerOperationOptions.until;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Before;
import org.junit.Test;
import org.powermock.utils.classes.Asynch;
import org.powermock.utils.classes.ExceptionThrowingAsynch;
import org.powermock.utils.classes.ExceptionThrowingFakeRepository;
import org.powermock.utils.classes.FakeRepository;
import org.powermock.utils.classes.FakeRepositoryEqualsOne;
import org.powermock.utils.classes.FakeRepositoryImpl;
import org.powermock.utils.classes.FakeRepositoryValue;
import org.powermock.utils.model.synchronizer.BlockingSupportedOperation;
import org.powermock.utils.model.synchronizer.Condition;
import org.powermock.utils.model.synchronizer.ConditionSpecification;

public class SynchronizerTest {

	private FakeRepository fakeRepository;

	@Before
	public void setup() {
		fakeRepository = new FakeRepositoryImpl();
	}

	@Test(timeout = 2000)
	public void foreverConditionSpecificationWithDirectBlock() throws Exception {
		new Asynch(fakeRepository).perform();
		await(fakeRepositoryValueEqualsOne()).block();
		assertEquals(1, fakeRepository.getValue());
	}

	@Test(timeout = 2000)
	public void foreverConditionSpecificationUsingUntilWithDirectBlock() throws Exception {
		new Asynch(fakeRepository).perform();
		await(until(fakeRepositoryValueEqualsOne())).block();
		assertEquals(1, fakeRepository.getValue());
	}

	@Test(timeout = 2000)
	public void foreverConditionWithHamcrestMatchersWithDirectBlock() throws Exception {
		new Asynch(fakeRepository).perform();
		await(until(valueCondition(), equalTo(1))).block();
		assertEquals(1, fakeRepository.getValue());
	}

	@Test(timeout = 2000, expected = TimeoutException.class)
	public void conditionBreaksAfterDurationTimeout() throws Exception {
		new Asynch(fakeRepository).perform();
		await(200, TimeUnit.MILLISECONDS, until(valueCondition(), equalTo(1))).block();
		assertEquals(1, fakeRepository.getValue());
	}

	@Test(timeout = 2000, expected = IllegalStateException.class)
	public void uncaughtExceptionsArePropagatedToAwaitingThreadAndBreaksForeverBlockWhenSetToCatchAllUncaughtExceptions() throws Exception {
		BlockingSupportedOperation operation = await(until(valueCondition(), equalTo(1))).andCatchAllUncaughtExceptions();
		new ExceptionThrowingAsynch().perform();
		operation.block();
	}

	@Test(timeout = 2000, expected = IllegalStateException.class)
	public void exceptionsInValueConditionArePropagatedToAwaitingThreadAndBreaksForeverBlock() throws Exception {
		final ExceptionThrowingFakeRepository repository = new ExceptionThrowingFakeRepository();
		new Asynch(repository).perform();
		await(until(new FakeRepositoryValue(repository), equalTo(1))).block();
	}

	private ConditionSpecification fakeRepositoryValueEqualsOne() {
		return new FakeRepositoryEqualsOne(fakeRepository);
	}

	private Condition<Integer> valueCondition() {
		return new FakeRepositoryValue(fakeRepository);
	}
}
