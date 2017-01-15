/*
 * Copyright 2008 the original author or authors.
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
package org.powermock.modules.junit4.internal.impl.testcaseworkaround;

import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.MethodValidator;
import org.junit.internal.runners.TestClass;
import org.powermock.modules.junit4.common.internal.impl.JUnitVersion;
import org.powermock.reflect.Whitebox;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;

/**
 * A custom {@link MethodValidator} that makes sure that test methods not
 * annotated by the Test annotation works in JUnit 4.4 with the custom
 * JUnit-runner when the test class is extending {@link TestCase}. This is
 * actually a workaround for JUnit 4.4 when the test case extends from the
 * {@code TestCase} class.
 */
@SuppressWarnings("deprecation")
public class PowerMockJUnit4MethodValidator extends MethodValidator {

    private static final String TEST_CLASS_FIELD;
    private static final String CLASS_UNDER_TEST_FIELD;
    private static final String ERRORS_FIELD;

    static {
        if(JUnitVersion.isGreaterThanOrEqualTo("4.12")) {
            TEST_CLASS_FIELD = "testClass";
            CLASS_UNDER_TEST_FIELD = "klass";
            ERRORS_FIELD = "errors";
        }
        else {
            TEST_CLASS_FIELD = "fTestClass";
            CLASS_UNDER_TEST_FIELD = "fClass";
            ERRORS_FIELD = "fErrors";
        }
    }

    public PowerMockJUnit4MethodValidator(TestClass testClass) {
        super(testClass);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void validateInstanceMethods() {
        validateTestMethods(After.class, false);
        validateTestMethods(Before.class, false);
        validateTestMethods(Test.class, false);

        TestClass testClass = Whitebox.getInternalState(this, TEST_CLASS_FIELD, MethodValidator.class);
        Class<?> classUnderTest = Whitebox.getInternalState(testClass, CLASS_UNDER_TEST_FIELD);
        List<Throwable> fErrors = Whitebox.getInternalState(this, ERRORS_FIELD, MethodValidator.class);

        List<Method> methods = getTestMethods(testClass, classUnderTest);
        if (methods.size() == 0)
            fErrors.add(new Exception("No runnable methods"));
    }

    private List<Method> getTestMethods(TestClass testClass, Class<?> classUnderTest) {
        List<Method> methods = testClass.getAnnotatedMethods(Test.class);
        if (methods.isEmpty()) {
            methods.addAll(getTestMethodsWithNoAnnotation(classUnderTest));
        }
        return methods;
    }

    /**
     * This is a rip-off of the
     * {@link MethodValidator#validateInstanceMethods()} with the exception that
     * this method also searches for test methods if the class extends
     * {@link TestCase} and has methods that starts with test which are not
     * annotated.
     */
    @SuppressWarnings("unchecked")
    private void validateTestMethods(Class<? extends Annotation> annotation, boolean isStatic) {
        TestClass testClass = Whitebox.getInternalState(this, TEST_CLASS_FIELD, MethodValidator.class);
        Class<?> classUnderTest = Whitebox.getInternalState(testClass, CLASS_UNDER_TEST_FIELD);
        final List<Method> methods;
        if (TestCase.class.equals(classUnderTest.getSuperclass()) && !isStatic) {
            methods = getTestMethodsWithNoAnnotation(classUnderTest);
        } else {
            methods = testClass.getAnnotatedMethods(annotation);
        }

        List<Throwable> fErrors = Whitebox.getInternalState(this, ERRORS_FIELD, MethodValidator.class);
        for (Method each : methods) {
            if (Modifier.isStatic(each.getModifiers()) != isStatic) {
                String state = isStatic ? "should" : "should not";
                fErrors.add(new Exception("Method " + each.getName() + "() " + state + " be static"));
            }
            if (!Modifier.isPublic(each.getDeclaringClass().getModifiers()))
                fErrors.add(new Exception("Class " + each.getDeclaringClass().getName() + " should be public"));
            if (!Modifier.isPublic(each.getModifiers()))
                fErrors.add(new Exception("Method " + each.getName() + " should be public"));
            if (each.getReturnType() != Void.TYPE)
                fErrors.add(new Exception("Method " + each.getName() + " should be void"));
            if (each.getParameterTypes().length != 0)
                fErrors.add(new Exception("Method " + each.getName() + " should have no parameters"));
        }
    }

    private List<Method> getTestMethodsWithNoAnnotation(Class<?> testClass) {
        List<Method> potentialTestMethods = new LinkedList<Method>();
        Method[] methods = testClass.getMethods();
        for (Method method : methods) {
            if (method.getName().startsWith("test")) {
                potentialTestMethods.add(method);
            }
        }
        return potentialTestMethods;
    }
}
