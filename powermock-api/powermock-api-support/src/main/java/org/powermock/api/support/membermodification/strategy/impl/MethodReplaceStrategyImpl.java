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

import org.powermock.api.support.MethodProxy;
import org.powermock.api.support.membermodification.strategy.MethodReplaceStrategy;
import org.powermock.reflect.internal.WhiteboxImpl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class MethodReplaceStrategyImpl implements MethodReplaceStrategy {

    private final Method method;

    public MethodReplaceStrategyImpl(Method method) {
        if (method == null) {
            throw new IllegalArgumentException("Cannot replace a null method.");
        }
        this.method = method;
    }

    @Override
    public void with(Method method) {
        if (method == null) {
            throw new IllegalArgumentException("A metod cannot be replaced with null.");
        }
        if (!Modifier.isStatic(this.method.getModifiers())) {
            throw new IllegalArgumentException(String.format("Replace requires static methods, '%s' is not static", this.method));
        } else if (!Modifier.isStatic(method.getModifiers())) {
            throw new IllegalArgumentException(String.format("Replace requires static methods, '%s' is not static", method));
        } else  if(!this.method.getReturnType().isAssignableFrom(method.getReturnType())) {
            throw new IllegalArgumentException(String.format("The replacing method (%s) needs to return %s and not %s.",method.toString(), this.method.getReturnType().getName(), method.getReturnType().getName()));
        } else if(!WhiteboxImpl.checkIfParameterTypesAreSame(this.method.isVarArgs(),this.method.getParameterTypes(), method.getParameterTypes())) {
            throw new IllegalArgumentException(String.format("The replacing method, \"%s\", needs to have the same number of parameters of the same type as as method \"%s\".",method.toString(),this.method.toString()));
        } else {
            MethodProxy.proxy(this.method, new MethodInvocationHandler(method));
        }
    }

    @Override
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

        @Override
        public Object invoke(Object object, Method invokingMethod, Object[] arguments) throws Throwable {
            return methodDelegator.invoke(object, arguments);
        }
    }
}
