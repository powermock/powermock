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

import java.lang.reflect.Method;
import java.util.Set;

import org.easymock.internal.MockInvocationHandler;
import org.easymock.internal.MocksControl.MockType;
import org.powermock.core.spi.MethodInvocationControl;
import org.powermock.reflect.internal.WhiteboxImpl;

/**
 * The default implementation of the {@link MethodInvocationControl} interface.
 */
public class EasyMockMethodInvocationControl<T> implements MethodInvocationControl {

	private MockInvocationHandler invocationHandler;

	private Set<Method> mockedMethods;

	@SuppressWarnings("unused")
	private T mockInstance;

	/**
	 * Initializes internal state.
	 * 
	 * @param invocationHandler
	 *            The mock invocation handler to be associated with this
	 *            instance.
	 * @param methodsToMock
	 *            The methods that are mocked for this instance. If
	 *            <code>methodsToMock</code> is null all methods for the
	 *            <code>invocationHandler</code> are considered to be mocked.
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
	 * @param invocationHandler
	 *            The mock invocation handler to be associated with this
	 *            instance.
	 * @param methodsToMock
	 *            The methods that are mocked for this instance. If
	 *            <code>methodsToMock</code> is null all methods for the
	 *            <code>invocationHandler</code> are considered to be mocked.
	 */
	public EasyMockMethodInvocationControl(MockInvocationHandler invocationHandler, Set<Method> methodsToMock) {
		this(invocationHandler, methodsToMock, null);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isMocked(Method method) {
		return mockedMethods == null || (mockedMethods != null && mockedMethods.contains(method));
	}

	public Object invoke(Object proxy, Method method, Object[] arguments) throws Throwable {
		return invocationHandler.invoke(proxy, method, arguments);
	}

	public MockType getMockType() {
		return WhiteboxImpl.getInternalState(invocationHandler.getControl(), MockType.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public Object replay(Object... mocks) {
		invocationHandler.getControl().replay();
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object verify(Object... mocks) {
		invocationHandler.getControl().verify();
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object reset(Object... mocks) {
		invocationHandler.getControl().reset();
		return null;
	}
}
