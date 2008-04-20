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
import java.lang.reflect.Method;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.tests.utils.PrepareForTestExtractor;

/**
 * Default implementation of the {@link PrepareForTestExtractor} interface.
 * 
 * @author Johan Haleby
 */
public class PrepareForTestExtractorImpl implements PrepareForTestExtractor {

	/**
	 * {@inheritDoc}
	 */
	public Class<?>[] getClassLevelElements(Class<?> testCase) {
		return doGetEntitiesForAnnotation(testCase);
	}

	/**
	 * {@inheritDoc}
	 */
	public Class<?>[] getMethodLevelElements(Method testMethod) {
		return doGetEntitiesForAnnotation(testMethod);
	}

	/**
	 * {@inheritDoc}
	 */
	private Class<?>[] doGetEntitiesForAnnotation(AnnotatedElement element) {
		PrepareForTest annotation = element.getAnnotation(PrepareForTest.class);
		Class<?>[] classesToMock = new Class<?>[0];
		if (annotation != null) {
			classesToMock = annotation.value();
		}
		return classesToMock;
	}
}
