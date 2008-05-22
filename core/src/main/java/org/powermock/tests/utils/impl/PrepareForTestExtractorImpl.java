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
import java.util.LinkedList;
import java.util.List;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.internal.IndicateReloadClass;
import org.powermock.tests.utils.TestClassesExtractor;

/**
 * Default implementation of the {@link TestClassesExtractor} interface.
 * 
 * @author Johan Haleby
 */
public class PrepareForTestExtractorImpl implements TestClassesExtractor {

	/**
	 * {@inheritDoc}
	 */
	public String[] getTestClasses(AnnotatedElement element) {
		List<String> all = new LinkedList<String>();

		PrepareForTest prepareAnnotation = element.getAnnotation(PrepareForTest.class);
		if (prepareAnnotation != null) {
			final Class<?>[] classesToMock = prepareAnnotation.value();
			for (Class<?> classToMock : classesToMock) {
				if (!classToMock.equals(IndicateReloadClass.class)) {
					all.add(classToMock.getName());
				}
			}
			String[] fullyQualifiedNames = prepareAnnotation.fullyQualifiedNames();
			for (String string : fullyQualifiedNames) {
				if (!"".equals(string)) {
					all.add(string);
				}
			}
		}

		return all.toArray(new String[0]);

	}
}
