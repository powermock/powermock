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

package org.powermock.modules.testng.internal;

import org.powermock.core.classloader.MockClassLoaderBuilder;
import org.powermock.core.transformers.MockTransformer;
import org.powermock.core.transformers.impl.ClassMockTransformer;
import org.powermock.core.transformers.impl.InterfaceMockTransformer;
import org.powermock.tests.utils.IgnorePackagesExtractor;
import org.powermock.tests.utils.TestClassesExtractor;
import org.powermock.tests.utils.impl.PowerMockIgnorePackagesExtractorImpl;
import org.powermock.tests.utils.impl.PrepareForTestExtractorImpl;
import org.powermock.tests.utils.impl.StaticConstructorSuppressExtractorImpl;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
class ClassLoaderFactory {
    
    private final TestClassesExtractor testClassesExtractor;
    private final IgnorePackagesExtractor ignorePackagesExtractor;
    private final StaticConstructorSuppressExtractorImpl staticConstructorSuppressExtractor;
    private final ExpectedExceptionsExtractor expectedExceptionsExtractor;
    private final List<MockTransformer> mockTransformerChain;
    
    ClassLoaderFactory() {
        
        testClassesExtractor = new PrepareForTestExtractorImpl();
        ignorePackagesExtractor = new PowerMockIgnorePackagesExtractorImpl();
        expectedExceptionsExtractor = new PowerMockExpectedExceptionsExtractorImpl();
        staticConstructorSuppressExtractor = new StaticConstructorSuppressExtractorImpl();
        
        mockTransformerChain = getMockTransformers();
        
    }
    
    private List<MockTransformer> getMockTransformers() {
        List<MockTransformer> mockTransformerChain = new ArrayList<MockTransformer>();
        
        mockTransformerChain.add(new ClassMockTransformer());
        mockTransformerChain.add(new InterfaceMockTransformer());
        
        return mockTransformerChain;
    }
    
    ClassLoader createClassLoader(Class<?> testClass) {
        return MockClassLoaderBuilder.create()
                                     .addMockTransformerChain(mockTransformerChain)
                                     .addIgnorePackage(ignorePackagesExtractor.getPackagesToIgnore(testClass))
                                     .addIgnorePackage(expectedExceptionsExtractor.getPackagesToIgnore(testClass))
                                     .addClassesToModify(testClassesExtractor.getTestClasses(testClass))
                                     .addClassesToModify(staticConstructorSuppressExtractor.getClassesToModify(testClass))
                                     .build();
    }
    
}
