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
package org.powermock.core;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.sf.cglib.proxy.Enhancer;

import org.easymock.classextension.internal.objenesis.Objenesis;
import org.easymock.classextension.internal.objenesis.ObjenesisStd;
import org.easymock.classextension.internal.objenesis.instantiator.ObjectInstantiator;

/**
 * Various utilities for accessing internals of a class. Basically a simplified
 * reflection utility intended for tests.
 */
public class WhiteboxImpl {

	/**
	 * Convenience method to get a method from a class type without having to
	 * catch the checked exceptions otherwise required. These exceptions are
	 * wrapped as runtime exceptions.
	 * <p>
	 * The method will first try to look for a declared method in the same
	 * class. If the method is not declared in this class it will look for the
	 * method in the super class. This will continue throughout the whole class
	 * hierarchy. If the method is not found an {@link IllegalArgumentException}
	 * is thrown.
	 * 
	 * @param type
	 *            The type of the class where the method is located.
	 * @param methodName
	 *            The method names.
	 * @param parameterTypes
	 *            All parameter types of the method (may be <code>null</code>).
	 * @return A <code>java.lang.reflect.Method</code>.
	 * @throws IllegalArgumentException
	 *             If a method cannot be found in the hierarchy.
	 */
	public static Method getMethod(Class<?> type, String methodName, Class<?>... parameterTypes) {
		Class<?> thisType = type;
		if (parameterTypes == null) {
			parameterTypes = new Class<?>[0];
		}
		while (thisType != null) {
			final Method[] declaredMethods = thisType.getDeclaredMethods();
			for (Method method : declaredMethods) {
				if (methodName.equals(method.getName()) && checkIfTypesAreSame(parameterTypes, method.getParameterTypes())) {
					return method;
				}
			}
			thisType = thisType.getSuperclass();
		}

		throwExceptionIfMethodWasNotFound(type, methodName, null, parameterTypes);
		return null;
	}

	/**
	 * Create a new instance of a class without invoking its constructor.
	 * <p>
	 * No byte-code manipulation is needed to perform this operation and thus
	 * it's not necessary use the <code>PowerMockRunner</code> or
	 * <code>PrepareForTest</code> annotation to use this functionality.
	 * 
	 * @param <T>
	 *            The type of the instance to create.
	 * @param classToInstantiate
	 *            The type of the instance to create.
	 * @return A new instance of type T, created without invoking the
	 *         constructor.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T newInstance(Class<T> classToInstantiate) {
		Objenesis objenesis = new ObjenesisStd();
		ObjectInstantiator thingyInstantiator = objenesis.getInstantiatorOf(classToInstantiate);
		return (T) thingyInstantiator.newInstance();
	}

	/**
	 * Convenience method to get a (declared) constructor from a class type
	 * without having to catch the checked exceptions otherwise required. These
	 * exceptions are wrapped as runtime exceptions. The constructor is also set
	 * to accessible.
	 * 
	 * @param type
	 *            The type of the class where the constructor is located.
	 * @param parameterTypes
	 *            All parameter types of the constructor (may be
	 *            <code>null</code>).
	 * @return A <code>java.lang.reflect.Constructor</code>.
	 */
	public static Constructor<?> getConstructor(Class<?> type, Class<?>... parameterTypes) {
		try {
			final Constructor<?> constructor = getArgumentType(type).getDeclaredConstructor(parameterTypes);
			constructor.setAccessible(true);
			return constructor;
		} catch (Exception e) {
			throw new IllegalArgumentException("Failed to lookup constructor.", e);
		}
	}

	/**
	 * Set the value of a field using reflection. This method will traverse the
	 * super class hierarchy until a field with name <tt>fieldName</tt> is
	 * found.
	 * 
	 * @param object
	 *            the object to modify
	 * @param fieldName
	 *            the name of the field
	 * @param value
	 *            the new value of the field
	 */
	public static void setInternalState(Object object, String fieldName, Object value) {
		Field foundField = findFieldInHierarchy(object, fieldName);
		setField(object, value, foundField);
	}

	/**
	 * Set the value of a field using reflection. This method will traverse the
	 * super class hierarchy until the first field of type <tt>fieldType</tt> is
	 * found. The <tt>value</tt> will then be assigned to this field.
	 * 
	 * @param object
	 *            the object to modify
	 * @param fieldType
	 *            the type of the field
	 * @param value
	 *            the new value of the field
	 */
	public static void setInternalState(Object object, Class<?> fieldType, Object value) {
		setField(object, value, findFieldInHierarchy(object, new FieldTypeMatcherStrategy(fieldType)));
	}

	/**
	 * Set the value of a field using reflection. This method will traverse the
	 * super class hierarchy until the first field assignable to the
	 * <tt>value</tt> type is found. The <tt>value</tt> will then be assigned to
	 * this field.
	 * 
	 * @param object
	 *            the object to modify
	 * @param value
	 *            the new value of the field
	 */
	public static void setInternalState(Object object, Object value) {
		setField(object, value, findFieldInHierarchy(object, new AssignableToFieldTypeMatcherStrategy(getArgumentType(value))));
	}

	/**
	 * Set the value of a field using reflection at at specific place in the
	 * class hierarchy (<tt>where</tt>). This first field assignable to
	 * <tt>object</tt> will then be set to <tt>value</tt>.
	 * 
	 * @param object
	 *            the object to modify
	 * @param value
	 *            the new value of the field
	 * @param where
	 *            the class in the hierarchy where the field is defined
	 */
	public static void setInternalState(Object object, Object value, Class<?> where) {
		setField(object, value, findField(object, new AssignableToFieldTypeMatcherStrategy(getArgumentType(value)), where));
	}

