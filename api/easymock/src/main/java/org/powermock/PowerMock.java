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
package org.powermock;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.easymock.IExpectationSetters;
import org.easymock.classextension.ConstructorArgs;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;

/**
 * PowerMock extends EasyMock functionality with several new features such as
 * mocking static and private methods, mocking new instances and more. Use
 * PowerMock instead of EasyMock where applicable.
 * 
 * @deprecated Use {@link org.powermock.api.easymock.PowerMock} instead. This
 *             class may be removed in the next version.
 */
public class PowerMock {
	/**
	 * Creates a mock object that supports mocking of final and native methods.
	 * 
	 * @param <T>
	 *            the type of the mock object
	 * @param type
	 *            the type of the mock object
	 * @param methods
	 *            optionally what methods to mock
	 * @return the mock object.
	 */
	public static synchronized <T> T createMock(Class<T> type, Method... methods) {
		return org.powermock.api.easymock.PowerMock.createMock(type, methods);
	}

	/**
	 * Creates a mock object that supports mocking of final and native methods.
	 * 
	 * @param <T>
	 *            the type of the mock object
	 * @param type
	 *            the type of the mock object
	 * @return the mock object.
	 */
	public static synchronized <T> T createMock(Class<T> type) {
		return org.powermock.api.easymock.PowerMock.createMock(type);
	}

	/**
	 * Creates a mock object that supports mocking of final and native methods
	 * and invokes a specific constructor.
	 * 
	 * @param <T>
	 *            the type of the mock object
	 * @param type
	 *            the type of the mock object
	 * @param constructorArgs
	 *            The constructor arguments that will be used to invoke a
	 *            special constructor.
	 * @param methods
	 *            optionally what methods to mock
	 * @return the mock object.
	 */
	public static <T> T createMock(Class<T> type, ConstructorArgs constructorArgs, Method... methods) {
		return org.powermock.api.easymock.PowerMock.createMock(type, constructorArgs, methods);
	}

	/**
	 * Creates a mock object that supports mocking of final and native methods
	 * and invokes a specific constructor based on the supplied argument values.
	 * 
	 * @param <T>
	 *            the type of the mock object
	 * @param type
	 *            the type of the mock object
	 * @param constructorArguments
	 *            The constructor arguments that will be used to invoke a
	 *            certain constructor.
	 * @return the mock object.
	 */
	public static <T> T createMock(Class<T> type, Object... constructorArguments) {
		return org.powermock.api.easymock.PowerMock.createMock(type, constructorArguments);
	}

	/**
	 * Creates a strict mock object that supports mocking of final and native
	 * methods.
	 * 
	 * @param <T>
	 *            the type of the mock object
	 * @param type
	 *            the type of the mock object
	 * @param methods
	 *            optionally what methods to mock
	 * @return the mock object.
	 */
	public static synchronized <T> T createStrictMock(Class<T> type, Method... methods) {
		return org.powermock.api.easymock.PowerMock.createStrictMock(type, methods);
	}

	/**
	 * Creates a strict mock object that supports mocking of final and native
	 * methods.
	 * 
	 * @param <T>
	 *            the type of the mock object
	 * @param type
	 *            the type of the mock object
	 * @return the mock object.
	 */
	public static synchronized <T> T createStrictMock(Class<T> type) {
		return org.powermock.api.easymock.PowerMock.createStrictMock(type);
	}

	/**
	 * Creates a nice mock object that supports mocking of final and native
	 * methods.
	 * 
	 * @param <T>
	 *            the type of the mock object
	 * @param type
	 *            the type of the mock object
	 * @param methods
	 *            optionally what methods to mock
	 * @return the mock object.
	 */
	public static synchronized <T> T createNiceMock(Class<T> type, Method... methods) {
		return org.powermock.api.easymock.PowerMock.createNiceMock(type, methods);
	}

	/**
	 * Creates a nice mock object that supports mocking of final and native
	 * methods.
	 * 
	 * @param <T>
	 *            the type of the mock object
	 * @param type
	 *            the type of the mock object
	 * @return the mock object.
	 */
	public static synchronized <T> T createNiceMock(Class<T> type) {
		return org.powermock.api.easymock.PowerMock.createNiceMock(type);
	}

	/**
	 * Creates a strict mock object that supports mocking of final and native
	 * methods and invokes a specific constructor.
	 * 
	 * @param <T>
	 *            the type of the mock object
	 * @param type
	 *            the type of the mock object
	 * @param constructorArgs
	 *            The constructor arguments that will be used to invoke a
	 *            special constructor.
	 * @param methods
	 *            optionally what methods to mock
	 * @return the mock object.
	 */
	public static <T> T createStrictMock(Class<T> type, ConstructorArgs constructorArgs, Method... methods) {
		return org.powermock.api.easymock.PowerMock.createStrictMock(type, constructorArgs, methods);
	}

	/**
	 * Creates a nice mock object that supports mocking of final and native
	 * methods and invokes a specific constructor.
	 * 
	 * @param <T>
	 *            the type of the mock object
	 * @param type
	 *            the type of the mock object
	 * @param constructorArgs
	 *            The constructor arguments that will be used to invoke a
	 *            special constructor.
	 * @param methods
	 *            optionally what methods to mock
	 * @return the mock object.
	 */
	public static <T> T createNiceMock(Class<T> type, ConstructorArgs constructorArgs, Method... methods) {
		return org.powermock.api.easymock.PowerMock.createNiceMock(type, constructorArgs, methods);
	}

	/**
	 * Creates a strict mock object that supports mocking of final and native
	 * methods and invokes a specific constructor based on the supplied argument
	 * values.
	 * 
	 * @param <T>
	 *            the type of the mock object
	 * @param type
	 *            the type of the mock object
	 * @param constructorArguments
	 *            The constructor arguments that will be used to invoke a
	 *            certain constructor.
	 * @return the mock object.
	 */
	public static <T> T createStrictMock(Class<T> type, Object... constructorArguments) {
		return org.powermock.api.easymock.PowerMock.createStrictMock(type, constructorArguments);
	}

	/**
	 * Creates a nice mock object that supports mocking of final and native
	 * methods and invokes a specific constructor based on the supplied argument
	 * values.
	 * 
	 * @param <T>
	 *            the type of the mock object
	 * @param type
	 *            the type of the mock object
	 * @param constructorArguments
	 *            The constructor arguments that will be used to invoke a
	 *            certain constructor.
	 * @return the mock object.
	 */
	public static <T> T createNiceMock(Class<T> type, Object... constructorArguments) {
		return org.powermock.api.easymock.PowerMock.createNiceMock(type, constructorArguments);
	}

	/**
	 * Enable static mocking for a class.
	 * 
	 * @param type
	 *            the class to enable static mocking
	 * @param methods
	 *            optionally what methods to mock
	 */
	public static synchronized void mockStatic(Class<?> type, Method... methods) {
		org.powermock.api.easymock.PowerMock.mockStatic(type, methods);
	}

	/**
	 * Enable static mocking for a class.
	 * 
	 * @param type
	 *            the class to enable static mocking
	 */
	public static synchronized void mockStatic(Class<?> type) {
		org.powermock.api.easymock.PowerMock.mockStatic(type, (Method[]) null);
	}

	/**
	 * Enable strict static mocking for a class.
	 * 
	 * @param type
	 *            the class to enable static mocking
	 * @param methods
	 *            optionally what methods to mock
	 */
	public static synchronized void mockStaticStrict(Class<?> type, Method... methods) {
		org.powermock.api.easymock.PowerMock.mockStaticStrict(type, methods);
	}

	/**
	 * Enable strict static mocking for a class.
	 * 
	 * @param type
	 *            the class to enable static mocking
	 */
	public static synchronized void mockStaticStrict(Class<?> type) {
		org.powermock.api.easymock.PowerMock.mockStaticStrict(type);
	}

