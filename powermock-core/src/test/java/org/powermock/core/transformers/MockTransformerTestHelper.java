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

import org.powermock.core.classloader.bytebuddy.ByteBuddyMockClassLoader;
import org.powermock.core.classloader.javassist.JavassistMockClassLoader;
import org.powermock.core.test.MockClassLoaderFactory;
import org.powermock.core.transformers.javassist.AbstractJavaAssistMockTransformer;
import org.powermock.core.transformers.support.DefaultMockTransformerChain;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class MockTransformerTestHelper {
    
    static Collection<Object[]> createTransformerTestData(final Class<?>... transformerClass) {
        List<Object[]> data = new ArrayList<Object[]>();
        
        for (TransformStrategy strategy : TransformStrategy.values()) {
            List<MockTransformerChain> transformer = createTransformers(strategy, transformerClass);
            
            for (MockTransformerChain mockTransformer : transformer) {
                data.add(new Object[]{
                    strategy,
                    mockTransformer,
                    createClassLoaderFactory(transformerClass[0])
                });
            }
        }
        
        return data;
    }
    
    private static MockClassLoaderFactory createClassLoaderFactory(final Class<?> transformerClass) {
        if (AbstractJavaAssistMockTransformer.class.isAssignableFrom(transformerClass)){
            return new MockClassLoaderFactory(JavassistMockClassLoader.class);
        }else {
            return new MockClassLoaderFactory(ByteBuddyMockClassLoader.class);
        }
    
    }
    
    private static List<MockTransformerChain> createTransformers(final TransformStrategy strategy, final Class<?>... classes) {
        List<MockTransformerChain> transformers = new ArrayList<MockTransformerChain>();
        
        for (Class<?> transformerClass : classes) {
            MockTransformer transformer = getInstance(strategy, transformerClass);
            
            transformers.add(createChainFrom(transformer));
        }
        
        return transformers;
    }
    
    private static MockTransformer getInstance(final TransformStrategy strategy, final Class<?> transformerClass) {
        try {
            Constructor<?> constructor = transformerClass.getConstructor(TransformStrategy.class);
            return (MockTransformer) constructor.newInstance(strategy);
        } catch (Exception e) {
            throw new RuntimeException("Cannot create an instance of transformer.", e);
        }
    }
    
    private static MockTransformerChain createChainFrom(final MockTransformer transformer) {
        return DefaultMockTransformerChain.newBuilder().append(transformer).build();
    }
}
