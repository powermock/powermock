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
package org.powermock.api.mockito.internal.invocation;

import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoAssertionError;
import org.mockito.exceptions.misusing.NotAMockException;
import org.mockito.internal.InternalMockHandler;
import org.mockito.internal.creation.DelegatingMethod;
import org.mockito.internal.debugging.Localized;
import org.mockito.internal.debugging.LocationImpl;
import org.mockito.internal.exceptions.stacktrace.StackTraceFilter;
import org.mockito.internal.invocation.InvocationImpl;
import org.mockito.internal.invocation.MatchersBinder;
import org.mockito.internal.invocation.realmethod.CleanTraceRealMethod;
import org.mockito.internal.invocation.realmethod.RealMethod;
import org.mockito.internal.progress.MockingProgress;
import org.mockito.internal.progress.SequenceNumber;
import org.mockito.internal.progress.ThreadSafeMockingProgress;
import org.mockito.internal.stubbing.InvocationContainer;
import org.mockito.internal.verification.VerificationDataImpl;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.invocation.Invocation;
import org.mockito.invocation.MockHandler;
import org.mockito.verification.VerificationMode;
import org.powermock.api.mockito.internal.verification.StaticMockAwareVerificationMode;
import org.powermock.api.mockito.repackaged.MethodInterceptorFilter;
import org.powermock.api.support.SafeExceptionRethrower;
import org.powermock.core.MockGateway;
import org.powermock.core.MockRepository;
import org.powermock.core.spi.MethodInvocationControl;
import org.powermock.reflect.Whitebox;
import org.powermock.reflect.internal.WhiteboxImpl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * A Mockito implementation of the {@link MethodInvocationControl} interface.
 */
public class MockitoMethodInvocationControl implements MethodInvocationControl {

    private final MethodInterceptorFilter methodInterceptorFilter;

    private final Set<Method> mockedMethods;
    private final Object delegator;

    /*
     * This field is required to fix the problem was that finalize methods could be called before an expected method
     * because the GC kicked in too soon since now object held a reference to the mock.
     * Even if it is not used in class we still need keep reference to mock object to prevent calling `finalize`
     * method.
     */

    private final Object mockInstance;

    /**
     * Creates a new instance.
     *
     * @param methodInterceptionFilter
     *            The methodInterceptionFilter to be associated with this
     *            instance.
     * @param mockInstance
     *          The actual mock instance. May be <code>null</code>. Even
     *            though the mock instance may not be used it's needed to keep a
     *            reference to this object otherwise it may be garbage collected
     *            in some situations. For example when mocking static methods we
     *            don't return the mock object and thus it will be garbage
     *            collected (and thus the finalize method will be invoked which
     *            will be caught by the proxy and the test will fail because we
     *            haven't setup expectations for this method) because then that
     *            object has no reference. In order to avoid this we keep a
     *            reference to this instance here.
     * @param methodsToMock
     *            The methods that are mocked for this instance. If
     *            <code>methodsToMock</code> is null or empty, all methods for
     *            the <code>invocationHandler</code> are considered to be
     *            mocked.
     */
    public MockitoMethodInvocationControl(MethodInterceptorFilter methodInterceptionFilter, Object mockInstance,  Method... methodsToMock) {
        this(methodInterceptionFilter,  null, mockInstance,  methodsToMock);
    }