	/**
	 * Enable nice static mocking for a class.
	 * 
	 * @param type
	 *            the class to enable static mocking
	 * @param methods
	 *            optionally what methods to mock
	 */
	public static synchronized void mockStaticNice(Class<?> type, Method... methods) {
		org.powermock.api.easymock.PowerMock.mockStaticNice(type, methods);
	}

	/**
	 * Enable nice static mocking for a class.
	 * 
	 * @param type
	 *            the class to enable static mocking
	 */
	public static synchronized void mockStaticNice(Class<?> type) {
		org.powermock.api.easymock.PowerMock.mockStaticNice(type);
	}

	/**
	 * A utility method that may be used to specify several methods that should
	 * <i>not</i> be mocked in an easy manner (by just passing in the method
	 * names of the method you wish <i>not</i> to mock). Note that you cannot
	 * uniquely specify a method to exclude using this method if there are
	 * several methods with the same name in <code>type</code>. This method will
	 * mock ALL methods that doesn't match the supplied name(s) regardless of
	 * parameter types and signature. If this is not the case you should
	 * fall-back on using the {@link #createMock(Class, Method...)} method
	 * instead.
	 * 
	 * @param <T>
	 *            The type of the mock.
	 * @param type
	 *            The type that'll be used to create a mock instance.
	 * @param methodNames
	 *            The names of the methods that should be mocked. If
	 *            <code>null</code>, then this method will have the same effect
	 *            as just calling {@link #createMock(Class, Method...)} with the
	 *            second parameter as <code>new Method[0]</code> (i.e. all
	 *            methods in that class will be mocked).
	 * @return A mock object of type <T>.
	 */
	public static synchronized <T> T createPartialMockForAllMethodsExcept(Class<T> type, String... methodNames) {
		return org.powermock.api.easymock.PowerMock.createPartialMockForAllMethodsExcept(type, methodNames);
	}

	/**
	 * A utility method that may be used to specify several methods that should
	 * <i>not</i> be nicely mocked in an easy manner (by just passing in the
	 * method names of the method you wish <i>not</i> to mock). Note that you
	 * cannot uniquely specify a method to exclude using this method if there
	 * are several methods with the same name in <code>type</code>. This method
	 * will mock ALL methods that doesn't match the supplied name(s) regardless
	 * of parameter types and signature. If this is not the case you should
	 * fall-back on using the {@link #createMock(Class, Method...)} method
	 * instead.
	 * 
	 * @param <T>
	 *            The type of the mock.
	 * @param type
	 *            The type that'll be used to create a mock instance.
	 * @param methodNames
	 *            The names of the methods that should be mocked. If
	 *            <code>null</code>, then this method will have the same effect
	 *            as just calling {@link #createMock(Class, Method...)} with the
	 *            second parameter as <code>new Method[0]</code> (i.e. all
	 *            methods in that class will be mocked).
	 * @return A mock object of type <T>.
	 */
	public static synchronized <T> T createNicePartialMockForAllMethodsExcept(Class<T> type, String... methodNames) {
		return org.powermock.api.easymock.PowerMock.createNicePartialMockForAllMethodsExcept(type, methodNames);
	}

	/**
	 * A utility method that may be used to specify several methods that should
	 * <i>not</i> be strictly mocked in an easy manner (by just passing in the
	 * method names of the method you wish <i>not</i> to mock). Note that you
	 * cannot uniquely specify a method to exclude using this method if there
	 * are several methods with the same name in <code>type</code>. This method
	 * will mock ALL methods that doesn't match the supplied name(s) regardless
	 * of parameter types and signature. If this is not the case you should
	 * fall-back on using the {@link #createMock(Class, Method...)} method
	 * instead.
	 * 
	 * @param <T>
	 *            The type of the mock.
	 * @param type
	 *            The type that'll be used to create a mock instance.
	 * @param methodNames
	 *            The names of the methods that should be mocked. If
	 *            <code>null</code>, then this method will have the same effect
	 *            as just calling {@link #createMock(Class, Method...)} with the
	 *            second parameter as <code>new Method[0]</code> (i.e. all
	 *            methods in that class will be mocked).
	 * @return A mock object of type <T>.
	 */
	public static synchronized <T> T createStrictPartialMockForAllMethodsExcept(Class<T> type, String... methodNames) {
		return org.powermock.api.easymock.PowerMock.createStrictPartialMockForAllMethodsExcept(type, methodNames);
	}

	/**
	 * Mock all methods of a class except for a specific one. Use this method
	 * only if you have several overloaded methods.
	 * 
	 * @param <T>
	 *            The type of the mock.
	 * @param type
	 *            The type that'll be used to create a mock instance.
	 * @param methodNameToExclude
	 *            The name of the method not to mock.
	 * @param firstArgumentType
	 *            The type of the first parameter of the method not to mock
	 * @param moreTypes
	 *            Optionally more parameter types that defines the method. Note
	 *            that this is only needed to separate overloaded methods.
	 * @return A mock object of type <T>.
	 */
	public static synchronized <T> T createPartialMockForAllMethodsExcept(Class<T> type, String methodNameToExclude, Class<?> firstArgumentType,
			Class<?>... moreTypes) {
		return org.powermock.api.easymock.PowerMock.createPartialMockForAllMethodsExcept(type, methodNameToExclude, firstArgumentType, moreTypes);
	}

	/**
	 * Mock all methods of a class except for a specific one nicely. Use this
	 * method only if you have several overloaded methods.
	 * 
	 * @param <T>
	 *            The type of the mock.
	 * @param type
	 *            The type that'll be used to create a mock instance.
	 * @param methodNameToExclude
	 *            The name of the method not to mock.
	 * @param firstArgumentType
	 *            The type of the first parameter of the method not to mock
	 * @param moreTypes
	 *            Optionally more parameter types that defines the method. Note
	 *            that this is only needed to separate overloaded methods.
	 * @return A mock object of type <T>.
	 */
	public static synchronized <T> T createNicePartialMockForAllMethodsExcept(Class<T> type, String methodNameToExclude, Class<?> firstArgumentType,
			Class<?>... moreTypes) {
		return org.powermock.api.easymock.PowerMock.createNicePartialMockForAllMethodsExcept(type, methodNameToExclude, firstArgumentType, moreTypes);
	}

	/**
	 * Mock all methods of a class except for a specific one strictly. Use this
	 * method only if you have several overloaded methods.
	 * 
	 * @param <T>
	 *            The type of the mock.
	 * @param type
	 *            The type that'll be used to create a mock instance.
	 * @param methodNameToExclude
	 *            The name of the method not to mock.
	 * @param firstArgumentType
	 *            The type of the first parameter of the method not to mock
	 * @param moreTypes
	 *            Optionally more parameter types that defines the method. Note
	 *            that this is only needed to separate overloaded methods.
	 * @return A mock object of type <T>.
	 */
	public static synchronized <T> T createStrictPartialMockForAllMethodsExcept(Class<T> type, String methodNameToExclude,
			Class<?> firstArgumentType, Class<?>... moreTypes) {
		return org.powermock.api.easymock.PowerMock.createStrictPartialMockForAllMethodsExcept(type, methodNameToExclude, firstArgumentType,
				moreTypes);
	}

	/**
	 * Mock a single specific method. Use this to handle overloaded methods.
	 * 
	 * @param <T>
	 *            The type of the mock.
	 * @param type
	 *            The type that'll be used to create a mock instance.
	 * @param methodNameToMock
	 *            The name of the method to mock
	 * @param firstArgumentType
	 *            The type of the first parameter of the method to mock
	 * @param additionalArgumentTypes
	 *            Optionally more parameter types that defines the method. Note
	 *            that this is only needed to separate overloaded methods.
	 * @return A mock object of type <T>.
	 */
	public static synchronized <T> T createPartialMock(Class<T> type, String methodNameToMock, Class<?> firstArgumentType,
			Class<?>... additionalArgumentTypes) {
		return org.powermock.api.easymock.PowerMock.createPartialMock(type, methodNameToMock, firstArgumentType, additionalArgumentTypes);
	}

