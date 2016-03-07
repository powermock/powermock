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

package org.powermock.core.agent;

import org.powermock.reflect.Whitebox;

/**
 * Factory to create an instance of JavaAgentFrameworkRegister,
 * depends on which mocking framework is loaded in runtime.
 */
public class JavaAgentFrameworkRegisterFactory {

    public static JavaAgentFrameworkRegister create() {

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        return getInstanceForClassLoader(classLoader);
    }

    private static JavaAgentFrameworkRegister getInstanceForClassLoader(ClassLoader classLoader) {
        Class<JavaAgentFrameworkRegister> frameworkReporterClass = getJavaAgentFrameworkRegisterClass(classLoader);
        return Whitebox.newInstance(frameworkReporterClass);
    }
    
    @SuppressWarnings("unchecked")
    private static Class<JavaAgentFrameworkRegister> getJavaAgentFrameworkRegisterClass(ClassLoader classLoader) {
        Class<JavaAgentFrameworkRegister> agentFrameworkRegisterClass;
        try {
            agentFrameworkRegisterClass = (Class<JavaAgentFrameworkRegister>) classLoader.loadClass(getImplementerClassName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return agentFrameworkRegisterClass;
    }
    
    @SuppressWarnings("SameReturnValue")
    private static String getImplementerClassName() {
        return "org.powermock.api.extension.agent.JavaAgentFrameworkRegisterImpl";
    }
    
}
