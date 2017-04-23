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

package org.powermock.core.transformers.support;


import org.powermock.core.transformers.ClassWrapper;
import org.powermock.core.transformers.MockTransformer;
import org.powermock.core.transformers.MockTransformerChain;

import java.util.ArrayList;
import java.util.List;

public class DefaultMockTransformerChain implements MockTransformerChain {
    
    private final List<MockTransformer> transformers;
    
    private DefaultMockTransformerChain(final List<MockTransformer> transformers) {
        this.transformers = transformers;
    }
    
    @Override
    public <T> ClassWrapper<T> transform(final ClassWrapper<T> clazz) throws Exception {
        ClassWrapper<T> classWrapper = clazz;
        for (MockTransformer transformer : transformers) {
            classWrapper = transformer.transform(classWrapper);
        }
        return classWrapper;
    }
    
    public static MockTransformerChainBuilder newBuilder() {
        return new MockTransformerChainBuilder();
    }
    
    public static class MockTransformerChainBuilder {
        
        private final List<MockTransformer> transformers;
        
        private MockTransformerChainBuilder() {
            transformers = new ArrayList<MockTransformer>();
        }
        
        public MockTransformerChainBuilder append(MockTransformer transformer) {
            transformers.add(transformer);
            return this;
        }
        
        public MockTransformerChainBuilder append(final List<MockTransformer> mockTransformerChain) {
            transformers.addAll(mockTransformerChain);
            return this;
        }
        
        public MockTransformerChain build() {
            return new DefaultMockTransformerChain(transformers);
        }
    }
}