	/**
	 * Strictly mock a single specific method. Use this to handle overloaded
	 * methods.
	 * 
	 * @param <T>
	 *            The type of the mock.
	 * @param type
	 *            The type that'll be used to create a mock instance.
	 * @param methodNameToMock
	 *            The name of the method to mock
	 * @param firstArgumentType
	 *            The type of the first parameter of the method to mock
	 * @param additionalArgumentTypes
	 *            Optionally more parameter types that defines the method. Note
	 *            that this is only needed to separate overloaded methods.
	 * @return A mock object of type <T>.
	 */
	public static synchronized <T> T createStrictPartialMock(Class<T> type, String methodNameToMock, Class<?> firstArgumentType,
			Class<?>... additionalArgumentTypes) {
		return org.powermock.api.easymock.PowerMock.createStrictPartialMock(type, methodNameToMock, firstArgumentType, additionalArgumentTypes);
	}

	/**
	 * Nicely mock a single specific method. Use this to handle overloaded
	 * methods.
	 * 
	 * @param <T>
	 *            The type of the mock.
	 * @param type
	 *            The type that'll be used to create a mock instance.
	 * @param methodNameToMock
	 *            The name of the method to mock
	 * @param firstArgumentType
	 *            The type of the first parameter of the method to mock
	 * @param additionalArgumentTypes
	 *            Optionally more parameter types that defines the method. Note
	 *            that this is only needed to separate overloaded methods.
	 * @return A mock object of type <T>.
	 */
	public static synchronized <T> T createNicePartialMock(Class<T> type, String methodNameToMock, Class<?> firstArgumentType,
			Class<?>... additionalArgumentTypes) {
		return org.powermock.api.easymock.PowerMock.createNicePartialMock(type, methodNameToMock, firstArgumentType, additionalArgumentTypes);
	}

	/**
	 * Mock a single static method.
	 * 
	 * @param clazz
	 *            The class where the method is specified in.
	 * @param methodNameToMock
	 *            The first argument
	 * @param firstArgumentType
	 *            The first argument type.
	 * @param additionalArgumentTypes
	 *            Optional additional argument types.
	 */
	public static synchronized void mockStaticPartial(Class<?> clazz, String methodNameToMock, Class<?> firstArgumentType,
			Class<?>... additionalArgumentTypes) {
		org.powermock.api.easymock.PowerMock.mockStaticPartial(clazz, methodNameToMock, firstArgumentType, additionalArgumentTypes);
	}

	/**
	 * Mock a single static method (strict).
	 * 
	 * @param clazz
	 *            The class where the method is specified in.
	 * @param methodNameToMock
	 *            The first argument
	 * @param firstArgumentType
	 *            The first argument type.
	 * @param additionalArgumentTypes
	 *            Optional additional argument types.
	 */
	public static synchronized void mockStaticPartialStrict(Class<?> clazz, String methodNameToMock, Class<?> firstArgumentType,
			Class<?>... additionalArgumentTypes) {
		org.powermock.api.easymock.PowerMock.mockStaticPartialStrict(clazz, methodNameToMock, firstArgumentType, additionalArgumentTypes);
	}

	/**
	 * Mock a single static method (nice).
	 * 
	 * @param clazz
	 *            The class where the method is specified in.
	 * @param methodNameToMock
	 *            The first argument
	 * @param firstArgumentType
	 *            The first argument type.
	 * @param additionalArgumentTypes
	 *            Optional additional argument types.
	 */
	public static synchronized void mockStaticPartialNice(Class<?> clazz, String methodNameToMock, Class<?> firstArgumentType,
			Class<?>... additionalArgumentTypes) {
		org.powermock.api.easymock.PowerMock.mockStaticPartialNice(clazz, methodNameToMock, firstArgumentType, additionalArgumentTypes);
	}

	/**
	 * A utility method that may be used to mock several <b>static</b> methods
	 * in an easy way (by just passing in the method names of the method you
	 * wish to mock). Note that you cannot uniquely specify a method to mock
	 * using this method if there are several methods with the same name in
	 * <code>type</code>. This method will mock ALL methods that match the
	 * supplied name regardless of parameter types and signature. If this is the
	 * case you should fall-back on using the
	 * {@link #mockStatic(Class, Method...)} method instead.
	 * 
	 * @param clazz
	 *            The class that contains the static methods that should be
	 *            mocked.
	 * @param methodNames
	 *            The names of the methods that should be mocked. If
	 *            <code>null</code>, then this method will have the same effect
	 *            as just calling {@link #mockStatic(Class, Method...)} with the
	 *            second parameter as <code>new Method[0]</code> (i.e. all
	 *            methods in that class will be mocked).
	 */
	public static synchronized void mockStaticPartial(Class<?> clazz, String... methodNames) {
		org.powermock.api.easymock.PowerMock.mockStaticPartial(clazz, methodNames);
	}

	/**
	 * A utility method that may be used to mock several <b>static</b> methods
	 * (strict) in an easy way (by just passing in the method names of the
	 * method you wish to mock). Note that you cannot uniquely specify a method
	 * to mock using this method if there are several methods with the same name
	 * in <code>type</code>. This method will mock ALL methods that match the
	 * supplied name regardless of parameter types and signature. If this is the
	 * case you should fall-back on using the
	 * {@link #mockStaticStrict(Class, Method...)} method instead.
	 * 
	 * @param clazz
	 *            The class that contains the static methods that should be
	 *            mocked.
	 * @param methodNames
	 *            The names of the methods that should be mocked. If
	 *            <code>null</code>, then this method will have the same effect
	 *            as just calling {@link #mockStatic(Class, Method...)} with the
	 *            second parameter as <code>new Method[0]</code> (i.e. all
	 *            methods in that class will be mocked).
	 */
	public static synchronized void mockStaticPartialStrict(Class<?> clazz, String... methodNames) {
		org.powermock.api.easymock.PowerMock.mockStaticPartialStrict(clazz, methodNames);
	}

	/**
	 * A utility method that may be used to mock several <b>static</b> methods
	 * (nice) in an easy way (by just passing in the method names of the method
	 * you wish to mock). Note that you cannot uniquely specify a method to mock
	 * using this method if there are several methods with the same name in
	 * <code>type</code>. This method will mock ALL methods that match the
	 * supplied name regardless of parameter types and signature. If this is the
	 * case you should fall-back on using the
	 * {@link #mockStaticStrict(Class, Method...)} method instead.
	 * 
	 * @param clazz
	 *            The class that contains the static methods that should be
	 *            mocked.
	 * @param methodNames
	 *            The names of the methods that should be mocked. If
	 *            <code>null</code>, then this method will have the same effect
	 *            as just calling {@link #mockStatic(Class, Method...)} with the
	 *            second parameter as <code>new Method[0]</code> (i.e. all
	 *            methods in that class will be mocked).
	 */
	public static synchronized void mockStaticPartialNice(Class<?> clazz, String... methodNames) {
		org.powermock.api.easymock.PowerMock.mockStaticPartialNice(clazz, methodNames);
	}

