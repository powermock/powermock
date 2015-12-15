/*
 * Copyright 2009 the original author or authors.
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
package org.powermock.modules.testng;

import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.agent.support.PowerMockAgentTestInitializer;
import org.testng.IObjectFactory;
import org.testng.internal.ObjectFactoryImpl;

import java.lang.reflect.Constructor;

/**
 * The PowerMock object factory for PowerMock java agent.
 */
public class PowerMockObjectFactory implements IObjectFactory {

     static {
        if(PowerMockObjectFactory.class.getClassLoader() != ClassLoader.getSystemClassLoader()) {
            throw new IllegalStateException("PowerMockObjectFactory can only be used with the system classloader but was loaded by "+PowerMockObjectFactory.class.getClassLoader());
        }
        PowerMockAgent.initializeIfPossible();
    }

    private ObjectFactoryImpl defaultObjectFactory = new ObjectFactoryImpl();

    @Override
    public Object newInstance(Constructor constructor, Object... params) {
        final Class<?> testClass = constructor.getDeclaringClass();
        PowerMockAgentTestInitializer.initialize(testClass);
        return defaultObjectFactory.newInstance(constructor, params);
    }
}