/*
 *   Copyright 2016 the original author or authors.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package org.powermock.api.mockito;

import org.mockito.MockSettings;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.mockito.stubbing.OngoingStubbing;
import org.mockito.verification.VerificationMode;
import org.powermock.api.mockito.expectation.ConstructorAwareExpectationSetup;
import org.powermock.api.mockito.expectation.ConstructorExpectationSetup;
import org.powermock.api.mockito.expectation.DefaultConstructorExpectationSetup;
import org.powermock.api.mockito.expectation.PowerMockitoStubber;
import org.powermock.api.mockito.expectation.WithOrWithoutExpectedArguments;
import org.powermock.api.mockito.internal.PowerMockitoCore;
import org.powermock.api.mockito.internal.expectation.DefaultMethodExpectationSetup;
import org.powermock.api.mockito.internal.mockcreation.DefaultMockCreator;
import org.powermock.api.mockito.internal.verification.DefaultPrivateMethodVerification;
import org.powermock.api.mockito.internal.verification.VerifyNoMoreInteractions;
import org.powermock.api.mockito.verification.ConstructorArgumentsVerification;
import org.powermock.api.mockito.verification.PrivateMethodVerification;
import org.powermock.api.support.membermodification.MemberModifier;
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

    private static final PowerMockitoCore POWERMOCKITO_CORE = new PowerMockitoCore();
    
    /**
     * Enable static mocking for all methods of a class.
     *
     * @param type the class to enable static mocking
     */
    public static synchronized void mockStatic(Class<?> type, Class<?>... types) {
        DefaultMockCreator.mock(type, true, false, null, null, (Method[]) null);
        if (types != null && types.length > 0) {
            for (Class<?> aClass : types) {
                DefaultMockCreator.mock(aClass, true, false, null, null, (Method[]) null);
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
     * <p>
     * <pre>
     * mockStatic(Foo.class, RETURNS_SMART_NULLS);
     * mockStatic(Foo.class, new YourOwnAnswer());
     * </pre>
     *
     * @param classMock     class to mock
     * @param defaultAnswer default answer for unstubbed methods
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
     * <p>
     * <pre>
     *   mockStatic(Listener.class, withSettings()
     *     .name(&quot;firstListner&quot;).defaultBehavior(RETURNS_SMART_NULLS));
     *   );
     * </pre>
     * <p>
     * <b>Use it carefully and occasionally</b>. What might be reason your test
     * needs non-standard mocks? Is the code under test so complicated that it
     * requires non-standard mocks? Wouldn't you prefer to refactor the code
     * under test so it is testable in a simple way?
     * <p>
     * See also {@link Mockito#withSettings()}
     *
     * @param classToMock  class to mock
     * @param mockSettings additional mock settings
     */
    public static void mockStatic(Class<?> classToMock, MockSettings mockSettings) {
        DefaultMockCreator.mock(classToMock, true, false, null, mockSettings, (Method[]) null);
    }
    
    /**
     * Creates a mock object that supports mocking of final and native methods.
     *
     * @param <T>  the type of the mock object
     * @param type the type of the mock object
     * @return the mock object.
     */
    public static synchronized <T> T mock(Class<T> type) {
        return DefaultMockCreator.mock(type, false, false, null, null, (Method[]) null);
    }
    
    /**
     * Creates mock with a specified strategy for its answers to interactions.
     * It's quite advanced feature and typically you don't need it to write
     * decent tests. However it can be helpful when working with legacy systems.
     * <p>
     * It is the default answer so it will be used <b>only when you don't</b>
     * stub the method call.
     * <p>
     * <pre>
     * Foo mock = mock(Foo.class, RETURNS_SMART_NULLS);
     * Foo mockTwo = mock(Foo.class, new YourOwnAnswer());
     * </pre>
     * <p>
     * <p>
     * See examples in javadoc for {@link Mockito} class
     * </p>
     *
     * @param classToMock   class or interface to mock
     * @param defaultAnswer default answer for unstubbed methods
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
     * <p>
     * <pre>
     *   Listener mock = mock(Listener.class, withSettings()
     *     .name(&quot;firstListner&quot;).defaultBehavior(RETURNS_SMART_NULLS));
     *   );
     * </pre>
     * <p>
     * <b>Use it carefully and occasionally</b>. What might be reason your test
     * needs non-standard mocks? Is the code under test so complicated that it
     * requires non-standard mocks? Wouldn't you prefer to refactor the code
     * under test so it is testable in a simple way?
     * <p>
     * See also {@link Mockito#withSettings()}
     * <p>
     * See examples in javadoc for {@link Mockito} class
     *
     * @param classToMock  class or interface to mock
     * @param mockSettings additional mock settings
     * @return mock object
     */
    public static <T> T mock(Class<T> classToMock, MockSettings mockSettings) {
        return DefaultMockCreator.mock(classToMock, false, false, null, mockSettings, (Method[]) null);
    }
    
    /**
     * Spy on objects that are final or otherwise not &quot;spyable&quot; from
     * normal Mockito.
     *
     * @param <T>    the type of the mock object
     * @param object the object to spy on
     * @return the spy object.
     * @see PowerMockito#spy(Object)
     */
    @SuppressWarnings("unchecked")
    public static synchronized <T> T spy(T object) {
        MockSettings mockSettings = Mockito.withSettings().defaultAnswer(Mockito.CALLS_REAL_METHODS);
        return DefaultMockCreator.mock((Class<T>) Whitebox.getType(object), false, true, object, mockSettings, (Method[]) null);
    }
    
    /**
     * Spy on classes (not &quot;spyable&quot; from normal Mockito).
     *
     * @param <T>  the type of the class mock
     * @param type the type of the class mock
     * @see PowerMockito#spy(Object)
     */
    public static synchronized <T> void spy(Class<T> type) {
        MockSettings mockSettings = Mockito.withSettings().defaultAnswer(Mockito.CALLS_REAL_METHODS);
        DefaultMockCreator.mock(type, true, true, type, mockSettings, (Method[]) null);
    }
    
    /**
     * Verifies certain behavior of the <code>mockedClass</code> <b>happened once</b>
     * <p>
     * Alias to {@code verifyStatic(classMock, times(1))} E.g:
     * <p>
     * <pre>
     * verifyStatic(ClassWithStaticMethod.class);
     * ClassWithStaticMethod.someStaticMethod(&quot;some arg&quot;);
     * </pre>
     * <p>
     * Above is equivalent to:
     * <p>
     * <pre>
     * verifyStatic(ClassWithStaticMethod.class, times(1));
     * ClassWithStaticMethod.someStaticMethod(&quot;some arg&quot;);
     * </pre>
     * <p>
     * <p>
     * Although it is possible to verify a stubbed invocation, usually <b>it's
     * just redundant</b>. Let's say you've stubbed foo.bar(). If your code
     * cares what foo.bar() returns then something else breaks(often before even
     * verify() gets executed). If your code doesn't care what get(0) returns
     * then it should not be stubbed.
     *
     * @param mockedClass the mocked class behavior of that have to be verified.
     */
    public static synchronized <T> void verifyStatic(Class<T> mockedClass) {
        verifyStatic(mockedClass, times(1));
    }
    
    /**
     * Verifies certain behavior of the <code>mockedClass</code> happened at least once / exact number of times
     * / never. E.g:
     * <p>
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
     * <p>
     * <b>times(1) is the default</b> and can be omitted
     * <p>
     *
     * @param mockedClass      the mocked class behavior of that have to be verified.
     * @param verificationMode times(x), atLeastOnce() or never()
     */
    public static synchronized <T> void verifyStatic(Class<T> mockedClass, VerificationMode verificationMode) {
        Mockito.verify(mockedClass, verificationMode);
    }
    
    /**
     * Verify a private method invocation for an instance.
     *
     * @throws Exception If something unexpected goes wrong.
     * @see Mockito#verify(Object)
     */
    public static PrivateMethodVerification verifyPrivate(Object object) throws Exception {
        return verifyPrivate(object, times(1));
    }
    
    /**
     * Verify a private method invocation with a given verification mode.
     *
     * @throws Exception If something unexpected goes wrong.
     * @see Mockito#verify(Object)
     */
    public static PrivateMethodVerification verifyPrivate(Object object, VerificationMode verificationMode) throws Exception {
        Mockito.verify(object, verificationMode);
        return new DefaultPrivateMethodVerification(object);
    }
    
    /**
     * Verify a private method invocation for a class.
     *
     * @throws Exception If something unexpected goes wrong.
     * @see Mockito#verify(Object)
     */
    public static PrivateMethodVerification verifyPrivate(Class<?> clazz) throws Exception {
        return verifyPrivate((Object) clazz);
    }
    
    /**
     * Verify a private method invocation for a class with a given verification
     * mode.
     *
     * @throws Exception If something unexpected goes wrong.
     * @see Mockito#verify(Object)
     */
    public static PrivateMethodVerification verifyPrivate(Class<?> clazz, VerificationMode verificationMode) throws Exception {
        return verifyPrivate((Object) clazz, verificationMode);
    }
    
    /**
     * Verifies certain behavior <b>happened once</b>
     * <p>
     * Alias to <code>verifyNew(mockClass, times(1))</code> E.g:
     * <p>
     * <pre>
     * verifyNew(ClassWithStaticMethod.class);
     * </pre>
     * <p>
     * Above is equivalent to:
     * <p>
     * <pre>
     * verifyNew(ClassWithStaticMethod.class, times(1));
     * </pre>
     * <p>
     * <p>
     *
     * @param mock Class mocked by PowerMock.
     */
    @SuppressWarnings("unchecked")
    public static synchronized <T> ConstructorArgumentsVerification verifyNew(Class<T> mock) {
        return verifyNew(mock, times(1));
    }
    
    /**
     * Verifies certain behavior happened at least once / exact number of times
     * / never. E.g:
     * <p>
     * <pre>
     * verifyNew(ClassWithStaticMethod.class, times(5));
     *
     * verifyNew(ClassWithStaticMethod.class, atLeast(2));
     *
     * //you can use flexible argument matchers, e.g:
     * verifyNew(ClassWithStaticMethod.class, atLeastOnce());
     * </pre>
     * <p>
     * <b>times(1) is the default</b> and can be omitted
     * <p>
     *
     * @param mock to be verified
     * @param mode times(x), atLeastOnce() or never()
     */
    @SuppressWarnings("unchecked")
    public static <T> ConstructorArgumentsVerification verifyNew(Class<T> mock, VerificationMode mode) {
       return  POWERMOCKITO_CORE.verifyNew(mock, mode);
    }
    
    /**
     * Expect calls to private methods.
     *
     * @throws Exception If something unexpected goes wrong.
     * @see PowerMockito#when(Object)
     */
    public static <T> OngoingStubbing<T> when(Object instance, String methodName, Object... arguments) throws Exception {
        return Mockito.when(Whitebox.<T>invokeMethod(instance, methodName, arguments));
    }
    
    /**
     * Expect calls to private methods.
     *
     * @throws Exception If something unexpected goes wrong.
     * @see PowerMockito#when(Object)
     */
    public static <T> WithOrWithoutExpectedArguments<T> when(Object instance, Method method) throws Exception {
        return new DefaultMethodExpectationSetup<T>(instance, method);
    }
    
    /**
     * Expect calls to private static methods.
     *
     * @throws Exception If something unexpected goes wrong.
     * @see PowerMockito#when(Object)
     */
    public static <T> WithOrWithoutExpectedArguments<T> when(Class<?> cls, Method method) throws Exception {
        return new DefaultMethodExpectationSetup<T>(cls, method);
    }
    
    /**
     * Expect calls to private methods without having to specify the method
     * name. The method will be looked up using the parameter types (if
     * possible).
     *
     * @throws Exception If something unexpected goes wrong.
     * @see PowerMockito#when(Object)
     */
    public static <T> OngoingStubbing<T> when(Object instance, Object... arguments) throws Exception {
        return Mockito.when(Whitebox.<T>invokeMethod(instance, arguments));
    }
    
    /**
     * Expect a static private or inner class method call.
     *
     * @throws Exception If something unexpected goes wrong.
     * @see PowerMockito#when(Object)
     */
    public static <T> OngoingStubbing<T> when(Class<?> clazz, String methodToExpect, Object... arguments)
        throws Exception {
        return Mockito.when(Whitebox.<T>invokeMethod(clazz, methodToExpect, arguments));
    }
    
    /**
     * Expect calls to private static methods without having to specify the
     * method name. The method will be looked up using the parameter types if
     * possible
     *
     * @throws Exception If something unexpected goes wrong.
     * @see PowerMockito#when(Object)
     */
    public static <T> OngoingStubbing<T> when(Class<?> klass, Object... arguments) throws Exception {
        return Mockito.when(Whitebox.<T>invokeMethod(klass, arguments));
    }
    
    /**
     * Just delegates to the original {@link PowerMockito#when(Object)} method.
     *
     * @see PowerMockito#when(Object)
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
     * @param fullyQualifiedName The fully-qualified name of the inner/local/anonymous type to
     *                           expect.
     */
    @SuppressWarnings("unchecked")
    public static synchronized <T> ConstructorExpectationSetup<T> whenNew(String fullyQualifiedName) throws Exception {
        final Class<T> forName = (Class<T>) Class.forName(fullyQualifiedName);
        return new DefaultConstructorExpectationSetup<T>(forName);
    }
    
    /**
     * Checks if any of given mocks (can be both instance and class mocks) has
     * any unverified interaction. Delegates to the orignal
     * {@link PowerMockito#verifyNoMoreInteractions(Object...)} if the mock is not a
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
     * <p>
     * <p>
     * Example:
     * <p>
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
     * <p>
     * See examples in javadoc for {@link Mockito} class
     *
     * @param mocks to be verified
     */
    public static void verifyNoMoreInteractions(Object... mocks) {
        VerifyNoMoreInteractions.verifyNoMoreInteractions(mocks);
    }
    
    /**
     * Verifies that no interactions happened on given mocks (can be both
     * instance and class mocks). Delegates to the orignal
     * {@link PowerMockito#verifyNoMoreInteractions(Object...)} if the mock is not a
     * PowerMockito mock.
     * <p>
     * <pre>
     * verifyZeroInteractions(mockOne, mockTwo);
     * </pre>
     * <p>
     * This method will also detect invocations that occurred before the test
     * method, for example: in setUp(), &#064;Before method or in constructor.
     * Consider writing nice code that makes interactions only in test methods.
     * <p>
     * See also {@link Mockito#never()} - it is more explicit and communicates
     * the intent well.
     * <p>
     * See examples in javadoc for {@link Mockito} class
     *
     * @param mocks to be verified
     */
    //TODO cover by test
    public static void verifyZeroInteractions(Object... mocks) {
        VerifyNoMoreInteractions.verifyNoMoreInteractions(mocks);
    }
    
    /**
     * Use doAnswer() when you want to stub a void method with generic
     * {@link Answer}.
     * <p>
     * Stubbing voids requires different approach from
     * {@link PowerMockito#when(Object)} because the compiler does not like void
     * methods inside brackets...
     * <p>
     * Example:
     * <p>
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
     * @param answer to answer when the stubbed method is called
     * @return stubber - to select a method for stubbing
     */
    public static PowerMockitoStubber doAnswer(Answer<?> answer) {
        return POWERMOCKITO_CORE.doAnswer(answer);
    }
    
    /**
     * Use doThrow() when you want to stub the void method with an exception.
     * <p>
     * Stubbing voids requires different approach from
     * {@link PowerMockito#when(Object)} because the compiler does not like void
     * methods inside brackets...
     * <p>
     * Example:
     * <p>
     * <pre>
     * doThrow(new RuntimeException()).when(mock).someVoidMethod();
     * </pre>
     *
     * @param toBeThrown to be thrown when the stubbed method is called
     * @return stubber - to select a method for stubbing
     */
    public static PowerMockitoStubber doThrow(Throwable toBeThrown) {
        return POWERMOCKITO_CORE.doThrow(toBeThrown);
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
     * See also javadoc {@link PowerMockito#spy(Object)} to find out more about
     * partial mocks. <b>Mockito.spy() is a recommended way of creating partial
     * mocks.</b> The reason is it guarantees real methods are called against
     * correctly constructed object because you're responsible for constructing
     * the object passed to spy() method.
     * <p>
     * Example:
     * <p>
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
    //TODO cover by test
    public static PowerMockitoStubber doCallRealMethod() {
        return POWERMOCKITO_CORE.doCallRealMethod();
    }
    
    /**
     * Use doNothing() for setting void methods to do nothing. <b>Beware that
     * void methods on mocks do nothing by default!</b> However, there are rare
     * situations when doNothing() comes handy:
     * <p>
     * 1. Stubbing consecutive calls on a void method:
     * <p>
     * <pre>
     * doNothing().doThrow(new RuntimeException()).when(mock).someVoidMethod();
     *
     * //does nothing the first time:
     * mock.someVoidMethod();
     *
     * //throws RuntimeException the next time:
     * mock.someVoidMethod();
     * </pre>
     * <p>
     * 2. When you spy real objects and you want the void method to do nothing:
     * <p>
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
        return POWERMOCKITO_CORE.doNothing();
    }
    
    /**
     * Use doReturn() in those rare occasions when you cannot use
     * {@link PowerMockito#when(Object)}.
     * <p>
     * <b>Beware that {@link PowerMockito#when(Object)} is always recommended for
     * stubbing because it is argument type-safe and more readable</b>
     * (especially when stubbing consecutive calls).
     * <p>
     * Here are those rare occasions when doReturn() comes handy:
     * <p>
     * <p>
     * 1. When spying real objects and calling real methods on a spy brings side
     * effects
     * <p>
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
     * <p>
     * 2. Overriding a previous exception-stubbing:
     * <p>
     * <pre>
     * when(mock.foo()).thenThrow(new RuntimeException());
     *
     * //Impossible: the exception-stubbed foo() method is called so RuntimeException is thrown.
     * when(mock.foo()).thenReturn(&quot;bar&quot;);
     *
     * //You have to use doReturn() for stubbing:
     * doReturn(&quot;bar&quot;).when(mock).foo();
     * </pre>
     * <p>
     * Above scenarios shows a tradeoff of Mockito's ellegant syntax. Note that
     * the scenarios are very rare, though. Spying should be sporadic and
     * overriding exception-stubbing is very rare.
     * <p>
     * See examples in javadoc for {@link Mockito} class
     *
     * @param toBeReturned to be returned when the stubbed method is called
     * @return stubber - to select a method for stubbing
     */
    public static PowerMockitoStubber doReturn(Object toBeReturned) {
        return POWERMOCKITO_CORE.doReturn(toBeReturned);
    }
    
    
    /**
     * Same as {@link #doReturn(Object)} but sets consecutive values to be returned. Remember to use
     * <code>doReturn()</code> in those rare occasions when you cannot use {@link PowerMockito#when(Object)}.
     * <p>
     * <b>Beware that {@link PowerMockito#when(Object)} is always recommended for stubbing because it is argument type-safe
     * and more readable</b> (especially when stubbing consecutive calls).
     * <p>
     * Here are those rare occasions when doReturn() comes handy:
     * <p>
     * <p>
     * <ol>
     * <li>When spying real objects and calling real methods on a spy brings side effects
     * <p>
     * <pre class="code"><code class="java">
     * List list = new LinkedList();
     * List spy = spy(list);
     * <p>
     * //Impossible: real method is called so spy.get(0) throws IndexOutOfBoundsException (the list is yet empty)
     * when(spy.get(0)).thenReturn("foo", "bar", "qix");
     * <p>
     * //You have to use doReturn() for stubbing:
     * doReturn("foo", "bar", "qix").when(spy).get(0);
     * </code></pre>
     * </li>
     * <p>
     * <li>Overriding a previous exception-stubbing:
     * <pre class="code"><code class="java">
     * when(mock.foo()).thenThrow(new RuntimeException());
     * <p>
     * //Impossible: the exception-stubbed foo() method is called so RuntimeException is thrown.
     * when(mock.foo()).thenReturn("bar", "foo", "qix");
     * <p>
     * //You have to use doReturn() for stubbing:
     * doReturn("bar", "foo", "qix").when(mock).foo();
     * </code></pre>
     * </li>
     * </ol>
     * <p>
     * Above scenarios shows a trade-off of Mockito's elegant syntax. Note that the scenarios are very rare, though.
     * Spying should be sporadic and overriding exception-stubbing is very rare. Not to mention that in general
     * overridding stubbing is a potential code smell that points out too much stubbing.
     * <p>
     * See examples in javadoc for {@link PowerMockito} class
     *
     * @param toBeReturned       to be returned when the stubbed method is called
     * @param othersToBeReturned to be returned in consecutive calls when the stubbed method is called
     * @return stubber - to select a method for stubbing
     * @since 1.6.5
     */
    public static PowerMockitoStubber doReturn(Object toBeReturned, Object... othersToBeReturned) {
        return POWERMOCKITO_CORE.doAnswer(toBeReturned, othersToBeReturned);
    }
}
