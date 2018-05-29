/*
 * Copyright 2013 the original author or authors.
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
package org.powermock.modules.testng.internal;

import org.powermock.tests.utils.IgnorePackagesExtractor;
import org.testng.annotations.Test;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class PowerMockExpectedExceptionsExtractorImpl implements IgnorePackagesExtractor {

    public String[] getPackagesToIgnore(AnnotatedElement element) {
        List<String> ignoredPackages = new LinkedList<String>();
        if (element instanceof Class<?>) {
            Class<?> klazz = (Class<?>) element;
            for (Method method : klazz.getMethods()) {
                Test annotation = method.getAnnotation(Test.class);
                if (annotation != null) {
                    Class<?>[] ignores = annotation.expectedExceptions();
                    if (ignores != null) {
                        for (Class<?> ignorePackage : ignores) {
                            ignoredPackages.add(ignorePackage.getName());
                        }
                    }
                }
            }
            Class<?> superclass = klazz.getSuperclass();
            if (superclass != null && !superclass.equals(Object.class)) {
                String[] packagesToIgnore = getPackagesToIgnore(superclass);
                ignoredPackages.addAll(Arrays.asList(packagesToIgnore));
            }
        }

        return ignoredPackages.toArray(new String[ignoredPackages.size()]);
    }
}

