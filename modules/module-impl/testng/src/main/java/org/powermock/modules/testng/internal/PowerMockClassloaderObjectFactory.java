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

import org.powermock.core.MockRepository;
import org.powermock.reflect.proxyframework.ClassLoaderRegisterProxyFramework;
import org.testng.IObjectFactory;

import java.lang.reflect.Constructor;

public class PowerMockClassloaderObjectFactory implements IObjectFactory {

    private final ClassLoaderFactory classLoaderFactory;

    public PowerMockClassloaderObjectFactory() {
        classLoaderFactory = new ClassLoaderFactory();
    }

    @Override
    public Object newInstance(Constructor constructor, Object... params) {

        /*
         * For extra safety clear the MockitoRepository on each new
		 * instantiation of the object factory. This is good in cases where a
		 * previous test has used e.g. PowerMock#createMock(..) to create a mock
		 * without using this factory. That means that there's some state left in
		 * the MockRepository that hasn't been cleared. Currently clearing the
		 * MockRepository from any classloader will clear the previous state but
		 * it's not certain that this is always the case.
		 */
        MockRepository.clear();

        Object testInstance = new TestClassInstanceFactory(constructor, classLoaderFactory, params).create();

        ClassLoaderRegisterProxyFramework.registerProxyframework(testInstance.getClass().getClassLoader());

        return testInstance;
    }


}