	/**
	 * A utility method that may be used to mock several methods in an easy way
	 * (by just passing in the method names of the method you wish to mock).
	 * Note that you cannot uniquely specify a method to mock using this method
	 * if there are several methods with the same name in <code>type</code>.
	 * This method will mock ALL methods that match the supplied name regardless
	 * of parameter types and signature. If this is the case you should
	 * fall-back on using the {@link #createMock(Class, Method...)} method
	 * instead.
	 * 
	 * @param <T>
	 *            The type of the mock.
	 * @param type
	 *            The type that'll be used to create a mock instance.
	 * @param methodNames
	 *            The names of the methods that should be mocked. If
	 *            <code>null</code>, then this method will have the same effect
	 *            as just calling {@link #createMock(Class, Method...)} with the
	 *            second parameter as <code>new Method[0]</code> (i.e. all
	 *            methods in that class will be mocked).
	 * @return A mock object of type <T>.
	 */
	public static synchronized <T> T createPartialMock(Class<T> type, String... methodNames) {
		return org.powermock.api.easymock.PowerMock.createPartialMock(type, methodNames);
	}

	/**
	 * A utility method that may be used to mock several methods in an easy way
	 * (by just passing in the method names of the method you wish to mock).
	 * Note that you cannot uniquely specify a method to mock using this method
	 * if there are several methods with the same name in <code>type</code>.
	 * This method will mock ALL methods that match the supplied name regardless
	 * of parameter types and signature. If this is the case you should
	 * fall-back on using the {@link #createMock(Class, Method...)} method
	 * instead.
	 * <p>
	 * With this method you can specify where the class hierarchy the methods
	 * are located. This is useful in, for example, situations where class A
	 * extends B and both have a method called "mockMe" (A overrides B's mockMe
	 * method) and you like to specify the only the "mockMe" method in B should
	 * be mocked. "mockMe" in A should be left intact. In this case you should
	 * do:
	 * 
	 * <pre>
	 * A tested = createPartialMock(A.class, B.class, &quot;mockMe&quot;);
	 * </pre>
	 * 
	 * 
	 * @param <T>
	 *            The type of the mock.
	 * @param type
	 *            The type that'll be used to create a mock instance.
	 * @param where
	 *            Where in the class hierarchy the methods resides.
	 * @param methodNames
	 *            The names of the methods that should be mocked. If
	 *            <code>null</code>, then this method will have the same effect
	 *            as just calling {@link #createMock(Class, Method...)} with the
	 *            second parameter as <code>new Method[0]</code> (i.e. all
	 *            methods in that class will be mocked).
	 * @return A mock object of type <T>.
	 */
	public static synchronized <T> T createPartialMock(Class<T> type, Class<? super T> where, String... methodNames) {
		return org.powermock.api.easymock.PowerMock.createPartialMock(type, where, methodNames);
	}

	/**
	 * A utility method that may be used to strictly mock several methods in an
	 * easy way (by just passing in the method names of the method you wish to
	 * mock). Note that you cannot uniquely specify a method to mock using this
	 * method if there are several methods with the same name in
	 * <code>type</code>. This method will mock ALL methods that match the
	 * supplied name regardless of parameter types and signature. If this is the
	 * case you should fall-back on using the
	 * {@link #createMock(Class, Method...)} method instead.
	 * 
	 * @param <T>
	 *            The type of the mock.
	 * @param type
	 *            The type that'll be used to create a mock instance.
	 * @param methodNames
	 *            The names of the methods that should be mocked. If
	 *            <code>null</code>, then this method will have the same effect
	 *            as just calling {@link #createMock(Class, Method...)} with the
	 *            second parameter as <code>new Method[0]</code> (i.e. all
	 *            methods in that class will be mocked).
	 * @return A mock object of type <T>.
	 */
	public static synchronized <T> T createStrictPartialMock(Class<T> type, String... methodNames) {
		return org.powermock.api.easymock.PowerMock.createStrictPartialMock(type, methodNames);
	}

	/**
	 * A utility method that may be used to strictly mock several methods in an
	 * easy way (by just passing in the method names of the method you wish to
	 * mock). Note that you cannot uniquely specify a method to mock using this
	 * method if there are several methods with the same name in
	 * <code>type</code>. This method will mock ALL methods that match the
	 * supplied name regardless of parameter types and signature. If this is the
	 * case you should fall-back on using the
	 * {@link #createMock(Class, Method...)} method instead.
	 * <p>
	 * With this method you can specify where the class hierarchy the methods
	 * are located. This is useful in, for example, situations where class A
	 * extends B and both have a method called "mockMe" (A overrides B's mockMe
	 * method) and you like to specify the only the "mockMe" method in B should
	 * be mocked. "mockMe" in A should be left intact. In this case you should
	 * do:
	 * 
	 * <pre>
	 * A tested = createPartialMockStrict(A.class, B.class, &quot;mockMe&quot;);
	 * </pre>
	 * 
	 * @param <T>
	 *            The type of the mock.
	 * @param type
	 *            The type that'll be used to create a mock instance.
	 * @param where
	 *            Where in the class hierarchy the methods resides.
	 * @param methodNames
	 *            The names of the methods that should be mocked. If
	 *            <code>null</code>, then this method will have the same effect
	 *            as just calling {@link #createMock(Class, Method...)} with the
	 *            second parameter as <code>new Method[0]</code> (i.e. all
	 *            methods in that class will be mocked).
	 * @return A mock object of type <T>.
	 */
	public static synchronized <T> T createStrictPartialMock(Class<T> type, Class<? super T> where, String... methodNames) {
		return org.powermock.api.easymock.PowerMock.createStrictPartialMock(type, where, methodNames);
	}

	/**
	 * A utility method that may be used to nicely mock several methods in an
	 * easy way (by just passing in the method names of the method you wish to
	 * mock). Note that you cannot uniquely specify a method to mock using this
	 * method if there are several methods with the same name in
	 * <code>type</code>. This method will mock ALL methods that match the
	 * supplied name regardless of parameter types and signature. If this is the
	 * case you should fall-back on using the
	 * {@link #createMock(Class, Method...)} method instead.
	 * 
	 * @param <T>
	 *            The type of the mock.
	 * @param type
	 *            The type that'll be used to create a mock instance.
	 * @param methodNames
	 *            The names of the methods that should be mocked. If
	 *            <code>null</code>, then this method will have the same effect
	 *            as just calling {@link #createMock(Class, Method...)} with the
	 *            second parameter as <code>new Method[0]</code> (i.e. all
	 *            methods in that class will be mocked).
	 * @return A mock object of type <T>.
	 */
	public static synchronized <T> T createNicePartialMock(Class<T> type, String... methodNames) {
		return org.powermock.api.easymock.PowerMock.createNicePartialMock(type, methodNames);
	}

	/**
	 * A utility method that may be used to nicely mock several methods in an
	 * easy way (by just passing in the method names of the method you wish to
	 * mock). Note that you cannot uniquely specify a method to mock using this
	 * method if there are several methods with the same name in
	 * <code>type</code>. This method will mock ALL methods that match the
	 * supplied name regardless of parameter types and signature. If this is the
	 * case you should fall-back on using the
	 * {@link #createMock(Class, Method...)} method instead.
	 * <p>
	 * With this method you can specify where the class hierarchy the methods
	 * are located. This is useful in, for example, situations where class A
	 * extends B and both have a method called "mockMe" (A overrides B's mockMe
	 * method) and you like to specify the only the "mockMe" method in B should
	 * be mocked. "mockMe" in A should be left intact. In this case you should
	 * do:
	 * 
	 * <pre>
	 * A tested = createPartialMockNice(A.class, B.class, &quot;mockMe&quot;);
	 * </pre>
	 * 
	 * @param <T>
	 *            The type of the mock.
	 * @param type
	 *            The type that'll be used to create a mock instance.
	 * @param where
	 *            Where in the class hierarchy the methods resides.
	 * @param methodNames
	 *            The names of the methods that should be mocked. If
	 *            <code>null</code>, then this method will have the same effect
	 *            as just calling {@link #createMock(Class, Method...)} with the
	 *            second parameter as <code>new Method[0]</code> (i.e. all
	 *            methods in that class will be mocked).
	 * @return A mock object of type <T>.
	 */
	public static synchronized <T> T createNicePartialMock(Class<T> type, Class<? super T> where, String... methodNames) {
		return org.powermock.api.easymock.PowerMock.createNiceMock(type, where, methodNames);
	}

