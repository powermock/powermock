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
import java.lang.reflect.Modifier;

import org.powermock.api.support.MethodProxy;
import org.powermock.api.support.membermodification.strategy.MethodReplaceStrategy;

public class MethodReplaceStrategyImpl implements MethodReplaceStrategy {

	private final Method method;

	public MethodReplaceStrategyImpl(Method method) {
		if (method == null) {
			throw new IllegalArgumentException("Cannot replace a null method.");
		}
		this.method = method;
	}

	public void with(Method method) {
		if (method == null) {
			throw new IllegalArgumentException("A metod cannot be replaced with null.");
		}
		if (!Modifier.isStatic(this.method.getModifiers())) {
			throw new IllegalArgumentException(String.format("Replace requires static methods, '%s' is not static", this.method));
		} else if (!Modifier.isStatic(method.getModifiers())) {
			throw new IllegalArgumentException(String.format("Replace requires static methods, '%s' is not static", method));
		} else {
			MethodProxy.proxy(this.method, new MethodInvocationHandler(method));
		}
	}

	public void with(InvocationHandler invocationHandler) {
		if (invocationHandler == null) {
			throw new IllegalArgumentException("Invocation handler cannot be null");
		}
		MethodProxy.proxy(method, invocationHandler);
	}

	private final class MethodInvocationHandler implements InvocationHandler {
		private final Method methodDelegator;

		public MethodInvocationHandler(Method methodDelegator) {
			this.methodDelegator = methodDelegator;
		}

		public Object invoke(Object object, Method invokingMethod, Object[] arguments) throws Throwable {
			return methodDelegator.invoke(object, arguments);
		}
	}
}
