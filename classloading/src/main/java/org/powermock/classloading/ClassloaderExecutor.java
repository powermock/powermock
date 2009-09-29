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
package org.powermock.classloading;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import org.powermock.api.support.DeepCloner;
import org.powermock.reflect.Whitebox;

public class ClassloaderExecutor {

    private final ClassLoader classloader;

    public ClassloaderExecutor(ClassLoader classloader) {
        this.classloader = classloader;
    }

    @SuppressWarnings("unchecked")
    public <T> T execute(Callable<T> callable) {
        return (T) execute(callable, Whitebox.getMethod(callable.getClass(), "call"));
    }

    public void execute(Runnable runnable) {
        execute(Executors.callable(runnable));
    }

    private Object execute(Object instance, Method method, Object... arguments) {
        final Object objectLoadedWithClassloader = DeepCloner.clone(classloader, instance);
        final Object[] argumentsLoadedByClassLoader = new Object[arguments.length];
        for (int i = 0; i < arguments.length; i++) {
            final Object argument = arguments[i];
            argumentsLoadedByClassLoader[i] = DeepCloner.clone(classloader, argument);
        }

        final Object result;
        try {
            result = Whitebox.invokeMethod(objectLoadedWithClassloader, method.getName(), argumentsLoadedByClassLoader);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result == null ? null : DeepCloner.clone(Thread.currentThread().getContextClassLoader(), result);
    }
}