/*
 * Copyright 2012 the original author or authors.
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
package org.powermock.api.mockito.internal.expectation;

import org.mockito.Matchers;
import org.mockito.stubbing.Answer;
import org.mockito.stubbing.OngoingStubbing;
import org.powermock.core.spi.support.InvocationSubstitute;

import java.lang.reflect.Constructor;

import static org.mockito.Mockito.when;

/**
 * Implementation of OngoingStubbing that delegates invocations to all supplied ctors
 * @param <T>
 */
public class DelegatingToConstructorsOngoingStubbing<T> implements OngoingStubbing<T>{

    private final OngoingStubbing<T> stubbing;
    private final Constructor<?>[] ctors;

    public DelegatingToConstructorsOngoingStubbing(Constructor<?>[] ctors, OngoingStubbing<T> stubbing) {
        if(stubbing == null) {
            throw new IllegalArgumentException("Internal error: Ongoing stubbing must be provided");
        }
        this.ctors = ctors;
        this.stubbing = stubbing;
    }

    @Override
    public OngoingStubbing<T> thenReturn(final T value) {
        stubbing.thenReturn(value);
        return new InvokeStubMethod() {
            @Override
            public void performStubbing(OngoingStubbing<T> when) {
                when.thenReturn(value);
            }
        }.invoke();
    }

    @Override
    public OngoingStubbing<T> thenReturn(final T value, final T... values) {
        stubbing.thenReturn(value, values);
        return new InvokeStubMethod() {
            @Override
            public void performStubbing(OngoingStubbing<T> when) {
                when.thenReturn(value, values);
            }
        }.invoke();
    }

    @Override
    public OngoingStubbing<T> thenThrow(final Throwable... throwables) {
        stubbing.thenThrow(throwables);
        return new InvokeStubMethod() {
            @Override
            public void performStubbing(OngoingStubbing<T> when) {
                when.thenThrow(throwables);
            }
        }.invoke();
    }
    
    @Override
    public OngoingStubbing<T> thenThrow(final Class<? extends Throwable> throwableType) {
        stubbing.thenThrow(throwableType);
        return new InvokeStubMethod() {
            @Override
            public void performStubbing(OngoingStubbing<T> when) {
                when.thenThrow(throwableType);
            }
        }.invoke();
    }
 
    @Override
    public OngoingStubbing<T> thenThrow(final Class<? extends Throwable> toBeThrown, final Class<? extends Throwable>[] nextToBeThrown) {
        stubbing.thenThrow(toBeThrown, nextToBeThrown);
        return new InvokeStubMethod() {
            @Override
            public void performStubbing(OngoingStubbing<T> when) {
                when.thenThrow(toBeThrown, nextToBeThrown);
            }
        }.invoke();
    }

    @Override
    public OngoingStubbing<T> thenCallRealMethod() {
        stubbing.thenCallRealMethod();
        return new InvokeStubMethod() {
            @Override
            public void performStubbing(OngoingStubbing<T> when) {
                when.thenCallRealMethod();
            }
        }.invoke();
    }

    @Override
    public OngoingStubbing<T> thenAnswer(final Answer<?> answer) {
        stubbing.thenAnswer(answer);
        return new InvokeStubMethod() {
            @Override
            public void performStubbing(OngoingStubbing<T> when) {
                when.thenAnswer(answer);
            }
        }.invoke();
    }

    @Override
    public OngoingStubbing<T> then(final Answer<?> answer) {
        stubbing.then(answer);
        return new InvokeStubMethod() {
            @Override
            public void performStubbing(OngoingStubbing<T> when) {
                when.then(answer);
            }
        }.invoke();
    }

    @Override
    public <M> M getMock() {
        return stubbing.getMock();
    }


    private abstract class InvokeStubMethod {
        public OngoingStubbing<T> invoke() {
            final InvocationSubstitute<T> mock = stubbing.getMock();
            for (Constructor<?> constructor : ctors) {
                final Class<?>[] parameterTypesForCtor = constructor.getParameterTypes();
                Object[] paramArgs = new Object[parameterTypesForCtor.length];
                for (int i = 0; i < parameterTypesForCtor.length; i++) {
                    Class<?> paramType = parameterTypesForCtor[i];
                    paramArgs[i] = Matchers.any(paramType);
                }
                try {
                    final OngoingStubbing<T> when = when(mock.performSubstitutionLogic(paramArgs));
                    performStubbing(when);
                } catch (Exception e) {
                    throw new RuntimeException("PowerMock internal error",e);
                }
            }

            return stubbing;
        }

        public abstract void performStubbing(OngoingStubbing<T> when);

    }
}
