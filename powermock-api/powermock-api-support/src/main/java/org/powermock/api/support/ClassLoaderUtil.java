/*
 * Copyright 2009 the original author or authors.
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
package org.powermock.api.support;

public class ClassLoaderUtil {

    /**
     * Loads a class with a specific classloader, wraps the
     * {@link ClassNotFoundException} in a runtime exeception.
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> loadClass(Class<T> type, ClassLoader classloader) {
        return loadClass(type.getName(), classloader);
    }

    /**
     * Loads a class from the current classloader
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> loadClass(String className) {
        return loadClass(className, ClassLoaderUtil.class.getClassLoader());
    }

    /**
     * Check whether a classloader can load the given class.
     */
    @SuppressWarnings("unchecked")
    public static <T> boolean hasClass(Class<T> type, ClassLoader classloader) {
        try {
            loadClass(type.getName(), classloader);
            return true;
        } catch (RuntimeException e) {
            if(e.getCause() instanceof ClassNotFoundException) {
                return false;
            }
            throw e;
        }
    }

    /**
     * Load a class from a specific classloader
     */
    public static <T> Class<T> loadClass(String className, ClassLoader classloader) {
        if(className == null) {
            throw new IllegalArgumentException("className cannot be null");
        }

        if(classloader == null) {
            throw new IllegalArgumentException("classloader cannot be null");
        }

        try {
            return (Class<T>) Class.forName(className, false, classloader);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
