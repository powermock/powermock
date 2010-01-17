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

import java.util.concurrent.TimeUnit;

import org.powermock.utils.model.synchronizer.Duration;

public class DurationImpl implements Duration {

	private final long value;
	private final TimeUnit unit;

	public DurationImpl(long value, TimeUnit unit) {
		if (value <= 0) {
			throw new IllegalArgumentException("value must be > 0");
		}
		if (unit == null) {
			throw new IllegalArgumentException("TimeUnit cannot be null");
		}
		this.value = value;
		this.unit = unit;
	}

	public TimeUnit getTimeUnit() {
		return unit;
	}

	public long getValue() {
		return value;
	}
}
