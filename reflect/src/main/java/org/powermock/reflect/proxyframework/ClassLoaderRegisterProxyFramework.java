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

package org.powermock.reflect.proxyframework;

import org.powermock.reflect.Whitebox;

/**
 *  Class which use specific class loader to register {@link org.powermock.reflect.spi.ProxyFramework}. This class is
 *  used to avoid class cast exception when a client class (which uses {@link RegisterProxyFramework}) is loaded with
 *  anther class loader.
 */
public class ClassLoaderRegisterProxyFramework {

    public static void registerProxyframework(ClassLoader classLoader) {
        final Class<?> proxyFrameworkClass;
        try {
            proxyFrameworkClass = Class.forName("org.powermock.api.extension.proxyframework.ProxyFrameworkImpl", false, classLoader);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Extension API internal error: org.powermock.api.extension.proxyframework.ProxyFrameworkImpl could not be located in classpath.");
        }

        final Class<?> proxyFrameworkRegistrar;
        try {
            proxyFrameworkRegistrar = Class.forName(RegisterProxyFramework.class.getName(), false, classLoader);
        } catch (ClassNotFoundException e) {
            // Should never happen
            throw new RuntimeException("PowerMock cannot load RegisterProxyFramework which is a part of PowerMock " +
                                               "bundle. Please, report about error." ,e);
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
