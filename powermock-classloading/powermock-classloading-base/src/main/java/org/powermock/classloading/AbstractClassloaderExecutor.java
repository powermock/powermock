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

package org.powermock.classloading;

import org.powermock.api.support.ClassLoaderUtil;
import org.powermock.api.support.SafeExceptionRethrower;
import org.powermock.classloading.spi.DeepClonerSPI;
import org.powermock.reflect.Whitebox;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public abstract class AbstractClassloaderExecutor implements ClassloaderExecutor {
    @Override
    @SuppressWarnings("unchecked")
    public <T> T execute(Callable<T> callable) {
        assertArgumentNotNull(callable, "callable");
        return (T) execute(callable, Whitebox.getMethod(callable.getClass(), "call"));
    }
    
    @Override
    public void execute(Runnable runnable) {
        assertArgumentNotNull(runnable, "runnable");
        execute(runnable, Whitebox.getMethod(runnable.getClass(), "run"));
    }
    
    private void assertArgumentNotNull(Object object, String argumentName) {
        if (object == null) {
            throw new IllegalArgumentException(argumentName + " cannot be null.");
        }
    }
    
    protected abstract Object execute(Object instance, Method method, Object... arguments);
    
    Object executeWithClassLoader(Object instance, Method method, ClassLoader classloader, Object[] arguments) {
        final DeepClonerSPI deepCloner = createDeepCloner(classloader);
        final Object objectLoadedWithClassloader = deepCloner.clone(instance);
        final Object[] argumentsLoadedByClassLoader = cloneArguments(arguments, deepCloner);
        
        return invokeWithClassLoader(classloader, method, objectLoadedWithClassloader, argumentsLoadedByClassLoader);
    }
    
    private Object invokeWithClassLoader(final ClassLoader classloader, final Method method, final Object objectLoadedWithClassloader,
                                         final Object[] argumentsLoadedByClassLoader) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(classloader);
            Object result = getResult(method, objectLoadedWithClassloader, argumentsLoadedByClassLoader);
            return cloneResult(result);
        } finally {
            Thread.currentThread().setContextClassLoader(classLoader);
        }
    }
    
    private Object cloneResult(Object result) {return result == null ? null : createDeepCloner(getClass().getClassLoader()).clone(result);}
    
    private Object getResult(Method method, Object objectLoadedWithClassloader, Object[] argumentsLoadedByClassLoader) {
        Object result = null;
        try {
            result = Whitebox.invokeMethod(objectLoadedWithClassloader, method.getName(), argumentsLoadedByClassLoader);
        } catch (Exception e) {
            SafeExceptionRethrower.safeRethrow(e);
        }
        return result;
    }
    
    private Object[] cloneArguments(Object[] arguments, DeepClonerSPI deepCloner) {
        final Object[] argumentsLoadedByClassLoader = new Object[arguments.length];
        for (int i = 0; i < arguments.length; i++) {
            final Object argument = arguments[i];
            argumentsLoadedByClassLoader[i] = deepCloner.clone(argument);
        }
        return argumentsLoadedByClassLoader;
    }
    
    private DeepClonerSPI createDeepCloner(ClassLoader classLoader) {
        final Class<DeepClonerSPI> deepClonerClass = ClassLoaderUtil.loadClass("org.powermock.classloading.DeepCloner");
        ClassLoader currentCL = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(deepClonerClass.getClass().getClassLoader());
            return doCreateDeepCloner(classLoader, deepClonerClass);
        } finally {
            Thread.currentThread().setContextClassLoader(currentCL);
        }
    }
    
    private DeepClonerSPI doCreateDeepCloner(final ClassLoader classLoader, final Class<DeepClonerSPI> deepClonerClass) {
        final Constructor<DeepClonerSPI> constructor = Whitebox.getConstructor(deepClonerClass, ClassLoader.class);
        try {
            return constructor.newInstance(classLoader);
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate DeepCloner. The DeepCloner implementation must have a one-arg constructor taking a Classloader as parameter.", e);
        }
    }
}