	/**
	 * A utility method that may be used to mock several methods in an easy way
	 * (by just passing in the method names of the method you wish to mock). The
	 * mock object created will support mocking of final methods and invokes the
	 * default constructor (even if it's private).
	 * 
	 * @param <T>
	 *            the type of the mock object
	 * @param type
	 *            the type of the mock object
	 * @param methodNames
	 *            The names of the methods that should be mocked. If
	 *            <code>null</code>, then this method will have the same effect
	 *            as just calling {@link #createMock(Class, Method...)} with the
	 *            second parameter as <code>new Method[0]</code> (i.e. all
	 *            methods in that class will be mocked).
	 * @return the mock object.
	 */
	public static <T> T createPartialMockAndInvokeDefaultConstructor(Class<T> type, String... methodNames) throws Exception {
		return org.powermock.api.easymock.PowerMock.createPartialMockAndInvokeDefaultConstructor(type, methodNames);
	}

	/**
	 * A utility method that may be used to nicely mock several methods in an
	 * easy way (by just passing in the method names of the method you wish to
	 * mock). The mock object created will support mocking of final methods and
	 * invokes the default constructor (even if it's private).
	 * 
	 * @param <T>
	 *            the type of the mock object
	 * @param type
	 *            the type of the mock object
	 * @param methodNames
	 *            The names of the methods that should be mocked. If
	 *            <code>null</code>, then this method will have the same effect
	 *            as just calling {@link #createMock(Class, Method...)} with the
	 *            second parameter as <code>new Method[0]</code> (i.e. all
	 *            methods in that class will be mocked).
	 * @return the mock object.
	 */
	public static <T> T createNicePartialMockAndInvokeDefaultConstructor(Class<T> type, String... methodNames) throws Exception {
		return org.powermock.api.easymock.PowerMock.createNicePartialMockAndInvokeDefaultConstructor(type, methodNames);
	}

	/**
	 * A utility method that may be used to strictly mock several methods in an
	 * easy way (by just passing in the method names of the method you wish to
	 * mock). The mock object created will support mocking of final methods and
	 * invokes the default constructor (even if it's private).
	 * 
	 * @param <T>
	 *            the type of the mock object
	 * @param type
	 *            the type of the mock object
	 * @param methodNames
	 *            The names of the methods that should be mocked. If
	 *            <code>null</code>, then this method will have the same effect
	 *            as just calling {@link #createMock(Class, Method...)} with the
	 *            second parameter as <code>new Method[0]</code> (i.e. all
	 *            methods in that class will be mocked).
	 * @return the mock object.
	 */
	public static <T> T createStrictPartialMockAndInvokeDefaultConstructor(Class<T> type, String... methodNames) throws Exception {
		return org.powermock.api.easymock.PowerMock.createStrictPartialMockAndInvokeDefaultConstructor(type, methodNames);
	}

	/**
	 * * A utility method that may be used to mock several methods in an easy
	 * way (by just passing in the method names of the method you wish to mock).
	 * The mock object created will support mocking of final and native methods
	 * and invokes a specific constructor based on the supplied argument values.
	 * 
	 * @param <T>
	 *            the type of the mock object
	 * @param type
	 *            the type of the mock object
	 * @param methodNames
	 *            The names of the methods that should be mocked. If
	 *            <code>null</code>, then this method will have the same effect
	 *            as just calling {@link #createMock(Class, Method...)} with the
	 *            second parameter as <code>new Method[0]</code> (i.e. all
	 *            methods in that class will be mocked).
	 * @param constructorArguments
	 *            The constructor arguments that will be used to invoke a
	 *            certain constructor. (optional)
	 * @return the mock object.
	 */
	public static <T> T createPartialMock(Class<T> type, String[] methodNames, Object... constructorArguments) {
		return org.powermock.api.easymock.PowerMock.createPartialMock(type, methodNames, constructorArguments);
	}

	/**
	 * * A utility method that may be used to strictly mock several methods in
	 * an easy way (by just passing in the method names of the method you wish
	 * to mock). The mock object created will support mocking of final and
	 * native methods and invokes a specific constructor based on the supplied
	 * argument values.
	 * 
	 * @param <T>
	 *            the type of the mock object
	 * @param type
	 *            the type of the mock object
	 * @param methodNames
	 *            The names of the methods that should be mocked. If
	 *            <code>null</code>, then this method will have the same effect
	 *            as just calling {@link #createMock(Class, Method...)} with the
	 *            second parameter as <code>new Method[0]</code> (i.e. all
	 *            methods in that class will be mocked).
	 * @param constructorArguments
	 *            The constructor arguments that will be used to invoke a
	 *            certain constructor. (optional)
	 * @return the mock object.
	 */
	public static <T> T createStrictPartialMock(Class<T> type, String[] methodNames, Object... constructorArguments) {
		return org.powermock.api.easymock.PowerMock.createStrictPartialMock(type, methodNames, constructorArguments);
	}

	/**
	 * * A utility method that may be used to nicely mock several methods in an
	 * easy way (by just passing in the method names of the method you wish to
	 * mock). The mock object created will support mocking of final and native
	 * methods and invokes a specific constructor based on the supplied argument
	 * values.
	 * 
	 * @param <T>
	 *            the type of the mock object
	 * @param type
	 *            the type of the mock object
	 * @param methodNames
	 *            The names of the methods that should be mocked. If
	 *            <code>null</code>, then this method will have the same effect
	 *            as just calling {@link #createMock(Class, Method...)} with the
	 *            second parameter as <code>new Method[0]</code> (i.e. all
	 *            methods in that class will be mocked).
	 * @param constructorArguments
	 *            The constructor arguments that will be used to invoke a
	 *            certain constructor. (optional)
	 * @return the mock object.
	 */
	public static <T> T createNicePartialMock(Class<T> type, String[] methodNames, Object... constructorArguments) {
		return org.powermock.api.easymock.PowerMock.createNicePartialMock(type, methodNames, constructorArguments);
	}

	/**
	 * A utility method that may be used to mock several methods in an easy way
	 * (by just passing in the method names of the method you wish to mock). Use
	 * this to handle overloaded methods. The mock object created will support
	 * mocking of final and native methods and invokes a specific constructor
	 * based on the supplied argument values.
	 * 
	 * @param <T>
	 *            the type of the mock object
	 * @param type
	 *            the type of the mock object
	 * @param methodName
	 *            The names of the methods that should be mocked. If
	 *            <code>null</code>, then this method will have the same effect
	 *            as just calling {@link #createMock(Class, Method...)} with the
	 *            second parameter as <code>new Method[0]</code> (i.e. all
	 *            methods in that class will be mocked).
	 * @param methodParameterTypes
	 *            Parameter types that defines the method. Note that this is
	 *            only needed to separate overloaded methods.
	 * @param constructorArguments
	 *            The constructor arguments that will be used to invoke a
	 *            certain constructor. (optional)
	 * @return the mock object.
	 */
	public static <T> T createPartialMock(Class<T> type, String methodName, Class<?>[] methodParameterTypes, Object... constructorArguments) {
		return org.powermock.api.easymock.PowerMock.createPartialMock(type, methodName, methodParameterTypes, constructorArguments);
	}

