package org.powermock.tests.utils.impl;

import java.lang.reflect.AnnotatedElement;
import java.util.LinkedList;
import java.util.List;

import org.powermock.core.MockRepository;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.tests.utils.StaticConstructorSuppressionExtractor;


/**
 * Default implementation of the {@link StaticConstructorSuppressionExtractor}
 * interface.
 * 
 * @author Johan Haleby
 */
public class StaticConstructorSuppressImpl implements StaticConstructorSuppressionExtractor {

	/**
	 * {@inheritDoc}
	 */
	public String[] getClassLevelElements(Class<?> testCase) {
		return doGetEntitiesForAnnotation(testCase);
	}

	/**
	 * {@inheritDoc}
	 */
	private String[] doGetEntitiesForAnnotation(AnnotatedElement element) {
		List<String> all = new LinkedList<String>();

		SuppressStaticInitializationFor annotation = element.getAnnotation(SuppressStaticInitializationFor.class);
		if (annotation != null) {
			final String[] value = annotation.value();
			for (String classToSuppress : value) {
				all.add(classToSuppress);
				MockRepository.addSuppressStaticInitializer(classToSuppress);
			}
		}

		return all.toArray(new String[0]);
	}
}
