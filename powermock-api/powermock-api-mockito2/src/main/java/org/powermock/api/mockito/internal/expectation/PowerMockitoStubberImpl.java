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

import org.mockito.stubbing.Answer;
import org.mockito.stubbing.Stubber;
import org.powermock.api.mockito.expectation.PowerMockitoStubber;
import org.powermock.api.mockito.expectation.PrivatelyExpectedArguments;
import org.powermock.api.mockito.invocation.MockitoMethodInvocationControl;
import org.powermock.core.MockRepository;
import org.powermock.reflect.Whitebox;

import java.lang.reflect.Method;

/**
 * Extension of the standard Mocktio stubber implementation that also support
 * PowerMockito created mocks.
 */
public class PowerMockitoStubberImpl implements PowerMockitoStubber, Stubber {
    
    private final Stubber stubber;
    
    public PowerMockitoStubberImpl(final Stubber stubber) {this.stubber = stubber;}
    
    @Override
    public <T> T when(final T instanceMock) {
        final MockitoMethodInvocationControl invocationControl = (MockitoMethodInvocationControl) MockRepository.getInstanceMethodInvocationControl(instanceMock);
        final T returnValue;
        if (invocationControl == null) {
            returnValue = stubber.when(instanceMock);
        } else {
            final Object mock = invocationControl.getMockHandlerAdaptor().getMock();
            stubber.when(mock);
            returnValue = instanceMock;
        }
        return returnValue;
    }
    
    @Override
    public Stubber doThrow(final Throwable... toBeThrown) {return stubber.doThrow(toBeThrown);}
    
    @Override
    public Stubber doThrow(final Class<? extends Throwable> toBeThrown) {return stubber.doThrow(toBeThrown);}
    
    @Override
    public Stubber doThrow(final Class<? extends Throwable> toBeThrown, final Class<? extends Throwable>[] nextToBeThrown) {return stubber.doThrow(toBeThrown, nextToBeThrown);}
    
    @Override
    public Stubber doAnswer(final Answer answer) {return stubber.doAnswer(answer);}
    
    @Override
    public Stubber doNothing() {return stubber.doNothing();}
    
    @Override
    public Stubber doReturn(final Object toBeReturned) {return stubber.doReturn(toBeReturned);}
    
    @Override
    public Stubber doReturn(final Object toBeReturned,
                            final Object... nextToBeReturned) {return stubber.doReturn(toBeReturned, nextToBeReturned);}
    
    @Override
    public Stubber doCallRealMethod() {return stubber.doCallRealMethod();}
    
    @Override
    public void when(Class<?> classMock) {
        MockitoMethodInvocationControl invocationControl = (MockitoMethodInvocationControl) MockRepository.getStaticMethodInvocationControl(classMock);
        final Object mock = invocationControl.getMockHandlerAdaptor().getMock();
        stubber.when(mock);
    }
    
    @Override
    public <T> PrivatelyExpectedArguments when(T mock, Method method) throws Exception {
        assertNotNull(mock, "mock");
        assertNotNull(method, "Method");
        this.when(mock);
        return new DefaultPrivatelyExpectedArguments(mock, method);
    }
    
    @Override
    public <T> void when(T mock, Object... arguments) throws Exception {
        assertNotNull(mock, "mock");
        this.when(mock);
        Whitebox.invokeMethod(mock, arguments);
    }
    
    @Override
    public <T> void when(T mock, String methodToExpect, Object... arguments) throws Exception {
        assertNotNull(mock, "mock");
        assertNotNull(methodToExpect, "methodToExpect");
        this.when(mock);
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
}
