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
