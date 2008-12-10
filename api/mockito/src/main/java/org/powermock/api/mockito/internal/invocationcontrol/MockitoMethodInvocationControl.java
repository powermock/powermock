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
import java.util.Set;

import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;

import org.mockito.Mockito;
import org.mockito.cglib.proxy.MethodProxy;
import org.mockito.internal.MockHandler;
import org.mockito.internal.creation.MethodInterceptorFilter;
import org.mockito.internal.progress.MockingProgress;
import org.mockito.internal.progress.ThreadSafeMockingProgress;
import org.mockito.internal.verification.api.VerificationMode;
import org.powermock.core.MockGateway;
import org.powermock.core.spi.MethodInvocationControl;
import org.powermock.core.spi.support.InvocationSubstitute;
import org.powermock.reflect.Whitebox;

/**
 * A Mockito implementation of the {@link MethodInvocationControl} interface.
 */
public class MockitoMethodInvocationControl<T> implements MethodInvocationControl {

	private final MethodInterceptorFilter<MockHandler<T>> invocationHandler;

	private final Set<Method> mockedMethods;

	private final T delegator;

	/**
	 * Creates a new instance.
	 * 
	 * @param invocationHandler
	 *            The mock invocation handler to be associated with this
	 *            instance.
	 * @param methodsToMock
	 *            The methods that are mocked for this instance. If
	 *            <code>methodsToMock</code> is null or empty, all methods for
	 *            the <code>invocationHandler</code> are considered to be
	 *            mocked.
	 */
	public MockitoMethodInvocationControl(MethodInterceptorFilter<MockHandler<T>> invocationHandler, Method... methodsToMock) {
		this(invocationHandler, null, methodsToMock);
	}

	/**
	 * Creates a new instance with a delegator. This delegator may be
	 * <code>null</code> (if it is then no calls will be forwarded to this
	 * instance). If a delegator exists (i.e. not null) all non-mocked calls
	 * will be delegated to that instance.
	 * 
	 * @param invocationHandler
	 *            The mock invocation handler to be associated with this
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
	public MockitoMethodInvocationControl(MethodInterceptorFilter<MockHandler<T>> invocationHandler, T delegator, Method... methodsToMock) {

		if (invocationHandler == null) {
			throw new IllegalArgumentException("Invocation Handler cannot be null.");
		}

		mockedMethods = toSet(methodsToMock);

		this.delegator = delegator;
		this.invocationHandler = invocationHandler;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isMocked(Method method) {
		return mockedMethods == null || (mockedMethods != null && mockedMethods.contains(method));
	}

	private boolean isInVerificationMode() {
		try {
			MockingProgress internalState = (MockingProgress) Whitebox.invokeMethod(ThreadSafeMockingProgress.class, "threadSafely");
			return Whitebox.getInternalState(internalState, VerificationMode.class) == null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public Object invoke(final Object obj, final Method method, final Object[] arguments) throws Throwable {
		Object interceptionObject = obj;
		// If the method is static we should get the substitution mock.
		if (Modifier.isStatic(method.getModifiers()) && isMocked(method)) {
			final InvocationSubstitute<?> substituteObject = (InvocationSubstitute<?>) Whitebox.getInternalState(invocationHandler.getDelegate(),
					"instance");
			if (substituteObject != null) {
				interceptionObject = substituteObject;
			}
		}
		final Object returnValue = invocationHandler.intercept(interceptionObject, method, arguments, getMethodProxy(method));
		if (returnValue == null && isInVerificationMode()) {
			return MockGateway.SUPPRESS;
		}
		return returnValue;
	}

	/**
	 * Get a method proxy if needed. This is needed when this method invocation
	 * control is in spy mode (i.e. the {@link #delegator} is set). What Mockito
	 * does in its
	 * {@link MockHandler#intercept(Object, Method, Object[], MethodProxy)}
	 * method is to invoke a MethodProxy that in its turn invoke the original
	 * method. Since we don't have access to this method proxy we create a
	 * Javassist proxy for the MethodProxy class. When the invoke method is
	 * called we simply invoke the method on the original delegator.
	 * <p>
	 * The reason why we're not using a CgLib proxy is because the
	 * {@link MethodProxy} has a private constructor and CgLib cannot proxy
	 * classes with a private constructor (but Javassist can). However I failed
	 * to instantiate the generated Javaassist Proxy Class using reflection (got
	 * a exception) so instead we're using {@link Whitebox#newInstance(Class)}
	 * to create an instance of the class using Objenisis (i.e. the constructor
	 * is never invoked which is actually good).
	 */
	@SuppressWarnings("unchecked")
	private MethodProxy getMethodProxy(final Method method) {
		if (isMockitoSpy()) {
			ProxyFactory f = new ProxyFactory();
			f.setSuperclass(MethodProxy.class);
			MethodHandler mi = new MethodHandler() {
				public Object invoke(Object self, Method m, Method proceed, Object[] args) throws Throwable {
					final Object[] realArguments = (Object[]) args[1];
					// execute the original method.
					final Object invoke = method.invoke(delegator, realArguments);
					return invoke;
				}
			};
			f.setFilter(new MethodFilter() {
				public boolean isHandled(Method m) {
					return !m.getName().equals("finalize");
				}
			});
			Class<MethodProxy> c = f.createClass();
			final MethodProxy methodProxy = Whitebox.newInstance(c);
			((ProxyObject) methodProxy).setHandler(mi);
			return methodProxy;
		}
		return null;
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

	private Set<Method> toSet(Method... methods) {
		return methods == null ? null : new HashSet<Method>(Arrays.asList(methods));
	}

	private boolean isMockitoSpy() {
		return delegator != null;
	}
}