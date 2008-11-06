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
import java.util.LinkedHashSet;
import java.util.Set;

import org.powermock.core.IndicateReloadClass;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest;
import org.powermock.tests.utils.TestClassesExtractor;

/**
 * Implementation of the {@link TestClassesExtractor} interface that extract
 * classes from the {@link PrepareForTest} or {@link PrepareOnlyThisForTest}
 * annotations.
 * 
 */
public class PrepareForTestExtractorImpl extends AbstractTestClassExtractor {

	/**
	 * {@inheritDoc}
	 */
	public String[] getTestClasses(AnnotatedElement element) {
		Set<String> all = new LinkedHashSet<String>();

		PrepareForTest prepareForTestAnnotation = element.getAnnotation(PrepareForTest.class);
		PrepareOnlyThisForTest prepareOnlyThisForTestAnnotation = element.getAnnotation(PrepareOnlyThisForTest.class);
		final boolean prepareForTestAnnotationPresent = prepareForTestAnnotation != null;
		final boolean prepareOnlyThisForTestAnnotationPresent = prepareOnlyThisForTestAnnotation != null;

		if (!prepareForTestAnnotationPresent && !prepareOnlyThisForTestAnnotationPresent) {
			return null;
		}

		if (prepareForTestAnnotationPresent) {
			final Class<?>[] classesToMock = prepareForTestAnnotation.value();
			for (Class<?> classToMock : classesToMock) {
				if (!classToMock.equals(IndicateReloadClass.class)) {
					addClassHierarchy(all, classToMock);
				}
			}

			addFullyQualifiedNames(all, prepareForTestAnnotation);
		}

		if (prepareOnlyThisForTestAnnotationPresent) {
			final Class<?>[] classesToMock = prepareOnlyThisForTestAnnotation.value();
			for (Class<?> classToMock : classesToMock) {
				if (!classToMock.equals(IndicateReloadClass.class)) {
					all.add(classToMock.getName());
				}
			}

			addFullyQualifiedNames(all, prepareOnlyThisForTestAnnotation);
		}

		return all.toArray(new String[0]);

	}

	private void addFullyQualifiedNames(Set<String> all, PrepareForTest annotation) {
		String[] fullyQualifiedNames = annotation.fullyQualifiedNames();
		addFullyQualifiedNames(all, fullyQualifiedNames);
	}

	private void addFullyQualifiedNames(Set<String> all, PrepareOnlyThisForTest annotation) {
		String[] fullyQualifiedNames = annotation.fullyQualifiedNames();
		addFullyQualifiedNames(all, fullyQualifiedNames);
	}

	private void addFullyQualifiedNames(Set<String> all, String[] fullyQualifiedNames) {
		for (String string : fullyQualifiedNames) {
			if (!"".equals(string)) {
				all.add(string);
			}
		}
	}

	private void addClassHierarchy(Set<String> all, Class<?> classToMock) {
		while (!classToMock.equals(Object.class)) {
			all.add(classToMock.getName());
			classToMock = classToMock.getSuperclass();
		}
	}
}
