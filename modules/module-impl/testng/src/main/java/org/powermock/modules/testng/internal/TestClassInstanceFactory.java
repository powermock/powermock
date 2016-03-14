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

import javassist.util.proxy.ProxyFactory;
import org.powermock.modules.testng.PowerMockTestCase;
import org.powermock.reflect.Whitebox;
import org.powermock.tests.utils.impl.MockPolicyInitializerImpl;

import java.lang.reflect.Constructor;

/**
 *
 */
class TestClassInstanceFactory {
    private final Constructor constructor;
    private final Class<?> testClass;
    private final Object[] params;
    private final ClassLoader mockLoader;

    TestClassInstanceFactory(Constructor constructor, ClassLoaderFactory classLoaderFactory, Object... params) {
        this.constructor = constructor;
        this.params = params;
        this.testClass = constructor.getDeclaringClass();
        this.mockLoader = classLoaderFactory.createClassLoader(testClass);
    }

     Object create() {

        try {

            initializeMockPolicy();

            final Class<?> testClassLoadedByMockedClassLoader = createTestClass(testClass);
            final Constructor<?> con = testClassLoadedByMockedClassLoader.getConstructor(constructor.getParameterTypes());
            final Object testInstance = con.newInstance(params);

            if (!extendsPowerMockTestCase(testClass)) {
                setInvocationHandler(testInstance);
            }

            return testInstance;
        } catch (Exception e) {
            throw new RuntimeException(String.format("Cannot create a new instance of test class %s", testClass), e);
        }
    }

    private void initializeMockPolicy() {new MockPolicyInitializerImpl(testClass).initialize(mockLoader);}


    /**
     * We proxy the test class in order to be able to clear state after each
     * test method invocation. It would be much better to be able to register a
     * testng listener programmtically but I cannot find a way to do so.
     */
    private Class<?> createTestClass(Class<?> actualTestClass) throws Exception {

        final Class<?> testClassLoadedByMockedClassLoader = Class.forName(actualTestClass.getName(), false, mockLoader);

        if (extendsPowerMockTestCase(actualTestClass)) {
            return testClassLoadedByMockedClassLoader;
        } else {
            return createProxyTestClass(testClassLoadedByMockedClassLoader);
        }
    }

    private Class<?> createProxyTestClass(Class<?> testClassLoadedByMockedClassLoader) throws Exception {
        Class<?> proxyFactoryClass = Class.forName(ProxyFactory.class.getName(), false, mockLoader);
        final Class<?> testNGMethodFilterByMockedClassLoader = Class.forName(TestNGMethodFilter.class.getName(), false, mockLoader);

        Object f = proxyFactoryClass.newInstance();
        Object filter = testNGMethodFilterByMockedClassLoader.newInstance();
        Whitebox.invokeMethod(f, "setFilter", filter);
        Whitebox.invokeMethod(f, "setSuperclass", testClassLoadedByMockedClassLoader);

        return Whitebox.invokeMethod(f, "createClass");
    }

    private void setInvocationHandler(Object testInstance) throws Exception {
        Class<?> powerMockTestNGMethodHandlerClass = Class.forName(PowerMockTestNGMethodHandler.class.getName(), false, mockLoader);
        Object powerMockTestNGMethodHandlerInstance = powerMockTestNGMethodHandlerClass.getConstructor(Class.class)
                                                                                       .newInstance(
                                                                                               testInstance.getClass());
        Whitebox.invokeMethod(testInstance, "setHandler", powerMockTestNGMethodHandlerInstance);
    }

    private boolean extendsPowerMockTestCase(Class<?> actualTestClass) {
        return PowerMockTestCase.class.isAssignableFrom(actualTestClass);
    }
}
