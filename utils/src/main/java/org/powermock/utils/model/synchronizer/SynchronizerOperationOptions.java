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
package org.powermock.utils.model.synchronizer;

import java.util.concurrent.TimeUnit;

import org.hamcrest.Matcher;
import org.powermock.utils.internal.ConditionSpecificationImpl;
import org.powermock.utils.internal.DurationImpl;
import org.powermock.utils.internal.ForeverImpl;

public class SynchronizerOperationOptions {

	public static Duration duration(long time, TimeUnit unit) {
		return new DurationImpl(time, unit);
	}
	
	public static Duration atMost(long time, TimeUnit unit) {
		return new DurationImpl(time, unit);
	}

	public static Duration forever() {
		return new ForeverImpl();
	}

	public static <T> ConditionSpecification until(Condition<T> condition, Matcher<T> matcher) {
		return new ConditionSpecificationImpl<T>(condition, matcher);
	}

	public static ConditionSpecification until(ConditionSpecification conditionSpecification) {
		return conditionSpecification;
	}
}
