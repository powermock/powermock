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

/**
 * Class which use specific class loader to register {@link org.powermock.reflect.spi.ProxyFramework}. This class is
 * used to avoid class cast exception when a client class (which uses {@link RegisterProxyFramework}) is loaded with
 * anther class loader.
 */
public class ClassLoaderRegisterProxyFramework {

    public static void registerProxyframework(ClassLoader classLoader) {
        new ProxyFrameworkHelper(classLoader).register();
    }

}
