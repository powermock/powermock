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
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.easymock.EasyMock;
import org.easymock.IExpectationSetters;
import org.easymock.classextension.internal.MocksClassControl;
import org.easymock.internal.LastControl;
import org.easymock.internal.MockInvocationHandler;
import org.easymock.internal.MocksControl;
import org.powermock.core.MockGateway;
import org.powermock.core.MockRepository;
import org.powermock.core.PowerMockUtils;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.invocationcontrol.method.MethodInvocationControl;
import org.powermock.core.invocationcontrol.newinstance.NewInvocationControl;

public class PowerMock {

	public static synchronized <T> T createMock(Class<T> type, Method... methods) {
		return doMock(type, false, methods);
	}

	/**
	 * Use to mock static methods.
	 * 
	 * @param type
	 * @param methods
	 */
	public static synchronized void mockStatic(Class<?> type, Method... methods) {
		doMock(type, true, methods);
	}

	private static <T> T doMock(Class<T> type, boolean isStatic, Method... methods) {
		MocksControl control = null;
		T mock = null;
		if (type.isInterface()) {
			control = (MocksControl) EasyMock.createControl();
			mock = control.createMock(type);
		} else {
			control = (MocksControl) org.easymock.classextension.EasyMock.createControl();
			mock = ((MocksClassControl) control).createMock(type, methods);
		}

		MockInvocationHandler h = new MockInvocationHandler(control);
		if (isStatic) {
			MockRepository.putClassMethodInvocationControl(type, h, methods);
		} else {
			MockRepository.putInstanceMethodInvocationControl(mock, h, methods);
		}
		return mock;
	}

