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
package org.powermock.api.mockito.internal.expectation;

import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;
import org.powermock.api.mockito.expectation.WithOrWithoutExpectedArguments;

import java.lang.reflect.Method;

public class DefaultMethodExpectationSetup<T> implements WithOrWithoutExpectedArguments<T> {

    private final Object object;

    private final Method method;

    public DefaultMethodExpectationSetup(Object object, Method method) {
        if (object == null) {
            throw new IllegalArgumentException("object to expect cannot be null");
        } else if (method == null) {
            throw new IllegalArgumentException("method to expect cannot be null");
        }
        this.object = object;
        this.method = method;
        this.method.setAccessible(true);
    }

    private static Object[] join(Object o, Object[] array) {
        Object[] res = new Object[array.length + 1];
        res[0] = o;
        System.arraycopy(array, 0, res, 1, array.length);
        return res;
    }

    @SuppressWarnings("unchecked")
    @Override
    public OngoingStubbing<T> withArguments(Object firstArgument, Object... additionalArguments) throws Exception {
        if (additionalArguments == null || additionalArguments.length == 0) {
            return (OngoingStubbing<T>) Mockito.when(method.invoke(object, firstArgument));
        } else {
            return (OngoingStubbing<T>) Mockito.when(method.invoke(object, join(firstArgument, additionalArguments)));
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public OngoingStubbing<T> withNoArguments() throws Exception {
        return (OngoingStubbing<T>) Mockito.when(method.invoke(object));
    }
}
