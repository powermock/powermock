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

import javassist.util.proxy.MethodFilter;

import java.lang.reflect.Method;

/**
 * Javassist method filter that ignores the toString, equals, finalize and
 * hashCode method otherwise the test output in Maven looks strange and
 * replayAll/verifyAll doesn't work as expected.
 */
public class TestNGMethodFilter implements MethodFilter {
    @Override
    public boolean isHandled(Method method) {
        return !isToString(method) && !isHashCode(method) && !isFinalize(method) && !isEquals(method);
    }

    private boolean isEquals(Method method) {
        return method.getName().equals("equals") && isOneArgumentMethodOfType(method, Object.class);
    }

    private boolean isFinalize(Method method) {
        return method.getName().equals("finalize") && isZeroArgumentMethod(method);
    }

    private boolean isHashCode(Method method) {
        return method.getName().equals("hashCode") && isZeroArgumentMethod(method);
    }

    private boolean isToString(Method method) {
        return (method.getName().equals("toString") && isZeroArgumentMethod(method));
    }

    private boolean isZeroArgumentMethod(Method method) {
        return hasArgumentLength(method, 0);
    }

    private boolean hasArgumentLength(Method method, int length) {
        return method.getParameterTypes().length == length;
    }

    private boolean isOneArgumentMethodOfType(Method method, Class<?> type) {
        return hasArgumentLength(method, 1) && Object.class.equals(method.getParameterTypes()[0]);
    }
}