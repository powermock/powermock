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
package org.powermock.modules.junit4.rule;

import org.powermock.classloading.ClassloaderExecutor;
import org.powermock.core.classloader.MockClassLoader;
import org.powermock.core.transformers.MockTransformer;
import org.powermock.core.transformers.impl.ClassMockTransformer;
import org.powermock.core.transformers.impl.InterfaceMockTransformer;
import org.powermock.reflect.proxyframework.ClassLoaderRegisterProxyFramework;
import org.powermock.tests.utils.MockPolicyInitializer;
import org.powermock.tests.utils.impl.PowerMockIgnorePackagesExtractorImpl;
import org.powermock.tests.utils.impl.PrepareForTestExtractorImpl;
import org.powermock.tests.utils.impl.StaticConstructorSuppressExtractorImpl;

import java.util.ArrayList;
import java.util.List;

public class PowerMockClassloaderExecutor {

    public static ClassloaderExecutor forClass(Class<?> testClass, MockPolicyInitializer mockPolicyInitializer) {
        List<MockTransformer> mockTransformerChain = new ArrayList<MockTransformer>();
        mockTransformerChain.add(new ClassMockTransformer());
        mockTransformerChain.add(new InterfaceMockTransformer());
    
        String[] classesToLoadByMockClassloader = new String[0];
        String[] packagesToIgnore = new String[0];
        MockClassLoader mockLoader = new MockClassLoader(classesToLoadByMockClassloader, packagesToIgnore);
        mockLoader.setMockTransformerChain(mockTransformerChain);
        PrepareForTestExtractorImpl testClassesExtractor = new PrepareForTestExtractorImpl();
        StaticConstructorSuppressExtractorImpl staticInitializationExtractor = new StaticConstructorSuppressExtractorImpl();
        PowerMockIgnorePackagesExtractorImpl ignorePackagesExtractor = new PowerMockIgnorePackagesExtractorImpl();
    
        mockLoader.addIgnorePackage(ignorePackagesExtractor.getPackagesToIgnore(testClass));
        mockLoader.addClassesToModify(testClassesExtractor.getTestClasses(testClass));
        mockLoader.addClassesToModify(staticInitializationExtractor.getTestClasses(testClass));
        ClassLoaderRegisterProxyFramework.registerProxyframework(mockLoader);
        mockPolicyInitializer.initialize(mockLoader);
        return new ClassloaderExecutor(mockLoader);
    }


}
