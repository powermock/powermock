/*
s * Copyright 2008 the original author or authors.
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

import org.mockito.MockSettings;
import org.mockito.Mockito;
import org.mockito.internal.progress.MockingProgress;
import org.mockito.internal.stubbing.answers.CallsRealMethods;
import org.mockito.internal.stubbing.answers.DoesNothing;
import org.mockito.internal.stubbing.answers.Returns;
import org.mockito.internal.stubbing.answers.ThrowsException;
import org.mockito.stubbing.Answer;
import org.mockito.stubbing.OngoingStubbing;
import org.mockito.verification.VerificationMode;
import org.powermock.api.mockito.expectation.ConstructorExpectationSetup;
import org.powermock.api.mockito.expectation.PowerMockitoStubber;
import org.powermock.api.mockito.expectation.WithOrWithoutExpectedArguments;
import org.powermock.api.mockito.internal.PowerMockitoCore;
import org.powermock.api.mockito.internal.expectation.ConstructorAwareExpectationSetup;
import org.powermock.api.mockito.internal.expectation.DefaultConstructorExpectationSetup;
import org.powermock.api.mockito.internal.expectation.DefaultMethodExpectationSetup;
import org.powermock.api.mockito.internal.mockcreation.MockCreator;
import org.powermock.api.mockito.internal.verification.DefaultConstructorArgumentsVerfication;
import org.powermock.api.mockito.internal.verification.DefaultPrivateMethodVerification;
import org.powermock.api.mockito.internal.verification.VerifyNoMoreInteractions;
import org.powermock.api.mockito.verification.ConstructorArgumentsVerification;
import org.powermock.api.mockito.verification.PrivateMethodVerification;
import org.powermock.api.support.membermodification.MemberModifier;
import org.powermock.core.MockRepository;
import org.powermock.core.spi.NewInvocationControl;
import org.powermock.reflect.Whitebox;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.withSettings;

/**
 * PowerMockito extends Mockito functionality with several new features such as
 * mocking static and private methods and more. Use PowerMock instead of Mockito
 * where applicable.
 *
 * @see Mockito
 */
public class PowerMockito extends MemberModifier {
    private static final String NO_OBJECT_CREATION_ERROR_MESSAGE_TEMPLATE = "No instantiation of class %s was recorded during the test. Note that only expected object creations (e.g. those using whenNew(..)) can be verified.";
    private static final PowerMockitoCore POWERMOCKITO_CORE = new PowerMockitoCore();

    /**
     * Enable static mocking for all methods of a class.
     *
     * @param type
     *            the class to enable static mocking
     */
    public static synchronized void mockStatic(Class<?> type, Class<?>... types) {
        MockCreator.mock(type, true, false, null, null, (Method[]) null);
        if(types != null && types.length > 0) {
            for (Class<?> aClass : types) {
                MockCreator.mock(aClass, true, false, null, null, (Method[]) null);
            }
        }
    }

    /**
     * Creates class mock with a specified strategy for its answers to
     * interactions. It's quite advanced feature and typically you don't need it
     * to write decent tests. However it can be helpful when working with legacy
     * systems.
     * <p>
     * It is the default answer so it will be used <b>only when you don't</b>
     * stub the method call.
     *
     * <pre>
     * mockStatic(Foo.class, RETURNS_SMART_NULLS);
     * mockStatic(Foo.class, new YourOwnAnswer());
     * </pre>
     *
     * @param classMock
     *            class to mock
     * @param defaultAnswer
     *            default answer for unstubbed methods
     *
     * @return mock object
     */
    public static void mockStatic(Class<?> classMock, @SuppressWarnings("rawtypes") Answer defaultAnswer) {
        mockStatic(classMock, withSettings().defaultAnswer(defaultAnswer));
    }

