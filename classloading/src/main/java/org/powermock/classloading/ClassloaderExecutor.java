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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import org.powermock.reflect.Whitebox;

public class ClassloaderExecutor {

    private static final String IGNORED_PACKAGES = "java.";
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
        final Class<?> typeLoadWithClassloader = loadClassWithClassloader(classloader, (Class<?>) Whitebox.getType(instance));
        final Object objectLoadedWithClassloader = instantiate(typeLoadWithClassloader, instance);
        final Object[] argumentsLoadedByClassLoader = new Object[arguments.length];
        for (int i = 0; i < arguments.length; i++) {
            final Object argument = arguments[i];
            argumentsLoadedByClassLoader[i] = instantiate(loadClassWithClassloader(classloader, Whitebox.getType(argument)), argument);
        }

        final Object result;
        try {
            result = Whitebox.invokeMethod(objectLoadedWithClassloader, method.getName(), argumentsLoadedByClassLoader);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result == null ? null
                : instantiate(loadClassWithClassloader(Thread.currentThread().getContextClassLoader(), (Class<?>) Whitebox.getType(result)), result);
    }

    @SuppressWarnings("unchecked")
    private <T> Class<T> loadClassWithClassloader(ClassLoader classloader, Class<T> type) {
        try {
            return (Class<T>) Class.forName(type.getName(), false, classloader);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private Object instantiate(Class<?> targetClass, Object source) {
        Object target = Whitebox.newInstance(targetClass);
        Class<?> currentTargetClass = targetClass;
        while (currentTargetClass != null && !currentTargetClass.getName().startsWith(IGNORED_PACKAGES)) {
            for (Field field : currentTargetClass.getDeclaredFields()) {
                field.setAccessible(true);
                try {
                    final Field declaredField = source.getClass().getDeclaredField(field.getName());
                    declaredField.setAccessible(true);
                    final Object object = declaredField.get(source);
                    final Object instantiatedValue;
                    if (Whitebox.getType(object).getName().startsWith(IGNORED_PACKAGES) || object == null) {
                        instantiatedValue = object;
                    } else {
                        instantiatedValue = instantiate(loadClassWithClassloader(targetClass.getClassLoader(), object.getClass()), object);
                    }
                    field.set(target, instantiatedValue);
                } catch (RuntimeException e) {
                    throw e;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            currentTargetClass = currentTargetClass.getSuperclass();
        }
        return target;
    }
}