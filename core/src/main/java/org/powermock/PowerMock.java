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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedList;
import java.util.List;

import net.sf.cglib.proxy.Enhancer;

import org.easymock.EasyMock;
import org.easymock.IExpectationSetters;
import org.easymock.IMocksControl;
import org.easymock.classextension.ConstructorArgs;
import org.easymock.classextension.internal.MocksClassControl;
import org.easymock.internal.MockInvocationHandler;
import org.easymock.internal.MocksControl;
import org.powermock.core.MockGateway;
import org.powermock.core.MockRepository;
import org.powermock.core.PowerMockUtils;
import org.powermock.core.invocationcontrol.method.MethodInvocationControl;
import org.powermock.core.invocationcontrol.newinstance.NewInvocationControl;
import org.powermock.core.mockstrategy.MockStrategy;
import org.powermock.core.mockstrategy.impl.DefaultMockStrategy;
import org.powermock.core.mockstrategy.impl.NiceMockStrategy;
import org.powermock.core.mockstrategy.impl.StrictMockStrategy;

/**
 * PowerMock extends EasyMock functionality with several new features such as
 * mocking static and private methods, mocking new instances and more. Use
 * PowerMock instead of EasyMock where applicable.
 */
public class PowerMock {
	private static boolean replayAndVerifyIsNice;

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
		return doMock(type, false, new DefaultMockStrategy(), null, methods);
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
		return doMock(type, false, new DefaultMockStrategy(), constructorArgs, methods);
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
		Constructor<?> constructor = Whitebox.findConstructorOrThrowException(type, constructorArguments);
		ConstructorArgs constructorArgs = new ConstructorArgs(constructor, constructorArguments);
		return doMock(type, false, new DefaultMockStrategy(), constructorArgs, (Method[]) null);
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
		return doMock(type, false, new StrictMockStrategy(), null, methods);
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
		return doMock(type, false, new NiceMockStrategy(), null, methods);
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
		return doMock(type, false, new StrictMockStrategy(), constructorArgs, methods);
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
		return doMock(type, false, new NiceMockStrategy(), constructorArgs, methods);
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
		Constructor<?> constructor = Whitebox.findConstructorOrThrowException(type, constructorArguments);
		ConstructorArgs constructorArgs = new ConstructorArgs(constructor, constructorArguments);
		return doMock(type, false, new StrictMockStrategy(), constructorArgs, (Method[]) null);
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
		Constructor<?> constructor = Whitebox.findConstructorOrThrowException(type, constructorArguments);
		ConstructorArgs constructorArgs = new ConstructorArgs(constructor, constructorArguments);
		return doMock(type, false, new NiceMockStrategy(), constructorArgs, (Method[]) null);
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
		doMock(type, true, new DefaultMockStrategy(), null, methods);
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
		doMock(type, true, new StrictMockStrategy(), null, methods);
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
		doMock(type, true, new NiceMockStrategy(), null, methods);
	}

	/**
	 * A utility method that may be used to specify several methods that should
	 * <i>not</i> be mocked in an easy manner (by just passing in the method
	 * names of the method you wish <i>not</i> to mock). Note that you cannot
	 * uniquely specify a method to exclude using this method if there are
	 * several methods with the same name in <code>type</code>. This method
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
	 *            <code>null</code>, then this method will have the same
	 *            effect as just calling {@link #createMock(Class, Method...)}
	 *            with the second parameter as <code>null</code> (i.e. all
	 *            methods in that class will be mocked).
	 * @return A mock object of type <T>.
	 */
	public static synchronized <T> T createPartialMockForAllMethodsExcept(Class<T> type, String... methodNames) {

		if (methodNames.length == 0) {
			return createMock(type);
		}

		return createMock(type, Whitebox.getAllMethodExcept(type, methodNames));
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
		/*
		 * The reason why we've split the first and "additional types" is
		 * because it should not intervene with the mockAllExcept(type,
		 * String...methodNames) method.
		 */
		Class<?>[] argumentTypes = mergeArgumentTypes(firstArgumentType, moreTypes);

		return createMock(type, Whitebox.getAllMetodsExcept(type, methodNameToExclude, argumentTypes));
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
		return doMockSpecific(type, new DefaultMockStrategy(), new String[] { methodNameToMock }, null, mergeArgumentTypes(firstArgumentType,
				additionalArgumentTypes));
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
	public static synchronized <T> T createPartialMockStrict(Class<T> type, String methodNameToMock, Class<?> firstArgumentType,
			Class<?>... additionalArgumentTypes) {
		return doMockSpecific(type, new StrictMockStrategy(), new String[] { methodNameToMock }, null, mergeArgumentTypes(firstArgumentType,
				additionalArgumentTypes));
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
	public static synchronized <T> T createPartialMockNice(Class<T> type, String methodNameToMock, Class<?> firstArgumentType,
			Class<?>... additionalArgumentTypes) {
		return doMockSpecific(type, new NiceMockStrategy(), new String[] { methodNameToMock }, null, mergeArgumentTypes(firstArgumentType,
				additionalArgumentTypes));
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
	public static synchronized void mockStaticMethod(Class<?> clazz, String methodNameToMock, Class<?> firstArgumentType,
			Class<?>... additionalArgumentTypes) {
		doMockSpecific(clazz, new DefaultMockStrategy(), new String[] { methodNameToMock }, null, mergeArgumentTypes(firstArgumentType,
				additionalArgumentTypes));
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
	public static synchronized void mockStaticMethodStrict(Class<?> clazz, String methodNameToMock, Class<?> firstArgumentType,
			Class<?>... additionalArgumentTypes) {
		doMockSpecific(clazz, new StrictMockStrategy(), new String[] { methodNameToMock }, null, mergeArgumentTypes(firstArgumentType,
				additionalArgumentTypes));
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
	public static synchronized void mockStaticMethodNice(Class<?> clazz, String methodNameToMock, Class<?> firstArgumentType,
			Class<?>... additionalArgumentTypes) {
		doMockSpecific(clazz, new NiceMockStrategy(), new String[] { methodNameToMock }, null, mergeArgumentTypes(firstArgumentType,
				additionalArgumentTypes));
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
	 *            <code>null</code>, then this method will have the same
	 *            effect as just calling {@link #mockStatic(Class, Method...)}
	 *            with the second parameter as <code>null</code> (i.e. all
	 *            methods in that class will be mocked).
	 */
	public static synchronized void mockStaticMethod(Class<?> clazz, String... methodNames) {
		mockStatic(clazz, Whitebox.getMethods(clazz, methodNames));
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
	 *            <code>null</code>, then this method will have the same
	 *            effect as just calling {@link #mockStatic(Class, Method...)}
	 *            with the second parameter as <code>null</code> (i.e. all
	 *            methods in that class will be mocked).
	 */
	public static synchronized void mockStaticMethodStrict(Class<?> clazz, String... methodNames) {
		mockStaticStrict(clazz, Whitebox.getMethods(clazz, methodNames));
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
	 *            <code>null</code>, then this method will have the same
	 *            effect as just calling {@link #mockStatic(Class, Method...)}
	 *            with the second parameter as <code>null</code> (i.e. all
	 *            methods in that class will be mocked).
	 */
	public static synchronized void mockStaticNice(Class<?> clazz, String... methodNames) {
		mockStaticNice(clazz, Whitebox.getMethods(clazz, methodNames));
	}

	static <T> T doMockSpecific(Class<T> type, MockStrategy mockStrategy, String[] methodNamesToMock, ConstructorArgs constructorArgs,
			Class<?>... argumentTypes) {
		List<Method> methods = new LinkedList<Method>();
		for (String methodName : methodNamesToMock) {
			methods.add(Whitebox.findMethodOrThrowException(type, methodName, argumentTypes));
		}

		final Method[] methodArray = methods.toArray(new Method[0]);
		if (Whitebox.areAllMethodsStatic(methodArray)) {
			if (mockStrategy instanceof DefaultMockStrategy) {
				mockStatic(type, methodArray);
			} else if (mockStrategy instanceof StrictMockStrategy) {
				mockStaticStrict(type, methodArray);
			} else {
				mockStaticNice(type, methodArray);
			}
			return null;
		}

		T mock = null;
		if (mockStrategy instanceof DefaultMockStrategy) {
			mock = createMock(type, constructorArgs, methodArray);
		} else if (mockStrategy instanceof StrictMockStrategy) {
			mock = createStrictMock(type, constructorArgs, methodArray);
		} else {
			mock = createNiceMock(type, constructorArgs, methodArray);
		}

		return mock;
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
	 *            <code>null</code>, then this method will have the same
	 *            effect as just calling {@link #createMock(Class, Method...)}
	 *            with the second parameter as <code>null</code> (i.e. all
	 *            methods in that class will be mocked).
	 * @return A mock object of type <T>.
	 */
	public static synchronized <T> T createPartialMock(Class<T> type, String... methodNames) {
		return createMock(type, Whitebox.getMethods(type, methodNames));
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
	 *            <code>null</code>, then this method will have the same
	 *            effect as just calling {@link #createMock(Class, Method...)}
	 *            with the second parameter as <code>null</code> (i.e. all
	 *            methods in that class will be mocked).
	 * @return A mock object of type <T>.
	 */
	public static synchronized <T> T createPartialMockStrict(Class<T> type, String... methodNames) {
		return createStrictMock(type, Whitebox.getMethods(type, methodNames));
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
	 *            <code>null</code>, then this method will have the same
	 *            effect as just calling {@link #createMock(Class, Method...)}
	 *            with the second parameter as <code>null</code> (i.e. all
	 *            methods in that class will be mocked).
	 * @return A mock object of type <T>.
	 */
	public static synchronized <T> T createPartialMockNice(Class<T> type, String... methodNames) {
		return createNiceMock(type, Whitebox.getMethods(type, methodNames));
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
	 *            <code>null</code>, then this method will have the same
	 *            effect as just calling {@link #createMock(Class, Method...)}
	 *            with the second parameter as <code>null</code> (i.e. all
	 *            methods in that class will be mocked).
	 * @param constructorArguments
	 *            The constructor arguments that will be used to invoke a
	 *            certain constructor. (optional)
	 * @return the mock object.
	 */
	public static <T> T createPartialMock(Class<T> type, String[] methodNames, Object... constructorArguments) {
		Constructor<?> constructor = Whitebox.findConstructorOrThrowException(type, constructorArguments);
		ConstructorArgs constructorArgs = new ConstructorArgs(constructor, constructorArguments);
		return doMock(type, false, new DefaultMockStrategy(), constructorArgs, Whitebox.getMethods(type, methodNames));
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
	 *            <code>null</code>, then this method will have the same
	 *            effect as just calling {@link #createMock(Class, Method...)}
	 *            with the second parameter as <code>null</code> (i.e. all
	 *            methods in that class will be mocked).
	 * @param constructorArguments
	 *            The constructor arguments that will be used to invoke a
	 *            certain constructor. (optional)
	 * @return the mock object.
	 */
	public static <T> T createPartialMockStrict(Class<T> type, String[] methodNames, Object... constructorArguments) {
		Constructor<?> constructor = Whitebox.findConstructorOrThrowException(type, constructorArguments);
		ConstructorArgs constructorArgs = new ConstructorArgs(constructor, constructorArguments);
		return doMock(type, false, new StrictMockStrategy(), constructorArgs, Whitebox.getMethods(type, methodNames));
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
	 *            <code>null</code>, then this method will have the same
	 *            effect as just calling {@link #createMock(Class, Method...)}
	 *            with the second parameter as <code>null</code> (i.e. all
	 *            methods in that class will be mocked).
	 * @param constructorArguments
	 *            The constructor arguments that will be used to invoke a
	 *            certain constructor. (optional)
	 * @return the mock object.
	 */
	public static <T> T createPartialMockNice(Class<T> type, String[] methodNames, Object... constructorArguments) {
		Constructor<?> constructor = Whitebox.findConstructorOrThrowException(type, constructorArguments);
		ConstructorArgs constructorArgs = new ConstructorArgs(constructor, constructorArguments);
		return doMock(type, false, new NiceMockStrategy(), constructorArgs, Whitebox.getMethods(type, methodNames));
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
	 *            <code>null</code>, then this method will have the same
	 *            effect as just calling {@link #createMock(Class, Method...)}
	 *            with the second parameter as <code>null</code> (i.e. all
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
		Constructor<?> constructor = Whitebox.findConstructorOrThrowException(type, constructorArguments);
		ConstructorArgs constructorArgs = new ConstructorArgs(constructor, constructorArguments);
		return doMockSpecific(type, new DefaultMockStrategy(), new String[] { methodName }, constructorArgs, methodParameterTypes);
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
	 *            <code>null</code>, then this method will have the same
	 *            effect as just calling {@link #createMock(Class, Method...)}
	 *            with the second parameter as <code>null</code> (i.e. all
	 *            methods in that class will be mocked).
	 * @param methodParameterTypes
	 *            Parameter types that defines the method. Note that this is
	 *            only needed to separate overloaded methods.
	 * @param constructorArguments
	 *            The constructor arguments that will be used to invoke a
	 *            certain constructor. (optional)
	 * @return the mock object.
	 */
	public static <T> T createPartialMockStrict(Class<T> type, String methodName, Class<?>[] methodParameterTypes, Object... constructorArguments) {
		Constructor<?> constructor = Whitebox.findConstructorOrThrowException(type, constructorArguments);
		ConstructorArgs constructorArgs = new ConstructorArgs(constructor, constructorArguments);
		return doMockSpecific(type, new StrictMockStrategy(), new String[] { methodName }, constructorArgs, methodParameterTypes);
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
	 *            <code>null</code>, then this method will have the same
	 *            effect as just calling {@link #createMock(Class, Method...)}
	 *            with the second parameter as <code>null</code> (i.e. all
	 *            methods in that class will be mocked).
	 * @param methodParameterTypes
	 *            Parameter types that defines the method. Note that this is
	 *            only needed to separate overloaded methods.
	 * @param constructorArguments
	 *            The constructor arguments that will be used to invoke a
	 *            certain constructor. (optional)
	 * @return the mock object.
	 */
	public static <T> T createPartialMockNice(Class<T> type, String methodName, Class<?>[] methodParameterTypes, Object... constructorArguments) {
		Constructor<?> constructor = Whitebox.findConstructorOrThrowException(type, constructorArguments);
		ConstructorArgs constructorArgs = new ConstructorArgs(constructor, constructorArguments);
		return doMockSpecific(type, new NiceMockStrategy(), new String[] { methodName }, constructorArgs, methodParameterTypes);
	}

	/**
	 * A utility method that may be used to mock several methods in an easy way
	 * (by just passing in the method names of the method you wish to mock). Use
	 * this to handle overloaded methods <i>and</i> overloaded constructors.
	 * The mock object created will support mocking of final and native methods
	 * and invokes a specific constructor based on the supplied argument values.
	 * 
	 * @param <T>
	 *            the type of the mock object
	 * @param type
	 *            the type of the mock object
	 * @param methodName
	 *            The names of the methods that should be mocked. If
	 *            <code>null</code>, then this method will have the same
	 *            effect as just calling {@link #createMock(Class, Method...)}
	 *            with the second parameter as <code>null</code> (i.e. all
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
		ConstructorArgs constructorArgs = new ConstructorArgs(Whitebox.getConstructor(type, constructorParameterTypes), constructorArguments);
		return doMockSpecific(type, new DefaultMockStrategy(), new String[] { methodName }, constructorArgs, methodParameterTypes);
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
	 *            <code>null</code>, then this method will have the same
	 *            effect as just calling {@link #createMock(Class, Method...)}
	 *            with the second parameter as <code>null</code> (i.e. all
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
	public static <T> T createPartialMockStrict(Class<T> type, String methodName, Class<?>[] methodParameterTypes, Object[] constructorArguments,
			Class<?>[] constructorParameterTypes) {
		ConstructorArgs constructorArgs = new ConstructorArgs(Whitebox.getConstructor(type, constructorParameterTypes), constructorArguments);
		return doMockSpecific(type, new StrictMockStrategy(), new String[] { methodName }, constructorArgs, methodParameterTypes);
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
	 *            <code>null</code>, then this method will have the same
	 *            effect as just calling {@link #createMock(Class, Method...)}
	 *            with the second parameter as <code>null</code> (i.e. all
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
	public static <T> T createPartialMockNice(Class<T> type, String methodName, Class<?>[] methodParameterTypes, Object[] constructorArguments,
			Class<?>[] constructorParameterTypes) {
		ConstructorArgs constructorArgs = new ConstructorArgs(Whitebox.getConstructor(type, constructorParameterTypes), constructorArguments);
		return doMockSpecific(type, new NiceMockStrategy(), new String[] { methodName }, constructorArgs, methodParameterTypes);
	}

	/**
	 * Used to specify expectations on private static methods. If possible use
	 * variant with only method name.
	 */
	public static synchronized <T> IExpectationSetters<T> expectPrivate(Class<?> clazz, Method method, Object... arguments) throws Exception {
		return doExpectPrivate(clazz, method, arguments);
	}

	/**
	 * Used to specify expectations on private methods. If possible use variant
	 * with only method name.
	 */
	public static synchronized <T> IExpectationSetters<T> expectPrivate(Object instance, Method method, Object... arguments) throws Exception {
		return doExpectPrivate(instance, method, arguments);
	}

	/**
	 * Used to specify expectations on private methods. Use this method to
	 * handle overloaded methods.
	 */
	@SuppressWarnings("all")
	public static synchronized <T> IExpectationSetters<T> expectPrivate(Object instance, String methodName, Class<?>[] parameterTypes,
			Object... arguments) throws Exception {

		if (arguments == null) {
			arguments = new Object[0];
		}

		if (instance == null) {
			throw new IllegalArgumentException("instance cannot be null.");
		} else if (arguments.length != parameterTypes.length) {
			throw new IllegalArgumentException("The length of the arguments must be equal to the number of parameter types.");
		}

		Method foundMethod = Whitebox.getMethod(instance.getClass(), methodName, parameterTypes);

		Whitebox.throwExceptionIfMethodWasNotFound(instance.getClass(), methodName, foundMethod, parameterTypes);

		return doExpectPrivate(instance, foundMethod, arguments);
	}

	/**
	 * Used to specify expectations on private methods using methodName.
	 */
	@SuppressWarnings("unchecked")
	public static synchronized <T> IExpectationSetters<T> expectPrivate(Object instance, String methodName, Object... arguments) throws Exception {

		Method[] methods = Whitebox.getMethods(instance.getClass(), methodName);
		Method methodToExpect;
		if (methods.length == 1) {
			methodToExpect = methods[0];
		} else {
			methodToExpect = Whitebox.findMethodOrThrowException(instance, null, methodName, arguments);
		}
		return doExpectPrivate(instance, methodToExpect, arguments);
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
		return org.easymock.classextension.EasyMock.expectLastCall();
	}

	/**
	 * Sometimes it is useful to allow replay and verify on non-mocks. For
	 * example when using partial mocking in some tests and no mocking in other
	 * test-methods, but using the same setUp and tearDown.
	 */
	public static synchronized void niceReplayAndVerify() {
		replayAndVerifyIsNice = true;
	}

	/**
	 * Test if a object is a mock created by EasyMock or not.
	 */
	private static boolean isEasyMocked(Object mock) {
		return Enhancer.isEnhanced(mock.getClass()) || Proxy.isProxyClass(mock.getClass());
	}

	/**
	 * Switches the mocks or classes to replay mode. Note that you must use this
	 * method when using PowerMock!
	 * 
	 * @param mocks
	 *            mock objects or classes loaded by PowerMock.
	 */
	public static synchronized void replay(Object... mocks) {
		try {
			for (Object mock : mocks) {
				if (mock instanceof Class) {
					replay((Class<?>) mock);
				} else {
					MockInvocationHandler instanceInvocationHandler = getInstanceInvocationHandler(mock);
					if (instanceInvocationHandler != null) {
						instanceInvocationHandler.getControl().replay();
					} else {
						if (replayAndVerifyIsNice && !isEasyMocked(mock)) {
							// ignore non-mock
						} else {
							/*
							 * Delegate to easy mock class extension if we have
							 * no handler registered for this object.
							 */
							org.easymock.classextension.EasyMock.replay(mock);
						}
					}
				}
			}
		} catch (Throwable t) {
			clearState();
			throw new RuntimeException(t);
		}
	}

	/**
	 * Switches the mocks or classes to verify mode. Note that you must use this
	 * method when using PowerMock!
	 * 
	 * @param mocks
	 *            mock objects or classes loaded by PowerMock.
	 */
	public static synchronized void verify(Object... objects) {
		try {
			for (Object mock : objects) {
				if (mock instanceof Class) {
					verifyClass((Class<?>) mock);
				} else {
					MockInvocationHandler instanceInvocationHandler = getInstanceInvocationHandler(mock);
					if (instanceInvocationHandler != null) {
						instanceInvocationHandler.getControl().verify();
					} else {
						if (replayAndVerifyIsNice && !isEasyMocked(mock)) {
							// ignore non-mock
						} else {
							/*
							 * Delegate to easy mock class extension if we have
							 * no handler registered for this object.
							 */
							org.easymock.classextension.EasyMock.verify(mock);
						}
					}
				}
			}
		} finally {
			clearState();
		}
	}

	/**
	 * Convenience method for createMock followed by expectNew.
	 * 
	 * @param type
	 *            The class that should be mocked.
	 * @return A mock object of the same type as the mock.
	 * @throws Exception
	 */
	public static synchronized <T> T createMockAndExpectNew(Class<T> type) throws Exception {
		T mock = org.easymock.classextension.EasyMock.createMock(type);
		expectNew(type).andReturn(mock);
		return mock;
	}

	/**
	 * Allows specifying expectations on new invocations. For example you might
	 * want to throw an exception or return a mock. Note that you must replay
	 * the class when using this method since this behavior is part of the class
	 * mock.
	 */
	@SuppressWarnings("unchecked")
	public static synchronized <T> IExpectationSetters<T> expectNew(Class<T> type) throws Exception {
		if (type == null) {
			throw new IllegalArgumentException("type cannot be null");
		}

		final Class<T> unmockedType = (Class<T>) Whitebox.getUnmockedType(type);

		/*
		 * Check if this object has been mocked before
		 */
		NewInvocationControl<T> newInvocationControl = (NewInvocationControl<T>) MockRepository.getNewInstanceSubstitute(unmockedType);
		if (newInvocationControl == null) {
			newInvocationControl = EasyMock.createMock(NewInvocationControl.class);
			MockRepository.putNewInstanceSubstitute(type, newInvocationControl);
		}
		return EasyMock.expect(newInvocationControl.createInstance());
	}

	/**
	 * Suppresses a whole hierarchy of constructor code. For example we have
	 * class A that extends class B which extends Object, invoking this method
	 * like:
	 * 
	 * <pre>
	 * suppressConstructorCodeHierarchy(A.class);
	 * </pre>
	 * 
	 * will suppress constructor code in both class A and class B. Java lang
	 * classes are not suppressed at the moment since these classes has to be
	 * statically modified since they are loaded by the system classloader. This
	 * may be available in future versions of PowerMock.
	 * 
	 * @param classes
	 *            The classes whose constructor code will be hierarchically
	 *            suppressed.
	 * 
	 */
	public static synchronized void suppressConstructorCodeHierarchy(Class<?>... classes) {
		for (Class<?> clazz : classes) {
			Class<?> tempClass = clazz;
			while (tempClass != Object.class) {
				suppressConstructorCode(tempClass);
				tempClass = tempClass.getSuperclass();
			}
		}
	}

	/**
	 * Suppress constructor calls on specific constructors only.
	 */
	public static synchronized void suppressConstructorCode(Constructor<?>... constructors) {
		for (Constructor<?> constructor : constructors) {
			MockGateway.addConstructorToSuppress(constructor);
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
		MockGateway.addConstructorToSuppress(Whitebox.getConstructor(clazz, parameterTypes));
	}

	/**
	 * Suppress all constructors in the given class.
	 * 
	 * @param classes
	 *            The classes whose constructors will be suppressed.
	 */
	public static synchronized void suppressConstructorCode(Class<?>... classes) {
		for (Class<?> clazz : classes) {
			suppressConstructorCode(clazz, false);
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
	public static synchronized void suppressConstructorCode(Class<?> clazz, boolean excludePrivateConstructors) {
		Constructor<?>[] ctors = null;

		if (excludePrivateConstructors) {
			ctors = clazz.getConstructors();
		} else {
			ctors = clazz.getDeclaredConstructors();
		}

		for (Constructor<?> ctor : ctors) {
			MockGateway.addConstructorToSuppress(ctor);
		}
	}

	/**
	 * Suppress specific method calls on all types containing this method. This
	 * works on both instance methods and static methods. Note that replay and
	 * verify are not needed as this is not part of a mock behavior.
	 */
	public static synchronized void suppressMethodCode(Method... methods) {
		for (Method method : methods) {
			MockGateway.addMethodToSuppress(method);
		}
	}

	/**
	 * Suppress all methods for this class.
	 * 
	 * @param classes
	 *            The class which methods will be suppressed.
	 */
	public static synchronized void suppressMethodCode(Class<?>... classes) {
		for (Class<?> clazz : classes) {
			suppressMethodCode(clazz, false);
		}
	}

	/**
	 * Suppress multiple methods for a class.
	 * 
	 * @param classes
	 *            The class whose methods will be suppressed.
	 * @param methodNames
	 *            The names of the methods that'll be suppressed.
	 */
	public static synchronized void suppressMethodCode(Class<?> clazz, String... methodNames) {
		for (Method method : Whitebox.getMethods(clazz, methodNames)) {
			MockGateway.addMethodToSuppress(method);
		}
	}

	/**
	 * Suppress all methods for this class.
	 * 
	 * @param classes
	 *            The class which methods will be suppressed.
	 * @param excludePrivateMethods
	 *            optionally not suppress private methods
	 */
	public static synchronized void suppressMethodCode(Class<?> clazz, boolean excludePrivateMethods) {
		Method[] methods = null;

		if (excludePrivateMethods) {
			methods = clazz.getMethods();
		} else {
			methods = clazz.getDeclaredMethods();
		}

		for (Method method : methods) {
			MockGateway.addMethodToSuppress(method);
		}
	}

	/**
	 * Suppress a specific method call. Use this for overloaded methods.
	 */
	public static synchronized void suppressMethodCode(Class<?> clazz, String methodName, Class<?>[] parameterTypes) {
		Method method = null;
		if (parameterTypes.length > 0) {
			method = Whitebox.getMethod(clazz, methodName, parameterTypes);
		} else {
			method = Whitebox.findMethodOrThrowException(clazz, methodName, parameterTypes);
		}
		MockGateway.addMethodToSuppress(method);
	}

	private static <T> T doMock(Class<T> type, boolean isStatic, MockStrategy mockStrategy, ConstructorArgs constructorArgs, Method... methods) {
		if (methods == null) {
			methods = new Method[] {};
		}

		final IMocksControl control = mockStrategy.createMockControl(type);
		T mock = null;
		if (type.isInterface()) {
			mock = control.createMock(type);
		} else {
			MocksClassControl mocksClassControl = ((MocksClassControl) control);
			if (constructorArgs == null) {
				// what is the difference between methods == null and methods ==
				// []
				if (isStatic == false && (methods == null || methods.length == 0)) {
					mock = mocksClassControl.createMock(type);
				} else {
					mock = mocksClassControl.createMock(type, methods);
				}
			} else {
				if (isStatic == false && (methods == null || methods.length == 0)) {
					mock = mocksClassControl.createMock(type, constructorArgs);
				} else {
					mock = mocksClassControl.createMock(type, constructorArgs, methods);
				}
			}
		}
		MockInvocationHandler h = new MockInvocationHandler((MocksControl) control);
		if (isStatic) {
			MockRepository.putClassMethodInvocationControl(type, h, methods);
		} else {
			MockRepository.putInstanceMethodInvocationControl(mock, h, methods);
		}
		return mock;
	}

	private static Class<?>[] mergeArgumentTypes(Class<?> firstArgumentType, Class<?>... additionalArgumentTypes) {
		Class<?>[] argumentTypes = new Class[additionalArgumentTypes.length + 1];
		argumentTypes[0] = firstArgumentType;
		if (additionalArgumentTypes.length != 0) {
			System.arraycopy(additionalArgumentTypes, 0, argumentTypes, 1, additionalArgumentTypes.length);
		}
		return argumentTypes;
	}

	@SuppressWarnings("unchecked")
	private static <T> IExpectationSetters<T> doExpectPrivate(Object instance, Method methodToExpect, Object... arguments) throws Exception {
		doInvokeMethod(instance, methodToExpect, arguments);
		return (IExpectationSetters<T>) org.easymock.classextension.EasyMock.expectLastCall();
	}

	private static void doInvokeMethod(Object instance, Method methodToExpect, Object... arguments) throws Exception {
		if (methodToExpect == null) {
			throw new IllegalArgumentException("Method cannot be null");
		}

		methodToExpect.setAccessible(true);

		try {
			methodToExpect.invoke(instance, arguments);
		} catch (InvocationTargetException e) {
			final Throwable cause = e.getCause();
			if (cause instanceof Exception) {
				throw (Exception) cause;
			} else {
				throw new RuntimeException(cause);
			}
		} catch (Exception e) {
			throw new RuntimeException("Failed to invoke method '" + methodToExpect.getName() + "'. Reason was: '" + e.getMessage() + "'.", e);
		}
	}

	private static synchronized void replay(Class<?>... types) {
		try {
			for (Class<?> type : types) {
				final MockInvocationHandler invocationHandler = getClassInvocationHandler(type);
				if (invocationHandler != null) {
					invocationHandler.getControl().replay();
				}

				NewInvocationControl<?> newInvocationControl = MockRepository.getNewInstanceSubstitute(type);
				if (newInvocationControl != null) {
					EasyMock.replay(newInvocationControl);
				}
			}
		} catch (Throwable t) {
			clearState();
			throw new RuntimeException(t);
		}
	}

	/**
	 * Note: doesn't clear PowerMock state.
	 */
	private static synchronized void verifyClass(Class<?>... types) {
		for (Class<?> type : types) {
			final MockInvocationHandler invocationHandler = getClassInvocationHandler(type);
			if (invocationHandler != null) {
				invocationHandler.getControl().verify();
			}
			NewInvocationControl<?> newInvocationControl = MockRepository.getNewInstanceSubstitute(type);
			if (newInvocationControl != null) {
				try {
					EasyMock.verify(newInvocationControl);
				} catch (AssertionError e) {
					PowerMockUtils.throwAssertionErrorForNewSubstitutionFailure(e, type);
				}
			}
		}
	}

	private static void clearState() {
		MockRepository.clear();
		MockGateway.clear();
		replayAndVerifyIsNice = false;
	}

	private static MockInvocationHandler getClassInvocationHandler(Class<?> type) {
		final MethodInvocationControl invocationControl = MockRepository.getClassMethodInvocationControl(type);

		return toInvocationHandler(invocationControl);
	}

	private static MockInvocationHandler getInstanceInvocationHandler(Object instance) {
		final MethodInvocationControl invocationControl = MockRepository.getInstanceMethodInvocationControl(instance);
		return toInvocationHandler(invocationControl);
	}

	private static MockInvocationHandler toInvocationHandler(final MethodInvocationControl invocationControl) {
		if (invocationControl == null) {
			return null;
		}
		return ((MockInvocationHandler) invocationControl.getInvocationHandler());
	}
}
