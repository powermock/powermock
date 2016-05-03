/*
 *   Copyright 2016 the original author or authors.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package org.powermock.core.agent;

/**
 * This register contains information about which class has been modified by PowerMock Java Agent.
 */
public interface JavaAgentClassRegister {

    /**
     * Check if class with {@code className} has been modified for the given class loader
     * @param classLoader - {@link ClassLoader} for that class should be checked
     * @param className - name of class
     * @return {@code true} if the given class has been modified, otherwise {@code false}
     */
    boolean isModifiedByAgent(ClassLoader classLoader, String className);

    /**
     * Register that the class with name {@code className} has been  modified for the given class loader.
     * @param loader - {@link ClassLoader} for that class has been modified.
     * @param className - name of the class which has been modified.
     */
    void registerClass(ClassLoader loader, String className);

    /**
     * Remove all registered classes for all class loaders.
     */
    void clear();
}
