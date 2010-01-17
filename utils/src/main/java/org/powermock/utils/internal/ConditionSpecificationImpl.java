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

import org.hamcrest.Matcher;
import org.powermock.utils.model.synchronizer.Condition;
import org.powermock.utils.model.synchronizer.ConditionSpecification;
import org.powermock.utils.model.synchronizer.Duration;

public class ConditionSpecificationImpl<T> implements ConditionSpecification {

	private final Condition<T> condition;
	private final Matcher<T> matcher;

	public ConditionSpecificationImpl(Condition<T> condition, Matcher<T> matcher) {
		if (condition == null) {
			throw new IllegalArgumentException("You must specify a condition (was null).");
		}
		if (matcher == null) {
			throw new IllegalArgumentException("You must specify a matcher (was null).");
		}
		this.matcher = matcher;
		this.condition = condition;
	}

	public boolean isConditionSatisified() throws Exception {
		return matcher.matches(condition.condition());
	}

	public Duration getPollInterval() {
		return condition.getPollInterval();
	}
}
