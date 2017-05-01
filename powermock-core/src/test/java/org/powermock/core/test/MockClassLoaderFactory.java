/*
 *
 *   Copyright 2017 the original author or authors.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package org.powermock.core.test;

import org.powermock.core.classloader.MockClassLoader;
import org.powermock.core.classloader.MockClassLoaderConfiguration;
import org.powermock.core.classloader.annotations.UseClassPathAdjuster;
import org.powermock.reflect.internal.WhiteboxImpl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class MockClassLoaderFactory {
    
    private final Class<? extends MockClassLoader> classLoaderClass;
    
    public MockClassLoaderFactory(Class<? extends MockClassLoader> classLoaderClass) {
        this.classLoaderClass = classLoaderClass;
    }
    
    public MockClassLoader getInstance(String[] classesToMock,
                                        UseClassPathAdjuster useClassPathAdjuster) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<?> constructor = WhiteboxImpl.getConstructor(classLoaderClass, classesToMock.getClass(), classesToMock.getClass(), UseClassPathAdjuster.class);
        return (MockClassLoader) constructor.newInstance(classesToMock, new String[0], useClassPathAdjuster);
    }
    
    public MockClassLoader getInstance(String[] classesToMock) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        MockClassLoaderConfiguration configuration = new MockClassLoaderConfiguration(classesToMock, new String[0]);
        return getInstance(configuration);
    }
    
    private MockClassLoader getInstance(MockClassLoaderConfiguration configuration) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<?> constructor = WhiteboxImpl.getConstructor(classLoaderClass, configuration.getClass());
        return (MockClassLoader) constructor.newInstance(new Object[]{configuration});
    }
    
    @Override
    public String toString() {
        return classLoaderClass.getSimpleName();
    }
}
