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

import org.mockito.Mockito;
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

	/**
	 * Initializes internal state.
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
		if (invocationHandler == null) {
			throw new IllegalArgumentException("Invocation Handler cannot be null.");
		}

		if (methodsToMock == null) {
			mockedMethods = new HashSet<Method>();
		} else {
			mockedMethods = toSet(methodsToMock);
		}

		this.invocationHandler = invocationHandler;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isMocked(Method method) {
		return (mockedMethods.isEmpty() || mockedMethods.contains(method));
	}

	private boolean isInVerificationMode() {
		try {
			MockingProgress internalState = (MockingProgress) Whitebox.invokeMethod(ThreadSafeMockingProgress.class, "threadSafely");
			return Whitebox.getInternalState(internalState, VerificationMode.class) == null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public Object invoke(Object obj, Method method, Object[] arguments) throws Throwable {
		Object interceptionObject = obj;
		// If the method is static we should get the substitution mock.
		if (Modifier.isStatic(method.getModifiers())) {
			final InvocationSubstitute<?> substituteObject = (InvocationSubstitute<?>) Whitebox.getInternalState(invocationHandler.getDelegate(),
					"instance");
			if (substituteObject != null) {
				interceptionObject = substituteObject;
			}
		}

		Object returnValue = invocationHandler.intercept(interceptionObject, method, arguments, null);
		if (returnValue == null && isInVerificationMode()) {
			return MockGateway.SUPPRESS;
		}
		return returnValue;
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
		Set<Method> set = new HashSet<Method>();
		if (methods.length > 0) {
			set.addAll(Arrays.asList(methods));
		}
		return set;
	}
}