	/**
	 * A utility method that may be used to specify several methods that should
	 * <i>not</i> be mocked in an easy manner (by just passing in the method
	 * names of the method you wish not to mock). Note that you cannot uniquely
	 * specify a method to exclude using this method if there are several
	 * methods with the same name in <code>type</code>. This method will mock
	 * ALL methods that match the supplied name regardless of parameter types
	 * and signature. If this is the case you should fall-back on using the
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
	public static synchronized <T> T mockAllExcept(Class<T> type, String... methodNames) {

		if (methodNames.length == 0) {
			return createMock(type);
		}

		List<Method> methodsToMock = new LinkedList<Method>();
		Method[] methods = type.getDeclaredMethods();
		iterateMethods: for (Method method : methods) {
			for (String methodName : methodNames) {
				if (method.getName().equals(methodName)) {
					continue iterateMethods;
				}
			}
			methodsToMock.add(method);
		}

		return createMock(type, methodsToMock.toArray(new Method[0]));
	}

	/**
	 * Mock all methods of a class except for a specific one.
	 * 
	 * @param <T>
	 * @param type
	 * @param methodNameToExclude
	 * @param moreTypes
	 * @return
	 */
	public static synchronized <T> T mockAllExcept(Class<T> type, String methodNameToExclude, Class<?> firstArgumentType, Class<?>... moreTypes) {
		/*
		 * The reason why we've split the first and "additional types" is
		 * because it should not intervene with the mockAllExcept(type,
		 * String...methodNames) method.
		 */
		Class<?>[] argumentTypes = mergeArgumentTypes(firstArgumentType, moreTypes);

		Method[] methods = type.getDeclaredMethods();
		List<Method> methodList = new ArrayList<Method>();
		outer: for (Method method : methods) {
			if (method.getName().equals(methodNameToExclude)) {
				if (argumentTypes != null && argumentTypes.length > 0) {
					final Class<?>[] args = method.getParameterTypes();
					if (args != null && args.length == argumentTypes.length) {
						for (int i = 0; i < args.length; i++) {
							if (args[i].equals(argumentTypes[i])) {
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
		return createMock(type, methodList.toArray(new Method[0]));
	}

	private static Class<?>[] mergeArgumentTypes(Class<?> firstArgumentType, Class<?>... additionalArgumentTypes) {
		Class<?>[] argumentTypes = new Class[additionalArgumentTypes.length + 1];
		argumentTypes[0] = firstArgumentType;
		if (additionalArgumentTypes.length != 0) {
			System.arraycopy(additionalArgumentTypes, 0, argumentTypes, 1, additionalArgumentTypes.length);
		}
		return argumentTypes;
	}

	/**
	 * Mock a single specific method.
	 * 
	 * @param <T>
	 *            The type of the mock.
	 * @param type
	 *            The type that'll be used to create a mock instance.
	 * @param methodNameToMock
	 * @param additionalArgumentTypes
	 * @return
	 */
	public static synchronized <T> T mockMethod(Class<T> type, String methodNameToMock, Class<?> firstArgumentType, Class<?>... additionalArgumentTypes) {
		return doMockSpecific(type, methodNameToMock, mergeArgumentTypes(firstArgumentType, additionalArgumentTypes));
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
	public static synchronized void mockStaticMethod(Class<?> clazz, String methodNameToMock, Class<?> firstArgumentType, Class<?>... additionalArgumentTypes) {
		doMockSpecific(clazz, methodNameToMock, mergeArgumentTypes(firstArgumentType, additionalArgumentTypes));
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

	static <T> T doMockSpecific(Class<T> type, String methodNameToMock, Class<?>... argumentTypes) {
		Method methodToMock = Whitebox.findMethodOrThrowException(type, methodNameToMock, argumentTypes);
		if (Modifier.isStatic(methodToMock.getModifiers())) {
			mockStatic(type, methodToMock);
			return null;
		}
		T mock = createMock(type, methodToMock);
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
	public static synchronized <T> T mockMethod(Class<T> type, String... methodNames) {
		return createMock(type, Whitebox.getMethods(type, methodNames));
	}

	public static synchronized <T> IExpectationSetters<T> expectPrivate(Class<?> clazz, Method method, Object... arguments) {
		return doExpectPrivate(clazz, method, arguments);
	}

	public static synchronized <T> IExpectationSetters<T> expectPrivate(Object instance, Method method, Object... arguments) {
		return doExpectPrivate(instance, method, arguments);
	}

	@SuppressWarnings("all")
	public static synchronized <T> IExpectationSetters<T> expectPrivate(Object instance, String methodName, Class<?>[] parameterTypes, Object... arguments) {

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
	 * Used to expect private methods or methods not "visible" from the test
	 * class (e.g. a method in a private inner class or a private method in a
	 * public class).
	 */
	@SuppressWarnings("unchecked")
	public static synchronized <T> IExpectationSetters<T> expectPrivate(Object instance, String methodName, Object... arguments) {

		Method methodToExpect = Whitebox.findMethodOrThrowException(instance, methodName, arguments);
		return doExpectPrivate(instance, methodToExpect, arguments);
	}

	public static synchronized IExpectationSetters<Object> expectVoid(Object instance, String methodName, Object... arguments) {
		Method methodToExpect = Whitebox.findMethodOrThrowException(instance, methodName, arguments);
		doInvokeMethod(instance, methodToExpect, arguments);
		return org.easymock.classextension.EasyMock.expectLastCall();
	}

	public static synchronized IExpectationSetters<Object> expectVoid(Class<?> clazz, String methodName, Object... arguments) {
		Method methodToExpect = Whitebox.findMethodOrThrowException(clazz, methodName, arguments);
		doInvokeMethod(clazz, methodToExpect, arguments);
		return org.easymock.classextension.EasyMock.expectLastCall();
	}

	@SuppressWarnings("all")
	public static synchronized IExpectationSetters<Object> expectVoid(Object instance, String methodName, Class<?>[] parameterTypes, Object... arguments) {

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

		return org.easymock.classextension.EasyMock.expectLastCall();
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

	@SuppressWarnings("unchecked")
	private static <T> IExpectationSetters<T> doExpectPrivate(Object instance, Method methodToExpect, Object... arguments) {
		doInvokeMethod(instance, methodToExpect, arguments);
		return (IExpectationSetters<T>) getLastControl(methodToExpect.getName());
	}

	private static void doInvokeMethod(Object instance, Method methodToExpect, Object... arguments) {
		if (methodToExpect == null) {
			throw new IllegalArgumentException("Method cannot be null");
		}

		methodToExpect.setAccessible(true);

		try {
			methodToExpect.invoke(instance, arguments);
		} catch (Exception e) {
			throw new RuntimeException("Failed to invoke method '" + methodToExpect.getName() + "'. Reason was: '" + e.getMessage() + "'.", e);
		}
	}

	private static MocksControl getLastControl(String methodName) {
		final MocksControl lastControl = LastControl.lastControl();
		if (lastControl == null) {
			throw new RuntimeException("Failed to expect method " + methodName + ". Perhaps you have forgot to prepare the mock for testing using the @"
					+ PrepareForTest.class.getSimpleName() + " annotation? If you're using JUnit 3, make sure you've created a "
					+ "PowerMock suite and added the test class to that suite.");
		}
		return lastControl;
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

	private static synchronized void verifyClass(Class<?>... types) {
		try {
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
		} finally {
			clearState();
		}
	}

	public static void clearState() {
		MockRepository.clear();
		MockGateway.clear();
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

	public static synchronized void replay(Object... objects) {
		try {
			for (Object tested : objects) {
				if (tested instanceof Class) {
					replay((Class<?>)tested);
				} else {
					MockInvocationHandler instanceInvocationHandler = getInstanceInvocationHandler(tested);
					if (instanceInvocationHandler != null) {
						instanceInvocationHandler.getControl().replay();
					} else {
						// Delegate to easy mock class extension if we have no
						// handler
						// registered for this object.
						org.easymock.classextension.EasyMock.replay(tested);
					}
				}

//				NewInvocationControl<?> newInvocationControl = MockRepository.getNewInstanceSubstitute(tested.getClass().getSuperclass());
//				if (newInvocationControl != null) {
//					EasyMock.replay(newInvocationControl);
//				}
			}
		} catch (Throwable t) {
			clearState();
			throw new RuntimeException(t);
		}
	}

	public static synchronized void verify(Object... objects) {
		try {
			for (Object tested : objects) {
				if (tested instanceof Class) {
					verifyClass((Class<?>)tested);
				} else {
					MockInvocationHandler instanceInvocationHandler = getInstanceInvocationHandler(tested);
					if (instanceInvocationHandler != null) {
						instanceInvocationHandler.getControl().verify();
					} else {
						// Delegate to easy mock class extension if we have no
						// handler
						// registered for this object.
						org.easymock.classextension.EasyMock.verify(tested);
					}
				}
			}
		} finally {
			clearState();
		}
	}

	/**
	 * Mock construction of a Class. For example, let's say you have a method
	 * like:
	 * 
	 * <pre>
	 * public final String getMessage() {
	 * 	MyClass myClass = new MyClass();
	 * 	return myClass.getMessage();
	 * }
	 * </pre>
	 * 
	 * In this case you'd like to mock the construction of MyClass so that you
	 * can expect the call to getMessage(). To achieve this you can write:
	 * 
	 * <pre>
	 * MyClass myClassMock = mockConstruction(MyClass.class);
	 * final String expected = &quot;Hello altered World&quot;;
	 * expect(myClassMock.getMessage()).andReturn(expected);
	 * replay(myClassMock);
	 * </pre>
	 * 
	 * @param type
	 *            The class that should be mocked.
	 * @return A mock object of the same type as the mock.
	 */
	public static synchronized <T> T mockConstruction(Class<T> type) {
		T replacementMock = org.easymock.classextension.EasyMock.createMock(type);
		/*
		 * If the class in an inner/member class we need to add the prefix. This
		 * is necessary since the NewAndPrivateMockTransformer requires a
		 * distinction between normal classes and anonymous inner classes.
		 */
		final String mockRepositoryKey = MockRepository.getMockRepositoryClassKey(type);

		MockRepository.putMock(mockRepositoryKey, replacementMock);
		return replacementMock;
	}

	@SuppressWarnings("unchecked")
	public static synchronized <T> IExpectationSetters<T> expectNew(Class<T> type) {
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

	public static synchronized void suppressMethodCode(Method... methods) {
		for (Method method : methods) {
			MockGateway.addMethodToSuppress(method);
		}
	}

	/**
	 * Suppress all methods for this class. TODO IMplement for objects as well.
	 * 
	 * @param classes
	 *            The class which methods will be suppressed.
	 */
	public static synchronized void suppressMethodCode(Class<?>... classes) {
		for (Class<?> clazz : classes) {
			suppressMethodCode(clazz, false);
		}
	}

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

	public static synchronized void suppressMethodCode(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
		Method method = null;
		if (parameterTypes.length > 0) {
			method = Whitebox.getMethod(clazz, methodName, parameterTypes);
		} else {
			method = Whitebox.findMethodOrThrowException(clazz, methodName, parameterTypes);
		}
		MockGateway.addMethodToSuppress(method);
	}
}