	/**
	 * Set the value of a field using reflection at a specific location (
	 * <tt>where</tt>) in the class hierarchy. The <tt>value</tt> will then be
	 * assigned to this field.
	 * 
	 * @param object
	 *            the object to modify
	 * @param fieldType
	 *            the type of the field the should be set.
	 * @param value
	 *            the new value of the field
	 * @param where
	 *            which class in the hierarchy defining the field
	 */
	public static void setInternalState(Object object, Class<?> fieldType, Object value, Class<?> where) {
		if (fieldType == null || where == null) {
			throw new IllegalArgumentException("fieldType and where cannot be null");
		}

		setField(object, value, findFieldOrThrowException(fieldType, where));
	}

	/**
	 * Set the value of a field using reflection. Use this method when you need
	 * to specify in which class the field is declared. This is useful if you
	 * have two fields in a class hierarchy that has the same name but you like
	 * to modify the latter.
	 * 
	 * @param object
	 *            the object to modify
	 * @param fieldName
	 *            the name of the field
	 * @param value
	 *            the new value of the field
	 * @param where
	 *            which class the field is defined
	 */
	public static void setInternalState(Object object, String fieldName, Object value, Class<?> where) {
		if (object == null || fieldName == null || fieldName.equals("") || fieldName.startsWith(" ")) {
			throw new IllegalArgumentException("object, field name, and \"where\" must not be empty or null.");
		}

		final Field field = getField(fieldName, where);
		try {
			field.set(object, value);
		} catch (Exception e) {
			throw new RuntimeException("Internal Error: Failed to set field in method setInternalState.", e);
		}
	}

	/**
	 * Get the value of a field using reflection. This method will iterate
	 * through the entire class hierarchy and return the value of the first
	 * field named <tt>fieldName</tt>. If you want to get a specific field value
	 * at specific place in the class hierarchy please refer to
	 * {@link #getInternalState(Object, String, Class)}.
	 * 
	 * 
	 * @param object
	 *            the object to modify
	 * @param fieldName
	 *            the name of the field
	 */
	public static Object getInternalState(Object object, String fieldName) {
		Field foundField = findFieldInHierarchy(object, fieldName);
		try {
			return foundField.get(object);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Internal error: Failed to get field in method getInternalState.", e);
		}
	}

	private static Field findFieldInHierarchy(Object object, String fieldName) {
		return findFieldInHierarchy(object, new FieldNameMatcherStrategy(fieldName));
	}

	private static Field findFieldInHierarchy(Object object, FieldMatcherStrategy strategy) {
		return findFieldUsingStrategy(strategy, object, true, getArgumentType(object));
	}

	private static Field findField(Object object, FieldMatcherStrategy strategy, Class<?> where) {
		return findFieldUsingStrategy(strategy, object, false, where);
	}

	private static Field findFieldUsingStrategy(
			FieldMatcherStrategy strategy, Object object, boolean checkHierarchy, Class<?> startClass) {
		if (object == null) {
			throw new IllegalArgumentException("The object containing the field cannot be null");
		}
		Field foundField=null;
		while (startClass != null) {
			final Field[] declaredFields = startClass.getDeclaredFields();
			for (Field field : declaredFields) {
				if (strategy.matches(field) && hasFieldProperModifier(object, field)) {
					if (foundField != null) {
						throw new IllegalArgumentException("Two or more fields matching " + strategy + ".");
					}
					foundField = field;
				}
			}
			if (foundField != null) {
				break;
			} else if (checkHierarchy == false) {
				break;
			}
			startClass = startClass.getSuperclass();
		}
		if (foundField == null) {
			strategy.notFound(object);
		}
		foundField.setAccessible(true);
		return foundField;
	}

	private static boolean hasFieldProperModifier(Object object, Field field) {
		return ((object instanceof Class && Modifier.isStatic(field.getModifiers())) || ((object instanceof Class == false && Modifier.isStatic(field.getModifiers())==false)));
	}

	/**
	 * Get the value of a field using reflection. Use this method when you need
	 * to specify in which class the field is declared. This might be useful
	 * when you have mocked the instance you are trying to access.
	 * 
	 * @param object
	 *            the object to modify
	 * @param fieldName
	 *            the name of the field
	 * @param where
	 *            which class the field is defined
	 */
	public static Object getInternalState(Object object, String fieldName, Class<?> where) {
		return getInternalState(object, fieldName, where, Object.class);
	}

