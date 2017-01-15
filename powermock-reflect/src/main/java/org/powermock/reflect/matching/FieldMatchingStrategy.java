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
package org.powermock.reflect.matching;

/**
 * Defines strategies for field matching.
 */
public enum FieldMatchingStrategy {
	/**
	 * All fields in the context must match <i>exactly</i> the fields in the
	 * target instance or class. This means that an exception will be thrown
	 * unless all fields in the context are found in the target.
	 */
	STRICT,
	/**
	 * All fields in the context are copied to the target instance or class. The
	 * context may contain additional fields not present in the target. Only
	 * fields that may be copied from the context to the target are taken into
	 * consideration. An exception will not be thrown if a field exists in the
	 * context but is non-existent in the target.
	 */
	MATCHING
}
