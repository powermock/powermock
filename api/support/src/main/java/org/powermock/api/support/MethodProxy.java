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
package org.powermock.api.support;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.powermock.core.MockRepository;
import org.powermock.reflect.Whitebox;
import org.powermock.reflect.exceptions.MethodNotFoundException;
import org.powermock.reflect.exceptions.TooManyMethodsFoundException;

public class MethodProxy {
	/**
	 * Add a proxy for this method. Each call to the method will be routed to
	 * the invocationHandler instead.
	 */
	public static void proxy(Method method, InvocationHandler invocationHandler) {
		assertInvocationHandlerNotNull(invocationHandler);
		MockRepository.putMethodProxy(method, invocationHandler);
	}

	/**
	 * Add a proxy for a method declared in class <code>declaringClass</code>.
	 * Each call to the method will be routed to the invocationHandler instead.
	 */
	public static void proxy(Class<?> declaringClass, String methodName, InvocationHandler invocationHandler) {
		assertInvocationHandlerNotNull(invocationHandler);
		if (declaringClass == null) {
			throw new IllegalArgumentException("declaringClass cannot be null");
		}
		if (methodName == null || methodName.length() == 0) {
			throw new IllegalArgumentException("methodName cannot be empty");
		}
		Method[] methods = Whitebox.getMethods(declaringClass, methodName);
		if (methods.length == 0) {
			throw new MethodNotFoundException(String.format("Couldn't find a method with name %s in the class hierarchy of %s", methodName,
					declaringClass.getName()));
		} else if (methods.length > 1) {
			throw new TooManyMethodsFoundException(String.format("Found %d methods with name %s in the class hierarchy of %s.", methods.length,
					methodName, declaringClass.getName()));
		}

		MockRepository.putMethodProxy(methods[0], invocationHandler);
	}

	private static void assertInvocationHandlerNotNull(InvocationHandler invocationHandler) {
		if (invocationHandler == null) {
			throw new IllegalArgumentException("invocationHandler cannot be null");
		}
	}
}
