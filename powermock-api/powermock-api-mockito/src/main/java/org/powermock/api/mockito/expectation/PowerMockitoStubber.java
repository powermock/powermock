/*
 * Copyright 2009 the original author or authors.
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
	 * 
	 * <pre>
	 * doThrow(new RuntimeException()).when(StaticList.class);
	 * StaticList.clear();
	 * 
	 * //following throws RuntimeException:
	 * StaticList.clear();
	 * </pre>
	 * 
	 * Read more about those methods:
	 * <p>
	 * {@link Mockito#doThrow(Throwable)}
	 * <p>
	 * {@link Mockito#doAnswer(Answer)}
	 * <p>
	 * {@link Mockito#doNothing()}
	 * <p>
	 * {@link Mockito#doReturn(Object)}
	 * <p>
	 * 
	 * See examples in javadoc for {@link Mockito}
	 */
	void when(Class<?> classMock);

	/**
	 * Allows to mock a private instance method when stubbing in
	 * doThrow()|doAnswer()|doNothing()|doReturn() style.
	 * <p>
	 * Example:
	 * 
	 * <pre>
	 * doThrow(new RuntimeException()).when(instance, method(&quot;myMethod&quot;)).withNoArguments();
	 * </pre>
	 * 
	 * Read more about those methods:
	 * <p>
	 * {@link Mockito#doThrow(Throwable)}
	 * <p>
	 * {@link Mockito#doAnswer(Answer)}
	 * <p>
	 * {@link Mockito#doNothing()}
	 * <p>
	 * {@link Mockito#doReturn(Object)}
	 * <p>
	 * 
	 * See examples in javadoc for {@link Mockito}
	 */
	<T> PrivatelyExpectedArguments when(T mock, Method method) throws Exception;

	/**
	 * Allows to mock a private instance method based on the parameters when
	 * stubbing in doThrow()|doAnswer()|doNothing()|doReturn() style.
	 * <p>
	 * Example:
	 * 
	 * <pre>
	 * doThrow(new RuntimeException()).when(instance, parameter1, parameter2);
	 * </pre>
	 * 
	 * Read more about those methods:
	 * <p>
	 * {@link Mockito#doThrow(Throwable)}
	 * <p>
	 * {@link Mockito#doAnswer(Answer)}
	 * <p>
	 * {@link Mockito#doNothing()}
	 * <p>
	 * {@link Mockito#doReturn(Object)}
	 * <p>
	 * 
	 * See examples in javadoc for {@link Mockito}
	 */
	<T> void when(T mock, Object... arguments) throws Exception;

	/**
	 * Allows to mock a private instance method based on method name and
	 * parameters when stubbing in doThrow()|doAnswer()|doNothing()|doReturn()
	 * style.
	 * <p>
	 * Example:
	 * 
	 * <pre>
	 * doThrow(new RuntimeException()).when(instance, &quot;methodName&quot;, parameter1, parameter2);
	 * </pre>
	 * 
	 * Read more about those methods:
	 * <p>
	 * {@link Mockito#doThrow(Throwable)}
	 * <p>
	 * {@link Mockito#doAnswer(Answer)}
	 * <p>
	 * {@link Mockito#doNothing()}
	 * <p>
	 * {@link Mockito#doReturn(Object)}
	 * <p>
	 * 
	 * See examples in javadoc for {@link Mockito}
	 */
	<T> void when(T mock, String methodToExpect, Object... arguments) throws Exception;

	/**
	 * Allows to mock a static private method when stubbing in
	 * doThrow()|doAnswer()|doNothing()|doReturn() style.
	 * <p>
	 * Example:
	 * 
	 * <pre>
	 * doThrow(new RuntimeException()).when(MyClass.class, method(&quot;myMethod&quot;)).withNoArguments();
	 * </pre>
	 * 
	 * Read more about those methods:
	 * <p>
	 * {@link Mockito#doThrow(Throwable)}
	 * <p>
	 * {@link Mockito#doAnswer(Answer)}
	 * <p>
	 * {@link Mockito#doNothing()}
	 * <p>
	 * {@link Mockito#doReturn(Object)}
	 * <p>
	 * 
	 * See examples in javadoc for {@link Mockito}
	 */
	<T> PrivatelyExpectedArguments when(Class<T> classMock, Method method) throws Exception;

	/**
	 * Allows to mock a static private method based on the parameters when
	 * stubbing in doThrow()|doAnswer()|doNothing()|doReturn() style.
	 * <p>
	 * Example:
	 * 
	 * <pre>
	 * doThrow(new RuntimeException()).when(MyClass.class, parameter1, parameter2);
	 * </pre>
	 * 
	 * Read more about those methods:
	 * <p>
	 * {@link Mockito#doThrow(Throwable)}
	 * <p>
	 * {@link Mockito#doAnswer(Answer)}
	 * <p>
	 * {@link Mockito#doNothing()}
	 * <p>
	 * {@link Mockito#doReturn(Object)}
	 * <p>
	 * 
	 * See examples in javadoc for {@link Mockito}
	 */
	<T> void when(Class<T> classMock, Object... arguments) throws Exception;

	/**
	 * Allows to mock a static private method based on method name and
	 * parameters when stubbing in doThrow()|doAnswer()|doNothing()|doReturn()
	 * style.
	 * <p>
	 * Example:
	 * 
	 * <pre>
	 * doThrow(new RuntimeException()).when(MyClass.class, &quot;methodName&quot;, parameter1, parameter2);
	 * </pre>
	 * 
	 * Read more about those methods:
	 * <p>
	 * {@link Mockito#doThrow(Throwable)}
	 * <p>
	 * {@link Mockito#doAnswer(Answer)}
	 * <p>
	 * {@link Mockito#doNothing()}
	 * <p>
	 * {@link Mockito#doReturn(Object)}
	 * <p>
	 * 
	 * See examples in javadoc for {@link Mockito}
	 */
	<T> void when(Class<T> classMock, String methodToExpect, Object... parameters) throws Exception;
}
