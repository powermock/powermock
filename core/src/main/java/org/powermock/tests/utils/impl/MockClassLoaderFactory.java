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

package org.powermock.tests.utils.impl;

import org.powermock.core.classloader.MockClassLoader;
import org.powermock.core.classloader.annotations.MockPolicy;
import org.powermock.core.classloader.annotations.UseClassPathAdjuster;
import org.powermock.core.spi.PowerMockPolicy;
import org.powermock.core.transformers.MockTransformer;
import org.powermock.core.transformers.impl.ClassMockTransformer;
import org.powermock.core.transformers.impl.InterfaceMockTransformer;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 */
class MockClassLoaderFactory {
    private final String[] packagesToIgnore;
    private final Class<?> testClass;
    private final String[] classesToLoadByMockClassloader;
    private final MockTransformer[] extraMockTransformers;

    public MockClassLoaderFactory(Class<?> testClass, String[] classesToLoadByMockClassloader, String[] packagesToIgnore, MockTransformer... extraMockTransformers) {
        this.testClass = testClass;
        this.classesToLoadByMockClassloader = classesToLoadByMockClassloader;
        this.packagesToIgnore = packagesToIgnore;
        this.extraMockTransformers = extraMockTransformers;
    }

    public ClassLoader create() {
        final String[] classesToLoadByMockClassloader = makeSureArrayContainsTestClassName(this.classesToLoadByMockClassloader, testClass.getName());

        final ClassLoader mockLoader;
        if (isContextClassLoaderShouldBeUsed(classesToLoadByMockClassloader)) {
            mockLoader = Thread.currentThread().getContextClassLoader();
        } else {
            mockLoader = createMockClassLoader(classesToLoadByMockClassloader);
        }
        return mockLoader;
    }

    protected ClassLoader createMockClassLoader(final String[] classesToLoadByMockClassloader) {

        List<MockTransformer> mockTransformerChain = getMockTransformers(extraMockTransformers);
        final UseClassPathAdjuster useClassPathAdjuster = testClass.getAnnotation(UseClassPathAdjuster.class);

        ClassLoader mockLoader = AccessController.doPrivileged(new PrivilegedAction<MockClassLoader>() {
            @Override
            public MockClassLoader run() {
                return new MockClassLoader(classesToLoadByMockClassloader, packagesToIgnore, useClassPathAdjuster);
            }
        });

        MockClassLoader mockClassLoader = (MockClassLoader) mockLoader;
        mockClassLoader.setMockTransformerChain(mockTransformerChain);
        new MockPolicyInitializerImpl(testClass).initialize(mockLoader);
        return mockLoader;
    }

    protected boolean isContextClassLoaderShouldBeUsed(String[] classesToLoadByMockClassloader) {
        return (classesToLoadByMockClassloader == null || classesToLoadByMockClassloader.length == 0) && !hasMockPolicyProvidedClasses(testClass);
    }

    protected List<MockTransformer> getMockTransformers(MockTransformer[] extraMockTransformers) {
        List<MockTransformer> mockTransformerChain = new ArrayList<MockTransformer>();

        mockTransformerChain.add(new ClassMockTransformer());
        mockTransformerChain.add(new InterfaceMockTransformer());

        Collections.addAll(mockTransformerChain, extraMockTransformers);
        return mockTransformerChain;
    }

    private String[] makeSureArrayContainsTestClassName(String[] arrayOfClassNames, String testClassName) {
        if (null == arrayOfClassNames || 0 == arrayOfClassNames.length) {
            return new String[]{testClassName};

        } else {
            List<String> modifiedArrayOfClassNames = new ArrayList<String>(arrayOfClassNames.length + 1);
            modifiedArrayOfClassNames.add(testClassName);
            for (String className : arrayOfClassNames) {
                if (testClassName.equals(className)) {
                    return arrayOfClassNames;
                } else {
                    modifiedArrayOfClassNames.add(className);
                }
            }
            return modifiedArrayOfClassNames.toArray(
                    new String[arrayOfClassNames.length + 1]);
        }
    }

    /**
     * @return <code>true</code> if there are some mock policies that
     * contributes with classes that should be loaded by the mock
     * classloader, <code>false</code> otherwise.
     */
    protected boolean hasMockPolicyProvidedClasses(Class<?> testClass) {
        boolean hasMockPolicyProvidedClasses = false;
        if (testClass.isAnnotationPresent(MockPolicy.class)) {
            MockPolicy annotation = testClass.getAnnotation(MockPolicy.class);
            Class<? extends PowerMockPolicy>[] value = annotation.value();
            hasMockPolicyProvidedClasses = new MockPolicyInitializerImpl(value).needsInitialization();
        }
        return hasMockPolicyProvidedClasses;
    }
}
