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
package org.powermock.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

import org.powermock.reflect.exceptions.ConstructorNotFoundException;
import org.powermock.reflect.exceptions.FieldNotFoundException;
import org.powermock.reflect.exceptions.MethodNotFoundException;
import org.powermock.reflect.exceptions.TooManyMethodsFoundException;
import org.powermock.reflect.internal.WhiteboxImpl;

/**
 * Various utilities for accessing internals of a class. Basically a simplified
 * reflection utility intended for tests.
 */
public class Whitebox {

	/**
	 * Convenience method to get a field from a class type.
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
	public static Field getField(Class<?> type, String fieldName) {
		return WhiteboxImpl.getField(type, fieldName);
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
	public static Field[] getFields(Class<?> clazz, String... fieldNames) {
		return WhiteboxImpl.getFields(clazz, fieldNames);
	}

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
	 * @throws MethodNotFoundException
	 *             If a method cannot be found in the hierarchy.
	 */
	public static Method getMethod(Class<?> type, String methodName, Class<?>... parameterTypes) {
		return WhiteboxImpl.getMethod(type, methodName, parameterTypes);
	}

	/**
	 * Convenience method to get a method from a class type without having to
	 * catch the checked exceptions otherwise required. These exceptions are
	 * wrapped as runtime exceptions.
	 * <p>
	 * The method will first try to look for a declared method in the same
	 * class. If the method is not declared in this class it will look for the
	 * method in the super class. This will continue throughout the whole class
	 * hierarchy. If the method is not found an {@link MethodNotFoundException}
	 * is thrown. Since the method name is not specified an
	 * {@link TooManyMethodsFoundException} is thrown if two or more methods
	 * matches the same parameter types in the same class.
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
	public static Method getMethod(Class<?> type, Class<?>... parameterTypes) {
		return WhiteboxImpl.getMethod(type, parameterTypes);
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
	public static <T> T newInstance(Class<T> classToInstantiate) {
		return WhiteboxImpl.newInstance(classToInstantiate);
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
	 * @throws ConstructorNotFoundException
	 *             if the constructor cannot be found.
	 */
	public static Constructor<?> getConstructor(Class<?> type, Class<?>... parameterTypes) {
		return WhiteboxImpl.getConstructor(type, parameterTypes);
	}

	/**
	 * Set the value of a field using reflection.
	 * 
	 * @param object
	 *            the object to modify
	 * @param fieldName
	 *            the name of the field
	 * @param value
	 *            the new value of the field
	 */
	public static void setInternalState(Object object, String fieldName, Object value) {
		WhiteboxImpl.setInternalState(object, fieldName, value);
	}

	/**
	 * Set the value of a field using reflection. This method will traverse the
	 * super class hierarchy until the first field assignable to the
	 * <tt>value</tt> type is found. The <tt>value</tt> (or
	 * <tt>additionaValues</tt> if present) will then be assigned to this field.
	 * 
	 * @param object
	 *            the object to modify
	 * @param value
	 *            the new value of the field
	 * @param additionalValues
	 *            Additional values to set on the object
	 */
	public static void setInternalState(Object object, Object value, Object... additionalValues) {
		WhiteboxImpl.setInternalState(object, value, additionalValues);
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
		WhiteboxImpl.setInternalState(object, value, where);
	}

	/**
	 * Set the value of a field using reflection. Use this method when you need
	 * to specify in which class the field is declared. This might be useful
	 * when you have mocked the instance you are trying to modify.
	 * 
	 * @param object
	 *            the object to modify
	 * @param fieldName
	 *            the name of the field
	 * @param value
	 *            the new value of the field
	 * @param where
	 *            the class in the hierarchy where the field is defined
	 */
	public static void setInternalState(Object object, String fieldName, Object value, Class<?> where) {
		WhiteboxImpl.setInternalState(object, fieldName, value, where);
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
		WhiteboxImpl.setInternalState(object, fieldType, value);
	}

