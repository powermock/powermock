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
package org.powermock.api.easymock.internal.invocationcontrol;

import org.easymock.MockType;
import org.easymock.internal.MockInvocationHandler;
import org.easymock.internal.MocksControl;
import org.powermock.core.spi.MethodInvocationControl;
import org.powermock.reflect.internal.WhiteboxImpl;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * The default implementation of the {@link MethodInvocationControl} interface.
 */
public class EasyMockMethodInvocationControl<T> implements MethodInvocationControl {

    private MockInvocationHandler invocationHandler;

    private Set<Method> mockedMethods;

    private T mockInstance;

    private boolean hasReplayed;
    private boolean hasVerified;

    /**
     * Initializes internal state.
     *
     * @param invocationHandler The mock invocation handler to be associated with this
     *                          instance.
     * @param methodsToMock     The methods that are mocked for this instance. If
     *                          {@code methodsToMock} is null all methods for the
     *                          {@code invocationHandler} are considered to be mocked.
     * @param mockInstance      The actual mock instance. May be {@code null}. Even
     *                          though the mock instance may not be used it's needed to keep a
     *                          reference to this object otherwise it may be garbage collected
     *                          in some situations. For example when mocking static methods we
     *                          don't return the mock object and thus it will be garbage
     *                          collected (and thus the finalize method will be invoked which
     *                          will be caught by the proxy and the test will fail because we
     *                          haven't setup expectations for this method) because then that
     *                          object has no reference. In order to avoid this we keep a
     *                          reference to this instance here.
     */
    public EasyMockMethodInvocationControl(MockInvocationHandler invocationHandler, Set<Method> methodsToMock, T mockInstance) {
        if (invocationHandler == null) {
            throw new IllegalArgumentException("Invocation Handler cannot be null.");
        }

        this.invocationHandler = invocationHandler;
        this.mockedMethods = methodsToMock;
        this.mockInstance = mockInstance;
    }

    /**
     * Initializes internal state.
     *
     * @param invocationHandler The mock invocation handler to be associated with this
     *                          instance.
     * @param methodsToMock     The methods that are mocked for this instance. If
     *                          {@code methodsToMock} is null all methods for the
     *                          {@code invocationHandler} are considered to be mocked.
     */
    public EasyMockMethodInvocationControl(MockInvocationHandler invocationHandler, Set<Method> methodsToMock) {
        this(invocationHandler, methodsToMock, null);
    }

    @Override
    public boolean isMocked(Method method) {
        return mockedMethods == null || (mockedMethods != null && mockedMethods.contains(method));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] arguments) throws Throwable {
        return invocationHandler.invoke(mockInstance == null ? proxy : mockInstance, method, arguments);
    }

    public MocksControl.MockType getMockType() {
        final MocksControl control = invocationHandler.getControl();
        if (WhiteboxImpl.getFieldsOfType(control, MocksControl.MockType.class).isEmpty()) {
            // EasyMock is of version 3.2+
            final MockType mockType = WhiteboxImpl.getInternalState(control, MockType.class);
            switch (mockType) {
                case DEFAULT:
                    return MocksControl.MockType.DEFAULT;
                case NICE:
                    return MocksControl.MockType.NICE;
                case STRICT:
                    return MocksControl.MockType.STRICT;
                default:
                    throw new IllegalStateException("PowerMock doesn't seem to work with the used EasyMock version. Please report to the PowerMock mailing list");
            }
        } else {
            return WhiteboxImpl.getInternalState(control, MocksControl.MockType.class);
        }
    }

    @Override
    public synchronized Object replay(Object... mocks) {
        // Silently ignore replay if someone has replayed the mock before.
        if (!hasReplayed) {
            invocationHandler.getControl().replay();
            hasReplayed = true;
        }
        return null;
    }

    public synchronized Object verify(Object... mocks) {
        // Silently ignore verify if someone has verified the mock before.
        if (!hasVerified) {
            invocationHandler.getControl().verify();
            hasVerified = true;
        }
        return null;
    }

    @Override
    public synchronized Object reset(Object... mocks) {
        invocationHandler.getControl().reset();
        hasReplayed = false;
        hasVerified = false;
        return null;
    }
}
