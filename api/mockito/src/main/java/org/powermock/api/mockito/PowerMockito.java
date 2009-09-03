package org.powermock.api.mockito;

import static org.mockito.Mockito.times;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.mockito.Mockito;
import org.mockito.exceptions.misusing.NotAMockException;
import org.mockito.exceptions.misusing.NullInsteadOfMockException;
import org.mockito.internal.MockHandler;
import org.mockito.internal.creation.MethodInterceptorFilter;
import org.mockito.internal.creation.MockSettingsImpl;
import org.mockito.internal.creation.jmock.ClassImposterizer;
import org.mockito.internal.invocation.MatchersBinder;
import org.mockito.internal.progress.MockingProgress;
import org.mockito.internal.util.MockName;
import org.mockito.internal.verification.api.VerificationMode;
import org.mockito.stubbing.OngoingStubbing;
import org.powermock.api.mockito.internal.invocationcontrol.MockitoMethodInvocationControl;
import org.powermock.api.mockito.internal.proxyframework.CgLibProxyFramework;
import org.powermock.core.ClassReplicaCreator;
import org.powermock.core.MockRepository;
import org.powermock.core.spi.MethodInvocationControl;
import org.powermock.core.spi.support.InvocationSubstitute;
import org.powermock.reflect.Whitebox;

/**
 * PowerMockito extends Mockito functionality with several new features such as
 * mocking static and private methods and more. Use PowerMock instead of Mockito
 * where applicable.
 */
public class PowerMockito {

	static {
		CgLibProxyFramework.registerProxyFramework();
	}

	/**
	 * Enable static mocking for all methods of a class.
	 * 
	 * @param type
	 *            the class to enable static mocking
	 */
	public static synchronized void mockStatic(Class<?> type) {
		doMock(type, true, false, null, (Method[]) null);
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
		return doMock(type, false, false, null, (Method[]) null);
	}

	/**
	 * Spy on objects that are final or otherwise not &quot;spyable&quot; from
	 * normal Mockito.
	 * 
	 * @see Mockito#spy(Object)
	 * 
	 * @param <T>
	 *            the type of the mock object
	 * @param type
	 *            the type of the mock object
	 * @return the spy object.
	 */
	@SuppressWarnings("unchecked")
	public static synchronized <T> T spy(T object) {
		return doMock((Class<T>) Whitebox.getType(object), false, true, null, (Method[]) null);
	}

	public static synchronized <T> void spy(Class<T> type) {
		doMock(type, true, true, type, (Method[]) null);
	}

