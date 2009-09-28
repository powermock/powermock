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
package org.powermock.api.mockito;

import static org.mockito.Mockito.times;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.mockito.Mockito;
import org.mockito.internal.progress.MockingProgress;
import org.mockito.internal.stubbing.answers.CallsRealMethods;
import org.mockito.internal.stubbing.answers.DoesNothing;
import org.mockito.internal.stubbing.answers.Returns;
import org.mockito.internal.stubbing.answers.ThrowsException;
import org.mockito.internal.verification.api.VerificationMode;
import org.mockito.stubbing.Answer;
import org.mockito.stubbing.OngoingStubbing;
import org.powermock.api.mockito.expectation.ConstructorExpectationSetup;
import org.powermock.api.mockito.expectation.PowerMockitoStubber;
import org.powermock.api.mockito.internal.PowerMockitoCore;
import org.powermock.api.mockito.internal.PowerMockitoWhenRepository;
import org.powermock.api.mockito.internal.expectation.DefaultConstructorExpectationSetup;
import org.powermock.api.mockito.internal.mockcreation.MockCreator;
import org.powermock.api.mockito.internal.verification.DefaultConstructorArgumentsVerfication;
import org.powermock.api.mockito.internal.verification.DefaultPrivateMethodVerification;
import org.powermock.api.mockito.internal.verification.VerifyNoMoreInteractions;
import org.powermock.api.mockito.verification.ConstructorArgumentsVerification;
import org.powermock.api.mockito.verification.PrivateMethodVerification;
import org.powermock.api.support.MethodProxy;
import org.powermock.api.support.Stubber;
import org.powermock.api.support.SuppressCode;
import org.powermock.core.MockRepository;
import org.powermock.core.spi.NewInvocationControl;
import org.powermock.reflect.Whitebox;
import org.powermock.reflect.internal.WhiteboxImpl;

/**
 * PowerMockito extends Mockito functionality with several new features such as
 * mocking static and private methods and more. Use PowerMock instead of Mockito
 * where applicable.
 */
public class PowerMockito {
    private static final PowerMockitoCore POWERMOCKITO_CORE = new PowerMockitoCore();

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
     * Alias to <code>verifyStatic(times(1))</code> E.g:
     * 
     * <pre>
     * verifyStatic();
     * ClassWithStaticMethod.someStaticMethod(&quot;some arg&quot;);
     * </pre>
     * 
     * Above is equivalent to:
     * 
     * <pre>
     * verifyStatic(times(1));
     * ClassWithStaticMethod.someStaticMethod(&quot;some arg&quot;);
     * </pre>
     * 
     * <p>
     * Although it is possible to verify a stubbed invocation, usually <b>it's
     * just redundant</b>. Let's say you've stubbed foo.bar(). If your code
     * cares what foo.bar() returns then something else breaks(often before even
     * verify() gets executed). If your code doesn't care what get(0) returns
     * then it should not be stubbed.
     */
    public static synchronized void verifyStatic() {
        verifyStatic(times(1));
    }

    /**
     * Verifies certain behavior happened at least once / exact number of times
     * / never. E.g:
     * 
     * <pre>
     *   verifyStatic(times(5));
     *   ClassWithStaticMethod.someStaticMethod(&quot;was called five times&quot;);
     *   
     *   verifyStatic(atLeast(2));
     *   ClassWithStaticMethod.someStaticMethod(&quot;was called at least two times&quot;);
     *   
     *   //you can use flexible argument matchers, e.g:
     *   verifyStatic(atLeastOnce());
     *   ClassWithStaticMethod.someMethod(&lt;b&gt;anyString()&lt;/b&gt;);
     * </pre>
     * 
     * <b>times(1) is the default</b> and can be omitted
     * <p>
     * 
     * @param mode
     *            times(x), atLeastOnce() or never()
     */
    public static synchronized void verifyStatic(VerificationMode verificationMode) {
        Whitebox.getInternalState(Mockito.class, MockingProgress.class).verificationStarted(verificationMode);
    }

    /**
     * Verify a private method invocation for an instance.
     * 
     * @see {@link Mockito#verify(Object)}
     * @throws Exception
     *             If something unexpected goes wrong.
     */
    public static PrivateMethodVerification verifyPrivate(Object object) throws Exception {
        return verifyPrivate(object, times(1));
    }

    /**
     * Verify a private method invocation with a given verification mode.
     * 
     * @see {@link Mockito#verify(Object)}
     * @throws Exception
     *             If something unexpected goes wrong.
     */
    public static PrivateMethodVerification verifyPrivate(Object object, VerificationMode verificationMode) throws Exception {
        Whitebox.getInternalState(Mockito.class, MockingProgress.class).verificationStarted(verificationMode);
        return new DefaultPrivateMethodVerification(object);
    }