	/**
	 * Get the value of a field using reflection. This method will traverse the
	 * super class hierarchy until the first field of type <tt>fieldType</tt> is
	 * found. The value of this field will be returned.
	 * 
	 * @param object
	 *            the object to modify
	 * @param fieldType
	 *            the type of the field
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getInternalState(Object object, Class<T> fieldType) {
		Field foundField = findFieldInHierarchy(object, new FieldTypeMatcherStrategy(fieldType));
		try {
			return (T) foundField.get(object);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Internal error: Failed to get field in method getInternalState.", e);
		}
	}

	/**
	 * Get the value of a field using reflection. Use this method when you need
	 * to specify in which class the field is declared. The first field matching
	 * the <tt>fieldType</tt> in <tt>where</tt> will is the field whose value
	 * will be returned.
	 * 
	 * @param <T>
	 *            the expected type of the field
	 * @param object
	 *            the object to modify
	 * @param fieldType
	 *            the type of the field
	 * @param where
	 *            which class the field is defined
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getInternalState(Object object, Class<T> fieldType, Class<?> where) {
		if (object == null) {
			throw new IllegalArgumentException("object and type are not allowed to be null");
		}

		try {
			return (T) findFieldOrThrowException(fieldType, where).get(object);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Internal error: Failed to get field in method getInternalState.", e);
		}
	}

	/**
	 * Get the value of a field using reflection. Use this method when you need
	 * to specify in which class the field is declared. This might be useful
	 * when you have mocked the instance you are trying to access. Use this
	 * method to avoid casting.
	 * 
	 * @param <T>
	 *            the expected type of the field
	 * @param object
	 *            the object to modify
	 * @param fieldName
	 *            the name of the field
	 * @param where
	 *            which class the field is defined
	 * @param type
	 *            the expected type of the field
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getInternalState(Object object, String fieldName, Class<?> where, Class<T> type) {
		if (object == null || fieldName == null || fieldName.equals("") || fieldName.startsWith(" ")) {
			throw new IllegalArgumentException("object, field name, and \"where\" must not be empty or null.");
		}

		if (type == null) {
			throw new IllegalArgumentException("type cannot be null.");
		}

		Field field = null;
		try {
			field = where.getDeclaredField(fieldName);
			field.setAccessible(true);
			return (T) field.get(object);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException("Field '" + fieldName + "' was not found in class " + where.getName() + ".");
		} catch (Exception e) {
			throw new RuntimeException("Internal error: Failed to get field in method getInternalState.", e);
		}
	}

	/**
	 * Invoke a private or inner class method. This might be useful to test
	 * private methods.
	 * 
	 * @throws Throwable
	 */
	public static synchronized Object invokeMethod(Object tested, String methodToExecute, Object... arguments) throws Exception {
		return doInvokeMethod(tested, null, methodToExecute, arguments);
	}

	/**
	 * Invoke a private or inner class method in cases where power mock cannot
	 * automatically determine the type of the parameters, for example when
	 * mixing primitive types and wrapper types in the same method. For most
	 * situations use {@link #invokeMethod(Class, String, Object...)} instead.
	 * 
	 * @throws Exception
	 *             Exception that may occur when invoking this method.
	 */
	public static synchronized Object invokeMethod(Object tested, String methodToExecute, Class<?>[] argumentTypes, Object... arguments)
			throws Exception {
		final Class<?> unmockedType = getArgumentType(tested);
		Method method = getMethod(unmockedType, methodToExecute, argumentTypes);
		if (method == null) {
			throwExceptionIfMethodWasNotFound(unmockedType, methodToExecute, null, arguments);
		}
		return performMethodInvocation(tested, method, arguments);
	}

	/**
	 * Invoke a private or inner class method in a subclass (defined by
	 * <code>definedIn</code>) in cases where power mock cannot automatically
	 * determine the type of the parameters, for example when mixing primitive
	 * types and wrapper types in the same method. For most situations use
	 * {@link #invokeMethod(Class, String, Object...)} instead.
	 * 
	 * @throws Exception
	 *             Exception that may occur when invoking this method.
	 */
	public static synchronized Object invokeMethod(Object tested, String methodToExecute, Class<?> definedIn, Class<?>[] argumentTypes,
			Object... arguments) throws Exception {
		Method method = getMethod(definedIn, methodToExecute, argumentTypes);
		if (method == null) {
			throwExceptionIfMethodWasNotFound(definedIn, methodToExecute, null, arguments);
		}
		return performMethodInvocation(tested, method, arguments);
	}

	/**
	 * Invoke a private or inner class method in that is located in a subclass
	 * of the tested instance. This might be useful to test private methods.
	 * 
	 * @throws Exception
	 *             Exception that may occur when invoking this method.
	 */
	public static synchronized Object invokeMethod(Object tested, Class<?> declaringClass, String methodToExecute, Object... arguments)
			throws Exception {
		return doInvokeMethod(tested, declaringClass, methodToExecute, arguments);
	}

	/**
	 * Invoke a private or inner class method in that is located in a subclass
	 * of the tested instance. This might be useful to test private methods.
	 * <p>
	 * Use this for overloaded methods.
	 * 
	 * @throws Exception
	 *             Exception that may occur when invoking this method.
	 */
	public static synchronized Object invokeMethod(Object object, Class<?> declaringClass, String methodToExecute, Class<?>[] parameterTypes,
			Object... arguments) throws Exception {
		if (object == null) {
			throw new IllegalArgumentException("object cannot be null");
		}

		final Method methodToInvoke = getMethod(declaringClass, methodToExecute, parameterTypes);
		// Invoke method
		return performMethodInvocation(object, methodToInvoke, arguments);
	}

	/**
	 * Invoke a private or inner class method. This might be useful to test
	 * private methods.
	 * 
	 */
	public static synchronized Object invokeMethod(Class<?> clazz, String methodToExecute, Object... arguments) throws Exception {
		return doInvokeMethod(clazz, null, methodToExecute, arguments);
	}

	private static Object doInvokeMethod(Object tested, Class<?> declaringClass, String methodToExecute, Object... arguments) throws Exception {
		Method methodToInvoke = findMethodOrThrowException(tested, declaringClass, methodToExecute, arguments);

		// Invoke test
		return performMethodInvocation(tested, methodToInvoke, arguments);
	}

