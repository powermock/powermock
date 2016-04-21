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

package org.powermock.modules.junit4.common.internal.impl;

import junit.framework.TestCase;
import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 *
 */
public class JUnit4TestMethodChecker {
    private final Class<?> testClass;
    private final Method potentialTestMethod;

    public JUnit4TestMethodChecker(Class<?> testClass, Method potentialTestMethod) {
        this.testClass = testClass;
        this.potentialTestMethod = potentialTestMethod;
    }

    public boolean isTestMethod() {
        return isJUnit3TestMethod() || isJUnit4TestMethod();
    }

    protected boolean isJUnit4TestMethod() {return potentialTestMethod.isAnnotationPresent(Test.class);}

    protected boolean isJUnit3TestMethod() {
        return potentialTestMethod.getName().startsWith("test")
                       && Modifier.isPublic(potentialTestMethod.getModifiers())
                       && potentialTestMethod.getReturnType()
                                             .equals(Void.TYPE) && TestCase.class.isAssignableFrom(testClass);
    }
}
