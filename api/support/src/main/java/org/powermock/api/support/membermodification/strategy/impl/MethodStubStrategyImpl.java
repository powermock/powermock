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
package org.powermock.api.support.membermodification.strategy.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.powermock.api.support.MethodProxy;
import org.powermock.api.support.Stubber;
import org.powermock.api.support.membermodification.strategy.MethodStubStrategy;

public class MethodStubStrategyImpl<T> implements MethodStubStrategy<T> {

	private final Method method;

	public MethodStubStrategyImpl(Method method) {
		if (method == null) {
			throw new IllegalArgumentException("Method to stub cannot be null.");
		}
		this.method = method;
	}

	@Deprecated
	public void andReturn(T returnValue) {
		toReturn(returnValue);
	}

	public void toThrow(final Throwable throwable) {
		InvocationHandler throwingInvocationHandler = new InvocationHandler() {

			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				throw throwable;
			}
		};
		MethodProxy.proxy(method, throwingInvocationHandler);
	}

	public void toReturn(T returnValue) {
		Stubber.stubMethod(method, returnValue);
	}
}
