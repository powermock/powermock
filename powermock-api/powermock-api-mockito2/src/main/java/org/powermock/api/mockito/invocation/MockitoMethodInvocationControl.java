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
package org.powermock.api.mockito.invocation;

import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoAssertionError;
import org.mockito.internal.debugging.Localized;
import org.mockito.internal.exceptions.stacktrace.StackTraceFilter;
import org.mockito.internal.progress.MockingProgress;
import org.mockito.internal.stubbing.InvocationContainerImpl;
import org.mockito.internal.verification.VerificationDataImpl;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.verification.VerificationMode;
import org.powermock.api.mockito.internal.invocation.InvocationControlAssertionError;
import org.powermock.core.MockGateway;
import org.powermock.core.spi.MethodInvocationControl;
import org.powermock.reflect.Whitebox;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * A Mockito implementation of the {@link MethodInvocationControl} interface.
 */
public class MockitoMethodInvocationControl<T> implements MethodInvocationControl {
    
    private final Set<Method> mockedMethods;
    private final Object delegator;
    private final MockHandlerAdaptor<T> mockHandlerAdaptor;
    
    /**
     * Creates a new instance with a delegator. This delegator may be
     * {@code null} (if it is then no calls will be forwarded to this
     * instance). If a delegator exists (i.e. not null) all non-mocked calls
     * will be delegated to that instance.
     *
     * @param delegator     If the user spies on an instance the original instance must be
     *                      injected here.
     * @param mockInstance  The actual mock instance. May be {@code null}. Even
     *                      though the mock instance may not be used it's needed to keep a
     *                      reference to this object otherwise it may be garbage collected
     *                      in some situations. For example when mocking static methods we
     *                      don't return the mock object and thus it will be garbage
     *                      collected (and thus the finalize method will be invoked which
     *                      will be caught by the proxy and the test will fail because we
     *                      haven't setup expectations for this method) because then that
     *                      object has no reference. In order to avoid this we keep a
     *                      reference to this instance here.
     * @param methodsToMock The methods that are mocked for this instance. If
     *                      {@code methodsToMock} is null or empty, all methods for
     *                      the {@code invocationHandler} are considered to be
     */
    public MockitoMethodInvocationControl(Object delegator, T mockInstance, Method... methodsToMock) {
        this.mockHandlerAdaptor = new MockHandlerAdaptor<T>(mockInstance);
        this.mockedMethods = toSet(methodsToMock);
        this.delegator = delegator;
    }
    
    @Override
    public boolean isMocked(Method method) {
        return mockedMethods == null || (mockedMethods.contains(method));
    }
    
    @Override
    public Object invoke(final Object mock, final Method method, final Object[] arguments) throws Throwable {
        /*
           * If we come here and it means that the class has been modified by
           * PowerMock. If this handler has a delegator (i.e. is in spy mode in
           * the current implementation) and it has been caught by the Mockito
           * proxy before our MockGateway we need to know if the method is private
           * or not. Because if the previously described preconditions are met and
           * the method is not private it means that Mockito has already processed
           * the method invocation and we should NOT delegate the call to Mockito
           * again (thus we return proceed). If we would do that Mockito will
           * receive multiple method invocations to proxy for each method
           * invocation. For privately spied methods Mockito haven't received the
           * invocation and thus we should delegate the call to the Mockito proxy.
           */
        final Object returnValue;
        if (isCanBeHandledByMockito(method) && hasBeenCaughtByMockitoProxy()) {
            returnValue = MockGateway.PROCEED;
        } else {
            returnValue = mockHandlerAdaptor.performIntercept(mock, method, arguments);
            if (returnValue == null) {
                return MockGateway.SUPPRESS;
            }
        }
        return returnValue;
    }
    
    private boolean isCanBeHandledByMockito(final Method method) {
        final int modifiers = method.getModifiers();
        return hasDelegator() && !Modifier.isPrivate(modifiers) && !Modifier.isFinal(modifiers) && !Modifier.isStatic(modifiers);
    }
    
    private boolean hasBeenCaughtByMockitoProxy() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceFilter filter = new StackTraceFilter();
        /*
        * We filter the stack-trace to check if "Mockito" exists as a stack trace element. (The filter method
        * remove all Mockito stack trace elements). If the filtered stack trace length is not equal to the original stack trace length
        * this means that the call has been caught by Mockito.
        */
        final StackTraceElement[] filteredStackTrace = filter.filter(stackTrace, true);
        return filteredStackTrace.length != stackTrace.length;
    }
    
    @Override
    public Object replay(Object... mocks) {
        throw new IllegalStateException("Internal error: No such thing as replay exists in Mockito.");
    }
    
    @Override
    public Object reset(Object... mocks) {
        throw new IllegalStateException("Internal error: No such thing as reset exists in Mockito.");
    }
    
    public Object verify(Object... mocks) {
        if (mocks == null || mocks.length != 1) {
            throw new IllegalArgumentException("Must supply one mock to the verify method.");
        }
        return Mockito.verify(mocks[0]);
    }
    
    public void verifyNoMoreInteractions() {
        try {
            InvocationContainerImpl invocationContainer = (InvocationContainerImpl) mockHandlerAdaptor.getInvocationContainer();
            VerificationDataImpl data = new VerificationDataImpl(invocationContainer, null);
            VerificationModeFactory.noMoreInteractions().verify(data);
        } catch (MockitoAssertionError e) {
            InvocationControlAssertionError.updateErrorMessageForVerifyNoMoreInteractions(e);
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("PowerMock internal error", e);
        }
    }
    
    private Set<Method> toSet(Method... methods) {
        return methods == null ? null : new HashSet<Method>(Arrays.asList(methods));
    }
    
    private boolean hasDelegator() {
        return delegator != null;
    }
    
    public MockHandlerAdaptor<T> getMockHandlerAdaptor() {
        return mockHandlerAdaptor;
    }
}