	/**
	 * Finds and returns a certain method. If the method couldn't be found this
	 * method delegates to
	 * {@link WhiteboxImpl#throwExceptionIfMethodWasNotFound(Object, String, Method, Object...)}
	 * .
	 * 
	 * @param tested
	 * @param declaringClass
	 *            The class where the method is supposed to be declared (may be
	 *            <code>null</code>).
	 * @param methodToExecute
	 * @param arguments
	 * @return
	 */
	public static Method findMethodOrThrowException(Object tested, Class<?> declaringClass, String methodToExecute, Object... arguments) {

		if (tested == null) {
			throw new IllegalArgumentException("The object to perform the operation on cannot be null.");
		}

		/*
		 * Get methods from the type if it's not mocked or from the super type
		 * if the tested object is mocked.
		 */
		Class<?> testedType = null;
		if (isClass(tested)) {
			testedType = (Class<?>) tested;
		} else {
			testedType = tested.getClass();
		}

		Method[] methods = null;
		if (declaringClass == null) {
			methods = getAllMethods(testedType);
		} else {
			methods = declaringClass.getDeclaredMethods();
		}
		Method potentialMethodToInvoke = null;
		for (Method method : methods) {
			if (method.getName().equals(methodToExecute)) {
				Class<?>[] paramTypes = method.getParameterTypes();
				if ((arguments != null && (paramTypes.length == arguments.length))) {
					if (paramTypes.length == 0) {
						potentialMethodToInvoke = method;
						break;
					}
					boolean wrappedMethodFound = true;
					boolean primitiveMethodFound = true;
					if (!checkIfTypesAreSame(paramTypes, arguments)) {
						wrappedMethodFound = false;
					}

					if (!checkIfTypesAreSame(paramTypes, convertArgumentTypesToPrimitive(paramTypes, arguments))) {
						primitiveMethodFound = false;
					}

					if (wrappedMethodFound || primitiveMethodFound) {
						if (potentialMethodToInvoke == null) {
							potentialMethodToInvoke = method;
						} else {
							/*
							 * We've already found a method match before, this
							 * means that PowerMock cannot determine which
							 * method to expect since there are two methods with
							 * the same name and the same number of arguments
							 * but one is using wrapper types.
							 */
							throwExceptionWhenMultipleMethodMatchesFound(new Method[] { potentialMethodToInvoke, method });
						}
					}
				} else if (method.isVarArgs() && areAllArgumentsOfSameType(arguments)) {
					potentialMethodToInvoke = method;
					break;
				} else if (arguments != null && (paramTypes.length != arguments.length)) {
					continue;
				}
			}
		}

		WhiteboxImpl.throwExceptionIfMethodWasNotFound(getArgumentType(tested), methodToExecute, potentialMethodToInvoke, arguments);
		return potentialMethodToInvoke;
	}

/**
	 * Finds and returns a certain constructor. If the constructor couldn't be
	 * found this method delegates to
	 * {@link Whitebox#throwExceptionIfConstructorWasNotFound(Class, Object...).
	 * 
	 * @param type
	 * @param arguments
	 * @return
	 */
	public static Constructor<?> findConstructorOrThrowException(Class<?> type, Object... arguments) {
		if (type == null) {
			throw new IllegalArgumentException("Class type cannot be null.");
		}

		Class<?> unmockedType = getUnmockedType(type);

		Constructor<?>[] constructors = unmockedType.getDeclaredConstructors();
		Constructor<?> potentialConstructor = null;
		for (Constructor<?> constructor : constructors) {
			Class<?>[] paramTypes = constructor.getParameterTypes();
			if ((arguments != null && (paramTypes.length == arguments.length))) {
				if (paramTypes.length == 0) {
					potentialConstructor = constructor;
					break;
				}
				boolean wrappedConstructorFound = true;
				boolean primitiveConstructorFound = true;
				if (!checkIfTypesAreSame(paramTypes, arguments)) {
					wrappedConstructorFound = false;
				}

				if (!checkIfTypesAreSame(paramTypes, convertArgumentTypesToPrimitive(paramTypes, arguments))) {
					primitiveConstructorFound = false;
				}

				if (wrappedConstructorFound || primitiveConstructorFound) {
					if (potentialConstructor == null) {
						potentialConstructor = constructor;
					} else {
						/*
						 * We've already found a constructor match before, this
						 * means that PowerMock cannot determine which method to
						 * expect since+ there are two methods with the same
						 * name and the same number of arguments but one is
						 * using wrapper types.
						 */
						throwExceptionWhenMultipleConstructorMatchesFound(new Constructor<?>[] { potentialConstructor, constructor });
					}
				}
			} else if (constructor.isVarArgs() && areAllArgumentsOfSameType(arguments)) {
				potentialConstructor = constructor;
				break;
			} else if (arguments != null && (paramTypes.length != arguments.length)) {
				continue;
			}
		}

		WhiteboxImpl.throwExceptionIfConstructorWasNotFound(type, potentialConstructor, arguments);
		return potentialConstructor;
	}

	private static Class<?>[] convertArgumentTypesToPrimitive(Class<?>[] paramTypes, Object[] arguments) {
		Class<?>[] types = new Class<?>[arguments.length];
		for (int i = 0; i < arguments.length; i++) {
			Class<?> argumentType = null;
			if (arguments[i] == null) {
				argumentType = paramTypes[i];
			} else {
				argumentType = getArgumentType(arguments[i]);
			}
			Class<?> primitiveWrapperType = PrimitiveWrapper.getPrimitiveFromWrapperType(argumentType);
			if (primitiveWrapperType == null) {
				types[i] = argumentType;
			} else {
				types[i] = primitiveWrapperType;
			}
		}
		return types;
	}

	public static void throwExceptionIfMethodWasNotFound(Class<?> type, String methodName, Method methodToMock, Object... arguments) {
		if (methodToMock == null) {
			throw new IllegalArgumentException("No method found with name '" + methodName + "' with argument types: [ "
					+ getArgumentsAsString(arguments) + "] in class " + getUnmockedType(type).getName());
		}
	}

	static void throwExceptionIfConstructorWasNotFound(Class<?> type, Constructor<?> potentialConstructor, Object... arguments) {
		if (potentialConstructor == null) {
			throw new IllegalArgumentException("No constructor found in class '" + getUnmockedType(type).getName() + "' with argument types: [ "
					+ getArgumentsAsString(arguments) + " ]");
		}
	}

