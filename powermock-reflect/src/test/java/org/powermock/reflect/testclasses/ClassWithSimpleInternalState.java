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

package org.powermock.reflect.testclasses;

public class ClassWithSimpleInternalState {

	private int someIntState = -1;

	private String someStringState = "-1";

	private static long someStaticLongState = -1L;

	public static long getLong() {
		return someStaticLongState;
	}

	public long getSomeStaticLongState() {
		return someStaticLongState;
	}

	public int getSomeIntState() {
		return someIntState;
	}

	public String getSomeStringState() {
		return someStringState;
	}

	public static void setLong(long state) {
		someStaticLongState = state;
	}
}
