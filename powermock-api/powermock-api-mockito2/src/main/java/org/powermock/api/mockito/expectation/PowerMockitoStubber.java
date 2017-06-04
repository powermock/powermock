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
package org.powermock.api.mockito.expectation;

import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.mockito.stubbing.Stubber;

import java.lang.reflect.Method;

/**
 * Setup stubbing for private or void methods in final class, final void
 * methods, or static (final) methods.
 */
public interface PowerMockitoStubber extends Stubber {
    
    /**
     * Allows to choose a static method when stubbing in
     * doThrow()|doAnswer()|doNothing()|doReturn() style
     * <p>
     * Example:
     * </p>
     * <pre>
     * doThrow(new RuntimeException()).when(StaticList.class);
     * StaticList.clear();
     *
     * //following throws RuntimeException:
     * StaticList.clear();
     * </pre>
     * <p>
     * Read more about those methods:
     * </p>
     * <p>
     * {@link Mockito#doThrow(Class)}
     * </p>
     * <p>
     * {@link Mockito#doAnswer(Answer)}
     * </p>
     * <p>
     * {@link Mockito#doNothing()}
     * </p>
     * <p>
     * {@link Mockito#doReturn(Object)}
     * </p>
     *
     * @param classMock the mock class
     * @see Mockito
     */
    void when(Class<?> classMock);
    
    /**
     * Allows to mock a private instance method when stubbing in
     * doThrow()|doAnswer()|doNothing()|doReturn() style.
     * <p>
     * Example:
     * <pre>
     * doThrow(new RuntimeException()).when(instance, method(&quot;myMethod&quot;)).withNoArguments();
     * </pre>
     * </p>
     * <p>
     * Read more about those methods:
     * </p>
     * <p>
     * {@link Mockito#doThrow(Class)}
     * </p>
     * <p>
     * {@link Mockito#doAnswer(Answer)}
     * </p>
     * <p>
     * {@link Mockito#doNothing()}
     * </p>
     * <p>
     * {@link Mockito#doReturn(Object)}
     * </p>
     *
     * @param mock   the method
     * @param method private  method to be mocked
     * @see Mockito
     */
    <T> PrivatelyExpectedArguments when(T mock, Method method) throws Exception;
    
    /**
     * Allows to mock a private instance method based on the parameters when
     * stubbing in doThrow()|doAnswer()|doNothing()|doReturn() style.
     * <p>
     * Example:
     * </p>
     * <p>
     * <pre>
     * doThrow(new RuntimeException()).when(instance, parameter1, parameter2);
     * </pre>
     * </p>
     * <p>
     * Read more about those methods:
     * </p>
     * <p>
     * {@link Mockito#doThrow(Throwable...)}
     * </p>
     * <p>
     * {@link Mockito#doAnswer(Answer)}
     * </p>
     * <p>
     * {@link Mockito#doNothing()}
     * </p>
     * <p>
     * {@link Mockito#doReturn(Object)}
     * </p>
     *
     * @param mock      the Mock
     * @param arguments array of arguments is used to find suitable method to be mocked.
     * @see Mockito
     */
    <T> void when(T mock, Object... arguments) throws Exception;
    
    /**
     * Allows to mock a private instance method based on method name and
     * parameters when stubbing in doThrow()|doAnswer()|doNothing()|doReturn()
     * style.
     * <p>
     * Example:
     * </p>
     * <pre>
     * doThrow(new RuntimeException()).when(instance, &quot;methodName&quot;, parameter1, parameter2);
     * </pre>
     * </p>
     * <p>
     * Read more about those methods:
     * </p>
     * <p>
     * {@link Mockito#doThrow(Throwable...)}
     * </p>
     * <p>
     * {@link Mockito#doAnswer(Answer)}
     * </p>
     * <p>
     * {@link Mockito#doNothing()}
     * </p>
     * <p>
     * {@link Mockito#doReturn(Object)}
     * </p>
     *
     * @param mock           the Mock
     * @param methodToExpect name of method which have to mocked
     * @param arguments      array of arguments of <code>methodToExpect</code>
     * @see Mockito
     */
    <T> void when(T mock, String methodToExpect, Object... arguments) throws Exception;
    
    /**
     * Allows to mock a static private method when stubbing in
     * doThrow()|doAnswer()|doNothing()|doReturn() style.
     * <p>
     * Example:
     * <pre>
     * doThrow(new RuntimeException()).when(MyClass.class, method(&quot;myMethod&quot;)).withNoArguments();
     * </pre>
     * </p>
     * <p>
     * Read more about those methods:
     * </p>
     * <p>
     * {@link Mockito#doThrow(Throwable...)}
     * </p>
     * <p>
     * {@link Mockito#doAnswer(Answer)}
     * </p>
     * <p>
     * {@link Mockito#doNothing()}
     * </p>
     * <p>
     * {@link Mockito#doReturn(Object)}
     * </p>
     *
     * @param classMock class owner of private static method
     * @param method    private static method to be mocked
     * @see Mockito
     */
    <T> PrivatelyExpectedArguments when(Class<T> classMock, Method method) throws Exception;
    
    /**
     * Allows to mock a static private method based on the parameters when
     * stubbing in doThrow()|doAnswer()|doNothing()|doReturn() style.
     * <p>
     * Example:
     * <pre>
     * doThrow(new RuntimeException()).when(MyClass.class, parameter1, parameter2);
     * </pre>
     * </p>
     * <p>
     * Read more about those methods:
     * </p>
     * <p>
     * {@link Mockito#doThrow(Throwable...)}
     * </p>
     * <p>
     * {@link Mockito#doAnswer(Answer)}
     * </p>
     * <p>
     * {@link Mockito#doNothing()}
     * </p>
     * <p>
     * {@link Mockito#doReturn(Object)}
     * </p>
     *
     * @param classMock class owner of private static method
     * @param arguments array of arguments is used to find suitable method to be mocked.
     * @see Mockito
     */
    <T> void when(Class<T> classMock, Object... arguments) throws Exception;
    
    /**
     * Allows to mock a static private method based on method name and
     * parameters when stubbing in doThrow()|doAnswer()|doNothing()|doReturn()
     * style.
     * <p>
     * Example:
     * <pre>
     * doThrow(new RuntimeException()).when(MyClass.class, &quot;methodName&quot;, parameter1, parameter2);
     * </pre>
     * </p>
     * <p>
     * Read more about those methods:
     * </p>
     * <p>
     * {@link Mockito#doThrow(Throwable...)}
     * </p>
     * <p>
     * {@link Mockito#doAnswer(Answer)}
     * </p>
     * <p>
     * {@link Mockito#doNothing()}
     * </p>
     * <p>
     * {@link Mockito#doReturn(Object)}
     * </p>
     *
     * @param classMock      the class owner of static private method
     * @param methodToExpect name of method which have to mocked
     * @param arguments      array of arguments of <code>methodToExpect</code>
     * @see Mockito
     */
    <T> void when(Class<T> classMock, String methodToExpect, Object... arguments) throws Exception;
}
