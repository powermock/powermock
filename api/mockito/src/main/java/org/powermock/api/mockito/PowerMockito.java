package org.powermock.api.mockito;

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
import org.powermock.core.spi.support.NewInvocationSubstitute;
import org.powermock.reflect.internal.WhiteboxImpl;

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

		final String mockName = toInstanceName(type);

		MockHandler<T> mockHandler = new MockHandler<T>(mockName, WhiteboxImpl.getInternalState(Mockito.class, MockingProgress.class),
				new MatchersBinder());
		MethodInterceptorFilter<MockHandler<T>> filter = new MethodInterceptorFilter<MockHandler<T>>(type, mockHandler);

		T mock = (T) ClassImposterizer.INSTANCE.imposterise(filter, type);
		filter.setInstance(mock);

		final MockitoMethodInvocationControl<T> invocationControl = new MockitoMethodInvocationControl<T>(filter, methods);
		if (isStatic) {
			MockRepository.putStaticMethodInvocationControl(type, invocationControl);
			MockRepository.addObjectsToAutomaticallyReplayAndVerify(type);
		} else {
			MockRepository.putInstanceMethodInvocationControl(mock, invocationControl);
			if (mock instanceof NewInvocationSubstitute == false) {
				MockRepository.addObjectsToAutomaticallyReplayAndVerify(mock);
			}
		}
		return mock;
	}

	private static String toInstanceName(Class<?> clazz) {
		String className = clazz.getSimpleName();
		// lower case first letter
		return className.substring(0, 1).toLowerCase() + className.substring(1);
	}
}
