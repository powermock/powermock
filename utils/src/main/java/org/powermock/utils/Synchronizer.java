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

import java.util.concurrent.TimeUnit;

import org.powermock.utils.internal.AwaitOperationImpl;
import org.powermock.utils.model.synchronizer.ConditionSpecification;
import org.powermock.utils.model.synchronizer.Duration;
import org.powermock.utils.model.synchronizer.PollSpecification;
import org.powermock.utils.model.synchronizer.SynchronizerOperation;
import org.powermock.utils.model.synchronizer.SynchronizerOperationOptions;

public class Synchronizer extends SynchronizerOperationOptions {

	public static void block(ConditionSpecification conditionSpecification) throws Exception {
		block(forever(), conditionSpecification);
	}

	public static void block(long timeout, TimeUnit unit, ConditionSpecification conditionSpecification) throws Exception {
		block(duration(timeout, unit), conditionSpecification);
	}

	public static void block(Duration duration, ConditionSpecification conditionSpecification) throws Exception {
		await(duration, conditionSpecification).join();
	}

	public static void block(ConditionSpecification conditionSpecification, PollSpecification pollSpecification) throws Exception {
		block(forever(), conditionSpecification, pollSpecification);
	}

	public static void block(long timeout, TimeUnit unit, ConditionSpecification conditionSpecification, PollSpecification pollSpecification)
			throws Exception {
		block(duration(timeout, unit), conditionSpecification, pollSpecification);
	}

	public static void block(Duration duration, ConditionSpecification conditionSpecification, PollSpecification pollSpecification) throws Exception {
		await(duration, conditionSpecification, pollSpecification).join();
	}

	public static SynchronizerOperation await(long timeout, TimeUnit unit, ConditionSpecification conditionSpecification) {
		return await(duration(timeout, unit), conditionSpecification);
	}

	public static SynchronizerOperation await(ConditionSpecification conditionSpecification) {
		return await(forever(), conditionSpecification);
	}

	public static SynchronizerOperation await(Duration duration, ConditionSpecification conditionSpecification) {
		return await(duration, conditionSpecification, null);
	}

	public static SynchronizerOperation await(long timeout, TimeUnit unit, ConditionSpecification conditionSpecification,
			PollSpecification pollSpecification) {
		return await(duration(timeout, unit), conditionSpecification, pollSpecification);
	}

	public static SynchronizerOperation await(ConditionSpecification conditionSpecification, PollSpecification pollSpecification) {
		return await(forever(), conditionSpecification, pollSpecification);
	}

	public static SynchronizerOperation await(Duration duration, ConditionSpecification conditionSpecification, PollSpecification pollSpecification) {
		return new AwaitOperationImpl(duration, conditionSpecification, pollSpecification);
	}
}