	/**
	 * Set the value of a field using reflection at a specific location (
	 * <tt>where</tt>) in the class hierarchy. The <tt>value</tt> will then be
	 * assigned to this field. The first field matching the <tt>fieldType</tt>
	 * in the hierarchy will be set.
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
		WhiteboxImpl.setInternalState(object, fieldType, value, where);
	}

	/**
	 * Get the value of a field using reflection. This method will iterate
	 * through the entire class hierarchy and return the value of the first
	 * field named <tt>fieldName</tt>. If you want to get a specific field value
	 * at specific place in the class hierarchy please refer to
	 * {@link #getInternalState(Object, String, Class)}.
	 * 
	 * @param object
	 *            the object to modify
	 * @param fieldName
	 *            the name of the field
	 */
	public static <T> T getInternalState(Object object, String fieldName) {
		return WhiteboxImpl.<T> getInternalState(object, fieldName);
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
	public static <T> T getInternalState(Object object, String fieldName, Class<?> where) {
		return WhiteboxImpl.<T> getInternalState(object, fieldName, where);
	}

	/**
	 * Get the value of a field using reflection. Use this method when you need
	 * to specify in which class the field is declared. This might be useful
	 * when you have mocked the instance you are trying to access. Use this
	 * method to avoid casting.
	 * 
	 * @deprecated Use {@link #getInternalState(Object, String, Class)} instead.
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
	@Deprecated
	public static <T> T getInternalState(Object object, String fieldName, Class<?> where, Class<T> type) {
		return Whitebox.<T> getInternalState(object, fieldName, where);
	}

	/**
	 * Get the value of a field using reflection based on the fields type. This
	 * method will traverse the super class hierarchy until the first field of
	 * type <tt>fieldType</tt> is found. The value of this field will be
	 * returned.
	 * 
	 * @param object
	 *            the object to modify
	 * @param fieldType
	 *            the type of the field
	 */
	public static <T> T getInternalState(Object object, Class<T> fieldType) {
		return WhiteboxImpl.<T> getInternalState(object, fieldType);

	}

	/**
	 * Get the value of a field using reflection based on the field type. Use
	 * this method when you need to specify in which class the field is
	 * declared. The first field matching the <tt>fieldType</tt> in
	 * <tt>where</tt> is the field whose value will be returned.
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
	public static <T> T getInternalState(Object object, Class<T> fieldType, Class<?> where) {
		return WhiteboxImpl.<T> getInternalState(object, fieldType, where);
	}

	/**
	 * Invoke a private or inner class method without the need to specify the
	 * method name. This is thus a more refactor friendly version of the
	 * {@link #invokeMethod(Object, String, Object...)} method and is recommend
	 * over this method for that reason. This method might be useful to test
	 * private methods.
	 * 
	 * @throws Throwable
	 */
	public static synchronized <T> T invokeMethod(Object instance, Object... arguments) throws Exception {
		return WhiteboxImpl.<T> invokeMethod(instance, arguments);
	}

	/**
	 * Invoke a private or inner class static method without the need to specify
	 * the method name. This is thus a more refactor friendly version of the
	 * {@link #invokeMethod(Class, String, Object...)} method and is recommend
	 * over this method for that reason. This method might be useful to test
	 * private methods.
	 * 
	 */
	public static synchronized <T> T invokeMethod(Class<?> klass, Object... arguments) throws Exception {
		return WhiteboxImpl.<T> invokeMethod(klass, arguments);
	}

	/**
	 * Invoke a private or inner class method. This might be useful to test
	 * private methods.
	 */
	public static synchronized <T> T invokeMethod(Object instance, String methodToExecute, Object... arguments) throws Exception {
		return WhiteboxImpl.<T> invokeMethod(instance, methodToExecute, arguments);
	}

	/**
	 * Invoke a private or inner class method in cases where PowerMock cannot
	 * automatically determine the type of the parameters, for example when
	 * mixing primitive types and wrapper types in the same method. For most
	 * situations use {@link #invokeMethod(Object, Object...)} instead.
	 * 
	 * @throws Exception
	 *             Exception that may occur when invoking this method.
	 */
	public static synchronized <T> T invokeMethod(Object instance, String methodToExecute, Class<?>[] argumentTypes, Object... arguments)
			throws Exception {
		return WhiteboxImpl.<T> invokeMethod(instance, methodToExecute, argumentTypes, arguments);
	}

	/**
	 * Invoke a private or inner class method in a subclass (defined by
	 * <code>definedIn</code>) in cases where PowerMock cannot automatically
	 * determine the type of the parameters, for example when mixing primitive
	 * types and wrapper types in the same method. For most situations use
	 * {@link #invokeMethod(Object, Object...)} instead.
	 * 
	 * @throws Exception
	 *             Exception that may occur when invoking this method.
	 */
	public static synchronized <T> T invokeMethod(Object instance, String methodToExecute, Class<?> definedIn, Class<?>[] argumentTypes,
			Object... arguments) throws Exception {
		return WhiteboxImpl.<T> invokeMethod(instance, methodToExecute, definedIn, argumentTypes, arguments);
	}

	/**
	 * Invoke a private or inner class method in that is located in a subclass
	 * of the instance. This might be useful to test private methods.
	 * 
	 * @throws Exception
	 *             Exception that may occur when invoking this method.
	 */
	public static synchronized <T> T invokeMethod(Object instance, Class<?> declaringClass, String methodToExecute, Object... arguments)
			throws Exception {
		return WhiteboxImpl.<T> invokeMethod(instance, declaringClass, methodToExecute, arguments);
	}

	/**
	 * Invoke a private or inner class method in that is located in a subclass
	 * of the instance. This might be useful to test private methods.
	 * <p>
	 * Use this for overloaded methods.
	 * 
	 * @throws Exception
	 *             Exception that may occur when invoking this method.
	 */
	public static synchronized <T> T invokeMethod(Object object, Class<?> declaringClass, String methodToExecute, Class<?>[] parameterTypes,
			Object... arguments) throws Exception {
		return WhiteboxImpl.<T> invokeMethod(object, declaringClass, methodToExecute, parameterTypes, arguments);
	}

	/**
	 * Invoke a static private or inner class method. This may be useful to test
	 * private methods.
	 * 
	 */
	public static synchronized <T> T invokeMethod(Class<?> clazz, String methodToExecute, Object... arguments) throws Exception {
		return WhiteboxImpl.<T> invokeMethod(clazz, methodToExecute, arguments);
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
	 * @throws Exception
	 *             If an exception occur when invoking the constructor.
	 */
	public static <T> T invokeConstructor(Class<T> classThatContainsTheConstructorToTest, Class<?>[] parameterTypes, Object[] arguments)
			throws Exception {
		return WhiteboxImpl.invokeConstructor(classThatContainsTheConstructorToTest, parameterTypes, arguments);
	}

	/**
	 * Invoke a constructor. Useful for testing classes with a private
	 * constructor.
	 * 
	 * 
	 * @return The object created after the constructor has been invoked.
	 * @throws Exception
	 *             If an exception occur when invoking the constructor.
	 */
	public static <T> T invokeConstructor(Class<T> classThatContainsTheConstructorToTest, Object... arguments) throws Exception {
		return WhiteboxImpl.invokeConstructor(classThatContainsTheConstructorToTest, arguments);
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
		return WhiteboxImpl.getFirstParentConstructor(klass);
	}

	/**
	 * Get an array of {@link Method}'s that matches the supplied list of method
	 * names. Both instance and static methods are taken into account.
	 * 
	 * @param clazz
	 *            The class that should contain the methods.
	 * @param methodNames
	 *            Names of the methods that will be returned.
	 * @return An array of Method's. May be of length 0 but not
	 *         <code>null</code>.
	 * @throws MethodNotFoundException
	 *             If no method was found.
	 */
	public static Method[] getMethods(Class<?> clazz, String... methodNames) {
		return WhiteboxImpl.getMethods(clazz, methodNames);
	}

	/**
	 * @return The type of the of an object.
	 */
	public static Class<?> getType(Object object) {
		return WhiteboxImpl.getType(object);
	}

	/**
	 * Get all fields annotated with a particular annotation. This method
	 * traverses the class hierarchy when checking for the annotation.
	 * 
	 * @param object
	 *            The object to look for annotations. Note that if're you're
	 *            passing an object only instance fields are checked, passing a
	 *            class will only check static fields.
	 * @param annotation
	 *            The annotation type to look for.
	 * @param additionalAnnotations
	 *            Optionally more annotations to look for. If any of the
	 *            annotations are associated with a particular field it will be
	 *            added to the resulting <code>Set</code>.
	 * @return A set of all fields containing the particular annotation.
	 */
	public static Set<Field> getFieldsAnnotatedWith(Object object, Class<? extends Annotation> annotation,
			Class<? extends Annotation>... additionalAnnotations) {
		return WhiteboxImpl.getFieldsAnnotatedWith(object, annotation, additionalAnnotations);
	}

	/**
	 * Get all fields annotated with a particular annotation. This method
	 * traverses the class hierarchy when checking for the annotation.
	 * 
	 * @param object
	 *            The object to look for annotations. Note that if're you're
	 *            passing an object only instance fields are checked, passing a
	 *            class will only check static fields.
	 * @param annotationTypes
	 *            The annotation types to look for
	 * @return A set of all fields containing the particular annotation(s).
	 * @since 1.3
	 */
	public static Set<Field> getFieldsAnnotatedWith(Object object, Class<? extends Annotation>[] annotationTypes) {
		return WhiteboxImpl.getFieldsAnnotatedWith(object, annotationTypes);
	}

	/**
	 * Get all instance fields for a particular object. It returns all fields
	 * regardless of the field modifier and regardless of where in the class
	 * hierarchy a field is located.
	 * 
	 * @param object
	 *            The object whose instance fields to get.
	 * @return All instance fields in the hierarchy. All fields are set to
	 *         accessible
	 */
	public static Set<Field> getAllInstanceFields(Object object) {
		return WhiteboxImpl.getAllInstanceFields(object);
	}

	/**
	 * Get all static fields for a particular type.
	 * 
	 * @param type
	 *            The class whose static fields to get.
	 * @return All static fields in <code>type</code>. All fields are set to
	 *         accessible.
	 */
	public static Set<Field> getAllStaticFields(Class<?> type) {
		return WhiteboxImpl.getAllStaticFields(type);
	}

	/**
	 * Get all fields assignable from a particular type. This method traverses
	 * the class hierarchy when checking for the type.
	 * 
	 * @param object
	 *            The object to look for type. Note that if're you're passing an
	 *            object only instance fields are checked, passing a class will
	 *            only check static fields.
	 * @param type
	 *            The type to look for.
	 * @return A set of all fields of the particular type.
	 */
	public static Set<Field> getFieldsOfType(Object object, Class<?> type) {
		return WhiteboxImpl.getFieldsOfType(object, type);
	}

	/**
	 * Get an inner class type
	 * 
	 * @param declaringClass
	 *            The class in which the inner class is declared.
	 * @param name
	 *            The unqualified name (simple name) of the inner class.
	 * @return The type.
	 */
	public static Class<Object> getInnerClassType(Class<?> declaringClass, String name) throws ClassNotFoundException {
		return WhiteboxImpl.getInnerClassType(declaringClass, name);
	}

	/**
	 * Get the type of a local inner class.
	 * 
	 * @param declaringClass
	 *            The class in which the local inner class is declared.
	 * @param occurrence
	 *            The occurrence of the local class. For example if you have two
	 *            local classes in the <code>declaringClass</code> you must pass
	 *            in <code>1</code> if you want to get the type for the first
	 *            one or <code>2</code> if you want the second one.
	 * @param name
	 *            The unqualified name (simple name) of the local class.
	 * @return The type.
	 */
	public static Class<Object> getLocalClassType(Class<?> declaringClass, int occurrence, String name) throws ClassNotFoundException {
		return WhiteboxImpl.getLocalClassType(declaringClass, occurrence, name);
	}

	/**
	 * Get the type of an anonymous inner class.
	 * 
	 * @param declaringClass
	 *            The class in which the anonymous inner class is declared.
	 * @param occurrence
	 *            The occurrence of the anonymous inner class. For example if
	 *            you have two anonymous inner classes classes in the
	 *            <code>declaringClass</code> you must pass in <code>1</code> if
	 *            you want to get the type for the first one or <code>2</code>
	 *            if you want the second one.
	 * @return The type.
	 */
	public static Class<Object> getAnonymousInnerClassType(Class<?> declaringClass, int occurrence) throws ClassNotFoundException {
		return WhiteboxImpl.getAnonymousInnerClassType(declaringClass, occurrence);
	}

	public static void setInternalStateFromContext(Object instance, Object context, Object... additionalContexts) {
		WhiteboxImpl.setInternalStateFromContext(instance, context, additionalContexts);
	}

	public static void setInternalStateFromContext(Object instance, Class<?> context, Class<?>... additionalContexts) {
		WhiteboxImpl.setInternalStateFromContext(instance, context, additionalContexts);
	}
}
