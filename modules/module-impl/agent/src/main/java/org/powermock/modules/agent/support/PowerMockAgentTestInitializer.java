/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.powermock.modules.agent.support;

import org.powermock.core.MockRepository;
import org.powermock.core.agent.JavaAgentClassRegister;
import org.powermock.modules.agent.PowerMockClassRedefiner;
import org.powermock.reflect.Whitebox;
import org.powermock.reflect.proxyframework.RegisterProxyFramework;
import org.powermock.tests.utils.impl.MockPolicyInitializerImpl;
import org.powermock.tests.utils.impl.PowerMockIgnorePackagesExtractorImpl;
import org.powermock.tests.utils.impl.PrepareForTestExtractorImpl;
import org.powermock.tests.utils.impl.StaticConstructorSuppressExtractorImpl;

public class PowerMockAgentTestInitializer {

    public static void initialize(Class<?> testClass, JavaAgentClassRegister agentClassRegister) {
        /*
		 * For extra safety clear the MockitoRepository.
		 */
        MockRepository.clear();

        redefineClasses(testClass, agentClassRegister);

        final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

        registerProxyFramework(contextClassLoader);

        new MockPolicyInitializerImpl(testClass).initialize(contextClassLoader);
    }

    private static void redefineClasses(Class<?> testClass, JavaAgentClassRegister agentClassRegister) {
        final String[] packagesToIgnore = new PowerMockIgnorePackagesExtractorImpl().getPackagesToIgnore(testClass);

        redefineClassesToPrepare(testClass, packagesToIgnore, agentClassRegister);
        redefineClassesToSuppress(testClass, packagesToIgnore, agentClassRegister);
    }

    private static void redefineClassesToSuppress(Class<?> testClass, String[] packagesToIgnore, JavaAgentClassRegister agentClassRegister) {
        final String[] classesToSuppress = new StaticConstructorSuppressExtractorImpl().getTestClasses(testClass);
        redefine(classesToSuppress, packagesToIgnore, agentClassRegister);
    }

    private static void redefineClassesToPrepare(Class<?> testClass, String[] packagesToIgnore,
                                                 JavaAgentClassRegister agentClassRegister) {
        final String[] classesToPrepare = new PrepareForTestExtractorImpl(true).getTestClasses(testClass);
        redefine(classesToPrepare, packagesToIgnore, agentClassRegister);
    }

    private static void redefine(String[] classes, String[] packagesToIgnore, JavaAgentClassRegister agentClassRegister) {
        PowerMockClassRedefiner.redefine(classes, packagesToIgnore,agentClassRegister);
    }

    private static void registerProxyFramework(ClassLoader classLoader) {
        final Class<?> proxyFrameworkClass;
        try {
            proxyFrameworkClass = Class.forName("org.powermock.api.extension.proxyframework.ProxyFrameworkImpl", false, classLoader);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(
                    "Extension API error: org.powermock.api.extension.proxyframework.ProxyFrameworkImpl could not be located in classpath.");
        }

        final Class<?> proxyFrameworkRegistrar;
        try {
            proxyFrameworkRegistrar = Class.forName(RegisterProxyFramework.class.getName(), false, classLoader);
        } catch (ClassNotFoundException e) {
            // Should never happen
            throw new RuntimeException(e);
        }
        try {
            Whitebox.invokeMethod(proxyFrameworkRegistrar, "registerProxyFramework", Whitebox.newInstance(proxyFrameworkClass));
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
