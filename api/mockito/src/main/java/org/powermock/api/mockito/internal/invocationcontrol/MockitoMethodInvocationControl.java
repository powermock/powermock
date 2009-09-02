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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;

import org.hamcrest.Matcher;
import org.mockito.Mockito;
import org.mockito.cglib.proxy.MethodProxy;
import org.mockito.internal.MockHandler;
import org.mockito.internal.creation.MethodInterceptorFilter;
import org.mockito.internal.debugging.Localized;
import org.mockito.internal.invocation.Invocation;
import org.mockito.internal.invocation.realmethod.FilteredCGLIBProxyRealMethod;
import org.mockito.internal.invocation.realmethod.RealMethod;
import org.mockito.internal.matchers.MatchersPrinter;
import org.mockito.internal.progress.MockingProgress;
import org.mockito.internal.progress.SequenceNumber;
import org.mockito.internal.progress.ThreadSafeMockingProgress;
import org.mockito.internal.reporting.PrintSettings;
import org.mockito.internal.util.MockUtil;
import org.mockito.internal.verification.api.VerificationMode;
import org.powermock.core.MockGateway;
import org.powermock.core.spi.MethodInvocationControl;
import org.powermock.reflect.Whitebox;

/**
 * A Mockito implementation of the {@link MethodInvocationControl} interface.
 */
public class MockitoMethodInvocationControl<T> implements MethodInvocationControl {

	private final MethodInterceptorFilter invocationHandler;

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
	public MockitoMethodInvocationControl(MethodInterceptorFilter invocationHandler, Method... methodsToMock) {
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
	public MockitoMethodInvocationControl(MethodInterceptorFilter invocationHandler, T delegator, Method... methodsToMock) {
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
		Object interceptionObject = obj;
		// If the method is static we should get the substitution mock.

		final Object returnValue = performIntercept(invocationHandler, interceptionObject, method, arguments);
		if (returnValue == null && isInVerificationMode()) {
			return MockGateway.SUPPRESS;
		}
		return returnValue;
	}

	private Object performIntercept(MethodInterceptorFilter invocationHandler, Object interceptionObject, final Method method, Object[] arguments)
			throws Throwable {
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

		final FilteredCGLIBProxyRealMethod cglibProxyRealMethod;
		if (isMockitoSpy()) {
			cglibProxyRealMethod = new FilteredCGLIBProxyRealMethod(getMethodProxy(method));
		} else {
			cglibProxyRealMethod = new FilteredCGLIBProxyRealMethod(new RealMethod() {
				private MockUtil mockUtil = new MockUtil();

				public Object invoke(Object target, Object[] arguments) throws Throwable {
					if (mockUtil.isMock(target)) {
						return method.invoke(target, arguments);
					} else {
						System.out.println(method);
						System.out.println(target);
						return method.invoke(target, arguments);
					}
				}
			});
		}
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
						|| (!matchers.isEmpty() && invocation.length() > Whitebox.<Integer> getInternalState(Invocation.class, "MAX_LINE_LENGTH"))) {
					return method + matchersPrinter.getArgumentsBlock(matchers, printSettings);
				} else {
					return invocation;
				}
			}
		};
		return mockHandler.handle(invocation);
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