    /**
     * Creates a class mock with some non-standard settings.
     * <p>
     * The number of configuration points for a mock grows so we need a fluent
     * way to introduce new configuration without adding more and more
     * overloaded PowerMockito.mockStatic() methods. Hence {@link MockSettings}.
     *
     * <pre>
     *   mockStatic(Listener.class, withSettings()
     *     .name(&quot;firstListner&quot;).defaultBehavior(RETURNS_SMART_NULLS));
     *   );
     * </pre>
     *
     * <b>Use it carefully and occasionally</b>. What might be reason your test
     * needs non-standard mocks? Is the code under test so complicated that it
     * requires non-standard mocks? Wouldn't you prefer to refactor the code
     * under test so it is testable in a simple way?
     * <p>
     * See also {@link Mockito#withSettings()}
     *
     * @param classToMock
     *            class to mock
     * @param mockSettings
     *            additional mock settings
     * @return mock object
     */
    public static void mockStatic(Class<?> classToMock, MockSettings mockSettings) {
        MockCreator.mock(classToMock, true, false, null, mockSettings, (Method[]) null);
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
        return MockCreator.mock(type, false, false, null, null, (Method[]) null);
    }

    /**
     * Creates mock with a specified strategy for its answers to interactions.
     * It's quite advanced feature and typically you don't need it to write
     * decent tests. However it can be helpful when working with legacy systems.
     * <p>
     * It is the default answer so it will be used <b>only when you don't</b>
     * stub the method call.
     *
     * <pre>
     * Foo mock = mock(Foo.class, RETURNS_SMART_NULLS);
     * Foo mockTwo = mock(Foo.class, new YourOwnAnswer());
     * </pre>
     *
     * <p>
     * See examples in javadoc for {@link Mockito} class
     * </p>
     *
     * @param classToMock
     *            class or interface to mock
     * @param defaultAnswer
     *            default answer for unstubbed methods
     *
     * @return mock object
     */
    public static <T> T mock(Class<T> classToMock, @SuppressWarnings("rawtypes") Answer defaultAnswer) {
        return mock(classToMock, withSettings().defaultAnswer(defaultAnswer));
    }

    /**
     * Creates a mock with some non-standard settings.
     * <p>
     * The number of configuration points for a mock grows so we need a fluent
     * way to introduce new configuration without adding more and more
     * overloaded Mockito.mock() methods. Hence {@link MockSettings}.
     *
     * <pre>
     *   Listener mock = mock(Listener.class, withSettings()
     *     .name(&quot;firstListner&quot;).defaultBehavior(RETURNS_SMART_NULLS));
     *   );
     * </pre>
     *
     * <b>Use it carefully and occasionally</b>. What might be reason your test
     * needs non-standard mocks? Is the code under test so complicated that it
     * requires non-standard mocks? Wouldn't you prefer to refactor the code
     * under test so it is testable in a simple way?
     * <p>
     * See also {@link Mockito#withSettings()}
     * <p>
     * See examples in javadoc for {@link Mockito} class
     *
     * @param classToMock
     *            class or interface to mock
     * @param mockSettings
     *            additional mock settings
     * @return mock object
     */
    public static <T> T mock(Class<T> classToMock, MockSettings mockSettings) {
        return MockCreator.mock(classToMock, false, false, null, mockSettings, (Method[]) null);
    }

    /**
     * Spy on objects that are final or otherwise not &quot;spyable&quot; from
     * normal Mockito.
     *
     * @see Mockito#spy(Object)
     *
     * @param <T>
     *            the type of the mock object
     * @param object
     *            the object to spy on
     * @return the spy object.
     */
    @SuppressWarnings("unchecked")
    public static synchronized <T> T spy(T object) {
        return MockCreator.mock((Class<T>) Whitebox.getType(object), false, true, object, null, (Method[]) null);
    }

