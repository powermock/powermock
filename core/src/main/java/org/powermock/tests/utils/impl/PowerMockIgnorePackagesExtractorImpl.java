package org.powermock.tests.utils.impl;

import java.lang.reflect.AnnotatedElement;
import java.util.LinkedList;
import java.util.List;

import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.tests.utils.IgnorePackagesExtractor;

public class PowerMockIgnorePackagesExtractorImpl implements IgnorePackagesExtractor {

    public String[] getPackagesToIgnore(AnnotatedElement element) {
        List<String> ignoredPackages = new LinkedList<String>();
        PowerMockIgnore annotation = element.getAnnotation(PowerMockIgnore.class);
        if (annotation != null) {
            String[] ignores = annotation.value();
            for (String ignorePackage : ignores) {
                ignoredPackages.add(ignorePackage);
            }
        }
        if (element instanceof Class<?>) {
            Class<?> klazz = (Class<?>) element;
            Class<?> superclass = klazz.getSuperclass();
            if (superclass != null && !superclass.equals(Object.class)) {
                String[] packagesToIgnore = getPackagesToIgnore(superclass);
                for (String packageToIgnore : packagesToIgnore) {
                    ignoredPackages.add(packageToIgnore);
                }
            }
        }
        return ignoredPackages.toArray(new String[ignoredPackages.size()]);
    }

}
