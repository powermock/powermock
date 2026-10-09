package org.powermock.modules.junit5.internal;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * Redirects a Jupiter method invocation (test or lifecycle method) to the same method of the
 * MockClassLoader-loaded test class / shadow instance, with the MockClassLoader as context class loader.
 */
public class MockClassLoaderInvoker {

    public static void invoke(TestClassInClassLoader loaded, ReflectiveInvocationContext<Method> ic,
                              ExtensionContext context) throws Throwable {
        final Method method = findMethod(loaded.getClassLoader(), ic.getExecutable());
        final Object target = ic.getTarget().isPresent()
            ? loaded.getShadowInstance(ic.getTarget().get(), context)
            : null;
        final Object[] args = ic.getArguments().toArray();
        try {
            withContextClassLoader(loaded.getClassLoader(), () -> method.invoke(target, args));
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

    static Method findMethod(ClassLoader classLoader, Method original) throws ClassNotFoundException, NoSuchMethodException {
        Class<?> declaringClass = Class.forName(original.getDeclaringClass().getName(), false, classLoader);
        Class<?>[] originalTypes = original.getParameterTypes();
        Class<?>[] types = new Class<?>[originalTypes.length];
        for (int i = 0; i < types.length; i++) {
            types[i] = originalTypes[i].isPrimitive() ? originalTypes[i]
                : Class.forName(originalTypes[i].getName(), false, classLoader);
        }
        Method method = declaringClass.getDeclaredMethod(original.getName(), types);
        method.setAccessible(true);
        return method;
    }

    public static <T> T withContextClassLoader(ClassLoader classLoader, Callable<T> callable) throws Exception {
        Thread thread = Thread.currentThread();
        ClassLoader previous = thread.getContextClassLoader();
        thread.setContextClassLoader(classLoader);
        try {
            return callable.call();
        } finally {
            thread.setContextClassLoader(previous);
        }
    }
}