	/**
	 * A utility method that may be used to strictly mock several methods in an
	 * easy way (by just passing in the method names of the method you wish to
	 * mock). Use this to handle overloaded methods. The mock object created
	 * will support mocking of final and native methods and invokes a specific
	 * constructor based on the supplied argument values.
	 * 
	 * @param <T>
	 *            the type of the mock object
	 * @param type
	 *            the type of the mock object
	 * @param methodName
	 *            The names of the methods that should be mocked. If
	 *            <code>null</code>, then this method will have the same effect
	 *            as just calling {@link #createMock(Class, Method...)} with the
	 *            second parameter as <code>new Method[0]</code> (i.e. all
	 *            methods in that class will be mocked).
	 * @param methodParameterTypes
	 *            Parameter types that defines the method. Note that this is
	 *            only needed to separate overloaded methods.
	 * @param constructorArguments
	 *            The constructor arguments that will be used to invoke a
	 *            certain constructor. (optional)
	 * @return the mock object.
	 */
	public static <T> T createStrictPartialMock(Class<T> type, String methodName, Class<?>[] methodParameterTypes, Object... constructorArguments) {
		return org.powermock.api.easymock.PowerMock.createStrictPartialMock(type, methodName, methodParameterTypes, constructorArguments);
	}

	/**
	 * A utility method that may be used to nicely mock several methods in an
	 * easy way (by just passing in the method names of the method you wish to
	 * mock). Use this to handle overloaded methods. The mock object created
	 * will support mocking of final and native methods and invokes a specific
	 * constructor based on the supplied argument values.
	 * 
	 * @param <T>
	 *            the type of the mock object
	 * @param type
	 *            the type of the mock object
	 * @param methodName
	 *            The names of the methods that should be mocked. If
	 *            <code>null</code>, then this method will have the same effect
	 *            as just calling {@link #createMock(Class, Method...)} with the
	 *            second parameter as <code>new Method[0]</code> (i.e. all
	 *            methods in that class will be mocked).
	 * @param methodParameterTypes
	 *            Parameter types that defines the method. Note that this is
	 *            only needed to separate overloaded methods.
	 * @param constructorArguments
	 *            The constructor arguments that will be used to invoke a
	 *            certain constructor. (optional)
	 * @return the mock object.
	 */
	public static <T> T createNicePartialMock(Class<T> type, String methodName, Class<?>[] methodParameterTypes, Object... constructorArguments) {
		return org.powermock.api.easymock.PowerMock.createNicePartialMock(type, methodName, methodParameterTypes, constructorArguments);
	}

	/**
	 * A utility method that may be used to mock several methods in an easy way
	 * (by just passing in the method names of the method you wish to mock). Use
	 * this to handle overloaded methods <i>and</i> overloaded constructors. The
	 * mock object created will support mocking of final and native methods and
	 * invokes a specific constructor based on the supplied argument values.
	 * 
	 * @param <T>
	 *            the type of the mock object
	 * @param type
	 *            the type of the mock object
	 * @param methodName
	 *            The names of the methods that should be mocked. If
	 *            <code>null</code>, then this method will have the same effect
	 *            as just calling {@link #createMock(Class, Method...)} with the
	 *            second parameter as <code>new Method[0]</code> (i.e. all
	 *            methods in that class will be mocked).
	 * @param methodParameterTypes
	 *            Parameter types that defines the method. Note that this is
	 *            only needed to separate overloaded methods.
	 * @param constructorArguments
	 *            The constructor arguments that will be used to invoke a
	 *            certain constructor.
	 * @param constructorParameterTypes
	 *            Parameter types that defines the constructor that should be
	 *            invoked. Note that this is only needed to separate overloaded
	 *            constructors.
	 * @return the mock object.
	 */
	public static <T> T createPartialMock(Class<T> type, String methodName, Class<?>[] methodParameterTypes, Object[] constructorArguments,
			Class<?>[] constructorParameterTypes) {
		return org.powermock.api.easymock.PowerMock.createPartialMock(type, methodName, methodParameterTypes, constructorArguments);
	}

	/**
	 * A utility method that may be used to strictly mock several methods in an
	 * easy way (by just passing in the method names of the method you wish to
	 * mock). Use this to handle overloaded methods <i>and</i> overloaded
	 * constructors. The mock object created will support mocking of final and
	 * native methods and invokes a specific constructor based on the supplied
	 * argument values.
	 * 
	 * @param <T>
	 *            the type of the mock object
	 * @param type
	 *            the type of the mock object
	 * @param methodName
	 *            The names of the methods that should be mocked. If
	 *            <code>null</code>, then this method will have the same effect
	 *            as just calling {@link #createMock(Class, Method...)} with the
	 *            second parameter as <code>new Method[0]</code> (i.e. all
	 *            methods in that class will be mocked).
	 * @param methodParameterTypes
	 *            Parameter types that defines the method. Note that this is
	 *            only needed to separate overloaded methods.
	 * @param constructorArguments
	 *            The constructor arguments that will be used to invoke a
	 *            certain constructor.
	 * @param constructorParameterTypes
	 *            Parameter types that defines the constructor that should be
	 *            invoked. Note that this is only needed to separate overloaded
	 *            constructors.
	 * @return the mock object.
	 */
	public static <T> T createStrictPartialMock(Class<T> type, String methodName, Class<?>[] methodParameterTypes, Object[] constructorArguments,
			Class<?>[] constructorParameterTypes) {
		return org.powermock.api.easymock.PowerMock.createStrictPartialMock(type, methodName, methodParameterTypes, constructorArguments);
	}

	/**
	 * A utility method that may be used to nicely mock several methods in an
	 * easy way (by just passing in the method names of the method you wish to
	 * mock). Use this to handle overloaded methods <i>and</i> overloaded
	 * constructors. The mock object created will support mocking of final and
	 * native methods and invokes a specific constructor based on the supplied
	 * argument values.
	 * 
	 * @param <T>
	 *            the type of the mock object
	 * @param type
	 *            the type of the mock object
	 * @param methodName
	 *            The names of the methods that should be mocked. If
	 *            <code>null</code>, then this method will have the same effect
	 *            as just calling {@link #createMock(Class, Method...)} with the
	 *            second parameter as <code>new Method[0]</code> (i.e. all
	 *            methods in that class will be mocked).
	 * @param methodParameterTypes
	 *            Parameter types that defines the method. Note that this is
	 *            only needed to separate overloaded methods.
	 * @param constructorArguments
	 *            The constructor arguments that will be used to invoke a
	 *            certain constructor.
	 * @param constructorParameterTypes
	 *            Parameter types that defines the constructor that should be
	 *            invoked. Note that this is only needed to separate overloaded
	 *            constructors.
	 * @return the mock object.
	 */
	public static <T> T createNicePartialMock(Class<T> type, String methodName, Class<?>[] methodParameterTypes, Object[] constructorArguments,
			Class<?>[] constructorParameterTypes) {
		return org.powermock.api.easymock.PowerMock.createNicePartialMock(type, methodName, methodParameterTypes, constructorArguments);
	}

	/**
	 * Used to specify expectations on private static methods. If possible use
	 * variant with only method name.
	 */
	public static synchronized <T> IExpectationSetters<T> expectPrivate(Class<?> clazz, Method method, Object... arguments) throws Exception {
		return org.powermock.api.easymock.PowerMock.expectPrivate(clazz, method, arguments);
	}

	/**
	 * Used to specify expectations on private methods. If possible use variant
	 * with only method name.
	 */
	public static synchronized <T> IExpectationSetters<T> expectPrivate(Object instance, Method method, Object... arguments) throws Exception {
		return org.powermock.api.easymock.PowerMock.expectPrivate(instance, method, arguments);
	}

	/**
	 * Used to specify expectations on private methods. Use this method to
	 * handle overloaded methods.
	 */
	@SuppressWarnings("all")
	public static synchronized <T> IExpectationSetters<T> expectPrivate(Object instance, String methodName, Class<?>[] parameterTypes,
			Object... arguments) throws Exception {
		return org.powermock.api.easymock.PowerMock.expectPrivate(instance, methodName, parameterTypes, arguments);
	}

