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
package org.powermock.api.mockito.internal.mockcreation;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.mockito.Mockito;
import org.mockito.internal.MockHandler;
import org.mockito.internal.creation.MethodInterceptorFilter;
import org.mockito.internal.creation.MockSettingsImpl;
import org.mockito.internal.creation.jmock.ClassImposterizer;
import org.mockito.internal.invocation.MatchersBinder;
import org.mockito.internal.progress.MockingProgress;
import org.mockito.internal.util.MockName;
import org.powermock.api.mockito.internal.invocationcontrol.MockitoMethodInvocationControl;
import org.powermock.core.ClassReplicaCreator;
import org.powermock.core.MockRepository;
import org.powermock.core.spi.support.InvocationSubstitute;
import org.powermock.reflect.Whitebox;

public class MockCreator {

    @SuppressWarnings("unchecked")
    public static <T> T mock(Class<T> type, boolean isStatic, boolean isSpy, Object delegator, Method... methods) {
        if (type == null) {
            throw new IllegalArgumentException("The class to mock cannot be null");
        }

        T mock = null;
        final String mockName = toInstanceName(type);

        final Class<T> typeToMock;
        if (type.getName().startsWith("java.") && Modifier.isFinal(type.getModifiers())) {
            typeToMock = (Class<T>) new ClassReplicaCreator().createClassReplica(type);
        } else {
            typeToMock = type;
        }

        MockData<T> mockData = createMethodInvocationControl(mockName, typeToMock, methods, isSpy, (T) delegator);

        mock = mockData.getMock();
        if (isStatic) {
            MockRepository.putStaticMethodInvocationControl(type, mockData.getMethodInvocationControl());
        } else {
            MockRepository.putInstanceMethodInvocationControl(mock, mockData.getMethodInvocationControl());
        }

        if (mock instanceof InvocationSubstitute == false) {
            MockRepository.addObjectsToAutomaticallyReplayAndVerify(mock);
        }
        return mock;
    }

    private static <T> MockData<T> createMethodInvocationControl(final String mockName, Class<T> type, Method[] methods, boolean isSpy,
            Object delegator) {
        final MockSettingsImpl mockSettings;
        if (isSpy) {
            mockSettings = (MockSettingsImpl) new MockSettingsImpl().defaultAnswer(Mockito.CALLS_REAL_METHODS);
        } else {
            mockSettings = (MockSettingsImpl) Mockito.withSettings();
        }
        MockHandler<T> mockHandler = new MockHandler<T>(new MockName(mockName, type), Whitebox.getInternalState(Mockito.class,
                MockingProgress.class), new MatchersBinder(), mockSettings);
        MethodInterceptorFilter filter = new MethodInterceptorFilter(mockHandler, mockSettings);
        final T mock = (T) ClassImposterizer.INSTANCE.imposterise(filter, type);
        final MockitoMethodInvocationControl invocationControl = new MockitoMethodInvocationControl(filter,
                isSpy && delegator == null ? new Object() : delegator, methods);
        return new MockData<T>(invocationControl, mock);
    }

    private static String toInstanceName(Class<?> clazz) {
        String className = clazz.getSimpleName();
        // lower case first letter
        return className.substring(0, 1).toLowerCase() + className.substring(1);
    }

    /**
     * Class that encapsulate a mock and its corresponding invocation control.
     */
    private static class MockData<T> {
        private final MockitoMethodInvocationControl methodInvocationControl;

        private final T mock;

        MockData(MockitoMethodInvocationControl methodInvocationControl, T mock) {
            this.methodInvocationControl = methodInvocationControl;
            this.mock = mock;
        }

        public MockitoMethodInvocationControl getMethodInvocationControl() {
            return methodInvocationControl;
        }

        public T getMock() {
            return mock;
        }
    }
}