    /**
     * Creates a new instance with a delegator. This delegator may be
     * <code>null</code> (if it is then no calls will be forwarded to this
     * instance). If a delegator exists (i.e. not null) all non-mocked calls
     * will be delegated to that instance.
     *  @param methodInterceptionFilter
     *            The methodInterceptionFilter to be associated with this
     *            instance.
     * @param delegator
     *            If the user spies on an instance the original instance must be
     *            injected here.
     * @param mockInstance
     *            The actual mock instance. May be <code>null</code>. Even
     *            though the mock instance may not be used it's needed to keep a
     *            reference to this object otherwise it may be garbage collected
     *            in some situations. For example when mocking static methods we
     *            don't return the mock object and thus it will be garbage
     *            collected (and thus the finalize method will be invoked which
     *            will be caught by the proxy and the test will fail because we
     *            haven't setup expectations for this method) because then that
     *            object has no reference. In order to avoid this we keep a
     *            reference to this instance here.
     * @param methodsToMock
     *            The methods that are mocked for this instance. If
     *            <code>methodsToMock</code> is null or empty, all methods for
     *            the <code>invocationHandler</code> are considered to be
     */
    public MockitoMethodInvocationControl(MethodInterceptorFilter methodInterceptionFilter, Object delegator,
                                          Object mockInstance, Method... methodsToMock) {
        if (methodInterceptionFilter == null) {
            throw new IllegalArgumentException("Invocation Handler cannot be null.");
        }

        this.mockedMethods = toSet(methodsToMock);
        this.mockInstance = mockInstance;
        this.delegator = delegator;
        this.methodInterceptorFilter = methodInterceptionFilter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isMocked(Method method) {
        return mockedMethods == null || (mockedMethods.contains(method));
    }

    private boolean isInVerificationMode() {
        return getVerificationMode() != null;
    }

    private VerificationMode getVerificationMode() {
        try {
            MockingProgress progress = Whitebox.invokeMethod(ThreadSafeMockingProgress.class,
                    "threadSafely");
            return getVerificationModeFromMockProgress(progress);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private VerificationMode getVerificationModeFromMockProgress(MockingProgress mockingProgress) {
        if (mockingProgress == null) {
            return null;
        }
        if (mockingProgress instanceof ThreadSafeMockingProgress) {
            ThreadLocal<MockingProgress> threadLocal = Whitebox.getInternalState(mockingProgress, ThreadLocal.class);
            return getVerificationModeFromMockProgress(threadLocal.get());
        } else {
            Localized<VerificationMode> verificationMode = Whitebox.getInternalState(mockingProgress, Localized.class);
            return verificationMode == null ? null : verificationMode.getObject();
        }
    }

    @Override
    public Object invoke(final Object obj, final Method method, final Object[] arguments) throws Throwable {
        /*
           * If we come here and it means that the class has been modified by
           * PowerMock. If this handler has a delegator (i.e. is in spy mode in
           * the current implementation) and it has been caught by the Mockito
           * proxy before our Mockgateway we need to know if the method is private
           * or not. Because if the previously described preconditions are met and
           * the method is not private it means that Mockito has already processed
           * the method invocation and we should NOT delegate the call to Mockito
           * again (thus we return proceed). If we would do that Mockito will
           * receive multiple method invocations to proxy for each method
           * invocation. For privately spied methods Mockito haven't received the
           * invocation and thus we should delegate the call to the Mockito proxy.
           */
        final Object returnValue;
        final int methodModifiers = method.getModifiers();
        if (hasDelegator() && !Modifier.isPrivate(methodModifiers) && !Modifier.isFinal(methodModifiers)
                && !Modifier.isStatic(methodModifiers) && hasBeenCaughtByMockitoProxy()) {
            returnValue = MockGateway.PROCEED;
        } else {
            boolean inVerificationMode = isInVerificationMode();
            if (WhiteboxImpl.isClass(obj) && inVerificationMode) {
                handleStaticVerification((Class<?>) obj);
            }
            returnValue = performIntercept(methodInterceptorFilter, obj, method, arguments);
            if (returnValue == null) {
                return MockGateway.SUPPRESS;
            }
        }
        return returnValue;
    }

    private void handleStaticVerification(Class<?> cls) {
        VerificationMode verificationMode = getVerificationMode();
        if (verificationMode instanceof StaticMockAwareVerificationMode) {
            ((StaticMockAwareVerificationMode) verificationMode).setClassMock(cls);
        }
    }

    private boolean hasBeenCaughtByMockitoProxy() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceFilter filter = new StackTraceFilter();
        /*
        * We filter the stack-trace to check if "Mockito" exists as a stack trace element. (The filter method
        * remove all Mocktio stack trace elements). If the filtered stack trace length is not equal to the original stack trace length
        * this means that the call has been caught by Mockito.
        */
        final StackTraceElement[] filteredStackTrace = filter.filter(stackTrace, true);
        return filteredStackTrace.length != stackTrace.length;
    }

    private Object performIntercept(MethodInterceptorFilter invocationHandler, final Object interceptionObject,
                                    final Method method, Object[] arguments) throws Throwable {
        MockHandler mockHandler = invocationHandler.getHandler();

        final CleanTraceRealMethod cglibProxyRealMethod = new CleanTraceRealMethod(new RealMethod() {
            private static final long serialVersionUID = 4564320968038564170L;

            @Override
            public Object invoke(Object target, Object[] arguments) throws Throwable {
                /*
                     * Instruct the MockGateway to don't intercept the next call.
                     * The reason is that when Mockito is spying on objects it
                     * should call the "real method" (which is proxied by Mockito
                     * anyways) so that we don't end up in here one more time which
                     * causes infinite recursion. This should not be done if the
                     * interceptionObject is a final system class because these are
                     * never caught by the Mockito proxy.
                     */
                final Class<?> type = Whitebox.getType(interceptionObject);
                final boolean isFinalSystemClass = type.getName().startsWith("java.") && Modifier.isFinal(type.getModifiers());
                if (!isFinalSystemClass) {
                    MockRepository.putAdditionalState(MockGateway.DONT_MOCK_NEXT_CALL, true);
                }
                try {
                    return method.invoke(target, arguments);
                } catch (InvocationTargetException e) {
                    SafeExceptionRethrower.safeRethrow(e.getCause());
                }
                return null;
            }
        });

        Invocation invocation = new InvocationImpl(
                interceptionObject,
                new DelegatingMethod(method),
                arguments,
                SequenceNumber.next(),
                cglibProxyRealMethod,
                new LocationImpl()) {
            private static final long serialVersionUID = -3679957412502758558L;

            @Override
            public String toString() {
                return new ToStringGenerator().generate(getMock(), getMethod(), getArguments());
            }
        };

        try {
            return replaceMatchersBinderIfNeeded(mockHandler).handle(invocation);
        } catch (NotAMockException e) {
            if(invocation.getMock().getClass().getName().startsWith("java.") &&  MockRepository.getInstanceMethodInvocationControl(invocation.getMock()) != null) {
                return invocation.callRealMethod();
            } else {
                throw e;
            }
        } catch (MockitoAssertionError e) {
            InvocationControlAssertionError.updateErrorMessageForMethodInvocation(e);
            throw e;
        }
    }

    private MockHandler replaceMatchersBinderIfNeeded(MockHandler mockHandler) {
        if(!Whitebox.getFieldsOfType(mockHandler, MatchersBinder.class).isEmpty()) {
            Whitebox.setInternalState(mockHandler, new PowerMockMatchersBinder());
        } else if(!Whitebox.getFieldsOfType(mockHandler, InternalMockHandler.class).isEmpty()) {
            final MockHandler internalMockHandler = Whitebox.getInternalState(mockHandler, MockHandler.class);
            return replaceMatchersBinderIfNeeded(internalMockHandler);
        }
        return mockHandler;
    }

    @Override
    public Object replay(Object... mocks) {
        throw new IllegalStateException("Internal error: No such thing as replay exists in Mockito.");
    }

    @Override
    public Object reset(Object... mocks) {
        throw new IllegalStateException("Internal error: No such thing as reset exists in Mockito.");
    }

    @Override
    public Object verify(Object... mocks) {
        if (mocks == null || mocks.length != 1) {
            throw new IllegalArgumentException("Must supply one mock to the verify method.");
        }
        return Mockito.verify(mocks[0]);
    }

    public void verifyNoMoreInteractions() {
        try {
            final MockHandler mockHandler = methodInterceptorFilter.getHandler();
            if (mockHandler instanceof MockHandler) {
                InvocationContainer invocationContainer = Whitebox.invokeMethod(mockHandler, "getInvocationContainer");
                VerificationDataImpl data = new VerificationDataImpl(invocationContainer, null);
                VerificationModeFactory.noMoreInteractions().verify(data);
            } else {
                throw new RuntimeException(
                        "Cannot perform verifyNoMoreInteractions because of unknown mockhandler type "
                                + mockHandler.getClass());
            }
        } catch (MockitoAssertionError e) {
            InvocationControlAssertionError.updateErrorMessageForVerifyNoMoreInteractions(e);
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("PowerMock internal error",e);
        }
    }

    private Set<Method> toSet(Method... methods) {
        return methods == null ? null : new HashSet<Method>(Arrays.asList(methods));
    }

    private boolean hasDelegator() {
        return delegator != null;
    }

    public MethodInterceptorFilter getInvocationHandler() {
        return methodInterceptorFilter;
    }
}
