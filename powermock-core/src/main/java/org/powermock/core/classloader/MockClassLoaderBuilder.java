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

package org.powermock.core.classloader;

import org.powermock.core.classloader.annotations.UseClassPathAdjuster;
import org.powermock.core.transformers.MockTransformer;
import org.powermock.core.transformers.MockTransformerChain;
import org.powermock.core.transformers.MockTransformerChainFactory;
import org.powermock.core.transformers.TestClassAwareTransformer;
import org.powermock.utils.ArrayUtil;

import java.util.ArrayList;
import java.util.List;

import static org.powermock.core.transformers.support.FilterPredicates.isInstanceOf;
import static org.powermock.utils.Asserts.internalAssertNotNull;

public class MockClassLoaderBuilder {

    private final MockTransformerChainFactory transformerChainFactory;
    private final List<MockTransformer> extraMockTransformers;
    private final ByteCodeFramework byteCodeFramework;
    private String[] packagesToIgnore;
    private String[] classesToModify;
    
    public static MockClassLoaderBuilder create(ByteCodeFramework byteCodeFramework) {
        return new MockClassLoaderBuilder(byteCodeFramework);
    }
    
    private UseClassPathAdjuster useClassPathAdjuster;
    private Class<?> testClass;
    
    private MockClassLoaderBuilder(final ByteCodeFramework byteCodeFramework) {
        this.byteCodeFramework = byteCodeFramework;
        transformerChainFactory = byteCodeFramework.createTransformerChainFactory();
        extraMockTransformers = new ArrayList<MockTransformer>();
    }

    public MockClassLoader build() {
        internalAssertNotNull(testClass, "Test class is null during building classloader. ");
    
        final MockClassLoaderConfiguration configuration = new MockClassLoaderConfiguration(classesToModify, packagesToIgnore);
        final MockClassLoader classLoader = byteCodeFramework.createClassloader(configuration, useClassPathAdjuster);
    
        classLoader.setMockTransformerChain(createTransformerChain());

        return classLoader;
    }
    
    private MockTransformerChain createTransformerChain() {
        final MockTransformerChain mockTransformerChain = transformerChainFactory.createDefaultChain(extraMockTransformers);
        
        final Iterable<MockTransformer> testAwareTransformer = mockTransformerChain.filter(isInstanceOf(TestClassAwareTransformer.class));
        for (MockTransformer transformer : testAwareTransformer) {
            ((TestClassAwareTransformer) transformer).setTestClass(testClass);
        }
        
        return mockTransformerChain;
    }
    
    public MockClassLoaderBuilder addIgnorePackage(String[] packagesToIgnore) {
        this.packagesToIgnore = ArrayUtil.addAll(this.packagesToIgnore, packagesToIgnore);
        return this;
    }

    public MockClassLoaderBuilder addClassesToModify(String[] classesToModify) {
        this.classesToModify = ArrayUtil.addAll(this.classesToModify, classesToModify);
        return this;
    }
    
    public MockClassLoaderBuilder addExtraMockTransformers(MockTransformer... mockTransformers) {
        if (mockTransformers != null) {
            for (MockTransformer mockTransformer : mockTransformers) {
                if (mockTransformer != null) {
                    extraMockTransformers.add(mockTransformer);
                }
            }
        }
        return this;
    }
    
    public MockClassLoaderBuilder addClassPathAdjuster(final UseClassPathAdjuster useClassPathAdjuster) {
        this.useClassPathAdjuster = useClassPathAdjuster;
        return this;
    }
    
    public MockClassLoaderBuilder forTestClass(final Class<?> testClass) {
        this.testClass = testClass;
        return this;
    }
}
