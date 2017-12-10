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

import org.powermock.core.classloader.annotations.MockPolicy;
import org.powermock.core.classloader.annotations.PrepareEverythingForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.core.classloader.annotations.UseClassPathAdjuster;
import org.powermock.core.spi.PowerMockPolicy;
import org.powermock.core.transformers.MockTransformer;
import org.powermock.tests.utils.ArrayMerger;
import org.powermock.tests.utils.TestClassesExtractor;
import org.powermock.tests.utils.impl.ArrayMergerImpl;
import org.powermock.tests.utils.impl.MockPolicyInitializerImpl;
import org.powermock.tests.utils.impl.PowerMockIgnorePackagesExtractorImpl;
import org.powermock.tests.utils.impl.PrepareForTestExtractorImpl;
import org.powermock.tests.utils.impl.StaticConstructorSuppressExtractorImpl;

import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;

public class MockClassLoaderFactory {
    
    private final String[] packagesToIgnore;
    private final Class<?> testClass;
    private final TestClassesExtractor prepareForTestExtractor;
    private final TestClassesExtractor suppressionExtractor;
    private final ArrayMerger arrayMerger;
    
    
    public MockClassLoaderFactory(Class<?> testClass) {
        this(testClass, new PowerMockIgnorePackagesExtractorImpl().getPackagesToIgnore(testClass));
    }
    
    public MockClassLoaderFactory(Class<?> testClass, String[] packagesToIgnore) {
        this.testClass = testClass;
        this.prepareForTestExtractor = new PrepareForTestExtractorImpl();
        this.suppressionExtractor = new StaticConstructorSuppressExtractorImpl();
        this.packagesToIgnore = packagesToIgnore;
        arrayMerger = new ArrayMergerImpl();
    }
    
    public ClassLoader createForClass(final MockTransformer extraMockTransformer) {
        if (testClass.isAnnotationPresent(PrepareEverythingForTest.class)) {
            return create(new String[]{MockClassLoader.MODIFY_ALL_CLASSES}, extraMockTransformer);
        } else {
            final String[] prepareForTestClasses = prepareForTestExtractor.getTestClasses(testClass);
            final String[] suppressStaticClasses = suppressionExtractor.getTestClasses(testClass);
            return create(arrayMerger.mergeArrays(String.class, prepareForTestClasses, suppressStaticClasses), extraMockTransformer);
        }
    }
    
    public ClassLoader createForMethod(final Method method, final MockTransformer extraMockTransformer) {
        if (method.isAnnotationPresent(PrepareEverythingForTest.class)) {
            final String[] classesToLoadByMockClassloader = {MockClassLoader.MODIFY_ALL_CLASSES};
            return create(classesToLoadByMockClassloader, extraMockTransformer);
        } else {
            final String[] suppressStaticClasses = getStaticSuppressionClasses(method);
            final String[] prepareForTestClasses = prepareForTestExtractor.getTestClasses(method);
            final String[] classesToLoadByMockClassloader = arrayMerger.mergeArrays(String.class, prepareForTestClasses, suppressStaticClasses);
            return create(classesToLoadByMockClassloader, extraMockTransformer);
        }
    }
    
    public ClassLoader create(final String[] prepareForTestClasses, final MockTransformer extraMockTransformer) {
        final String[] classesToLoadByMockClassloader = makeSureArrayContainsTestClassName(prepareForTestClasses, testClass.getName());
        
        final ClassLoader mockLoader;
        if (isContextClassLoaderShouldBeUsed(classesToLoadByMockClassloader)) {
            mockLoader = Thread.currentThread().getContextClassLoader();
        } else {
            mockLoader = createMockClassLoader(classesToLoadByMockClassloader, extraMockTransformer);
        }
        return mockLoader;
    }
    
    private String[] getStaticSuppressionClasses(Method method) {
        final String[] testClasses;
        if (method.isAnnotationPresent(SuppressStaticInitializationFor.class)) {
            testClasses = suppressionExtractor.getTestClasses(method);
        } else {
            testClasses = suppressionExtractor.getTestClasses(testClass);
        }
        return testClasses;
    }
    
    private ClassLoader createMockClassLoader(final String[] classesToLoadByMockClassloader, final MockTransformer extraMockTransformer) {
        
        final ClassLoader mockLoader = createWithPrivilegeAccessController(classesToLoadByMockClassloader, extraMockTransformer);
    
        initialize(mockLoader);
        
        return mockLoader;
    }
    
    private ClassLoader createWithPrivilegeAccessController(final String[] classesToLoadByMockClassloader,
                                                            final MockTransformer extraMockTransformer) {
        return AccessController.doPrivileged(new PrivilegedAction<MockClassLoader>() {
                @Override
                public MockClassLoader run() {
                    final UseClassPathAdjuster useClassPathAdjuster = testClass.getAnnotation(UseClassPathAdjuster.class);
                    return MockClassLoaderFactory.this.createMockClassLoader(classesToLoadByMockClassloader, extraMockTransformer, useClassPathAdjuster);
                }
            });
    }
    
    private MockClassLoader createMockClassLoader(final String[] classesToLoadByMockClassloader,
                                                  final MockTransformer extraMockTransformer,
                                                  final UseClassPathAdjuster useClassPathAdjuster) {
        return MockClassLoaderBuilder.create()
                                     .addIgnorePackage(packagesToIgnore)
                                     .addClassesToModify(classesToLoadByMockClassloader)
                                     .addClassPathAdjuster(useClassPathAdjuster)
                                     .addExtraMockTransformer(extraMockTransformer)
                                     .build();
    }
    
    private void initialize(final ClassLoader mockLoader) {
        new MockPolicyInitializerImpl(testClass).initialize(mockLoader);
    }
    
    private boolean isContextClassLoaderShouldBeUsed(String[] classesToLoadByMockClassloader) {
        return (classesToLoadByMockClassloader == null || classesToLoadByMockClassloader.length == 0) && !hasMockPolicyProvidedClasses(testClass);
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
     * @return {@code true} if there are some mock policies that
     * contributes with classes that should be loaded by the mock
     * classloader, {@code false} otherwise.
     */
    private boolean hasMockPolicyProvidedClasses(Class<?> testClass) {
        boolean hasMockPolicyProvidedClasses = false;
        if (testClass.isAnnotationPresent(MockPolicy.class)) {
            MockPolicy annotation = testClass.getAnnotation(MockPolicy.class);
            Class<? extends PowerMockPolicy>[] value = annotation.value();
            hasMockPolicyProvidedClasses = new MockPolicyInitializerImpl(value).needsInitialization();
        }
        return hasMockPolicyProvidedClasses;
    }
}
