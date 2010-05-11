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
package org.powermock.modules.testng.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import javassist.util.proxy.MethodHandler;

import org.powermock.core.MockRepository;
import org.powermock.reflect.Whitebox;
import org.testng.annotations.Test;

/*
 * Javassist handler that takes care of cleaning up {@link MockRepository} state
 * after each method annotated with {@link Test}.
 */
public class PowerMockTestNGMethodHandler implements MethodHandler {

    private Object annotationEnabler;

    public PowerMockTestNGMethodHandler(Class<?> testClass) {
        try {
            Class<?> annotationEnablerClass = Class.forName("org.powermock.api.extension.listener.AnnotationEnabler");
            annotationEnabler = Whitebox.newInstance(annotationEnablerClass);
        } catch (ClassNotFoundException e) {
            annotationEnabler = null;
        }
    }

    public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
        System.err.println("METHOD = "+thisMethod);
        injectMocksUsingAnnotationEnabler(self);
        try {
            final Object result = proceed.invoke(self, args);
            if (thisMethod.isAnnotationPresent(Test.class)) {
                clearMockFields();
                MockRepository.clear();
            }
            return result;
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

    private void clearMockFields() throws Exception, IllegalAccessException {
        if (annotationEnabler != null) {
            Set<Field> mockFields = Whitebox.getFieldsAnnotatedWith(this, Whitebox
                    .<Class<? extends Annotation>[]> invokeMethod(annotationEnabler, "getMockAnnotations"));
            for (Field field : mockFields) {
                field.set(this, null);
            }
        }
    }

    private void injectMocksUsingAnnotationEnabler(Object self) throws Exception {
        if (annotationEnabler != null) {
            Whitebox.invokeMethod(annotationEnabler, "beforeTestMethod", new Class<?>[] { Object.class, Method.class,
                    Object[].class }, self, null, null);
        }
    }
}
