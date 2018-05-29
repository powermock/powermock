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

import java.util.List;

/**
 * An implementation of interface should create a {@link MockTransformerChain} with full set of required transformers to enable all mocking features.
 *
 * @author Arthur Zagretdinov
 */
public interface MockTransformerChainFactory {
    
    /**
     * Create an {@link MockTransformerChain} with using default strategy {@link TransformStrategy#CLASSLOADER}
     *
     * @return an instance of MockTransformerChain
     */
    MockTransformerChain createDefaultChain();
    
    /**
     * Create an {@link MockTransformerChain} with using the given <code>transformStrategy</code>
     *
     * @return an instance of MockTransformerChain
     */
    MockTransformerChain createDefaultChain(TransformStrategy transformStrategy);
    
    /**
     * Create an {@link MockTransformerChain} with using default strategy {@link TransformStrategy#CLASSLOADER} and with the given <code>extraMockTransformers</code>
     *
     * @return an instance of MockTransformerChain
     */
    MockTransformerChain createDefaultChain(List<MockTransformer> extraMockTransformers);
    
    /**
     * Create an {@link MockTransformerChain} with using the given <code>testClassTransformer</code> as transformer for test class.
     *
     * @return an instance of MockTransformerChain
     */
    MockTransformerChain createTestClassChain(MockTransformer testClassTransformer);
}
