package org.powermock.api.mockito;

import static org.mockito.Mockito.times;

import java.lang.reflect.Method;

import org.mockito.Mockito;
import org.mockito.exceptions.misusing.NotAMockException;
import org.mockito.exceptions.misusing.NullInsteadOfMockException;
import org.mockito.internal.MockHandler;
import org.mockito.internal.creation.MethodInterceptorFilter;
import org.mockito.internal.creation.jmock.ClassImposterizer;
import org.mockito.internal.invocation.MatchersBinder;
import org.mockito.internal.progress.MockingProgress;
import org.mockito.internal.verification.api.VerificationMode;
import org.powermock.api.mockito.internal.invocationcontrol.MockitoMethodInvocationControl;
import org.powermock.api.mockito.internal.proxyframework.CgLibProxyFramework;
import org.powermock.core.MockRepository;
import org.powermock.core.spi.MethodInvocationControl;
import org.powermock.core.spi.support.InvocationSubstitute;
import org.powermock.reflect.Whitebox;

/**
 * PowerMockito extends Mockito functionality with several new features such as
 * mocking static and private methods, mocking new instances and more. Use
 * PowerMock instead of Mockito where applicable.
 */
public class PowerMockito {

	static {
		CgLibProxyFramework.registerProxyFramework();
	}

	/**
	 * Enable static mocking for a class.
	 * 
	 * @param type
	 *            the class to enable static mocking
	 * @param methods
	 *            optionally what methods to mock
	 */
	public static synchronized void mockStatic(Class<?> type, Method... methods) {
		doMock(type, true, methods);
	}

	/**
	 * Enable static mocking for all methods of a class.
	 * 
	 * @param type
	 *            the class to enable static mocking
	 */
	public static synchronized void mockStatic(Class<?> type) {
		doMock(type, true, (Method[]) null);
	}

	/**
	 * Creates a mock object that supports mocking of final and native methods.
	 * 
	 * @param <T>
	 *            the type of the mock object
	 * @param type
	 *            the type of the mock object
	 * @param methods
	 *            optionally what methods to mock
	 * @return the mock object.
	 */
	public static synchronized <T> T mock(Class<T> type, Method... methods) {
		return doMock(type, false, methods);
	}

	/**
	 * Creates a mock object that supports mocking of final and native methods.
	 * 
	 * @param <T>
	 *            the type of the mock object
	 * @param type
	 *            the type of the mock object
	 * @return the mock object.
	 */
	public static synchronized <T> T mock(Class<T> type) {
		return doMock(type, false, (Method[]) null);
	}

	@SuppressWarnings("unchecked")
	private static <T> T doMock(Class<T> type, boolean isStatic, Method... methods) {
		if (type == null) {
			throw new IllegalArgumentException("The class to mock cannot be null");
		}

		T mock = null;
		final String mockName = toInstanceName(type);
		if (isStatic) {
			/*
			 * The reason why we have to create a substitution mock for the
			 * static method calls is because of the
			 * org.mockito.internal.util.MockUtil#isMockitoMock(Object..)
			 * method. Mockito only assumes instance mocks and thus
			 * Enhancer.isEnhanced(mock.getClass()) won't work when working with
			 * class mocks. The isMockitoMock method is called by Mockito in
			 * verification mode if verification fails (it tries to get the name
			 * of the mock) and we get an inappropriate exception.
			 */
			final MockData<InvocationSubstitute> mockData = createMethodInvocationControl(mockName, InvocationSubstitute.class, methods);
			MockRepository.putStaticMethodInvocationControl(type, mockData.getMethodInvocationControl());
			MockRepository.addObjectsToAutomaticallyReplayAndVerify(mockData.getMock());
		} else {
			MockData<T> mockData = createMethodInvocationControl(mockName, type, methods);

			mock = mockData.getMock();
			MockRepository.putInstanceMethodInvocationControl(mock, mockData.getMethodInvocationControl());
			if (mock instanceof InvocationSubstitute == false) {
				MockRepository.addObjectsToAutomaticallyReplayAndVerify(mock);
			}
		}
		return mock;
	}