	private static String getArgumentsAsString(Object... arguments) {
		StringBuilder argumentsAsString = new StringBuilder();
		if (arguments != null && arguments.length != 0) {
			for (int i = 0; i < arguments.length; i++) {
				String argumentName = null;
				Object argument = arguments[i];

				if (argument instanceof Class) {
					argumentName = ((Class<?>) argument).getName();
				} else if (argument == null) {
					argumentName = "null";
				} else {
					argumentName = getArgumentType(argument).getName();
				}

				argumentsAsString.append(argumentName);
				if (i != arguments.length - 1) {
					argumentsAsString.append(", ");
				}
			}
		}
		return argumentsAsString.toString();
	}

	/**
	 * Invoke a constructor. Useful for testing classes with a private
	 * constructor when PowerMock cannot determine which constructor to invoke.
	 * This only happens if you have two constructors with the same number of
	 * arguments where one is using primitive data types and the other is using
	 * the wrapped counter part. For example:
	 * 
	 * <pre>
	 * public class MyClass {
	 *     private MyClass(Integer i) {
	 *         ...
	 *     } 
	 * 
	 *     private MyClass(int i) {
	 *         ...
	 *     }
	 * </pre>
	 * 
	 * This ought to be a really rare case. So for most situation, use
	 * {@link #invokeConstructor(Class, Object...)} instead.
	 * 
	 * 
	 * @return The object created after the constructor has been invoked.
	 */
	public static <T> T invokeConstructor(Class<T> classThatContainsTheConstructorToTest, Class<?>[] parameterTypes, Object[] arguments) {
		if (parameterTypes != null && arguments != null) {
			if (parameterTypes.length != arguments.length) {
				throw new IllegalArgumentException("parameterTypes and arguments must have the same length");
			}
		}

		Constructor<T> constructor = null;
		try {
			constructor = classThatContainsTheConstructorToTest.getDeclaredConstructor(parameterTypes);
		} catch (Exception e) {
			throw new RuntimeException("Could not lookup the constructor", e);
		}

		return createInstance(constructor, arguments);
	}

	/**
	 * Invoke a constructor. Useful for testing classes with a private
	 * constructor.
	 * 
	 * 
	 * @return The object created after the constructor has been invoked.
	 */
	public static <T> T invokeConstructor(Class<T> classThatContainsTheConstructorToTest, Object... arguments) {

		if (classThatContainsTheConstructorToTest == null) {
			throw new IllegalArgumentException("The class should contain the constructor cannot be null.");
		}

		Class<?>[] argumentTypes = null;
		if (arguments == null) {
			argumentTypes = new Class<?>[0];
		} else {
			argumentTypes = new Class<?>[arguments.length];
			for (int i = 0; i < arguments.length; i++) {
				argumentTypes[i] = getArgumentType(arguments[i]);
			}
		}

		Constructor<T> constructor = null;

		Constructor<T> potentialContstructorWrapped = null;
		Constructor<T> potentialContstructorPrimitive = null;

		try {
			potentialContstructorWrapped = classThatContainsTheConstructorToTest.getDeclaredConstructor(argumentTypes);
		} catch (Exception e) {
			// Do nothing, we'll try with primitive type next.
		}

		try {
			potentialContstructorPrimitive = classThatContainsTheConstructorToTest.getDeclaredConstructor(PrimitiveWrapper
					.toPrimitiveType(argumentTypes));
		} catch (Exception e) {
			// Do nothing
		}

		if (potentialContstructorPrimitive == null && potentialContstructorWrapped == null) {
			// Check if we can find a matching var args constructor.
			constructor = getPotentialVarArgsConstructor(classThatContainsTheConstructorToTest, arguments);
			if (constructor == null) {
				throw new RuntimeException("Failed to find a constructor with argument types: [" + getArgumentsAsString(arguments) + "]");
			}
		} else if (potentialContstructorPrimitive == null && potentialContstructorWrapped != null) {
			constructor = potentialContstructorWrapped;
		} else if (potentialContstructorPrimitive != null && potentialContstructorWrapped == null) {
			constructor = potentialContstructorPrimitive;
		} else if (arguments == null || arguments.length == 0 && potentialContstructorPrimitive != null) {
			constructor = potentialContstructorPrimitive;
		} else {
			throw new RuntimeException("Could not determine which constructor to execute. Please specify the parameter types by hand.");
		}

		return createInstance(constructor, arguments);
	}

	@SuppressWarnings("unchecked")
	private static <T> Constructor<T> getPotentialVarArgsConstructor(Class<T> classThatContainsTheConstructorToTest, Object... arguments) {
		if (areAllArgumentsOfSameType(arguments)) {
			Constructor<T>[] declaredConstructors = (Constructor<T>[]) classThatContainsTheConstructorToTest.getDeclaredConstructors();
			for (Constructor<T> possibleVarArgsConstructor : declaredConstructors) {
				if (possibleVarArgsConstructor.isVarArgs()) {
					if (arguments == null || arguments.length == 0) {
						return possibleVarArgsConstructor;
					} else if (possibleVarArgsConstructor.getParameterTypes()[0].getComponentType().isAssignableFrom(getArgumentType(arguments[0]))) {
						return possibleVarArgsConstructor;
					}
				}
			}
		}
		return null;
	}