    /**
     * Verify a private method invocation for a class.
     * 
     * @see {@link Mockito#verify(Object)}
     * @throws Exception
     *             If something unexpected goes wrong.
     */
    public static PrivateMethodVerification verifyPrivate(Class<?> clazz) throws Exception {
        return verifyPrivate((Object) clazz);
    }

    /**
     * Verify a private method invocation for a class with a given verification
     * mode.
     * 
     * @see {@link Mockito#verify(Object)}
     * @throws Exception
     *             If something unexpected goes wrong.
     */
    public static PrivateMethodVerification verifyPrivate(Class<?> clazz, VerificationMode verificationMode) throws Exception {
        return verifyPrivate((Object) clazz, verificationMode);
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
    public static synchronized <T> ConstructorArgumentsVerification verifyNew(Class<T> mock) {
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
    @SuppressWarnings("unchecked")
    public static <T> ConstructorArgumentsVerification verifyNew(Class<?> mock, VerificationMode mode) {
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
        Method methodToInvoke = WhiteboxImpl.findMethodOrThrowException(instance, null, methodName, arguments);
        whenStarted(instance, methodToInvoke);
        return Mockito.when(WhiteboxImpl.<T> performMethodInvocation(instance, methodToInvoke, arguments));
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
        Method methodToInvoke = WhiteboxImpl.findMethodOrThrowException(instance, null, null, arguments);
        whenStarted(instance, methodToInvoke);
        return Mockito.when(WhiteboxImpl.<T> performMethodInvocation(instance, methodToInvoke, arguments));
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
        // whenStarted(instance);
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
        // whenStarted(instance);
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
    public static <T> OngoingStubbing<T> when(Object instance, Class<?> declaringClass, String methodToExecute, Object... arguments)
            throws Exception {
        // whenStarted(instance);
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
        // whenStarted(object);
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
        Method methodToInvoke = WhiteboxImpl.findMethodOrThrowException(clazz, null, methodToExecute, arguments);
        whenStarted(clazz, methodToInvoke);
        return Mockito.when(WhiteboxImpl.<T> performMethodInvocation(clazz, methodToInvoke, arguments));
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
        Method methodToInvoke = WhiteboxImpl.findMethodOrThrowException(klass, null, null, arguments);
        whenStarted(klass, methodToInvoke);
        return Mockito.when(WhiteboxImpl.<T> performMethodInvocation(klass, methodToInvoke, arguments));
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
     * {@link #whenNew(Class, Object...)} instead.
     */
    public static synchronized <T> ConstructorExpectationSetup<T> whenNew(Class<T> type) {
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
    public static synchronized <T> ConstructorExpectationSetup<T> whenNew(String fullyQualifiedName) throws Exception {
        final Class<T> forName = (Class<T>) Class.forName(fullyQualifiedName);
        return new DefaultConstructorExpectationSetup<T>(forName);
    }

    /**
     * Checks if any of given mocks (can be both instance and class mocks) has
     * any unverified interaction. Delegates to the orignal
     * {@link Mockito#verifyNoMoreInteractions(Object...)} if the mock is not a
     * PowerMockito mock.
     * <p>
     * You can use this method after you verified your mocks - to make sure that
     * nothing else was invoked on your mocks.
     * <p>
     * See also {@link Mockito#never()} - it is more explicit and communicates
     * the intent well.
     * <p>
     * Stubbed invocations (if called) are also treated as interactions.
     * <p>
     * A word of <b>warning</b>: Some users who did a lot of classic,
     * expect-run-verify mocking tend to use verifyNoMoreInteractions() very
     * often, even in every test method. verifyNoMoreInteractions() is not
     * recommended to use in every test method. verifyNoMoreInteractions() is a
     * handy assertion from the interaction testing toolkit. Use it only when
     * it's relevant. Abusing it leads to overspecified, less maintainable
     * tests. You can find further reading <a href=
     * "http://monkeyisland.pl/2008/07/12/should-i-worry-about-the-unexpected/"
     * >here</a>.
     * <p>
     * This method will also detect unverified invocations that occurred before
     * the test method, for example: in setUp(), &#064;Before method or in
     * constructor. Consider writing nice code that makes interactions only in
     * test methods.
     * 
     * <p>
     * Example:
     * 
     * <pre>
     * //interactions
     * mock.doSomething();
     * mock.doSomethingUnexpected();
     * 
     * //verification
     * verify(mock).doSomething();
     * 
     * //following will fail because 'doSomethingUnexpected()' is unexpected
     * verifyNoMoreInteractions(mock);
     * 
     * </pre>
     * 
     * See examples in javadoc for {@link Mockito} class
     * 
     * @param mocks
     *            to be verified
     */
    public static void verifyNoMoreInteractions(Object... mocks) {
        VerifyNoMoreInteractions.verifyNoMoreInteractions(mocks);
    }

    /**
     * Verifies that no interactions happened on given mocks (can be both
     * instance and class mocks). Delegates to the orignal
     * {@link Mockito#verifyNoMoreInteractions(Object...)} if the mock is not a
     * PowerMockito mock.
     * 
     * <pre>
     * verifyZeroInteractions(mockOne, mockTwo);
     * </pre>
     * 
     * This method will also detect invocations that occurred before the test
     * method, for example: in setUp(), &#064;Before method or in constructor.
     * Consider writing nice code that makes interactions only in test methods.
     * <p>
     * See also {@link Mockito#never()} - it is more explicit and communicates
     * the intent well.
     * <p>
     * See examples in javadoc for {@link Mockito} class
     * 
     * @param mocks
     *            to be verified
     */
    public static void verifyZeroInteractions(Object... mocks) {
        VerifyNoMoreInteractions.verifyNoMoreInteractions(mocks);
    }

    /**
     * Use doAnswer() when you want to stub a void method with generic
     * {@link Answer}.
     * <p>
     * Stubbing voids requires different approach from
     * {@link Mockito#when(Object)} because the compiler does not like void
     * methods inside brackets...
     * <p>
     * Example:
     * 
     * <pre>
     * doAnswer(new Answer() {
     *     public Object answer(InvocationOnMock invocation) {
     *         Object[] args = invocation.getArguments();
     *         Mock mock = invocation.getMock();
     *         return null;
     *     }
     * }).when(mock).someMethod();
     * </pre>
     * <p>
     * See examples in javadoc for {@link Mockito} class
     * 
     * @param answer
     *            to answer when the stubbed method is called
     * @return stubber - to select a method for stubbing
     */
    public static PowerMockitoStubber doAnswer(Answer<?> answer) {
        return POWERMOCKITO_CORE.doAnswer(answer);
    }

    /**
     * Use doThrow() when you want to stub the void method with an exception.
     * <p>
     * Stubbing voids requires different approach from
     * {@link Mockito#when(Object)} because the compiler does not like void
     * methods inside brackets...
     * <p>
     * Example:
     * 
     * <pre>
     * doThrow(new RuntimeException()).when(mock).someVoidMethod();
     * </pre>
     * 
     * @param toBeThrown
     *            to be thrown when the stubbed method is called
     * @return stubber - to select a method for stubbing
     */
    public static PowerMockitoStubber doThrow(Throwable toBeThrown) {
        return POWERMOCKITO_CORE.doAnswer(new ThrowsException(toBeThrown));
    }

    /**
     * Use doCallRealMethod() when you want to call the real implementation of a
     * method.
     * <p>
     * As usual you are going to read <b>the partial mock warning</b>: Object
     * oriented programming is more less tackling complexity by dividing the
     * complexity into separate, specific, SRPy objects. How does partial mock
     * fit into this paradigm? Well, it just doesn't... Partial mock usually
     * means that the complexity has been moved to a different method on the
     * same object. In most cases, this is not the way you want to design your
     * application.
     * <p>
     * However, there are rare cases when partial mocks come handy: dealing with
     * code you cannot change easily (3rd party interfaces, interim refactoring
     * of legacy code etc.) However, I wouldn't use partial mocks for new,
     * test-driven & well-designed code.
     * <p>
     * See also javadoc {@link Mockito#spy(Object)} to find out more about
     * partial mocks. <b>Mockito.spy() is a recommended way of creating partial
     * mocks.</b> The reason is it guarantees real methods are called against
     * correctly constructed object because you're responsible for constructing
     * the object passed to spy() method.
     * <p>
     * Example:
     * 
     * <pre>
     * Foo mock = mock(Foo.class);
     * doCallRealMethod().when(mock).someVoidMethod();
     * 
     * // this will call the real implementation of Foo.someVoidMethod()
     * mock.someVoidMethod();
     * </pre>
     * <p>
     * See examples in javadoc for {@link Mockito} class
     * 
     * @return stubber - to select a method for stubbing
     */
    public static PowerMockitoStubber doCallRealMethod() {
        return POWERMOCKITO_CORE.doAnswer(new CallsRealMethods());
    }

    /**
     * Use doNothing() for setting void methods to do nothing. <b>Beware that
     * void methods on mocks do nothing by default!</b> However, there are rare
     * situations when doNothing() comes handy:
     * <p>
     * 1. Stubbing consecutive calls on a void method:
     * 
     * <pre>
     * doNothing().doThrow(new RuntimeException()).when(mock).someVoidMethod();
     * 
     * //does nothing the first time:
     * mock.someVoidMethod();
     * 
     * //throws RuntimeException the next time:
     * mock.someVoidMethod();
     * </pre>
     * 
     * 2. When you spy real objects and you want the void method to do nothing:
     * 
     * <pre>
     * List list = new LinkedList();
     * List spy = spy(list);
     * 
     * //let's make clear() do nothing
     * doNothing().when(spy).clear();
     * 
     * spy.add(&quot;one&quot;);
     * 
     * //clear() does nothing, so the list still contains &quot;one&quot;
     * spy.clear();
     * </pre>
     * <p>
     * See examples in javadoc for {@link Mockito} class
     * 
     * @return stubber - to select a method for stubbing
     */
    public static PowerMockitoStubber doNothing() {
        return POWERMOCKITO_CORE.doAnswer(new DoesNothing());
    }

    /**
     * Use doReturn() in those rare occasions when you cannot use
     * {@link Mockito#when(Object)}.
     * <p>
     * <b>Beware that {@link Mockito#when(Object)} is always recommended for
     * stubbing because it is argument type-safe and more readable</b>
     * (especially when stubbing consecutive calls).
     * <p>
     * Here are those rare occasions when doReturn() comes handy:
     * <p>
     * 
     * 1. When spying real objects and calling real methods on a spy brings side
     * effects
     * 
     * <pre>
     * List list = new LinkedList();
     * List spy = spy(list);
     * 
     * //Impossible: real method is called so spy.get(0) throws IndexOutOfBoundsException (the list is yet empty)
     * when(spy.get(0)).thenReturn(&quot;foo&quot;);
     * 
     * //You have to use doReturn() for stubbing:
     * doReturn(&quot;foo&quot;).when(spy).get(0);
     * </pre>
     * 
     * 2. Overriding a previous exception-stubbing:
     * 
     * <pre>
     * when(mock.foo()).thenThrow(new RuntimeException());
     * 
     * //Impossible: the exception-stubbed foo() method is called so RuntimeException is thrown. 
     * when(mock.foo()).thenReturn(&quot;bar&quot;);
     * 
     * //You have to use doReturn() for stubbing:
     * doReturn(&quot;bar&quot;).when(mock).foo();
     * </pre>
     * 
     * Above scenarios shows a tradeoff of Mockito's ellegant syntax. Note that
     * the scenarios are very rare, though. Spying should be sporadic and
     * overriding exception-stubbing is very rare.
     * <p>
     * See examples in javadoc for {@link Mockito} class
     * 
     * @param toBeReturned
     *            to be returned when the stubbed method is called
     * @return stubber - to select a method for stubbing
     */
    public static PowerMockitoStubber doReturn(Object toBeReturned) {
        return POWERMOCKITO_CORE.doAnswer(new Returns(toBeReturned));
    }

    /**
     * Suppress constructor calls on specific constructors only.
     */
    public static synchronized void suppressConstructor(Constructor<?>... constructors) {
        SuppressCode.suppressConstructor(constructors);
    }

    /**
     * This method can be used to suppress the code in a specific constructor.
     * 
     * @param clazz
     *            The class where the constructor is located.
     * @param parameterTypes
     *            The parameter types of the constructor to suppress.
     */
    public static synchronized void suppressSpecificConstructor(Class<?> clazz, Class<?>... parameterTypes) {
        SuppressCode.suppressSpecificConstructor(clazz, parameterTypes);
    }

    /**
     * Suppress all constructors in the given class and it's super classes.
     * 
     * @param classes
     *            The classes whose constructors will be suppressed.
     */
    public static synchronized void suppressConstructor(Class<?>... classes) {
        SuppressCode.suppressConstructor(classes);
    }

    /**
     * Suppress all constructors in the given class.
     * 
     * @param classes
     *            The classes whose constructors will be suppressed.
     * @param excludePrivateConstructors
     *            optionally keep code in private constructors
     */
    public static synchronized void suppressConstructor(Class<?> clazz, boolean excludePrivateConstructors) {
        SuppressCode.suppressConstructor(clazz, excludePrivateConstructors);
    }

    /**
     * Suppress specific fields. This works on both instance methods and static
     * methods. Note that replay and verify are not needed as this is not part
     * of a mock behavior.
     */
    public static synchronized void suppressField(Field... fields) {
        SuppressCode.suppressField(fields);
    }

    /**
     * Suppress all fields for these classes.
     */
    public static synchronized void suppressField(Class<?>[] classes) {
        SuppressCode.suppressField(classes);
    }

    /**
     * Suppress multiple methods for a class.
     * 
     * @param classes
     *            The class whose methods will be suppressed.
     * @param fieldNames
     *            The names of the methods that'll be suppressed. If field names
     *            are empty, <i>all</i> fields in the supplied class will be
     *            suppressed.
     */
    public static synchronized void suppressField(Class<?> clazz, String... fieldNames) {
        SuppressCode.suppressField(clazz, fieldNames);
    }

    /**
     * Suppress specific method calls on all types containing this method. This
     * works on both instance methods and static methods. Note that replay and
     * verify are not needed as this is not part of a mock behavior.
     */
    public static synchronized void suppressMethod(Method... methods) {
        SuppressCode.suppressMethod(methods);
    }

    /**
     * Suppress all methods for these classes.
     * 
     * @param cls
     *            The first class whose methods will be suppressed.
     * @param additionalClasses
     *            Additional classes whose methods will be suppressed.
     */
    public static synchronized void suppressMethod(Class<?> cls, Class<?>... additionalClasses) {
        SuppressCode.suppressMethod(cls, additionalClasses);
    }

    /**
     * Suppress all methods for these classes.
     * 
     * @param classes
     *            Classes whose methods will be suppressed.
     */
    public static synchronized void suppressMethod(Class<?>[] classes) {
        SuppressCode.suppressMethod(classes);
    }

    /**
     * Suppress multiple methods for a class.
     * 
     * @param clazz
     *            The class whose methods will be suppressed.
     * @param methodName
     *            The first method to be suppress in class <code>clazz</code>.
     * @param additionalMethodNames
     *            Additional methods to suppress in class <code>clazz</code>.
     */
    public static synchronized void suppressMethod(Class<?> clazz, String methodName, String... additionalMethodNames) {
        SuppressCode.suppressMethod(clazz, methodName, additionalMethodNames);
    }

    /**
     * Suppress multiple methods for a class.
     * 
     * @param clazz
     *            The class whose methods will be suppressed.
     * @param methodNames
     *            Methods to suppress in class <code>clazz</code>.
     */
    public static synchronized void suppressMethod(Class<?> clazz, String[] methodNames) {
        SuppressCode.suppressMethod(clazz, methodNames);
    }

    /**
     * suSuppress all methods for this class.
     * 
     * @param classes
     *            The class which methods will be suppressed.
     * @param excludePrivateMethods
     *            optionally not suppress private methods
     */
    public static synchronized void suppressMethod(Class<?> clazz, boolean excludePrivateMethods) {
        SuppressCode.suppressMethod(clazz, excludePrivateMethods);
    }

    /**
     * Suppress a specific method call. Use this for overloaded methods.
     */
    public static synchronized void suppressMethod(Class<?> clazz, String methodName, Class<?>[] parameterTypes) {
        SuppressCode.suppressMethod(clazz, methodName, parameterTypes);
    }

    /**
     * Add a method that should be intercepted and return another value (
     * <code>returnObject</code>) (i.e. the method is stubbed).
     */
    public static void stubMethod(Method method, Object returnObject) {
        Stubber.stubMethod(method, returnObject);
    }

    /**
     * Add a method that should be intercepted and return another value (
     * <code>returnObject</code>) (i.e. the method is stubbed).
     */
    public static void stubMethod(Class<?> declaringClass, String methodName, Object returnObject) {
        Stubber.stubMethod(declaringClass, methodName, returnObject);
    }

    /**
     * Add a proxy for this method. Each call to the method will be routed to
     * the invocationHandler instead.
     */
    public static void proxy(Method method, InvocationHandler invocationHandler) {
        MethodProxy.proxy(method, invocationHandler);
    }

    /**
     * Add a proxy for a method declared in class <code>declaringClass</code>.
     * Each call to the method will be routed to the invocationHandler instead.
     */
    public static void proxy(Class<?> declaringClass, String methodName, InvocationHandler invocationHandler) {
        MethodProxy.proxy(declaringClass, methodName, invocationHandler);
    }

    private static void whenStarted(Object instance, Method methodToInvoke) {
        PowerMockitoWhenRepository.add(instance, methodToInvoke);
    }
}
