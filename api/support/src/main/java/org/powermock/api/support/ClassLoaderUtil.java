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
    public static <T> Class<T> loadClassWithClassloader(ClassLoader classloader, Class<T> type) {
        try {
            return (Class<T>) Class.forName(type.getName(), false, classloader);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
