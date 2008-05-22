package org.powermock.tests.utils.impl;

import java.lang.reflect.AnnotatedElement;
import java.util.LinkedList;
import java.util.List;

import org.powermock.core.MockRepository;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.tests.utils.TestClassesExtractor;

/**
 * Implementation of the {@link TestClassesExtractor} interface for classes that
 * should have their static initializers suppressed.
 * 
 */
public class StaticConstructorSuppressImpl implements TestClassesExtractor {

	/**
	 * {@inheritDoc}
	 */
	public String[] getTestClasses(AnnotatedElement element) {
		List<String> all = new LinkedList<String>();

		SuppressStaticInitializationFor annotation = element.getAnnotation(SuppressStaticInitializationFor.class);
		if (annotation != null) {
			final String[] value = annotation.value();
			for (String classToSuppress : value) {
				if (!"".equals(classToSuppress)) {
					all.add(classToSuppress);
				}
				MockRepository.addSuppressStaticInitializer(classToSuppress);
			}
		}

		return all.toArray(new String[0]);
	}
}
