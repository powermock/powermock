/*
 * Copyright 2008 the original author or authors.
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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.powermock.core.MockRepository;
import org.powermock.reflect.Whitebox;
import org.powermock.reflect.internal.WhiteboxImpl;

public class SuppressCode {
	/**
	 * Suppress constructor calls on specific constructors only.
	 */
	public static synchronized void suppressConstructor(Constructor<?>... constructors) {
		if (constructors == null) {
			throw new IllegalArgumentException("constructors cannot be null.");
		}
		for (Constructor<?> constructor : constructors) {
			MockRepository.addConstructorToSuppress(constructor);
			// Also suppress all parent constructors
			Class<?> declaringClass = constructor.getDeclaringClass();
			if (declaringClass != null) {
				suppressConstructor((Class<?>) declaringClass.getSuperclass());
			}
		}
	}

	/**
	 * This method can be used to suppress the code in a specific constructor.
	 * 
	 * @param clazz
	 *            The class where the constructor is located.
	 * @param parameterTypes
	 *            The parameter types of the constructor to suppress.
	 */
	public static synchronized void suppressSpecificConstructor(Class<?> clazz, Class<?>... parameterTypes) {
		MockRepository.addConstructorToSuppress(Whitebox.getConstructor(clazz, parameterTypes));
	}

	/**
	 * Suppress all constructors in the given class and it's super classes.
	 * 
	 * @param classes
	 *            The classes whose constructors will be suppressed.
	 */
	public static synchronized void suppressConstructor(Class<?>... classes) {
		for (Class<?> clazz : classes) {
			Class<?> tempClass = clazz;
			while (tempClass != Object.class) {
				suppressConstructor(tempClass, false);
				tempClass = tempClass.getSuperclass();
			}
		}
	}

	/**
	 * Suppress all constructors in the given class.
	 * 
	 * @param classes
	 *            The classes whose constructors will be suppressed.
	 * @param excludePrivateConstructors
	 *            optionally keep code in private constructors
	 */
	public static synchronized void suppressConstructor(Class<?> clazz, boolean excludePrivateConstructors) {
		Constructor<?>[] ctors = null;

		if (excludePrivateConstructors) {
			ctors = clazz.getConstructors();
		} else {
			ctors = clazz.getDeclaredConstructors();
		}

		for (Constructor<?> ctor : ctors) {
			MockRepository.addConstructorToSuppress(ctor);
		}
	}

	/**
	 * Suppress specific fields. This works on both instance methods and static
	 * methods. Note that replay and verify are not needed as this is not part
	 * of a mock behavior.
	 */
	public static synchronized void suppressField(Field... fields) {
		for (Field field : fields) {
			MockRepository.addFieldToSuppress(field);
		}
	}

	/**
	 * Suppress all fields for these classes.
	 */
	public static synchronized void suppressField(Class<?>[] classes) {
		if (classes == null || classes.length == 0) {
			throw new IllegalArgumentException("You must supply at least one class.");
		}
		for (Class<?> clazz : classes) {
			suppressField(clazz.getDeclaredFields());
		}
	}

	/**
	 * Suppress multiple methods for a class.
	 * 
	 * @param classes
	 *            The class whose methods will be suppressed.
	 * @param fieldNames
	 *            The names of the methods that'll be suppressed. If field names
	 *            are empty, <i>all</i> fields in the supplied class will be
	 *            suppressed.
	 */
	public static synchronized void suppressField(Class<?> clazz, String... fieldNames) {
		if (fieldNames == null || fieldNames.length == 0) {
			suppressField(new Class<?>[] { clazz });
		} else {
			for (Field field : Whitebox.getFields(clazz, fieldNames)) {
				MockRepository.addFieldToSuppress(field);
			}
		}
	}

	/**
	 * Suppress specific method calls on all types containing this method. This
	 * works on both instance methods and static methods. Note that replay and
	 * verify are not needed as this is not part of a mock behavior.
	 */
	public static synchronized void suppressMethod(Method... methods) {
		for (Method method : methods) {
			MockRepository.addMethodToSuppress(method);
		}
	}

	/**
	 * Suppress all methods for these classes.
	 * 
	 * @param cls
	 *            The first class whose methods will be suppressed.
	 * @param additionalClasses
	 *            Additional classes whose methods will be suppressed.
	 */
	public static synchronized void suppressMethod(Class<?> cls, Class<?>... additionalClasses) {
		suppressMethod(cls, false);
		for (Class<?> clazz : additionalClasses) {
			suppressMethod(clazz, false);
		}
	}

	/**
	 * Suppress all methods for these classes.
	 * 
	 * @param classes
	 *            Classes whose methods will be suppressed.
	 */
	public static synchronized void suppressMethod(Class<?>[] classes) {
		for (Class<?> clazz : classes) {
			suppressMethod(clazz, false);
		}
	}

	/**
	 * Suppress multiple methods for a class.
	 * 
	 * @param clazz
	 *            The class whose methods will be suppressed.
	 * @param methodName
	 *            The first method to be suppress in class <code>clazz</code>.
	 * @param additionalMethodNames
	 *            Additional methods to suppress in class <code>clazz</code>.
	 */
	public static synchronized void suppressMethod(Class<?> clazz, String methodName, String... additionalMethodNames) {
		for (Method method : Whitebox.getMethods(clazz, methodName)) {
			MockRepository.addMethodToSuppress(method);
		}
		if (additionalMethodNames != null && additionalMethodNames.length > 0) {
			for (Method method : Whitebox.getMethods(clazz, additionalMethodNames)) {
				MockRepository.addMethodToSuppress(method);
			}
		}
	}

	/**
	 * Suppress multiple methods for a class.
	 * 
	 * @param clazz
	 *            The class whose methods will be suppressed.
	 * @param methodNames
	 *            Methods to suppress in class <code>clazz</code>.
	 */
	public static synchronized void suppressMethod(Class<?> clazz, String[] methodNames) {
		for (Method method : Whitebox.getMethods(clazz, methodNames)) {
			MockRepository.addMethodToSuppress(method);
		}
	}

	/**
	 * suSuppress all methods for this class.
	 * 
	 * @param classes
	 *            The class which methods will be suppressed.
	 * @param excludePrivateMethods
	 *            optionally not suppress private methods
	 */
	public static synchronized void suppressMethod(Class<?> clazz, boolean excludePrivateMethods) {
		Method[] methods = null;

		if (excludePrivateMethods) {
			methods = clazz.getMethods();
		} else {
			methods = clazz.getDeclaredMethods();
		}

		for (Method method : methods) {
			MockRepository.addMethodToSuppress(method);
		}
	}

	/**
	 * Suppress a specific method call. Use this for overloaded methods.
	 */
	public static synchronized void suppressMethod(Class<?> clazz, String methodName, Class<?>[] parameterTypes) {
		Method method = null;
		if (parameterTypes.length > 0) {
			method = Whitebox.getMethod(clazz, methodName, parameterTypes);
		} else {
			method = WhiteboxImpl.findMethodOrThrowException(clazz, methodName, parameterTypes);
		}
		MockRepository.addMethodToSuppress(method);
	}

}
