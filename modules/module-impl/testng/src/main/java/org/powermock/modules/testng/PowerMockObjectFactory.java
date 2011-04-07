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

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.testng.internal.PowerMockClassloaderObjectFactory;
import org.testng.IObjectFactory;
import org.testng.internal.ObjectFactoryImpl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * The PowerMock object factory. If the test class or any public method declared in the test class is annotated with
 * {@link PrepareForTest} or {@link SuppressStaticInitializationFor} the PowerMock classloader will enable the class
 * for PowerMock testing, otherwise a standard ObjectFactory is used.
 */
public class PowerMockObjectFactory implements IObjectFactory {

    private PowerMockClassloaderObjectFactory powerMockObjectFactory = new PowerMockClassloaderObjectFactory();

    private ObjectFactoryImpl defaultObjectFactory = new ObjectFactoryImpl();

    public Object newInstance(Constructor constructor, Object... params) {
        final Object testInstance;
        Class<?> testClass = constructor.getDeclaringClass();
        if (hasPowerMockAnnotation(testClass)) {
            testInstance = powerMockObjectFactory.newInstance(constructor, params);
        }
        else {
            testInstance = defaultObjectFactory.newInstance(constructor, params);
        }

        return testInstance;
    }

    private boolean hasPowerMockAnnotation(Class<?> testClass) {
        return isClassAnnotatedWithPowerMockAnnotation(testClass) || anyMethodInClassHasPowerMockAnnotation(testClass);
    }

    private boolean anyMethodInClassHasPowerMockAnnotation(Class<?> testClass) {
        final Method[] methods = testClass.getMethods();
        for (Method method : methods) {
            if(method.isAnnotationPresent(PrepareForTest.class) || method.isAnnotationPresent(SuppressStaticInitializationFor.class)) {
                return true;
            }
        }
        return false;
    }

    private boolean isClassAnnotatedWithPowerMockAnnotation(Class<?> testClass) {
        return testClass.isAnnotationPresent(PrepareForTest.class) || testClass.isAnnotationPresent(SuppressStaticInitializationFor.class);
    }
}