	/**
	 * Used to specify expectations on methods using the method name. Works on
	 * for example private or package private methods.
	 */
	public static synchronized <T> IExpectationSetters<T> expectPrivate(Object instance, String methodName, Object... arguments) throws Exception {
		return org.powermock.api.easymock.PowerMock.expectPrivate(instance, methodName, arguments);
	}

	/**
	 * Used to specify expectations on methods using the method name at a
	 * specific place in the class hierarchy (specified by the
	 * <code>where</code> parameter). Works on for example private or package
	 * private methods.
	 * <p>
	 * Use this for overloaded methods.
	 */
	public static synchronized <T> IExpectationSetters<T> expectPrivate(Object instance, String methodName, Class<?> where,
			Class<?>[] parameterTypes, Object... arguments) throws Exception {
		return org.powermock.api.easymock.PowerMock.expectPrivate(instance, methodName, where, arguments);
	}

	/**
	 * Used to specify expectations on methods using the method name at a
	 * specific place in the class hierarchy (specified by the
	 * <code>where</code> parameter). Works on for example private or package
	 * private methods.
	 */
	public static synchronized <T> IExpectationSetters<T> expectPrivate(Object instance, String methodName, Class<?> where, Object... arguments)
			throws Exception {
		return org.powermock.api.easymock.PowerMock.expectPrivate(instance, methodName, where, arguments);
	}

	/**
	 * This method just delegates to EasyMock class extensions
	 * {@link org.easymock.classextension.EasyMock#expectLastCall()} method.
	 * 
	 * @see org.easymock.classextension.EasyMock#expectLastCall()
	 * 
	 * @return The expectation setter.
	 */
	public static synchronized IExpectationSetters<Object> expectLastCall() {
		return org.powermock.api.easymock.PowerMock.expectLastCall();
	}

	/**
	 * Sometimes it is useful to allow replay and verify on non-mocks. For
	 * example when using partial mocking in some tests and no mocking in other
	 * test-methods, but using the same setUp and tearDown.
	 */
	public static synchronized void niceReplayAndVerify() {
		org.powermock.api.easymock.PowerMock.niceReplayAndVerify();
	}

	/**
	 * Replay all classes and mock objects known by PowerMock. This includes all
	 * classes that are prepared for test using the {@link PrepareForTest} or
	 * {@link PrepareOnlyThisForTest} annotations and all classes that have had
	 * their static initializers removed by using the
	 * {@link SuppressStaticInitializationFor} annotation. It also includes all
	 * mock instances created by PowerMock such as those created or used by
	 * {@link #createMock(Class, Method...)},
	 * {@link #mockStatic(Class, Method...)},
	 * {@link #expectNew(Class, Object...)},
	 * {@link #createPartialMock(Class, String...)} etc.
	 * <p>
	 * To make it easy to pass in additional mocks <i>not</i> created by the
	 * PowerMock API you can optionally specify them as <tt>additionalMocks</tt>
	 * . These are typically those mock objects you have created using pure
	 * EasyMock or EasyMock class extensions. No additional mocks needs to be
	 * specified if you're only using PowerMock API methods.
	 * <p>
	 * Note that the <tt>additionalMocks</tt> are also automatically verified
	 * when invoking the {@link #verifyAll()} method.
	 * 
	 * @param additionalMocks
	 *            Mocks not created by the PowerMock API. These are typically
	 *            those mock objects you have created using pure EasyMock or
	 *            EasyMock class extensions.
	 */
	public static synchronized void replayAll(Object... additionalMocks) {
		org.powermock.api.easymock.PowerMock.replayAll(additionalMocks);
	}

	/**
	 * Verify all classes and mock objects known by PowerMock. This includes all
	 * classes that are prepared for test using the {@link PrepareForTest} or
	 * {@link PrepareOnlyThisForTest} annotations and all classes that have had
	 * their static initializers removed by using the
	 * {@link SuppressStaticInitializationFor} annotation. It also includes all
	 * mock instances created by PowerMock such as those created or used by
	 * {@link #createMock(Class, Method...)},
	 * {@link #mockStatic(Class, Method...)},
	 * {@link #expectNew(Class, Object...)},
	 * {@link #createPartialMock(Class, String...)} etc.
	 * <p>
	 * Note that all <tt>additionalMocks</tt> passed to the
	 * {@link #replayAll(Object...)} method are also verified here
	 * automatically.
	 * 
	 */
	public static synchronized void verifyAll() {
		org.powermock.api.easymock.PowerMock.verifyAll();
	}

	/**
	 * Switches the mocks or classes to replay mode. Note that you must use this
	 * method when using PowerMock!
	 * 
	 * @param mocks
	 *            mock objects or classes loaded by PowerMock.
	 * @throws Exception
	 *             If something unexpected goes wrong.
	 */
	public static synchronized void replay(Object... mocks) {
		org.powermock.api.easymock.PowerMock.replay(mocks);
	}

	/**
	 * Switches the mocks or classes to verify mode. Note that you must use this
	 * method when using PowerMock!
	 * 
	 * @param mocks
	 *            mock objects or classes loaded by PowerMock.
	 */
	public static synchronized void verify(Object... objects) {
		org.powermock.api.easymock.PowerMock.verify(objects);
	}

	/**
	 * Convenience method for createMock followed by expectNew.
	 * 
	 * @param type
	 *            The class that should be mocked.
	 * @param arguments
	 *            The constructor arguments.
	 * @return A mock object of the same type as the mock.
	 * @throws Exception
	 */
	public static synchronized <T> T createMockAndExpectNew(Class<T> type, Object... arguments) throws Exception {
		return org.powermock.api.easymock.PowerMock.createMockAndExpectNew(type, arguments);
	}

	/**
	 * Convenience method for createMock followed by expectNew when PowerMock
	 * cannot determine which constructor to use automatically. This happens
	 * when you have one constructor taking a primitive type and another one
	 * taking the wrapper type of the primitive. For example <code>int</code>
	 * and <code>Integer</code>.
	 * 
	 * @param type
	 *            The class that should be mocked.
	 * @param parameterTypes
	 *            The constructor parameter types.
	 * @param arguments
	 *            The constructor arguments.
	 * @return A mock object of the same type as the mock.
	 * @throws Exception
	 */
	public static synchronized <T> T createMockAndExpectNew(Class<T> type, Class<?>[] parameterTypes, Object... arguments) throws Exception {
		return org.powermock.api.easymock.PowerMock.createMockAndExpectNew(type, parameterTypes, arguments);
	}

	/**
	 * Convenience method for createNiceMock followed by expectNew.
	 * 
	 * @param type
	 *            The class that should be mocked.
	 * @param arguments
	 *            The constructor arguments.
	 * @return A mock object of the same type as the mock.
	 * @throws Exception
	 */
	public static synchronized <T> T createNiceMockAndExpectNew(Class<T> type, Object... arguments) throws Exception {
		return org.powermock.api.easymock.PowerMock.createNiceMockAndExpectNew(type, arguments);
	}

	/**
	 * Convenience method for createNiceMock followed by expectNew when
	 * PowerMock cannot determine which constructor to use automatically. This
	 * happens when you have one constructor taking a primitive type and another
	 * one taking the wrapper type of the primitive. For example
	 * <code>int</code> and <code>Integer</code>.
	 * 
	 * @param type
	 *            The class that should be mocked.
	 * @param parameterTypes
	 *            The constructor parameter types.
	 * @param arguments
	 *            The constructor arguments.
	 * @return A mock object of the same type as the mock.
	 * @throws Exception
	 */
	public static synchronized <T> T createNiceMockAndExpectNew(Class<T> type, Class<?>[] parameterTypes, Object... arguments) throws Exception {
		return org.powermock.api.easymock.PowerMock.createNiceMockAndExpectNew(type, parameterTypes, arguments);
	}