    /**
     * Spy on classes (not &quot;spyable&quot; from normal Mockito).
     *
     * @see Mockito#spy(Object)
     *
     * @param <T>
     *            the type of the class mock
     * @param type
     *            the type of the class mock
     */
    public static synchronized <T> void spy(Class<T> type) {
        MockCreator.mock(type, true, true, type, null, (Method[]) null);
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
     * @param verificationMode
     *            times(x), atLeastOnce() or never()
     */
    public static synchronized void verifyStatic(VerificationMode verificationMode) {
        Whitebox.getInternalState(Mockito.class, MockingProgress.class).verificationStarted(
                POWERMOCKITO_CORE.wrapInStaticVerificationMode(verificationMode));
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
    public static PrivateMethodVerification verifyPrivate(Object object, VerificationMode verificationMode)
            throws Exception {
        Whitebox.getInternalState(Mockito.class, MockingProgress.class).verificationStarted(
                POWERMOCKITO_CORE.wrapInMockitoSpecificVerificationMode(object, verificationMode));
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
    public static PrivateMethodVerification verifyPrivate(Class<?> clazz, VerificationMode verificationMode)
            throws Exception {
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
        if (mock == null) {
            throw new IllegalArgumentException("Class to verify cannot be null");
        }
        NewInvocationControl<?> invocationControl = MockRepository.getNewInstanceControl(mock);
        if (invocationControl == null) {
            throw new IllegalStateException(String.format(NO_OBJECT_CREATION_ERROR_MESSAGE_TEMPLATE, Whitebox.getType(
                    mock).getName()));
        }
        invocationControl.verify();
        return new DefaultConstructorArgumentsVerfication<T>((NewInvocationControl<T>) invocationControl, mock);
    }

    /**
     * Verifies certain behavior happened at least once / exact number of times
     * / never. E.g:
     *
     * <pre>
     * verifyNew(ClassWithStaticMethod.class, times(5));
     *
     * verifyNew(ClassWithStaticMethod.class, atLeast(2));
     *
     * //you can use flexible argument matchers, e.g:
     * verifyNew(ClassWithStaticMethod.class, atLeastOnce());
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
        if (mock == null) {
            throw new IllegalArgumentException("Class to verify cannot be null");
        } else if (mode == null) {
            throw new IllegalArgumentException("Verify mode cannot be null");
        }
        NewInvocationControl<?> invocationControl = MockRepository.getNewInstanceControl(mock);
        MockRepository.putAdditionalState("VerificationMode", POWERMOCKITO_CORE.wrapInMockitoSpecificVerificationMode(
                mock, mode));
        if (invocationControl == null) {
            throw new IllegalStateException(String.format(NO_OBJECT_CREATION_ERROR_MESSAGE_TEMPLATE, Whitebox.getType(
                    mock).getName()));
        }
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
     * Expect calls to private methods.
     *
     * @see {@link Mockito#when(Object)}
     * @throws Exception
     *             If something unexpected goes wrong.
     */
    public static <T> WithOrWithoutExpectedArguments<T> when(Object instance, Method method) throws Exception {
        return new DefaultMethodExpectationSetup<T>(instance, method);
    }

    /**
     * Expect calls to private static methods.
     *
     * @see {@link Mockito#when(Object)}
     * @throws Exception
     *             If something unexpected goes wrong.
     */
    public static <T> WithOrWithoutExpectedArguments<T> when(Class<?> cls, Method method) throws Exception {
        return new DefaultMethodExpectationSetup<T>(cls, method);
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
     * Expect a static private or inner class method call.
     *
     * @see {@link Mockito#when(Object)}
     * @throws Exception
     *             If something unexpected goes wrong.
     */
    public static <T> OngoingStubbing<T> when(Class<?> clazz, String methodToExpect, Object... arguments)
            throws Exception {
        return Mockito.when(Whitebox.<T> invokeMethod(clazz, methodToExpect, arguments));
    }

    /**
     * Expect calls to private static methods without having to specify the
     * method name. The method will be looked up using the parameter types if
     * possible
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
     * want to throw an exception or return a mock.
     */
    public static synchronized <T> WithOrWithoutExpectedArguments<T> whenNew(Constructor<T> ctor) {
        return new ConstructorAwareExpectationSetup<T>(ctor);
    }

    /**
     * Allows specifying expectations on new invocations. For example you might
     * want to throw an exception or return a mock.
     */
    public static synchronized <T> ConstructorExpectationSetup<T> whenNew(Class<T> type) {
        return new DefaultConstructorExpectationSetup<T>(type);
    }

    /**
     * Allows specifying expectations on new invocations for private member
     * (inner) classes, local or anonymous classes. For example you might want
     * to throw an exception or return a mock.
     *
     * @param fullyQualifiedName
     *            The fully-qualified name of the inner/local/anonymous type to
     *            expect.
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
}
