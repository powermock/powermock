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

package org.powermock.reflect.context;

import java.util.HashMap;
import java.util.Map;

/**
 * The purpose of this context is that it should define fields not available in
 * the target object to where the state is supposed to be copied.
 */
public class ClassFieldsNotInTargetContext {
	private static long something = 42L;

	private static Map<?, ?> map = new HashMap<Object, Object>();

	public static long getLong() {
		return something;
	}

	public static Map<?, ?> getMap() {
		return map;
	}
}
