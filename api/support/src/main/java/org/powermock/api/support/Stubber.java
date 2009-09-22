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
package org.powermock.api.support;

import java.lang.reflect.Method;

import org.powermock.core.MockRepository;
import org.powermock.reflect.Whitebox;
import org.powermock.reflect.exceptions.MethodNotFoundException;
import org.powermock.reflect.exceptions.TooManyMethodsFoundException;

public class Stubber {
    /**
     * Add a method that should be intercepted and return another value (
     * <code>returnObject</code>) (i.e. the method is stubbed).
     */
    public static void stubMethod(Method method, Object returnObject) {
        MockRepository.putMethodToStub(method, returnObject);
    }

    /**
     * Add a method that should be intercepted and return another value (
     * <code>returnObject</code>) (i.e. the method is stubbed).
     */
    public static void stubMethod(Class<?> declaringClass, String methodName, Object returnObject) {
        if (declaringClass == null) {
            throw new IllegalArgumentException("declaringClass cannot be null");
        }
        if (methodName == null || methodName.length() == 0) {
            throw new IllegalArgumentException("methodName cannot be empty");
        }
        Method[] methods = Whitebox.getMethods(declaringClass, methodName);
        if (methods.length == 0) {
            throw new MethodNotFoundException(String.format("Couldn't find a method with name %s in the class hierarchy of %s", methodName,
                    declaringClass.getName()));
        } else if (methods.length > 1) {
            throw new TooManyMethodsFoundException(String.format("Found %d methods with name %s in the class hierarchy of %s.",
                    methods.length, methodName, declaringClass.getName()));
        }

        MockRepository.putMethodToStub(methods[0], returnObject);
    }

}
