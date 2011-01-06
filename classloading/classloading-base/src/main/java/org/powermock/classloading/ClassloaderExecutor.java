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

import org.powermock.api.support.ClassLoaderUtil;
import org.powermock.api.support.SafeExceptionRethrower;
import org.powermock.classloading.spi.DeepClonerSPI;
import org.powermock.classloading.spi.DoNotClone;
import org.powermock.reflect.Whitebox;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * A ClassLoaderExecutor can run any code in any classloader. E.g. assume you have a classloader
 * called myClassloader and you want to execute a (void) method called myMethod in myObject using this CL:
 * <pre>
 *     ClassloaderExecutor cle = new ClassloaderExecutor(myClassloader);
 *     cle.execute(new Runnable() {
 *          public void run() {
 *             myObject.myMethod();
 *          }
 *     });
 * </pre>
 *
 * What happens is that the entire object graph of myObject is deep-cloned into the <code>myClassloader</code> classloader
 * and then the <code>myObject.myMethod()</code> is executed.
 * <p>
 * You can also execute methods that return something:
 * <pre>
 *     ClassloaderExecutor cle = new ClassloaderExecutor(myClassloader);
 *     MyResult result = cle.execute(new Callable<MyResult>() {
 *          public MyResult call() throws Exception {
 *             return myObject.myMethod();
 *          }
 *     });
 * </pre>
 * Here we imagine that <code>myObject.myMethod()</code> returns an object of type <code>MyResult</code>. Again the entire
 * state will be deep-cloned to  <code>myClassloader</code> and then the <code>myObject.myMethod()</code> is executed.
 * The result of the method call is deep-cloned back into the original classloader (the one that made the call to
 * <code>cle.execute(..)</code>) and is ready for use.
 * </p>
 * <p>
 * Note that the ClassloaderExecutor requires a deep cloner implementing the {@link DeepClonerSPI} present in the class-path.
 * </p>
 */
public class ClassloaderExecutor {

	@DoNotClone
	private final ClassLoader classloader;

	public ClassloaderExecutor(ClassLoader classloader) {
		this.classloader = classloader;
	}

	@SuppressWarnings("unchecked")
	public <T> T execute(Callable<T> callable) {
		assertArgumentNotNull(callable, "callable");
		return (T) execute(callable, Whitebox.getMethod(callable.getClass(), "call"));
	}

	public void execute(Runnable runnable) {
		assertArgumentNotNull(runnable, "runnable");
		execute(runnable, Whitebox.getMethod(runnable.getClass(), "run"));
	}

	private void assertArgumentNotNull(Object object, String argumentName) {
		if (object == null) {
			throw new IllegalArgumentException(argumentName + " cannot be null.");
		}
	}

	private Object execute(Object instance, Method method, Object... arguments) {
        final DeepClonerSPI deepCloner = createDeepCloner(classloader);
		final Object objectLoadedWithClassloader = deepCloner.clone(instance);
		final Object[] argumentsLoadedByClassLoader = new Object[arguments.length];
		for (int i = 0; i < arguments.length; i++) {
			final Object argument = arguments[i];
			argumentsLoadedByClassLoader[i] = deepCloner.clone(argument);
		}

		Object result = null;
		try {
			result = Whitebox.invokeMethod(objectLoadedWithClassloader, method.getName(), argumentsLoadedByClassLoader);
		} catch (Exception e) {
			SafeExceptionRethrower.safeRethrow(e);
		}
		return result == null ? null : createDeepCloner(getClass().getClassLoader()).clone(result);
	}

    private DeepClonerSPI createDeepCloner(ClassLoader classLoader) {
        final Class<DeepClonerSPI> deepClonerClass = ClassLoaderUtil.loadClass("org.powermock.classloading.DeepCloner");
        final Constructor<DeepClonerSPI> constructor = Whitebox.getConstructor(deepClonerClass, ClassLoader.class);
        try {
            return constructor.newInstance(classLoader);
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate DeepCloner. The DeepCloner implementation must have a one-arg constructor taking a Classloader as parameter.", e);
        }
    }
}