	@SuppressWarnings("unchecked")
	private static <T> T doMock(Class<T> type, boolean isStatic, boolean isSpy, Object delegator, Method... methods) {
		if (type == null) {
			throw new IllegalArgumentException("The class to mock cannot be null");
		}

		T mock = null;
		final String mockName = toInstanceName(type);

		final Class<T> typeToMock;
		if (type.getName().startsWith("java.") && Modifier.isFinal(type.getModifiers())) {
			typeToMock = (Class<T>) new ClassReplicaCreator().createClassReplica(type);
		} else {
			typeToMock = type;
		}

		MockData<T> mockData = createMethodInvocationControl(mockName, typeToMock, methods, isSpy, (T) delegator);

		mock = mockData.getMock();
		if (isStatic) {
			MockRepository.putStaticMethodInvocationControl(type, mockData.getMethodInvocationControl());
		} else {
			MockRepository.putInstanceMethodInvocationControl(mock, mockData.getMethodInvocationControl());
		}

		if (isSpy && !isStatic) {
			// TODO Must add something as additional state so that MockGateway
			// can figure out if this _instance_ should never be proxied!! I.e.
			// we must remove the MockGateway#DONT_MOCK_NEXT_CALL and change it
			// to something that takes the instance (or class?) into account.
			// (it shouldn't be in case of spies, at least in case of instance
			// spies!)
		}

		if (mock instanceof InvocationSubstitute == false) {
			MockRepository.addObjectsToAutomaticallyReplayAndVerify(mock);
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

	/**
	 * Expect calls to private methods.
	 * 
	 * @see {@link Mockito#when(Object)}
	 * @throws Exception
	 *             If something unexpected goes wrong.
	 */
	public static <T> OngoingStubbing<T> when(Object instance, String methodName, Object... arguments) throws Exception {
		return Mockito.when(Whitebox.<T> invokeMethod(instance, methodName, arguments));
	}

	/**
	 * Expect calls to private methods without having to specify the method
	 * name. The method will be looked up using the parameter types (if
	 * possible).
	 * 
	 * @see {@link Mockito#when(Object)}
	 * @throws Exception
	 *             If something unexpected goes wrong.
	 */
	public static <T> OngoingStubbing<T> when(Object instance, Object... arguments) throws Exception {
		return Mockito.when(Whitebox.<T> invokeMethod(instance, arguments));
	}

	/**
	 * Expect a private or inner class method call in cases where PowerMock
	 * cannot automatically determine the type of the parameters, for example
	 * when mixing primitive types and wrapper types in the same method. For
	 * most situations use {@link #when(Object, Object...)} instead.
	 * 
	 * @see {@link Mockito#when(Object)}
	 * @throws Exception
	 *             If something unexpected goes wrong.
	 */
	public static <T> OngoingStubbing<T> when(Object instance, String methodToExecute, Class<?>[] argumentTypes, Object... arguments)
			throws Exception {
		return Mockito.when(Whitebox.<T> invokeMethod(instance, methodToExecute, argumentTypes, arguments));
	}

	/**
	 * Expected a private or inner class method call in a subclass (defined by
	 * <code>definedIn</code>) in cases where PowerMock cannot automatically
	 * determine the type of the parameters, for example when mixing primitive
	 * types and wrapper types in the same method. For most situations use
	 * {@link #invokeMethod(Object, Object...)} instead.
	 * 
	 * @see {@link Mockito#when(Object)}
	 * @throws Exception
	 *             If something unexpected goes wrong.
	 */
	public static <T> OngoingStubbing<T> when(Object instance, String methodToExecute, Class<?> definedIn, Class<?>[] argumentTypes,
			Object... arguments) throws Exception {
		return Mockito.when(Whitebox.<T> invokeMethod(instance, methodToExecute, definedIn, argumentTypes, arguments));
	}

	/**
	 * Expect a private or inner class method call that is located in a subclass
	 * of the instance.
	 * 
	 * @see {@link Mockito#when(Object)}
	 * @throws Exception
	 *             If something unexpected goes wrong.
	 */
	public static <T> OngoingStubbing<T> when(Object instance, Class<?> declaringClass, String methodToExecute, Object... arguments) throws Exception {
		return Mockito.when(Whitebox.<T> invokeMethod(instance, declaringClass, methodToExecute, arguments));
	}

	/**
	 * Expect a private or inner class method call in that is located in a
	 * subclass of the instance. This might be useful to test private methods.
	 * <p>
	 * Use this for overloaded methods.
	 * 
	 * @see {@link Mockito#when(Object)}
	 * @throws Exception
	 *             If something unexpected goes wrong.
	 */
	public static <T> OngoingStubbing<T> when(Object object, Class<?> declaringClass, String methodToExecute, Class<?>[] parameterTypes,
			Object... arguments) throws Exception {
		return Mockito.when(Whitebox.<T> invokeMethod(object, declaringClass, methodToExecute, parameterTypes, arguments));
	}

	/**
	 * Expect a static private or inner class method call.
	 * 
	 * @see {@link Mockito#when(Object)}
	 * @throws Exception
	 *             If something unexpected goes wrong.
	 */
	public static <T> OngoingStubbing<T> when(Class<?> clazz, String methodToExecute, Object... arguments) throws Exception {
		return Mockito.when(Whitebox.<T> invokeMethod(clazz, methodToExecute, arguments));
	}

	/**
	 * Expect calls to private methods without having to specify the method
	 * name. The method will be looked up using the parameter types (if
	 * possible).
	 * 
	 * @see {@link Mockito#when(Object)}
	 * @throws Exception
	 *             If something unexpected goes wrong.
	 */
	public static <T> OngoingStubbing<T> when(Class<?> klass, Object... arguments) throws Exception {
		return Mockito.when(Whitebox.<T> invokeMethod(klass, arguments));
	}

	/**
	 * Just delegates to the original {@link Mockito#when(Object)} method.
	 * 
	 * @see {@link Mockito#when(Object)}
	 */
	public static <T> OngoingStubbing<T> when(T methodCall) {
		return Mockito.when(methodCall);
	}

	private static void assertValidMock(Class<?> mock) {
		final MethodInvocationControl instanceInvocationHandler = MockRepository.getStaticMethodInvocationControl(mock);
		if (instanceInvocationHandler == null) {
			throw new NotAMockException("Argument passed to verifyStatic() is not a PowerMockito mock.");
		}
	}

	private static <T> MockData<T> createMethodInvocationControl(final String mockName, Class<T> type, Method[] methods, boolean isSpy,
			Object delegator) {
		final MockSettingsImpl mockSettings;
		if (isSpy) {
			mockSettings = (MockSettingsImpl) new MockSettingsImpl().defaultAnswer(Mockito.CALLS_REAL_METHODS);
		} else {
			mockSettings = (MockSettingsImpl) Mockito.withSettings();
		}
		MockHandler<T> mockHandler = new MockHandler<T>(new MockName(mockName, type),
				Whitebox.getInternalState(Mockito.class, MockingProgress.class), new MatchersBinder(), mockSettings);
		MethodInterceptorFilter filter = new MethodInterceptorFilter(type, mockHandler);
		final T mock = (T) ClassImposterizer.INSTANCE.imposterise(filter, type);
		final MockitoMethodInvocationControl invocationControl = new MockitoMethodInvocationControl(filter, delegator, methods);
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
		private final MockitoMethodInvocationControl methodInvocationControl;

		private final T mock;

		MockData(MockitoMethodInvocationControl methodInvocationControl, T mock) {
			this.methodInvocationControl = methodInvocationControl;
			this.mock = mock;
		}

		public MockitoMethodInvocationControl getMethodInvocationControl() {
			return methodInvocationControl;
		}

		public T getMock() {
			return mock;
		}
	}
}
