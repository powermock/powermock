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
package org.powermock.tests.utils.impl;

import java.lang.reflect.AnnotatedElement;

import org.powermock.tests.utils.TestClassesExtractor;

/**
 * Base class for all test class extractors.
 */
public abstract class AbstractTestClassExtractor implements TestClassesExtractor {

	public boolean isPrepared(AnnotatedElement element, String fullyQualifiedClassName) {
		if (fullyQualifiedClassName == null) {
			throw new IllegalArgumentException("fullyQualifiedClassName cannot be null.");
		}
		final String[] testClasses = getTestClasses(element);
		if (testClasses != null) {
			for (String className : testClasses) {
				if (className.equals(fullyQualifiedClassName)) {
					return true;
				}
			}
		}
		return false;
	}
}
