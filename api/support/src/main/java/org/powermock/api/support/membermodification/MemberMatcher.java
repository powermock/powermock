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
package org.powermock.api.support.membermodification;

import static java.util.Arrays.asList;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.powermock.reflect.Whitebox;
import org.powermock.reflect.exceptions.ConstructorNotFoundException;
import org.powermock.reflect.exceptions.FieldNotFoundException;
import org.powermock.reflect.exceptions.MethodNotFoundException;
import org.powermock.reflect.exceptions.TooManyConstructorsFoundException;
import org.powermock.reflect.exceptions.TooManyMethodsFoundException;
import org.powermock.reflect.internal.WhiteboxImpl;
import org.powermock.tests.utils.impl.ArrayMergerImpl;

/**
 * Finds members in classes.
 */
public class MemberMatcher {

	/**
	 * Get all methods in a class hierarchy of the supplied classes. Both
	 * declared an non-declared (no duplicates).
	 * 
	 * @param cls
	 *            The class whose methods to get.
	 * @param additionalClasses
	 *            Additional classes whose methods to get.
	 * @return All methods declared in this class hierarchy.
	 */
	public static Method[] methodsDeclaredIn(final Class<?> cls, final Class<?>... additionalClasses) {
		if (cls == null) {
			throw new IllegalArgumentException("You need to supply at least one class.");
		}
		Set<Method> methods = new HashSet<Method>();
		methods.addAll(asList(WhiteboxImpl.getAllMethods(cls)));
		for (Class<?> klass : additionalClasses) {
			methods.addAll(asList(WhiteboxImpl.getAllMethods(klass)));
		}
		return methods.toArray(new Method[methods.size()]);
	}

	/**
	 * Get a method when it cannot be determined by methodName or parameter
	 * types only.
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
	 * @throws MethodNotFoundException
	 *             If a method cannot be found in the hierarchy.
	 */
	public static Method method(Class<?> type, String methodName, Class<?>... parameterTypes) {
		final Method method = WhiteboxImpl.findMethod(type, methodName, parameterTypes);
		WhiteboxImpl.throwExceptionIfMethodWasNotFound(type, methodName, method, (Object[]) parameterTypes);
		return method;
	}

	/**
	 * Get a method without having to specify the method name.
	 * <p>
	 * The method will first try to look for a declared method in the same
	 * class. If the method is not declared in this class it will look for the
	 * method in the super class. This will continue throughout the whole class
	 * hierarchy. If the method is not found an {@link IllegalArgumentException}
	 * is thrown. Since the method name is not specified an
	 * {@link IllegalArgumentException} is thrown if two or more methods matches
	 * the same parameter types in the same class.
	 * 
	 * @param type
	 *            The type of the class where the method is located.
	 * @param parameterTypes
	 *            All parameter types of the method (may be <code>null</code>).
	 * @return A <code>java.lang.reflect.Method</code>.
	 * @throws MethodNotFoundException
	 *             If a method cannot be found in the hierarchy.
	 * @throws TooManyMethodsFoundException
	 *             If several methods were found.
	 */
	public static Method method(Class<?> type, Class<?>... parameterTypes) {
		return Whitebox.getMethod(type, parameterTypes);
	}

	/**
	 * Get an array of {@link Method}'s that matches the supplied list of method
	 * names. Both instance and static methods are taken into account.
	 * 
	 * @param clazz
	 *            The class that should contain the methods.
	 * @param methodName
	 *            The name of the first method.
	 * @param additionalMethodNames
	 *            Additional names of the methods that will be returned.
	 * @return An array of Method's. May be of length 0 but not
	 *         <code>null</code>.
	 * @throws MethodNotFoundException
	 *             If no method was found.
	 */
	public static Method[] methods(Class<?> clazz, String methodName, String... additionalMethodNames) {
		return Whitebox.getMethods(clazz, merge(methodName, additionalMethodNames));
	}

	/**
	 * Get an array of {@link Field}'s.
	 * 
	 * @param method
	 *            The first field.
	 * @param additionalMethods
	 *            Additional fields
	 * @return An array of {@link Field}.
	 */
	public static Method[] methods(Method method, Method... additionalMethods) {
		return merge(method, additionalMethods);
	}

	/**
	 * Get an array of {@link Method}'s that matches the supplied list of method
	 * names. Both instance and static methods are taken into account.
	 * 
	 * @param clazz
	 *            The class that should contain the methods.
	 * @param methodNames
	 *            The names of the methods.
	 * @return An array of Method's. May be of length 0 but not
	 *         <code>null</code>.
	 * @throws MethodNotFoundException
	 *             If no method was found.
	 */
	public static Method[] methods(Class<?> clazz, String[] methodNames) {
		return Whitebox.getMethods(clazz, methodNames);
	}

