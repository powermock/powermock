package org.powermock.tests.utils.impl;

import java.lang.reflect.AnnotatedElement;

import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.tests.utils.IgnorePackagesExtractor;

public class PowerMockIgnorePackagesExtractorImpl implements IgnorePackagesExtractor {

	public String[] getPackagesToIgnore(AnnotatedElement element) {
		PowerMockIgnore annotation = element.getAnnotation(PowerMockIgnore.class);
		if (annotation != null) {
			return annotation.value();
		}
		return new String[0];
	}

}
