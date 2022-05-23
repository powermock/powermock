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

package org.powermock.core.transformers;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.powermock.configuration.GlobalConfiguration;
import org.powermock.core.IndicateReloadClass;
import org.powermock.core.classloader.ByteCodeFramework;
import org.powermock.core.classloader.MockClassLoader;
import org.powermock.core.classloader.MockClassLoaderBuilder;
import org.powermock.reflect.internal.WhiteboxImpl;
import powermock.test.support.MainMockTransformerTestSupport.SupportClasses;
import powermock.test.support.TestWithTwoTestMethods;
import powermock.test.support.TestWithTwoTestMethods.NestedTestWithTwoTestMethods;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.reflect.Modifier.isPrivate;
import static java.lang.reflect.Modifier.isProtected;
import static java.lang.reflect.Modifier.isPublic;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;

@RunWith(Parameterized.class)
public class TestClassTransformerTest {
    
    @Parameterized.Parameter
    public ByteCodeFramework bytecodeFramework;
    
    @Parameterized.Parameter(1)
    public MockClassLoaderCase classLoaderCase;
    
    @Parameterized.Parameters(name = "{0}-{1}")
    public static List<?> values() {
        
        final List<Object[]> values = new ArrayList<Object[]>();
        
        for (ByteCodeFramework byteCodeFramework : ByteCodeFramework.values()) {
            for (MockClassLoaderCase eachFactoryAlternative : MockClassLoaderCase.values()) {
                values.add(new Object[]{byteCodeFramework, eachFactoryAlternative});
            }
        }
    
        return values;
    }
    
    @Before
    public void setUp() {
        GlobalConfiguration.powerMockConfiguration().setByteCodeFramework(bytecodeFramework);
    }
    
    @Test
    public void should_make_defer_constructor_non_public_for_inner_classes() throws Exception {
        MockClassLoader mockClassLoader = classLoaderCase.createMockClassLoaderThatPrepare(
            SupportClasses.SubClass.class,
            bytecodeFramework, Collections.<Method>emptyList());
        
        final Class<?> clazz = Class.forName(SupportClasses.SubClass.class.getName(), true, mockClassLoader);
    
        assertThat(SupportClasses.SubClass.class.getConstructors())
            .as("Original number of constructors")
            .hasSize(1);
    
        assertThatThrownBy(
            new ThrowingCallable() {
                @Override
                public void call() throws Throwable {
                    clazz.getConstructor(IndicateReloadClass.class);
                }
            }
        ).withFailMessage("A public defer-constructor still presents.")
         .isExactlyInstanceOf(NoSuchMethodException.class);
    
        assertThat(clazz.getConstructors())
            .as("Number of (public) constructors in modified class")
            .hasSize(1);
    
        assumeTrue(bytecodeFramework == ByteCodeFramework.Javassist);
        assertThat(clazz.getDeclaredConstructor(IndicateReloadClass.class))
            .as("But there should still be a non-public defer constructor!")
            .isNotNull();
    }
    
    @Test
    public void should_restore_original_constructors_accesses() throws Exception {
        MockClassLoader mockClassLoader = classLoaderCase.createMockClassLoaderThatPrepare(
            SupportClasses.MultipleConstructors.class,
            bytecodeFramework, Collections.<Method>emptyList());
        
        final Class<?> clazz = Class.forName(
            SupportClasses.MultipleConstructors.class.getName(),
            true, mockClassLoader
        );
        
        for (Constructor<?> originalConstructor : SupportClasses.MultipleConstructors.class.getDeclaredConstructors()) {
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
    
    @Test
    public void should_remove_test_annotation_from_all_method() throws ClassNotFoundException {
        
        final String methodName = "test_method_2";
        final MockClassLoader mockClassLoader = classLoaderCase.createMockClassLoaderThatPrepare(
            TestWithTwoTestMethods.class,
            bytecodeFramework,
            Collections.singletonList(WhiteboxImpl.findMethod(TestWithTwoTestMethods.class, methodName))
        );
        
        final Class<?> clazz = Class.forName(
            TestWithTwoTestMethods.class.getName(),
            true, mockClassLoader
        );
        
        final Method modifiedMethod = WhiteboxImpl.findMethod(clazz, methodName);
        
        assertThat(modifiedMethod.isAnnotationPresent(Test.class))
            .as("Test annotation has been removed.")
            .isFalse();
    }
    
    @Test
    public void should_not_remove_test_annotation_from_all_method_if_nested_class_in_test_class() throws ClassNotFoundException {
    
        final String methodName = "test_nested_method_2";
        
        final MockClassLoader mockClassLoader = classLoaderCase.createMockClassLoaderThatPrepare(
            TestWithTwoTestMethods.class,
            bytecodeFramework,
            Collections.singletonList(WhiteboxImpl.findMethod(NestedTestWithTwoTestMethods.class, methodName))
        );
        
        final Class<?> clazz = Class.forName(
            NestedTestWithTwoTestMethods.class.getName(),
            true, mockClassLoader
        );
        
        final Method modifiedMethod = WhiteboxImpl.findMethod(clazz, methodName);
        
        assertThat(modifiedMethod.isAnnotationPresent(Test.class))
            .as("Test annotation has been removed.")
            .isTrue();
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
                final Class<?> declaringClass = prepare4test.getDeclaringClass();
                if (declaringClass == null) {
                    return prepare4test;
                } else {
                    return declaringClass;
                }
            }
            
            @Override
            String[] preparations(Class<?> prepare4test) {
                return new String[]{prepare4test.getName()};
            }
        };
        
        abstract Class<?> chooseTestClass(Class<?> prepare4test);
        
        abstract String[] preparations(Class<?> prepare4test);
    
        MockClassLoader createMockClassLoaderThatPrepare(Class<?> prepare4test, final ByteCodeFramework bytecodeFramework,
                                                         final List<Method> methodsToRemoveAnnotations) {
            final Class<?> testClass = chooseTestClass(prepare4test);
            final MockTransformer testClassTransformer = TestClassTransformerBuilder
                                                             .forTestClass(testClass)
                                                             .removesTestMethodAnnotation(Test.class)
                                                             .fromMethods(new ArrayList<Method>(methodsToRemoveAnnotations));
        
            return MockClassLoaderBuilder.create(bytecodeFramework)
                                         .forTestClass(testClass)
                                         .addExtraMockTransformers(testClassTransformer)
                                         .addClassesToModify(preparations(prepare4test))
                                         .build();
        }
    }
    
}
