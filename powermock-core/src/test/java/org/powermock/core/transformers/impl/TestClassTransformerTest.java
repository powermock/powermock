/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.powermock.core.transformers.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.powermock.core.IndicateReloadClass;
import org.powermock.core.classloader.MockClassLoader;
import org.powermock.core.classloader.javassist.JavassistMockClassLoader;
import org.powermock.core.transformers.MockTransformer;
import powermock.test.support.MainMockTransformerTestSupport.SupportClasses;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.lang.reflect.Modifier.isPrivate;
import static java.lang.reflect.Modifier.isProtected;
import static java.lang.reflect.Modifier.isPublic;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class TestClassTransformerTest {
    
    @Parameterized.Parameter(0)
    public MockClassLoaderCase classLoaderCase;
    
    @Parameterized.Parameters(name = "{0}")
    public static List<?> values() {
        MockClassLoaderCase[] factoryAlternatives = MockClassLoaderCase.values();
        List<Object[]> values = Arrays.asList(new Object[factoryAlternatives.length][]);
        for (MockClassLoaderCase eachFactoryAlternative : factoryAlternatives) {
            values.set(eachFactoryAlternative.ordinal(),
                       new Object[]{eachFactoryAlternative});
        }
        return values;
    }
    
    @Test
    public void preparedSubclassShouldNotGetPublicDeferConstructor() throws Exception {
        MockClassLoader mockClassLoader = classLoaderCase
                                              .createMockClassLoaderThatPrepare(SupportClasses.SubClass.class);
        final Class<?> clazz = Class.forName(SupportClasses.SubClass.class.getName(), true, mockClassLoader);
        assertEquals("Original number of constructoprs",
                     1, SupportClasses.SubClass.class.getConstructors().length);
        try {
            fail("A public defer-constructor is not expected: "
                     + clazz.getConstructor(IndicateReloadClass.class));
        } catch (NoSuchMethodException is_expected) {}
        assertEquals("Number of (public) constructors in modified class",
                     1, clazz.getConstructors().length);
        
        assertNotNull("But there should still be a non-public defer constructor!",
                      clazz.getDeclaredConstructor(IndicateReloadClass.class));
    }
    
    @Test
    public void preparedClassConstructorsShouldKeepTheirAccessModifier() throws Exception {
        MockClassLoader mockClassLoader = classLoaderCase
                                              .createMockClassLoaderThatPrepare(SupportClasses.MultipleConstructors.class);
        final Class<?> clazz = Class.forName(
            SupportClasses.MultipleConstructors.class.getName(),
            true, mockClassLoader);
        for (Constructor<?> originalConstructor : SupportClasses
                                                      .MultipleConstructors.class.getDeclaredConstructors()) {
            Class[] paramTypes = originalConstructor.getParameterTypes();
            int originalModifiers = originalConstructor.getModifiers();
            int newModifiers = clazz.getDeclaredConstructor(paramTypes).getModifiers();
            String constructorName = 0 == paramTypes.length
                                         ? "Default constructor "
                                         : paramTypes[0].getSimpleName() + " constructor ";
            assertEquals(constructorName + "is public?",
                         isPublic(originalModifiers), isPublic(newModifiers));
            assertEquals(constructorName + "is protected?",
                         isProtected(originalModifiers), isProtected(newModifiers));
            assertEquals(constructorName + "is private?",
                         isPrivate(originalModifiers), isPrivate(newModifiers));
        }
    }
    
    enum MockClassLoaderCase {
        WHEN_PREPARED_CLASS_IS_TESTCLASS {
            @Override
            Class<?> chooseTestClass(Class<?> prepare4test) {
                return prepare4test;
            }
            
            @Override
            String[] preparations(Class<?> prepare4test) {
                return new String[]{MockClassLoader.MODIFY_ALL_CLASSES};
            }
        },
        WHEN_ENCLOSING_CLASS_IS_TESTCLASS {
            @Override
            Class<?> chooseTestClass(Class<?> prepare4test) {
                return prepare4test.getDeclaringClass();
            }
            
            @Override
            String[] preparations(Class<?> prepare4test) {
                return new String[]{prepare4test.getName()};
            }
        };
        
        abstract Class<?> chooseTestClass(Class<?> prepare4test);
        
        abstract String[] preparations(Class<?> prepare4test);
        
        MockClassLoader createMockClassLoaderThatPrepare(Class<?> prepare4test) {
            MockTransformer testClassTransformer = TestClassTransformer
                                                       .forTestClass(chooseTestClass(prepare4test))
                                                       .removesTestMethodAnnotation(Test.class)
                                                       .fromMethods(Collections.<Method>emptyList());
            MockClassLoader mockClassLoader =
                new JavassistMockClassLoader(preparations(prepare4test));
            mockClassLoader.setMockTransformerChain(Arrays.asList(
                new ClassMockTransformer(),
                testClassTransformer));
            return mockClassLoader;
        }
    }
}