	/**
	 * Verifies certain behavior <b>happened once</b>
	 * <p>
	 * Alias to <code>verifyStatic(mock, times(1))</code> E.g:
	 * 
	 * <pre>
	 * verifyStatic(ClassWithStaticMethod.class);
	 * ClassWithStaticMethod.someStaticMethod(&quot;some arg&quot;);
	 * </pre>
	 * 
	 * Above is equivalent to:
	 * 
	 * <pre>
	 * verifyStatic(ClassWithStaticMethod.class, times(1));
	 * ClassWithStaticMethod.someStaticMethod(&quot;some arg&quot;);
	 * </pre>
	 * 
	 * <p>
	 * Although it is possible to verify a stubbed invocation, usually <b>it's
	 * just redundant</b>. Let's say you've stubbed foo.bar(). If your code
	 * cares what foo.bar() returns then something else breaks(often before even
	 * verify() gets executed). If your code doesn't care what get(0) returns
	 * then it should not be stubbed.
	 * 
	 * @param mock
	 *            Class mocked by PowerMock.
	 */
	public static synchronized void verifyStatic(Class<?> mock) {
		verifyStatic(mock, times(1));
	}

	/**
	 * Verifies certain behavior happened at least once / exact number of times
	 * / never. E.g:
	 * 
	 * <pre>
	 *   verifyStatic(ClassWithStaticMethod.class, times(5));
	 *   ClassWithStaticMethod.someStaticMethod(&quot;was called five times&quot;);
	 *   
	 *   verifyStatic(ClassWithStaticMethod.class, atLeast(2));
	 *   ClassWithStaticMethod.someStaticMethod(&quot;was called at least two times&quot;);
	 *   
	 *   //you can use flexible argument matchers, e.g:
	 *   verifyStatic(ClassWithStaticMethod.class, atLeastOnce());
	 *   ClassWithStaticMethod.someMethod(&lt;b&gt;anyString()&lt;/b&gt;);
	 * </pre>
	 * 
	 * <b>times(1) is the default</b> and can be omitted
	 * <p>
	 * 
	 * @param mock
	 *            to be verified
	 * @param mode
	 *            times(x), atLeastOnce() or never()
	 */
	public static void verifyStatic(Class<?> mock, VerificationMode mode) {
		assertMockNotNull(mock);
		assertValidMock(mock);
		Whitebox.getInternalState(Mockito.class, MockingProgress.class).verificationStarted(mode);
	}

	private static void assertValidMock(Class<?> mock) {
		final MethodInvocationControl instanceInvocationHandler = MockRepository.getStaticMethodInvocationControl(mock);
		if (instanceInvocationHandler == null) {
			throw new NotAMockException("Argument passed to verifyStatic() is not a PowerMockito mock.");
		}
	}

	private static <T> MockData<T> createMethodInvocationControl(final String mockName, Class<T> type, Method[] methods) {
		MockHandler<T> mockHandler = new MockHandler<T>(mockName, Whitebox.getInternalState(Mockito.class, MockingProgress.class),
				new MatchersBinder());
		MethodInterceptorFilter<MockHandler<T>> filter = new MethodInterceptorFilter<MockHandler<T>>(type, mockHandler);
		final T mock = (T) ClassImposterizer.INSTANCE.imposterise(filter, type);

		filter.setInstance(mock);

		final MockitoMethodInvocationControl<T> invocationControl = new MockitoMethodInvocationControl<T>(filter, methods);
		return new MockData<T>(invocationControl, mock);
	}

	private static String toInstanceName(Class<?> clazz) {
		String className = clazz.getSimpleName();
		// lower case first letter
		return className.substring(0, 1).toLowerCase() + className.substring(1);
	}

	private static void assertMockNotNull(Class<?> mock) {
		if (mock == null) {
			throw new NullInsteadOfMockException("Argument passed to verifyStatic() is null! "
					+ "Concider using the @MockStatic annotation to ensure that you don't miss mock initialization.");
		}
	}

	/**
	 * Class that encapsulate a mock and its corresponding invocation control.
	 */
	private static class MockData<T> {
		private final MockitoMethodInvocationControl<T> methodInvocationControl;

		private final T mock;

		MockData(MockitoMethodInvocationControl<T> methodInvocationControl, T mock) {
			this.methodInvocationControl = methodInvocationControl;
			this.mock = mock;
		}

		public MockitoMethodInvocationControl<T> getMethodInvocationControl() {
			return methodInvocationControl;
		}

		public T getMock() {
			return mock;
		}
	}
}
