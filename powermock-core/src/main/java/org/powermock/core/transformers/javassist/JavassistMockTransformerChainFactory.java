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

package org.powermock.core.transformers.javassist;

import org.powermock.core.transformers.MockTransformer;
import org.powermock.core.transformers.MockTransformerChain;
import org.powermock.core.transformers.MockTransformerChainFactory;
import org.powermock.core.transformers.TransformStrategy;
import org.powermock.core.transformers.support.DefaultMockTransformerChain;
import org.powermock.core.transformers.support.DefaultMockTransformerChain.MockTransformerChainBuilder;

import java.util.List;

public class JavassistMockTransformerChainFactory implements MockTransformerChainFactory {
    
    private static final TransformStrategy DEFAULT = TransformStrategy.CLASSLOADER;
    
    @Override
    public MockTransformerChain createDefaultChain() {
        return createDefaultChain(DEFAULT);
    }
    
    @Override
    public MockTransformerChain createDefaultChain(final TransformStrategy transformStrategy) {
        return createDefaultChainBuilder(transformStrategy).build();
    }
    
    @Override
    public MockTransformerChain createTestClassChain(final MockTransformer testClassTransformer) {
        return createDefaultChainBuilder(DEFAULT)
                   .append(testClassTransformer)
                   .build();
    }
    
    @Override
    public MockTransformerChain createDefaultChain(final List<MockTransformer> extraMockTransformers) {
        return createDefaultChainBuilder(DEFAULT)
                   .append(extraMockTransformers)
                   .build();
    }
    
    private MockTransformerChainBuilder createDefaultChainBuilder(final TransformStrategy transformStrategy) {
        return DefaultMockTransformerChain.newBuilder()
                                          .append(new ClassFinalModifierMockTransformer(transformStrategy))
                                          .append(new ConstructorsMockTransformer(transformStrategy))
                                          .append(new InstrumentMockTransformer(transformStrategy))
                                          .append(new PackagePrivateClassesMockTransformer(transformStrategy))
                                          .append(new StaticFinalNativeMethodMockTransformer(transformStrategy))
                                          .append(new SuppressStaticInitializerMockTransformer(transformStrategy))
                                          .append(new MethodSizeMockTransformer(transformStrategy));
    }
}
