package org.powermock.api.mockito;

import static org.mockito.Mockito.times;

import java.lang.reflect.Method;

import org.mockito.Mockito;
import org.mockito.internal.MockHandler;
import org.mockito.internal.creation.MethodInterceptorFilter;
import org.mockito.internal.creation.jmock.ClassImposterizer;
import org.mockito.internal.invocation.MatchersBinder;
import org.mockito.internal.progress.MockingProgress;
import org.powermock.api.mockito.internal.invocationcontrol.MockitoMethodInvocationControl;
import org.powermock.api.mockito.internal.proxyframework.CgLibProxyFramework;
import org.powermock.core.MockRepository;
import org.powermock.core.spi.MethodInvocationControl;
import org.powermock.core.spi.support.NewInvocationSubstitute;
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

	private static <T> T doMock(Class<T> type, boolean isStatic, Method... methods) {
		if (type == null) {
			throw new IllegalArgumentException("The class to mock cannot be null");
		} else if (methods == null) {
			methods = new Method[] {};
		}

		T mock = null;
		final String mockName = toInstanceName(type);
		if (isStatic) {
			MockHandler<T> mockHandler = new MockHandler<T>(mockName, Whitebox.getInternalState(Mockito.class, MockingProgress.class),
					new MatchersBinder());
			MethodInterceptorFilter<MockHandler<T>> filter = new MethodInterceptorFilter<MockHandler<T>>(type, mockHandler);

			mock = (T) ClassImposterizer.INSTANCE.imposterise(filter, type);
			filter.setInstance(mock);

			final MockitoMethodInvocationControl<T> invocationControl = new MockitoMethodInvocationControl<T>(filter, methods);

			MockRepository.putStaticMethodInvocationControl(type, invocationControl);
			MockRepository.addObjectsToAutomaticallyReplayAndVerify(type);
		} else {
			MockHandler<T> mockHandler = new MockHandler<T>(mockName, Whitebox.getInternalState(Mockito.class, MockingProgress.class),
					new MatchersBinder());
			MethodInterceptorFilter<MockHandler<T>> filter = new MethodInterceptorFilter<MockHandler<T>>(type, mockHandler);
			mock = (T) ClassImposterizer.INSTANCE.imposterise(filter, type);

			filter.setInstance(mock);

			final MockitoMethodInvocationControl<T> invocationControl = new MockitoMethodInvocationControl<T>(filter, methods);

			MockRepository.putInstanceMethodInvocationControl(mock, invocationControl);
			if (mock instanceof NewInvocationSubstitute == false) {
				MockRepository.addObjectsToAutomaticallyReplayAndVerify(mock);
			}
		}
		return mock;
	}

	/**
	 * Switches the class mocks to verify mode.
	 * 
	 * @param mock
	 *            mocked classes loaded by PowerMock.
	 */
	public static synchronized void verifyStatic(Class<?> mock) {
		MethodInvocationControl instanceInvocationHandler = MockRepository.getStaticMethodInvocationControl(mock);
		if (instanceInvocationHandler != null) {
			Whitebox.getInternalState(Mockito.class, MockingProgress.class).verificationStarted(times(1));
		} else {
			Whitebox.getInternalState(Mockito.class, MockingProgress.class).verificationStarted(times(1));
		}
	}

	private static String toInstanceName(Class<?> clazz) {
		String className = clazz.getSimpleName();
		// lower case first letter
		return className.substring(0, 1).toLowerCase() + className.substring(1);
	}
}
