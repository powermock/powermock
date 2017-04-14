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

import org.powermock.core.classloader.javassist.JavassistMockClassLoader;
import org.powermock.core.transformers.MockTransformer;
import org.powermock.core.transformers.MockTransformerChainFactory;
import org.powermock.core.transformers.javassist.JavassistMockTransformerChainFactory;
import org.powermock.core.transformers.support.DefaultMockTransformerChain;
import org.powermock.utils.ArrayUtil;

import java.util.ArrayList;
import java.util.List;

/**
 *  MockClassLoader builder.
 */
public class MockClassLoaderBuilder {

    private final MockTransformerChainFactory transformerChainFactory;
    private String[] packagesToIgnore;
    private String[] classesToModify;
    
    public static MockClassLoaderBuilder create() {
        return new MockClassLoaderBuilder();
    }
    
    private MockClassLoaderBuilder() {
        transformerChainFactory = new JavassistMockTransformerChainFactory();
    }

    public MockClassLoader build() {
    
        MockClassLoader classLoader = new JavassistMockClassLoader(
            new MockClassLoaderConfiguration(classesToModify, packagesToIgnore)
        );

        classLoader.setMockTransformerChain(transformerChainFactory.createDefaultChain());

        return classLoader;
    }
    
    public MockClassLoaderBuilder addIgnorePackage(String[] packagesToIgnore) {
        this.packagesToIgnore = ArrayUtil.addAll(this.packagesToIgnore, packagesToIgnore);
        return this;
    }

    public MockClassLoaderBuilder addClassesToModify(String[] classesToModify) {
        this.classesToModify = ArrayUtil.addAll(this.classesToModify, classesToModify);
        return this;
    }
}
