/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.powermock.tests.utils.impl;

import org.powermock.tests.utils.TestClassesExtractor;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Base class for all test class extractors.
 */
public abstract class AbstractTestClassExtractor implements TestClassesExtractor {

    protected final boolean includeMethods;

    protected AbstractTestClassExtractor(){
        this(false);
    }

    protected AbstractTestClassExtractor(boolean includeMethods) {this.includeMethods = includeMethods;}

    /**
     * If <code>element</code> is a class this method traverses the hierarchy
     * and extracts classes that should be prepared for test in all super
     * classes.
     */
    @Override
    public final String[] getTestClasses(AnnotatedElement element) {
        final Set<String> classesToPrepareForTest = new HashSet<String>();
        if (element instanceof Class<?>) {
            extractClassesFromTestClass((Class<?>) element, classesToPrepareForTest);
        } else {
            extractClassesAndAddThemToList(element, classesToPrepareForTest);
        }
        return classesToPrepareForTest.toArray(new String[classesToPrepareForTest.size()]);
    }

    private void extractClassesFromTestClass(final Class<?> element, Set<String> classesToPrepareForTest) {
        Class<?> classToInvestigate = element;
        while (classToInvestigate != null && !classToInvestigate.equals(Object.class)) {
            extractClassesAndAddThemToList(classToInvestigate, classesToPrepareForTest);
            if (includeMethods) {
                classesToPrepareForTest.addAll(lookOverMethods(classToInvestigate));
            }
            classToInvestigate = classToInvestigate.getSuperclass();
        }
    }

    private Collection<String> lookOverMethods(Class<?> classToInvestigate) {
        Set<String> classesToPrepareForTest = new HashSet<String>();
        for (Method method : classToInvestigate.getMethods()) {
            extractClassesAndAddThemToList(method, classesToPrepareForTest);
        }
        return classesToPrepareForTest;
    }

    private void extractClassesAndAddThemToList(AnnotatedElement elementToExtractClassFrom, final Set<String> classesToPrepareForTest) {
        final String[] classesToModify = getClassesToModify(elementToExtractClassFrom);
        if (classesToModify != null) {
            Collections.addAll(classesToPrepareForTest, classesToModify);
        }
    }

    /**
     * Get the fully qualified names for classes that must should be modified
     * for this <code>element</code>.
     *
     * @param element The element that may contain info regarding which classes that
     *                must be modified by PowerMock.
     * @return An array of fully-qualified names to classes that must be
     * modified by PowerMock for the specific <code>element</code>.
     */
    protected abstract String[] getClassesToModify(AnnotatedElement element);

    @Override
    public boolean isPrepared(AnnotatedElement element, String fullyQualifiedClassName) {
        if (fullyQualifiedClassName == null) {
            throw new IllegalArgumentException("fullyQualifiedClassName cannot be null.");
        }
        final String[] testClasses = getTestClasses(element);
        for (String className : testClasses) {
            if (className.equals(fullyQualifiedClassName)) {
                return true;
            }
        }
        return false;
    }
}