	/**
	 * Convenience method for createStrictMock followed by expectNew.
	 * 
	 * @param type
	 *            The class that should be mocked.
	 * @param arguments
	 *            The constructor arguments.
	 * @return A mock object of the same type as the mock.
	 * @throws Exception
	 */
	public static synchronized <T> T createStrictMockAndExpectNew(Class<T> type, Object... arguments) throws Exception {
		return org.powermock.api.easymock.PowerMock.createStrictMockAndExpectNew(type, arguments);
	}

	/**
	 * Convenience method for createStrictMock followed by expectNew when
	 * PowerMock cannot determine which constructor to use automatically. This
	 * happens when you have one constructor taking a primitive type and another
	 * one taking the wrapper type of the primitive. For example
	 * <code>int</code> and <code>Integer</code>.
	 * 
	 * @param type
	 *            The class that should be mocked.
	 * @param parameterTypes
	 *            The constructor parameter types.
	 * @param arguments
	 *            The constructor arguments.
	 * @return A mock object of the same type as the mock.
	 * @throws Exception
	 */
	public static synchronized <T> T createStrictMockAndExpectNew(Class<T> type, Class<?>[] parameterTypes, Object... arguments) throws Exception {
		return org.powermock.api.easymock.PowerMock.createStrictMockAndExpectNew(type, parameterTypes, arguments);
	}

	/**
	 * Allows specifying expectations on new invocations. For example you might
	 * want to throw an exception or return a mock. Note that you must replay
	 * the class when using this method since this behavior is part of the class
	 * mock.
	 * <p>
	 * Use this method when you need to specify parameter types for the
	 * constructor when PowerMock cannot determine which constructor to use
	 * automatically. In most cases you should use
	 * {@link #expectNew(Class, Object...)} instead.
	 */
	public static synchronized <T> IExpectationSetters<T> expectNew(Class<T> type, Class<?>[] parameterTypes, Object... arguments) throws Exception {
		return org.powermock.api.easymock.PowerMock.expectNew(type, parameterTypes, arguments);
	}

	/**
	 * Allows specifying expectations on new invocations. For example you might
	 * want to throw an exception or return a mock. Note that you must replay
	 * the class when using this method since this behavior is part of the class
	 * mock.
	 */
	public static synchronized <T> IExpectationSetters<T> expectNew(Class<T> type, Object... arguments) throws Exception {
		return org.powermock.api.easymock.PowerMock.expectNew(type, arguments);
	}

	/**
	 * Allows specifying expectations on new invocations. For example you might
	 * want to throw an exception or return a mock.
	 * <p>
	 * This method checks the order of constructor invocations.
	 * <p>
	 * Note that you must replay the class when using this method since this
	 * behavior is part of the class mock.
	 */
	public static synchronized <T> IExpectationSetters<T> expectStrictNew(Class<T> type, Object... arguments) throws Exception {
		return org.powermock.api.easymock.PowerMock.expectStrictNew(type, arguments);
	}

	/**
	 * Allows specifying expectations on new invocations. For example you might
	 * want to throw an exception or return a mock. Note that you must replay
	 * the class when using this method since this behavior is part of the class
	 * mock.
	 * <p>
	 * This method checks the order of constructor invocations.
	 * <p>
	 * Use this method when you need to specify parameter types for the
	 * constructor when PowerMock cannot determine which constructor to use
	 * automatically. In most cases you should use
	 * {@link #expectNew(Class, Object...)} instead.
	 */
	public static synchronized <T> IExpectationSetters<T> expectStrictNew(Class<T> type, Class<?>[] parameterTypes, Object... arguments)
			throws Exception {
		return org.powermock.api.easymock.PowerMock.expectStrictNew(type, parameterTypes, arguments);
	}

	/**
	 * Allows specifying expectations on new invocations. For example you might
	 * want to throw an exception or return a mock.
	 * <p>
	 * This method allows any number of calls to a new constructor without
	 * throwing an exception.
	 * <p>
	 * Note that you must replay the class when using this method since this
	 * behavior is part of the class mock.
	 */
	public static synchronized <T> IExpectationSetters<T> expectNiceNew(Class<T> type, Object... arguments) throws Exception {
		return org.powermock.api.easymock.PowerMock.expectNiceNew(type, arguments);
	}

	/**
	 * Allows specifying expectations on new invocations. For example you might
	 * want to throw an exception or return a mock. Note that you must replay
	 * the class when using this method since this behavior is part of the class
	 * mock.
	 * <p>
	 * This method allows any number of calls to a new constructor without
	 * throwing an exception.
	 * <p>
	 * Use this method when you need to specify parameter types for the
	 * constructor when PowerMock cannot determine which constructor to use
	 * automatically. In most cases you should use
	 * {@link #expectNew(Class, Object...)} instead.
	 */
	public static synchronized <T> IExpectationSetters<T> expectNiceNew(Class<T> type, Class<?>[] parameterTypes, Object... arguments)
			throws Exception {
		return org.powermock.api.easymock.PowerMock.expectNiceNew(type, parameterTypes, arguments);
	}

	/**
	 * Suppress constructor calls on specific constructors only.
	 */
	public static synchronized void suppressConstructor(Constructor<?>... constructors) {
		org.powermock.api.easymock.PowerMock.suppressConstructor(constructors);
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
		org.powermock.api.easymock.PowerMock.suppressSpecificConstructor(clazz, parameterTypes);
	}

	/**
	 * Suppress all constructors in the given class and it's super classes.
	 * 
	 * @param classes
	 *            The classes whose constructors will be suppressed.
	 */
	public static synchronized void suppressConstructor(Class<?>... classes) {
		org.powermock.api.easymock.PowerMock.suppressConstructor(classes);
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
		org.powermock.api.easymock.PowerMock.suppressConstructor(clazz, excludePrivateConstructors);
	}

	/**
	 * Suppress specific method calls on all types containing this method. This
	 * works on both instance methods and static methods. Note that replay and
	 * verify are not needed as this is not part of a mock behavior.
	 */
	public static synchronized void suppressMethod(Method... methods) {
		org.powermock.api.easymock.PowerMock.suppressMethod(methods);
	}

	/**
	 * Suppress all methods for this classes.
	 * 
	 * @param classes
	 *            The class which methods will be suppressed.
	 */
	public static synchronized void suppressMethod(Class<?>... classes) {
		org.powermock.api.easymock.PowerMock.suppressMethod(classes);
	}

	/**
	 * Suppress multiple methods for a class.
	 * 
	 * @param classes
	 *            The class whose methods will be suppressed.
	 * @param methodNames
	 *            The names of the methods that'll be suppressed.
	 */
	public static synchronized void suppressMethod(Class<?> clazz, String... methodNames) {
		org.powermock.api.easymock.PowerMock.suppressMethod(clazz, methodNames);
	}

	/**
	 * Suppress all methods for this class.
	 * 
	 * @param classes
	 *            The class which methods will be suppressed.
	 * @param excludePrivateMethods
	 *            optionally not suppress private methods
	 */
	public static synchronized void suppressMethod(Class<?> clazz, boolean excludePrivateMethods) {
		org.powermock.api.easymock.PowerMock.suppressMethod(clazz, excludePrivateMethods);
	}

	/**
	 * Suppress a specific method call. Use this for overloaded methods.
	 */
	public static synchronized void suppressMethod(Class<?> clazz, String methodName, Class<?>[] parameterTypes) {
		org.powermock.api.easymock.PowerMock.suppressMethod(clazz, methodName, parameterTypes);
	}
}
