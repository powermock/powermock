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
 * Interface represent chain of {@link MockTransformer}.
 * Each transformer in chain instruments a class to enable one of mocking feature.
 *
 * @author Arthur Zagretdinov
 */
public interface MockTransformerChain {
    
    /**
     * Go thought all transformers in chain and instrument the {@code clazz}.
     *
     * @param clazz The class to be instrument to enabled class mocking.
     * @return A {@code ClassWrapper} representation of the instrumented class.
     */
    <T> ClassWrapper<T> transform(ClassWrapper<T> clazz) throws Exception;
}
