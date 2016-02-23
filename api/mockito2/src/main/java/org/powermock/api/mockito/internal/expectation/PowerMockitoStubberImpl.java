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

import org.mockito.internal.stubbing.StubberImpl;
import org.mockito.invocation.MockHandler;
import org.mockito.stubbing.Stubber;
import org.powermock.api.mockito.expectation.PowerMockitoStubber;
import org.powermock.api.mockito.expectation.PrivatelyExpectedArguments;
import org.powermock.api.mockito.internal.invocation.MockitoMethodInvocationControl;
import org.powermock.core.MockRepository;
import org.powermock.reflect.Whitebox;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Extension of the standard Mocktio stubber implementation that also support
 * PowerMockito created mocks.
 */
public class PowerMockitoStubberImpl extends StubberImpl implements PowerMockitoStubber {

    /**
     * {@inheritDoc}
     */
    @Override
    public void when(Class<?> classMock) {
        MockitoMethodInvocationControl invocationControl = (MockitoMethodInvocationControl) MockRepository
                .getStaticMethodInvocationControl(classMock);
        addAnswersForStubbing(invocationControl);
    }

    /**
     * Supports PowerMockito mocks. If <code>mock</code> is not a PowerMockito
     * mock it will delegate to Mockito.
     *
     * @see Stubber#when(Object)
     */
    @Override
    public <T> T when(T instanceMock) {
        MockitoMethodInvocationControl invocationControl = (MockitoMethodInvocationControl) MockRepository
                .getInstanceMethodInvocationControl(instanceMock);
        final T returnValue;
        if (invocationControl == null) {
            returnValue = super.when(instanceMock);
        } else {
            addAnswersForStubbing(invocationControl);
            returnValue = instanceMock;
        }
        return returnValue;
    }

    @SuppressWarnings("unchecked")
    private void addAnswersForStubbing(MockitoMethodInvocationControl invocationControl) {
        final MockHandler mockHandler = invocationControl.getInvocationHandler().getHandler();
        final List list = Whitebox.getInternalState(this, List.class);
        try {
            Whitebox.invokeMethod(mockHandler, "setAnswersForStubbing", list);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> PrivatelyExpectedArguments when(T mock, Method method) throws Exception {
        assertNotNull(mock, "mock");
        assertNotNull(method, "Method");
        prepareForStubbing(mock);
        return new DefaultPrivatelyExpectedArguments(mock, method);
    }

    @Override
    public <T> void when(T mock, Object... arguments) throws Exception {
        assertNotNull(mock, "mock");
        prepareForStubbing(mock);
        Whitebox.invokeMethod(mock, arguments);
    }

    @Override
    public <T> void when(T mock, String methodToExpect, Object... arguments) throws Exception {
        assertNotNull(mock, "mock");
        assertNotNull(methodToExpect, "methodToExpect");
        prepareForStubbing(mock);
        Whitebox.invokeMethod(mock, methodToExpect, arguments);
    }

    @Override
    public <T> void when(Class<T> classMock, Object... arguments) throws Exception {
        assertNotNull(classMock, "classMock");
        when(classMock);
        Whitebox.invokeMethod(classMock, arguments);
    }

    @Override
    public <T> void when(Class<T> classMock, String methodToExpect, Object... parameters) throws Exception {
        assertNotNull(classMock, "classMock");
        assertNotNull(methodToExpect, "methodToExpect");
        when(classMock);
        Whitebox.invokeMethod(classMock, methodToExpect, parameters);
    }

    @Override
    public <T> PrivatelyExpectedArguments when(Class<T> classMock, Method method) throws Exception {
        assertNotNull(classMock, "classMock");
        assertNotNull(method, "Method");
        when(classMock);
        return new DefaultPrivatelyExpectedArguments(classMock, method);
    }

    private void assertNotNull(Object object, String name) {
        if (object == null) {
            throw new IllegalArgumentException(name + " cannot be null");
        }
    }

    private <T> void prepareForStubbing(T mock) {
        MockitoMethodInvocationControl invocationControl = (MockitoMethodInvocationControl) MockRepository.getInstanceMethodInvocationControl(mock);
        addAnswersForStubbing(invocationControl);
    }

}
