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
package samples.powermockito.junit4.proxymethod;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.proxy;
import static org.powermock.api.mockito.PowerMockito.when;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import samples.suppressmethod.SuppressMethod;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SuppressMethod.class)
public class ProxyMethodTest {

	@Test(expected = ArrayStoreException.class)
	public void expectionThrowingMethodProxyWorksForJavaLangReflectMethods() throws Exception {
		proxy(Whitebox.getMethod(SuppressMethod.class, "getObject"), new ThrowingInvocationHandler());

		new SuppressMethod().getObject();
	}

	@Test(expected = ArrayStoreException.class)
	public void expectionThrowingMethodProxyWorksForMethodNames() throws Exception {
		proxy(SuppressMethod.class, "getObject", new ThrowingInvocationHandler());

		new SuppressMethod().getObject();
	}

	@Test
	public void returnValueChangingMethodProxyWorksForMethodNames() throws Exception {
		proxy(SuppressMethod.class, "getObject", new ReturnValueChangingInvocationHandler());

		assertEquals("hello world", new SuppressMethod().getObject());
	}

	@Test
	public void delegatingMethodProxyWorksForMethodNames() throws Exception {
		proxy(SuppressMethod.class, "getObject", new DelegatingInvocationHandler());

		assertSame(SuppressMethod.OBJECT, new SuppressMethod().getObject());
	}

	@Test
	public void mockingAndMethodProxyAtTheSameTimeWorks() throws Exception {
		proxy(SuppressMethod.class, "getObjectStatic", new DelegatingInvocationHandler());
		SuppressMethod tested = mock(SuppressMethod.class);

		when(tested.getObject()).thenReturn("Hello world");

		assertSame(SuppressMethod.OBJECT, SuppressMethod.getObjectStatic());

		assertEquals("Hello world", tested.getObject());

		verify(tested).getObject();
	}

	private final class ThrowingInvocationHandler implements InvocationHandler {
		public Object invoke(Object object, Method method, Object[] arguments) throws Throwable {
			throw new ArrayStoreException();
		}
	}

	private final class ReturnValueChangingInvocationHandler implements InvocationHandler {
		public Object invoke(Object object, Method method, Object[] arguments) throws Throwable {
			return "hello world";
		}
	}

	private final class DelegatingInvocationHandler implements InvocationHandler {
		public Object invoke(Object object, Method method, Object[] arguments) throws Throwable {
			return method.invoke(object, arguments);
		}
	}
}
