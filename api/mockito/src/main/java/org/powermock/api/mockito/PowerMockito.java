package org.powermock.api.mockito;

import static org.mockito.Mockito.times;

import java.lang.reflect.Method;

import org.mockito.Mockito;
import org.mockito.exceptions.misusing.NotAMockException;
import org.mockito.exceptions.misusing.NullInsteadOfMockException;
import org.mockito.internal.progress.MockingProgress;
import org.mockito.internal.verification.api.VerificationMode;
import org.mockito.stubbing.OngoingStubbing;
import org.powermock.api.mockito.expectation.ConstructorArgumentsVerification;
import org.powermock.api.mockito.expectation.ConstructorExpectationSetup;
import org.powermock.api.mockito.internal.expectation.DefaultConstructorArgumentsVerfication;
import org.powermock.api.mockito.internal.expectation.DefaultConstructorExpectationSetup;
import org.powermock.api.mockito.internal.mockcreation.MockCreator;
import org.powermock.api.mockito.internal.proxyframework.CgLibProxyFramework;
import org.powermock.core.MockRepository;
import org.powermock.core.spi.MethodInvocationControl;
import org.powermock.core.spi.NewInvocationControl;
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
        MockCreator.mock(type, true, false, null, (Method[]) null);
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
        return MockCreator.mock(type, false, false, null, (Method[]) null);
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
        return MockCreator.mock((Class<T>) Whitebox.getType(object), false, true, null, (Method[]) null);
    }

    public static synchronized <T> void spy(Class<T> type) {
        MockCreator.mock(type, true, true, type, (Method[]) null);
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
     * Verifies certain behavior <b>happened once</b>
     * <p>
     * Alias to <code>verifyNew(mockClass, times(1))</code> E.g:
     * 
     * <pre>
     * verifyNew(ClassWithStaticMethod.class);
     * </pre>
     * 
     * Above is equivalent to:
     * 
     * <pre>
     * verifyNew(ClassWithStaticMethod.class, times(1));
     * </pre>
     * 
     * <p>
     * 
     * @param mock
     *            Class mocked by PowerMock.
     */
    @SuppressWarnings("unchecked")
    public static synchronized <T> ConstructorArgumentsVerification verifyConstructionOf(Class<T> mock) {
        NewInvocationControl<?> invocationControl = MockRepository.getNewInstanceControl(mock);
        if (invocationControl == null) {
            throw new IllegalArgumentException(String.format("A constructor invocation in %s was unexpected.", Whitebox.getType(mock)));
        }
        invocationControl.verify();
        return new DefaultConstructorArgumentsVerfication<T>((NewInvocationControl<T>) invocationControl, mock);
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
    public static <T> ConstructorArgumentsVerification verifyConstructionOf(Class<?> mock, VerificationMode mode) {
        NewInvocationControl<?> invocationControl = MockRepository.getNewInstanceControl(mock);
        MockRepository.putAdditionalState("VerificationMode", mode);
        try {
            invocationControl.verify();
        } finally {
            MockRepository.removeAdditionalState("VerificationMode");
        }
        return new DefaultConstructorArgumentsVerfication<T>((NewInvocationControl<T>) invocationControl, mock);
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

    /**
     * Allows specifying expectations on new invocations. For example you might
     * want to throw an exception or return a mock. Note that you must replay
     * the class when using this method since this behavior is part of the class
     * mock.
     * <p>
     * Use this method when you need to specify parameter types for the
     * constructor when PowerMock cannot determine which constructor to use
     * automatically. In most cases you should use
     * {@link #whenConstructionOf(Class, Object...)} instead.
     */
    public static synchronized <T> ConstructorExpectationSetup<T> whenConstructionOf(Class<T> type) {
        return new DefaultConstructorExpectationSetup<T>(type);
    }

    /**
     * Allows specifying expectations on new invocations for private member
     * (inner) classes, local or anonymous classes. For example you might want
     * to throw an exception or return a mock. Note that you must replay the
     * class when using this method since this behavior is part of the class
     * mock.
     * 
     * @param fullyQualifiedName
     *            The fully-qualified name of the inner/local/anonymous type to
     *            expect.
     * @param arguments
     *            Optional number of arguments.
     */
    @SuppressWarnings("unchecked")
    public static synchronized <T> ConstructorExpectationSetup<T> whenConstructionOf(String fullyQualifiedName) throws Exception {
        final Class<T> forName = (Class<T>) Class.forName(fullyQualifiedName);
        return new DefaultConstructorExpectationSetup<T>(forName);
    }

    private static void assertValidMock(Class<?> mock) {
        final MethodInvocationControl instanceInvocationHandler = MockRepository.getStaticMethodInvocationControl(mock);
        if (instanceInvocationHandler == null) {
            throw new NotAMockException("Argument passed to verifyStatic() is not a PowerMockito mock.");
        }
    }

    private static void assertMockNotNull(Class<?> mock) {
        if (mock == null) {
            throw new NullInsteadOfMockException("Argument passed to verifyStatic() is null! "
                    + "Concider using the @MockStatic annotation to ensure that you don't miss mock initialization.");
        }
    }
}
