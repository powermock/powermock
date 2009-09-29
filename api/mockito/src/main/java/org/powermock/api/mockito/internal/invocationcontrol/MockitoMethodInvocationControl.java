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
package org.powermock.api.mockito.internal.invocationcontrol;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hamcrest.Matcher;
import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoAssertionError;
import org.mockito.internal.MockHandler;
import org.mockito.internal.creation.MethodInterceptorFilter;
import org.mockito.internal.debugging.Localized;
import org.mockito.internal.exceptions.base.StackTraceFilter;
import org.mockito.internal.invocation.Invocation;
import org.mockito.internal.invocation.realmethod.FilteredCGLIBProxyRealMethod;
import org.mockito.internal.invocation.realmethod.RealMethod;
import org.mockito.internal.matchers.MatchersPrinter;
import org.mockito.internal.progress.MockingProgress;
import org.mockito.internal.progress.SequenceNumber;
import org.mockito.internal.progress.ThreadSafeMockingProgress;
import org.mockito.internal.reporting.PrintSettings;
import org.mockito.internal.verification.api.VerificationMode;
import org.powermock.core.MockGateway;
import org.powermock.core.MockRepository;
import org.powermock.core.spi.MethodInvocationControl;
import org.powermock.reflect.Whitebox;

/**
 * A Mockito implementation of the {@link MethodInvocationControl} interface.
 */
public class MockitoMethodInvocationControl implements MethodInvocationControl {

    private final MethodInterceptorFilter methodInterceptorFilter;

    private final Set<Method> mockedMethods;
    private final Object delegator;

    /**
     * Creates a new instance.
     * 
     * @param methodInterceptionFilter
     *            The methodInterceptionFilter to be associated with this
     *            instance.
     * @param methodsToMock
     *            The methods that are mocked for this instance. If
     *            <code>methodsToMock</code> is null or empty, all methods for
     *            the <code>invocationHandler</code> are considered to be
     *            mocked.
     */
    public MockitoMethodInvocationControl(MethodInterceptorFilter methodInterceptionFilter, Method... methodsToMock) {
        this(methodInterceptionFilter, null, methodsToMock);
    }