	private static <T> T createInstance(Constructor<T> constructor, Object... arguments) {
		if (constructor == null) {
			throw new IllegalArgumentException("Constructor cannot be null");
		}
		constructor.setAccessible(true);

		T createdObject = null;
		try {
			if (constructor.isVarArgs()) {
				Class<?> varArgsType = constructor.getParameterTypes()[0].getComponentType();
				Object arrayInstance = createAndPopulateArray(varArgsType, arguments);
				createdObject = constructor.newInstance(new Object[] { arrayInstance });
			} else {
				createdObject = constructor.newInstance(arguments);
			}
		} catch (InvocationTargetException e) {
			throw new RuntimeException("An exception was caught when executing the constructor", e);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return createdObject;
	}

	private static Object createAndPopulateArray(Class<?> varArgsType, Object... arguments) {
		Object arrayInstance = Array.newInstance(varArgsType, arguments.length);
		for (int i = 0; i < arguments.length; i++) {
			Array.set(arrayInstance, i, arguments[i]);
		}
		return arrayInstance;
	}

	/**
	 * Get all methods in a class hierarchy! Both declared an non-declared (no
	 * duplicates).
	 * 
	 * @param clazz
	 *            The class whose methods to get.
	 * @return All methods declared in this class and all non-private members
	 *         visible in its subclass.
	 */
	private static Method[] getAllMethods(Class<?> clazz) {
		Set<Method> methods = new LinkedHashSet<Method>();

		Class<?> thisType = clazz;

		while (thisType != null) {
			final Method[] declaredMethods = thisType.getDeclaredMethods();
			for (Method method : declaredMethods) {
				methods.add(method);
			}
			thisType = thisType.getSuperclass();
		}
		return methods.toArray(new Method[0]);
	}

	/**
	 * Get the first parent constructor defined in a super class of
	 * <code>klass</code>.
	 * 
	 * @param klass
	 *            The class where the constructor is located. <code>null</code>
	 *            ).
	 * @return A <code>java.lang.reflect.Constructor</code>.
	 */
	public static Constructor<?> getFirstParentConstructor(Class<?> klass) {

		try {
			return getUnmockedType(klass).getSuperclass().getDeclaredConstructors()[0];
		} catch (Exception e) {
			throw new IllegalArgumentException("Failed to lookup constructor.", e);
		}
	}

	/**
	 * Finds and returns a method based on the input parameters. If no
	 * <code>parameterTypes</code> are present the method will return the first
	 * method with name <code>methodNameToMock</code>. If no method was found,
	 * <code>null</code> will be returned.
	 * 
	 * @param <T>
	 * @param type
	 * @param methodNameToMock
	 * @param parameterTypes
	 * @return
	 */
	static <T> Method findMethod(Class<T> type, String methodNameToMock, Class<?>... parameterTypes) {
		List<Method> matchingMethodsList = new LinkedList<Method>();
		for (Method method : getAllMethods(type)) {
			if (method.getName().equals(methodNameToMock)) {
				if (parameterTypes != null && parameterTypes.length > 0) {
					// If argument types was supplied, make sure that they
					// match.
					Class<?>[] paramTypes = method.getParameterTypes();
					if (!checkIfTypesAreSame(parameterTypes, paramTypes)) {
						continue;
					}
				}
				// Add the method to the matching methods list.
				matchingMethodsList.add(method);
			}
		}

		Method methodToMock = null;
		if (matchingMethodsList.size() > 0) {
			if (matchingMethodsList.size() == 1) {
				// We've found a unique method match.
				methodToMock = matchingMethodsList.get(0);
			} else if (parameterTypes.length == 0) {
				/*
				 * If we've found several matches and we've supplied no
				 * parameter types, go through the list of found methods and see
				 * if we have a method with no parameters. In that case return
				 * that method.
				 */
				for (Method method : matchingMethodsList) {
					if (method.getParameterTypes().length == 0) {
						methodToMock = method;
						break;
					}
				}

				if (methodToMock == null) {
					WhiteboxImpl.throwExceptionWhenMultipleMethodMatchesFound(matchingMethodsList.toArray(new Method[0]));
				}
			} else {
				// We've found several matching methods.
				WhiteboxImpl.throwExceptionWhenMultipleMethodMatchesFound(matchingMethodsList.toArray(new Method[0]));
			}
		}

		return methodToMock;
	}

	public static <T> Class<?> getUnmockedType(Class<T> type) {
		if (type == null) {
			throw new IllegalArgumentException("type cannot be null");
		}

		Class<?> unmockedType;
		if (Enhancer.isEnhanced(type)) {
			unmockedType = type.getSuperclass();
		} else if (Proxy.isProxyClass(type)) {
			unmockedType = type.getInterfaces()[0];
		} else {
			unmockedType = type;
		}
		return unmockedType;
	}

	static void throwExceptionWhenMultipleMethodMatchesFound(Method[] methods) {
		if (methods == null || methods.length < 2) {
			throw new IllegalArgumentException("Internal error: throwExceptionWhenMultipleMethodMatchesFound needs at least two methods.");
		}
		StringBuilder sb = new StringBuilder();
		sb
				.append("Several matching methods found, please specify the argument parameter types so that PowerMock can determine which method you're refering to.\n");
		sb.append("Matching methods in class ").append(methods[0].getDeclaringClass().getName()).append(" were:\n");

		for (Method method : methods) {
			sb.append(method.getReturnType().getName()).append(" ");
			sb.append(method.getName()).append("( ");
			final Class<?>[] parameterTypes = method.getParameterTypes();
			for (Class<?> paramType : parameterTypes) {
				sb.append(paramType.getName()).append(".class ");
			}
			sb.append(")\n");
		}
		throw new RuntimeException(sb.toString());
	}

	static void throwExceptionWhenMultipleConstructorMatchesFound(Constructor<?>[] constructors) {
		if (constructors == null || constructors.length < 2) {
			throw new IllegalArgumentException("Internal error: throwExceptionWhenMultipleMethodMatchesFound needs at least two methods.");
		}
		StringBuilder sb = new StringBuilder();
		sb
				.append("Several matching constructors found, please specify the argument parameter types so that PowerMock can determine which method you're refering to.\n");
		sb.append("Matching constructors in class ").append(constructors[0].getDeclaringClass().getName()).append(" were:\n");

		for (Constructor<?> constructor : constructors) {
			sb.append(constructor.getName()).append("( ");
			final Class<?>[] parameterTypes = constructor.getParameterTypes();
			for (Class<?> paramType : parameterTypes) {
				sb.append(paramType.getName()).append(".class ");
			}
			sb.append(")\n");
		}
		throw new RuntimeException(sb.toString());
	}

	@SuppressWarnings("all")
	public static Method findMethodOrThrowException(Class<?> type, String methodName, Class<?>... parameterTypes) {
		Method methodToMock = findMethod(type, methodName, parameterTypes);
		throwExceptionIfMethodWasNotFound(type, methodName, methodToMock, parameterTypes);
		return methodToMock;
	}

	/**
	 * Get an array of {@link Method}'s that matches the supplied list of method
	 * names.
	 * 
	 * @param clazz
	 *            The class that should contain the methods.
	 * @param methodNames
	 *            An array names of the methods that will be returned.
	 * @return An array of Method's. May be of length 0 but not
	 *         <code>null</code>.
	 */
	public static Method[] getMethods(Class<?> clazz, String... methodNames) {
		final List<Method> methodsToMock = new LinkedList<Method>();

		for (Method method : getAllMethods(clazz)) {
			for (String methodName : methodNames) {
				if (method.getName().equals(methodName)) {
					methodsToMock.add(method);
				}
			}
		}

		final Method[] methodArray = methodsToMock.toArray(new Method[0]);
		return methodArray;
	}

	public static Object performMethodInvocation(Object tested, Method methodToInvoke, Object... arguments) throws Exception {
		methodToInvoke.setAccessible(true);
		try {
			if (methodToInvoke.isVarArgs()) {
				Class<?> arrayType = methodToInvoke.getParameterTypes()[0].getComponentType();
				Object arrayInstance = createAndPopulateArray(arrayType, arguments);
				return methodToInvoke.invoke(tested, new Object[] { arrayInstance });
			} else {
				return methodToInvoke.invoke(tested, arguments == null ? new Object[] { arguments } : arguments);
			}
		} catch (InvocationTargetException e) {
			Throwable cause = e.getCause();
			if (cause instanceof Exception) {
				throw (Exception) cause;
			} else if (cause instanceof Error) {
				throw (Error) cause;
			} else {
				throw new RuntimeException(cause);
			}
		} catch (Exception e) {
			throw new RuntimeException("Failed to invoke method " + methodToInvoke.getName() + " on object " + tested + ". Reason was \""
					+ e.getMessage() + "\".", e);
		}
	}

	public static <T> Method[] getAllMethodExcept(Class<T> type, String... methodNames) {
		List<Method> methodsToMock = new LinkedList<Method>();
		Method[] methods = getAllMethods(type);
		iterateMethods: for (Method method : methods) {
			for (String methodName : methodNames) {
				if (method.getName().equals(methodName)) {
					continue iterateMethods;
				}
			}
			methodsToMock.add(method);
		}
		return methodsToMock.toArray(new Method[0]);
	}

	public static <T> Method[] getAllMetodsExcept(Class<T> type, String methodNameToExclude, Class<?>[] argumentTypes) {
		Method[] methods = getAllMethods(type);
		List<Method> methodList = new ArrayList<Method>();
		outer: for (Method method : methods) {
			if (method.getName().equals(methodNameToExclude)) {
				if (argumentTypes != null && argumentTypes.length > 0) {
					final Class<?>[] args = method.getParameterTypes();
					if (args != null && args.length == argumentTypes.length) {
						for (int i = 0; i < args.length; i++) {
							if (args[i].isAssignableFrom(getUnmockedType(argumentTypes[i]))) {
								/*
								 * Method was not found thus it should not be
								 * mocked. Continue to investigate the next
								 * method.
								 */
								continue outer;
							}
						}
					}
				} else {
					continue;
				}
			}
			methodList.add(method);
		}
		return methodList.toArray(new Method[0]);
	}

	public static boolean areAllMethodsStatic(Method... methods) {
		for (Method method : methods) {
			if (!Modifier.isStatic(method.getModifiers())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Check if all arguments are of the same type.
	 */
	static boolean areAllArgumentsOfSameType(Object[] arguments) {
		if (arguments == null || arguments.length <= 1) {
			return true;
		}

		// Handle null values
		int index = 0;
		Object object = null;
		while (object == null && index < arguments.length) {
			object = arguments[index++];
		}

		if (object == null) {
			return true;
		}
		// End of handling null values

		final Class<?> firstArgumentType = getArgumentType(object);
		for (int i = index; i < arguments.length; i++) {
			final Object argument = arguments[i];
			if (argument != null && !getArgumentType(argument).isAssignableFrom(firstArgumentType)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * @return <code>true</code> if all actual parameter types are assignable
	 *         from the expected arguments, <code>false</code> otherwise.
	 */
	private static boolean checkIfTypesAreSame(Class<?>[] parameterTypes, Object[] arguments) {
		if (parameterTypes == null) {
			throw new IllegalArgumentException("parameter types cannot be null");
		} else if (parameterTypes.length != arguments.length) {
			return false;
		}
		for (int i = 0; i < parameterTypes.length; i++) {
			Object argument = arguments[i];
			if (argument == null) {
				continue;
			} else {
				if (!parameterTypes[i].isAssignableFrom(getArgumentType(argument)) && !(parameterTypes[i].equals(Class.class) && isClass(argument))) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * @return The argument type of the of argument.
	 */
	private static Class<?> getArgumentType(Object argument) {
		Class<?> argumentType = null;
		if (isClass(argument)) {
			argumentType = (Class<?>) argument;
		} else if (argument != null) {
			argumentType = argument.getClass();
		}
		return getUnmockedType(argumentType);
	}

	private static boolean isClass(Object argument) {
		return argument instanceof Class<?>;
	}

	/**
	 * @return <code>true</code> if all actual parameter types are assignable
	 *         from the expected parameter types, <code>false</code> otherwise.
	 */
	private static boolean checkIfTypesAreSame(Class<?>[] expectedParameterTypes, Class<?>[] actualParameterTypes) {
		if (expectedParameterTypes == null || actualParameterTypes == null) {
			throw new IllegalArgumentException("parameter types cannot be null");
		} else if (expectedParameterTypes.length != actualParameterTypes.length) {
			return false;
		} else {
			for (int i = 0; i < expectedParameterTypes.length; i++) {
				if (!expectedParameterTypes[i].isAssignableFrom(getArgumentType(actualParameterTypes[i]))
						&& !expectedParameterTypes[i].equals(Class.class)) {
					return false;
				}
			}
		}
		return true;
	}

	private static Field getField(String fieldName, Class<?> where) {
		if (where == null) {
			throw new IllegalArgumentException("where cannot be null");
		}

		Field field = null;
		try {
			field = where.getDeclaredField(fieldName);
			field.setAccessible(true);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException("Field '" + fieldName + "' was not found in class " + where.getName() + ".");
		}
		return field;
	}

	private static Field findFieldOrThrowException(Class<?> fieldType, Class<?> where) {
		if (fieldType == null || where == null) {
			throw new IllegalArgumentException("fieldType and where cannot be null");
		}
		Field field = null;
		for (Field currentField : where.getDeclaredFields()) {
			currentField.setAccessible(true);
			if (currentField.getType().equals(fieldType)) {
				field = currentField;
				break;
			}
		}
		if (field == null) {
			throw new IllegalArgumentException("Cannot find a field of type " + fieldType + "in where.");
		}
		return field;
	}

	private static void setField(Object object, Object value, Field foundField) {
		foundField.setAccessible(true);
		try {
			foundField.set(object, value);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Internal error: Failed to set field in method setInternalState.", e);
		}
	}

	/**
	 * Class that should be implemented by field matching strategies.
	 */
	private static abstract class FieldMatcherStrategy {

		/**
		 * A field matcher that checks if a field matches a given criteria.
		 * 
		 * @param field
		 *            The field to check whether it matches the strategy or not.
		 * @return <code>true</code> if this field matches the strategy,
		 *         <code>false</code> otherwise.
		 * 
		 */
		public abstract boolean matches(Field field);

		/**
		 * Throws an {@link IllegalArgumentException} if the strategy criteria
		 * could not be found.
		 * 
		 * @param object
		 *            The object where the strategy criteria could not be found.
		 */
		public abstract void notFound(Object object) throws IllegalArgumentException;
	}

	private static class FieldNameMatcherStrategy extends FieldMatcherStrategy {

		private final String fieldName;

		public FieldNameMatcherStrategy(String fieldName) {
			if (fieldName == null || fieldName.equals("") || fieldName.startsWith(" ")) {
				throw new IllegalArgumentException("field name cannot be null.");
			}
			this.fieldName = fieldName;
		}

		@Override
		public boolean matches(Field field) {
			return fieldName.equals(field.getName());
		}

		@Override
		public void notFound(Object object) throws IllegalArgumentException {
			throw new IllegalArgumentException("No field named \"" + fieldName + "\" could be found in the class hierarchy of "
					+ getArgumentType(object).getName() + ".");
		}
		
		@Override
		public String toString() {
			return "fieldName " + fieldName;
		}
	}

	private static class FieldTypeMatcherStrategy extends FieldMatcherStrategy {

		final Class<?> expectedFieldType;

		public FieldTypeMatcherStrategy(Class<?> fieldType) {
			if (fieldType == null) {
				throw new IllegalArgumentException("field type cannot be null.");
			}
			this.expectedFieldType = fieldType;
		}

		@Override
		public boolean matches(Field field) {
			return expectedFieldType.equals(field.getType());
		}

		@Override
		public void notFound(Object object) throws IllegalArgumentException {
			throw new IllegalArgumentException("No field of type \"" + expectedFieldType.getName() + "\" could be found in the class hierarchy of "
					+ getArgumentType(object).getName() + ".");
		}

		@Override
		public String toString() {
			return "type " + expectedFieldType.getName();
		}
	}

	private static class AssignableToFieldTypeMatcherStrategy extends FieldTypeMatcherStrategy {

		private final Class<?> primitiveCounterpart;

		public AssignableToFieldTypeMatcherStrategy(Class<?> fieldType) {
			super(fieldType);
			primitiveCounterpart = PrimitiveWrapper.getPrimitiveFromWrapperType(expectedFieldType);
		}

		@Override
		public boolean matches(Field field) {
			final Class<?> actualFieldType = field.getType();
			return actualFieldType.isAssignableFrom(expectedFieldType)
					|| (primitiveCounterpart != null && actualFieldType.isAssignableFrom(primitiveCounterpart));
		}

		@Override
		public void notFound(Object object) throws IllegalArgumentException {
			throw new IllegalArgumentException("No field assignable to \"" + expectedFieldType.getName()
					+ "\" could be found in the class hierarchy of " + getArgumentType(object).getName() + ".");
		}

		@Override
		public String toString() {
			return "type " + primitiveCounterpart.getName();
		}
	}

}
