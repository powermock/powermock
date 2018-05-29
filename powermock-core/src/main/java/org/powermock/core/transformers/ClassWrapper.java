/*
 *
 *   Copyright 2017 the original author or authors.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package org.powermock.core.transformers;

/**
 * An interface represents an abstraction of the class to be able to pass class to different byte-code instrumentation frameworks.
 *
 * @param <T> - original class specific for byte-code modification framework.
 * @author Arthur Zagretdinov
 */
public interface ClassWrapper<T> {
    
    /**
     * Check if class is interface
     *
     * @return <code>true</code> if class is an interface.
     */
    boolean isInterface();
    
    /**
     * Get original object which represent class
     *
     * @return instance of original object.
     */
    T unwrap();
    
    /**
     * Wrap changed implementation to get a new instance of ClassWrapper
     * @param original -  original class specific for byte-code modification framework.
     * @return a new instance of ClassWrapper
     */
    ClassWrapper<T> wrap(T original);
}
