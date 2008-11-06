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
public class StaticConstructorSuppressExtractorImpl extends AbstractTestClassExtractor {

	/**
	 * {@inheritDoc}
	 */
	public String[] getTestClasses(AnnotatedElement element) {
		List<String> all = new LinkedList<String>();

		final SuppressStaticInitializationFor suppressAnnotation = element.getAnnotation(SuppressStaticInitializationFor.class);

		if (suppressAnnotation == null) {
			return null;
		} else {
			final String[] value = suppressAnnotation.value();
			for (String classToSuppress : value) {
				if (!"".equals(classToSuppress)) {
					all.add(classToSuppress);
					MockRepository.addSuppressStaticInitializer(classToSuppress);
				}
			}
		}

		return all.toArray(new String[0]);
	}
}