	/**
	 * Get a field from a class.
	 * <p>
	 * The method will first try to look for a declared field in the same class.
	 * If the method is not declared in this class it will look for the field in
	 * the super class. This will continue throughout the whole class hierarchy.
	 * If the field is not found an {@link IllegalArgumentException} is thrown.
	 * 
	 * @param type
	 *            The type of the class where the method is located.
	 * @param fieldName
	 *            The method names.
	 * @return A <code>java.lang.reflect.Field</code>.
	 * @throws FieldNotFoundException
	 *             If a field cannot be found in the hierarchy.
	 */
	public static Field field(Class<?> type, String fieldName) {
		return Whitebox.getField(type, fieldName);
	}

	/**
	 * Get an array of {@link Field}'s that matches the supplied list of field
	 * names.
	 * 
	 * @param clazz
	 *            The class that should contain the fields.
	 * @param firstFieldName
	 *            The name of the first field.
	 * @param additionalfieldNames
	 *            The additional names of the fields that will be returned.
	 * @return An array of Field's. May be of length 0 but not <code>null</code>
	 * 
	 */
	public static Field[] fields(Class<?> clazz, String firstFieldName, String... additionalfieldNames) {
		return Whitebox.getFields(clazz, merge(firstFieldName, additionalfieldNames));
	}

	/**
	 * Get all fields in a class hierarchy.
	 * 
	 * @param clazz
	 *            The class that should contain the fields.
	 * @param firstFieldName
	 *            The name of the first field.
	 * @param additionalfieldNames
	 *            The additional names of the fields that will be returned.
	 * @return An array of Field's. May be of length 0 but not <code>null</code>
	 * 
	 */
	public static Field[] fields(Class<?> clazz) {
		return WhiteboxImpl.getAllFields(clazz);
	}

	/**
	 * Get an array of {@link Field}'s.
	 * 
	 * @param field
	 *            The first field.
	 * @param additionalFields
	 *            Additional fields
	 * @return An array of {@link Field}.
	 */
	public static Field[] fields(Field field, Field... additionalFields) {
		return merge(field, additionalFields);
	}

	/**
	 * Get an array of {@link Field}'s that matches the supplied list of field
	 * names.
	 * 
	 * @param clazz
	 *            The class that should contain the fields.
	 * @param fieldNames
	 *            The names of the fields that will be returned.
	 * @return An array of Field's. May be of length 0 but not <code>null</code>
	 * 
	 */
	public static Field[] fields(Class<?> clazz, String[] fieldNames) {
		return Whitebox.getFields(clazz, fieldNames);
	}

	/**
	 * Returns a constructor specified in type.
	 * 
	 * @param type
	 *            The type of the class where the constructor is located.
	 * @param parameterTypes
	 *            All parameter types of the constructor (may be
	 *            <code>null</code>).
	 * @return A <code>java.lang.reflect.Constructor</code>.
	 * @throws ConstructorNotFoundException
	 *             if the constructor cannot be found.
	 */
	@SuppressWarnings("unchecked")
	public static <T> Constructor<T> constructor(Class<T> type, Class<?>... parameterTypes) {
		return (Constructor<T>) WhiteboxImpl.findUniqueConstructorOrThrowException(type, (Object[]) parameterTypes);
	}

	/**
	 * Returns the constructor specified in type.
	 * 
	 * @param type
	 *            The type of the class where the constructor is located.
	 * @return A <code>java.lang.reflect.Constructor</code>.
	 * @throws TooManyConstructorsFoundException
	 *             If more than one constructor was present in <code>type</code>
	 */
	@SuppressWarnings("unchecked")
	public static <T> Constructor<T> constructor(Class<T> type) {
		return (Constructor<T>) WhiteboxImpl.findConstructorOrThrowException(type);
	}

	/**
	 * Convenience method to get a constructor from a class.
	 * 
	 * @param constructor
	 *            The first constructor.
	 * @param additionalConstructors
	 *            Additional constructors
	 * @return An array of <code>java.lang.reflect.Constructor</code>.
	 */
	public static Constructor<?>[] constructors(Constructor<?> constructor, Constructor<?>... additionalConstructors) {
		return merge(constructor, additionalConstructors);
	}

	private static String[] merge(String first, String... additional) {
		return new ArrayMergerImpl().mergeArrays(String.class, new String[] { first }, additional);
	}

	private static Method[] merge(Method first, Method... additional) {
		return new ArrayMergerImpl().mergeArrays(Method.class, new Method[] { first }, additional);
	}

	private static Field[] merge(Field first, Field... additional) {
		return new ArrayMergerImpl().mergeArrays(Field.class, new Field[] { first }, additional);
	}

	private static Constructor<?>[] merge(Constructor<?> first, Constructor<?>... additional) {
		return new ArrayMergerImpl().mergeArrays(Constructor.class, new Constructor<?>[] { first }, additional);
	}
}
