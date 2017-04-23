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

import org.powermock.core.transformers.support.DefaultMockTransformerChain;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

class MockTransformerTestHelper {
    
    static <T extends MockTransformer> Iterable<Object[]> createTransformerTestData(final Class<T>... transformerClass) {
        List<Object[]> data = new ArrayList<Object[]>();
        
        for (TransformStrategy strategy : TransformStrategy.values()) {
            List<MockTransformerChain> transformer = createTransformers(strategy, transformerClass);
            for (MockTransformerChain mockTransformer : transformer) {
                data.add(new Object[]{
                    strategy,
                    mockTransformer
                });
            }
        }
        
        return data;
    }
    
    static Iterable<Object[]> createTransformerTestData(final MockTransformerChain mockTransformerChain) {
        List<Object[]> data = new ArrayList<Object[]>();
        
        for (TransformStrategy strategy : TransformStrategy.values()) {
            data.add(new Object[]{
                strategy,
                mockTransformerChain
            });
        }
        
        return data;
    }
    
    private static <T extends MockTransformer> List<MockTransformerChain> createTransformers(final TransformStrategy strategy,
                                                                                             final Class<T>... classes) {
        List<MockTransformerChain> transformers = new ArrayList<MockTransformerChain>();
        
        for (Class<T> transformerClass : classes) {
            MockTransformer transformer = getInstance(strategy, transformerClass);
            
            transformers.add(createChainFrom(transformer));
        }
        
        return transformers;
    }
    
    private static <T extends MockTransformer> MockTransformer getInstance(final TransformStrategy strategy,
                                                                           final Class<T> transformerClass) {
        try {
            Constructor<T> constructor = transformerClass.getConstructor(TransformStrategy.class);
            return constructor.newInstance(strategy);
        } catch (Exception e) {
            throw new RuntimeException("Cannot create an instance of transformer.", e);
        }
    }
    
    private static MockTransformerChain createChainFrom(final MockTransformer transformer) {
        return DefaultMockTransformerChain.newBuilder().append(transformer).build();
    }
}