    /**
     * Creates a new instance with a delegator. This delegator may be
     * <code>null</code> (if it is then no calls will be forwarded to this
     * instance). If a delegator exists (i.e. not null) all non-mocked calls
     * will be delegated to that instance.
     * 
     * @param methodInterceptionFilter
     *            The methodInterceptionFilter to be associated with this
     *            instance.
     * @param delegator
     *            If the user spies on an instance the original instance must be
     *            injected here.
     * @param methodsToMock
     *            The methods that are mocked for this instance. If
     *            <code>methodsToMock</code> is null or empty, all methods for
     *            the <code>invocationHandler</code> are considered to be
     *            mocked.
     */
    public MockitoMethodInvocationControl(MethodInterceptorFilter methodInterceptionFilter, Object delegator, Method... methodsToMock) {
        if (methodInterceptionFilter == null) {
            throw new IllegalArgumentException("Invocation Handler cannot be null.");
        }

        mockedMethods = toSet(methodsToMock);
        this.delegator = delegator;
        this.methodInterceptorFilter = methodInterceptionFilter;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isMocked(Method method) {
        return mockedMethods == null || (mockedMethods != null && mockedMethods.contains(method));
    }

    @SuppressWarnings("unchecked")
    private boolean isInVerificationMode() {
        try {
            MockingProgress progress = (MockingProgress) Whitebox.invokeMethod(ThreadSafeMockingProgress.class, "threadSafely");
            if (progress instanceof ThreadSafeMockingProgress) {
                ThreadLocal<MockingProgress> threadLocal = Whitebox.getInternalState(progress, ThreadLocal.class);
                return threadLocal.get() == null;
            } else {
                Localized<VerificationMode> verificationMode = Whitebox.getInternalState(progress, Localized.class);
                return verificationMode == null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

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
        if (hasDelegator() && !Modifier.isPrivate(method.getModifiers()) && !Modifier.isFinal(method.getModifiers())
                && hasBeenCaughtByMockitoProxy()) {
            returnValue = MockGateway.PROCEED;
        } else {
            returnValue = performIntercept(methodInterceptorFilter, obj, method, arguments);
            if (returnValue == null && isInVerificationMode()) {
                return MockGateway.SUPPRESS;
            }
        }
        return returnValue;
    }

    private boolean hasBeenCaughtByMockitoProxy() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceFilter filter = new StackTraceFilter();
        /*
         * We loop through all stack trace elements and see if it's "bad". Bad
         * means that the stack trance is cluttered with Mockito proxy
         * invocations which is why we know that the invocation has been caught
         * by the proxy if isBad returns true.
         */
        for (StackTraceElement stackTraceElement : stackTrace) {
            if (filter.isBad(stackTraceElement)) {
                return true;
            }
        }
        return false;
    }

    private Object performIntercept(MethodInterceptorFilter invocationHandler, Object interceptionObject, final Method method,
            Object[] arguments) throws Throwable {
        /*
         * Mockito 1.7 and 1.8.0 changes the CGLib Naming policy using
         * reflection if calling invocationHandler.intercept(..) directly which
         * will cause the invocation to fail since we're proxying the proxy. To
         * get around this we get the delegator using reflection and invoke its
         * intercept method directly instead and thus bypassing this and some
         * other stuff such as equals and hashcode checks. This is not safe and
         * should be regarded as hack! The best way would probably be to
         * persuade the Mockito guys to create some sort of hook-method to
         * replace the call to
         * "new CGLIBHacker().setMockitoNamingPolicy(methodProxy)" which is what
         * causing the trouble. If we could create our own CGLIBHacker which
         * uses the Whitebox hierarchy traverser mechanisms to set the field we
         * would be fine.
         */
        MockHandler<?> mockHandler = invocationHandler.getMockHandler();
        if (Whitebox.<Method> getInternalState(invocationHandler, "equalsMethod").equals(method)) {
            return Boolean.valueOf(interceptionObject == arguments[0]);
        } else if (Whitebox.<Method> getInternalState(invocationHandler, "hashCodeMethod").equals(method)) {
            return Whitebox.invokeMethod(invocationHandler, "hashCodeForMock", interceptionObject);
        }

        final FilteredCGLIBProxyRealMethod cglibProxyRealMethod = new FilteredCGLIBProxyRealMethod(new RealMethod() {
            public Object invoke(Object target, Object[] arguments) throws Throwable {
                /*
                 * Instruct the MockGateway to don't intercept the next call.
                 * The reason is that when Mockito is spying on objects it
                 * should call the "real method" (which is proxied by Mockito
                 * anyways) so that we don't end up in here one more time which
                 * causes infinite recursion.
                 */
                MockRepository.putAdditionalState(MockGateway.DONT_MOCK_NEXT_CALL, true);
                return method.invoke(target, arguments);
            }
        });
        // }
        Invocation invocation = new Invocation(interceptionObject, method, arguments, SequenceNumber.next(), cglibProxyRealMethod) {

            /**
             * We need to override this method because normally the String
             * "method" is assembled by calling the "qualifiedName" method but
             * this is not possible in our case. The reason is that the
             * qualifiedName method does
             * 
             * <pre>
             * new MockUtil().getMockName(mock)
             * </pre>
             * 
             * which later will call the "isMockitoMock" method which will
             * return false and an exception will be thrown. The reason why
             * "isMockitoMock" returns false is that the mock is not created by
             * the Mockito CGLib Enhancer in case of static methods.
             */
            @SuppressWarnings("unchecked")
            @Override
            protected String toString(List<Matcher> matchers, PrintSettings printSettings) {
                MatchersPrinter matchersPrinter = new MatchersPrinter();
                String method = Whitebox.getType(getMock()).getName() + "." + getMethodName();
                String invocation = method + matchersPrinter.getArgumentsLine(matchers, printSettings);
                if (printSettings.isMultiline()
                        || (!matchers.isEmpty() && invocation.length() > Whitebox.<Integer> getInternalState(Invocation.class,
                                "MAX_LINE_LENGTH"))) {
                    return method + matchersPrinter.getArgumentsBlock(matchers, printSettings);
                } else {
                    return invocation;
                }
            }
        };
        try {
            return mockHandler.handle(invocation);
        } catch (MockitoAssertionError e) {
            InvocationControlAssertionError.updateErrorMessageForMethodInvocation(e);
            throw e;
        }
    }

    public Object replay(Object... mocks) {
        throw new IllegalStateException("Internal error: No such thing as replay exists in Mockito.");
    }

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
            methodInterceptorFilter.getMockHandler().verifyNoMoreInteractions();
        } catch (MockitoAssertionError e) {
            InvocationControlAssertionError.updateErrorMessageForVerifyNoMoreInteractions(e);
            throw e;
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