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
package org.powermock.utils.internal;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.powermock.utils.model.synchronizer.ConditionSpecification;
import org.powermock.utils.model.synchronizer.Duration;
import org.powermock.utils.model.synchronizer.PollSpecification;
import org.powermock.utils.model.synchronizer.SynchronizerOperation;

public class AwaitOperationImpl implements SynchronizerOperation, UncaughtExceptionHandler {
	private final Duration maxWaitTime;
	private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
	private final CountDownLatch latch;
	private Exception exception = null;

	public AwaitOperationImpl(final Duration maxWaitTime, final ConditionSpecification specification, final PollSpecification pollSpecification) {
		if (maxWaitTime == null) {
			throw new IllegalArgumentException("You must specify a maximum waiting time (was null).");
		}
		if (specification == null) {
			throw new IllegalArgumentException("You must specify a condition that to match (was null).");
		}
		latch = new CountDownLatch(1);
		this.maxWaitTime = maxWaitTime;
		final Duration pollInterval = pollSpecification == null ? new DurationImpl(500, TimeUnit.MILLISECONDS) : pollSpecification.getPollInterval();
		executor.scheduleAtFixedRate(new Runnable() {
			public void run() {
				try {
					if (specification.isConditionSatisified()) {
						latch.countDown();
					}
				} catch (Exception e) {
					exception = e;
					latch.countDown();
				}
			}
		}, pollInterval.getValue(), pollInterval.getValue(), pollInterval.getTimeUnit());

	}

	public void join() throws Exception {
		try {
			final long timeout = maxWaitTime.getValue();
			final boolean finishedBeforeTimeout;
			if (timeout == ForeverImpl.DURATION_FOREVER) {
				latch.await();
				finishedBeforeTimeout = true;
			} else {
				finishedBeforeTimeout = latch.await(timeout, maxWaitTime.getTimeUnit());
			}
			if (exception != null) {
				throw exception;
			} else if (!finishedBeforeTimeout) {
				throw new TimeoutException(String.format("Operation didn't complete within %s %s.", timeout, maxWaitTime.getTimeUnit().toString()
						.toLowerCase()));
			}
		} finally {
			executor.shutdown();
			if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
				executor.shutdownNow();
			}
		}
	}

	public void uncaughtException(Thread thread, Throwable throwable) {
		if (throwable instanceof Exception) {
			exception = (Exception) throwable;
			if (latch.getCount() != 0) {
				latch.countDown();
			}
		} else {
			throw new RuntimeException(throwable);
		}
	}

	public SynchronizerOperation andCatchAllUncaughtExceptions() {
		Thread.setDefaultUncaughtExceptionHandler(this);
		return this;
	}
}
