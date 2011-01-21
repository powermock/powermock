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
