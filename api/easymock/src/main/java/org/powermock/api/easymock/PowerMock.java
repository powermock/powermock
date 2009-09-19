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
package org.powermock.api.easymock;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.sf.cglib.proxy.Enhancer;

import org.easymock.IExpectationSetters;
import org.easymock.IMocksControl;
import org.easymock.classextension.ConstructorArgs;
import org.easymock.classextension.internal.MocksClassControl;
import org.easymock.internal.MockInvocationHandler;
import org.easymock.internal.MocksControl;
import org.powermock.api.easymock.internal.invocationcontrol.EasyMockMethodInvocationControl;
import org.powermock.api.easymock.internal.invocationcontrol.NewInvocationControlAssertionError;
import org.powermock.api.easymock.internal.invocationcontrol.NewInvocationControlImpl;
import org.powermock.api.easymock.internal.mockstrategy.MockStrategy;
import org.powermock.api.easymock.internal.mockstrategy.impl.DefaultMockStrategy;
import org.powermock.api.easymock.internal.mockstrategy.impl.NiceMockStrategy;
import org.powermock.api.easymock.internal.mockstrategy.impl.StrictMockStrategy;
import org.powermock.api.easymock.internal.proxyframework.CgLibProxyFramework;
import org.powermock.api.support.SuppressCode;
import org.powermock.core.ClassReplicaCreator;
import org.powermock.core.MockRepository;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.core.spi.MethodInvocationControl;
import org.powermock.core.spi.NewInvocationControl;
import org.powermock.core.spi.support.InvocationSubstitute;
import org.powermock.reflect.Whitebox;
import org.powermock.reflect.internal.WhiteboxImpl;

/**
 * PowerMock extends EasyMock functionality with several new features such as
 * mocking static and private methods, mocking new instances and more. Use
 * PowerMock instead of EasyMock where applicable.
 */
public class PowerMock {

    private static final String NICE_REPLAY_AND_VERIFY_KEY = "PowerMock.niceReplayAndVerify";

    static {
        CgLibProxyFramework.registerProxyFramework();
    }

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
     * Creates a mock object that supports mocking of final and native methods.
     * 
     * @param <T>
     *            the type of the mock object
     * @param type
     *            the type of the mock object
     * @return the mock object.
     */
    public static synchronized <T> T createMock(Class<T> type) {
        return doMock(type, false, new DefaultMockStrategy(), null, (Method[]) null);
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
        Constructor<?> constructor = WhiteboxImpl.findConstructorOrThrowException(type, constructorArguments);
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
        return doMock(type, false, new StrictMockStrategy(), null, (Method[]) null);
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
        return doMock(type, false, new NiceMockStrategy(), null, (Method[]) null);
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
        Constructor<?> constructor = WhiteboxImpl.findConstructorOrThrowException(type, constructorArguments);
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
        Constructor<?> constructor = WhiteboxImpl.findConstructorOrThrowException(type, constructorArguments);
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
     * Enable static mocking for a class.
     * 
     * @param type
     *            the class to enable static mocking
     */
    public static synchronized void mockStatic(Class<?> type) {
        doMock(type, true, new DefaultMockStrategy(), null, (Method[]) null);
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
     * Enable strict static mocking for a class.
     * 
     * @param type
     *            the class to enable static mocking
     */
    public static synchronized void mockStaticStrict(Class<?> type) {
        doMock(type, true, new StrictMockStrategy(), null, (Method[]) null);
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
     * Enable nice static mocking for a class.
     * 
     * @param type
     *            the class to enable static mocking
     */
    public static synchronized void mockStaticNice(Class<?> type) {
        doMock(type, true, new NiceMockStrategy(), null, (Method[]) null);
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
        if (methodNames != null && methodNames.length == 0) {
            return createMock(type);
        }

        return createMock(type, WhiteboxImpl.getAllMethodExcept(type, methodNames));
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
        if (methodNames != null && methodNames.length == 0) {
            return createNiceMock(type);
        }

        return createNiceMock(type, WhiteboxImpl.getAllMethodExcept(type, methodNames));
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
        if (methodNames != null && methodNames.length == 0) {
            return createStrictMock(type);
        }

        return createStrictMock(type, WhiteboxImpl.getAllMethodExcept(type, methodNames));
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
        final Class<?>[] argumentTypes = mergeArgumentTypes(firstArgumentType, moreTypes);

        return createMock(type, WhiteboxImpl.getAllMetodsExcept(type, methodNameToExclude, argumentTypes));
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
        /*
         * The reason why we've split the first and "additional types" is
         * because it should not intervene with the mockAllExcept(type,
         * String...methodNames) method.
         */
        final Class<?>[] argumentTypes = mergeArgumentTypes(firstArgumentType, moreTypes);

        return createNiceMock(type, WhiteboxImpl.getAllMetodsExcept(type, methodNameToExclude, argumentTypes));
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
        /*
         * The reason why we've split the first and "additional types" is
         * because it should not intervene with the mockAllExcept(type,
         * String...methodNames) method.
         */
        final Class<?>[] argumentTypes = mergeArgumentTypes(firstArgumentType, moreTypes);

        return createStrictMock(type, WhiteboxImpl.getAllMetodsExcept(type, methodNameToExclude, argumentTypes));
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
    public static synchronized <T> T createStrictPartialMock(Class<T> type, String methodNameToMock, Class<?> firstArgumentType,
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
    public static synchronized <T> T createNicePartialMock(Class<T> type, String methodNameToMock, Class<?> firstArgumentType,
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
    public static synchronized void mockStaticPartial(Class<?> clazz, String methodNameToMock, Class<?> firstArgumentType,
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
    public static synchronized void mockStaticPartialStrict(Class<?> clazz, String methodNameToMock, Class<?> firstArgumentType,
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
    public static synchronized void mockStaticPartialNice(Class<?> clazz, String methodNameToMock, Class<?> firstArgumentType,
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
     *            <code>null</code>, then this method will have the same effect
     *            as just calling {@link #mockStatic(Class, Method...)} with the
     *            second parameter as <code>new Method[0]</code> (i.e. all
     *            methods in that class will be mocked).
     */
    public static synchronized void mockStaticPartial(Class<?> clazz, String... methodNames) {
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
     *            <code>null</code>, then this method will have the same effect
     *            as just calling {@link #mockStatic(Class, Method...)} with the
     *            second parameter as <code>new Method[0]</code> (i.e. all
     *            methods in that class will be mocked).
     */
    public static synchronized void mockStaticPartialStrict(Class<?> clazz, String... methodNames) {
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
     *            <code>null</code>, then this method will have the same effect
     *            as just calling {@link #mockStatic(Class, Method...)} with the
     *            second parameter as <code>new Method[0]</code> (i.e. all
     *            methods in that class will be mocked).
     */
    public static synchronized void mockStaticPartialNice(Class<?> clazz, String... methodNames) {
        mockStaticNice(clazz, Whitebox.getMethods(clazz, methodNames));
    }

    static <T> T doMockSpecific(Class<T> type, MockStrategy mockStrategy, String[] methodNamesToMock, ConstructorArgs constructorArgs,
            Class<?>... argumentTypes) {
        List<Method> methods = new LinkedList<Method>();
        for (String methodName : methodNamesToMock) {
            methods.add(WhiteboxImpl.findMethodOrThrowException(type, methodName, argumentTypes));
        }

        final Method[] methodArray = methods.toArray(new Method[0]);
        if (WhiteboxImpl.areAllMethodsStatic(methodArray)) {
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
     *            <code>null</code>, then this method will have the same effect
     *            as just calling {@link #createMock(Class, Method...)} with the
     *            second parameter as <code>new Method[0]</code> (i.e. all
     *            methods in that class will be mocked).
     * @return A mock object of type <T>.
     */
    public static synchronized <T> T createPartialMock(Class<T> type, String... methodNames) {
        return createMock(type, Whitebox.getMethods(type, methodNames));
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
        return createMock(type, Whitebox.getMethods(where, methodNames));
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
        return createStrictMock(type, Whitebox.getMethods(type, methodNames));
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
        return createStrictMock(type, Whitebox.getMethods(where, methodNames));
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
        return createNiceMock(type, Whitebox.getMethods(type, methodNames));
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
        return createNiceMock(type, Whitebox.getMethods(where, methodNames));
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
        return createMock(type, new ConstructorArgs(Whitebox.getConstructor(type)), Whitebox.getMethods(type, methodNames));
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
        return createNiceMock(type, new ConstructorArgs(Whitebox.getConstructor(type)), Whitebox.getMethods(type, methodNames));
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
        return createStrictMock(type, new ConstructorArgs(Whitebox.getConstructor(type)), Whitebox.getMethods(type, methodNames));
    }

    /**
     * A utility method that may be used to mock several methods in an easy way
     * (by just passing in the method names of the method you wish to mock). The
     * mock object created will support mocking of final and native methods and
     * invokes a specific constructor based on the supplied argument values.
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
        Constructor<?> constructor = WhiteboxImpl.findConstructorOrThrowException(type, constructorArguments);
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
        Constructor<?> constructor = WhiteboxImpl.findConstructorOrThrowException(type, constructorArguments);
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
        Constructor<?> constructor = WhiteboxImpl.findConstructorOrThrowException(type, constructorArguments);
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
        Constructor<?> constructor = WhiteboxImpl.findConstructorOrThrowException(type, constructorArguments);
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
        Constructor<?> constructor = WhiteboxImpl.findConstructorOrThrowException(type, constructorArguments);
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
        Constructor<?> constructor = WhiteboxImpl.findConstructorOrThrowException(type, constructorArguments);
        ConstructorArgs constructorArgs = new ConstructorArgs(constructor, constructorArguments);
        return doMockSpecific(type, new NiceMockStrategy(), new String[] { methodName }, constructorArgs, methodParameterTypes);
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

        WhiteboxImpl.throwExceptionIfMethodWasNotFound(instance.getClass(), methodName, foundMethod, parameterTypes);

        return doExpectPrivate(instance, foundMethod, arguments);
    }

    /**
     * Used to specify expectations on methods using the method name. Works on
     * for example private or package private methods.
     */
    public static synchronized <T> IExpectationSetters<T> expectPrivate(Object instance, String methodName, Object... arguments) throws Exception {
        if (instance == null) {
            throw new IllegalArgumentException("Instance or class cannot be null.");
        }

        return expectPrivate(instance, methodName, Whitebox.getType(instance), arguments);
    }

    /**
     * Used to specify expectations on methods without specifying a method name.
     * Works on for example private or package private methods. PowerMock tries
     * to find a unique method to expect based on the argument parameters. If
     * PowerMock is unable to locate a unique method you need to revert to using
     * {@link #expectPrivate(Object, String, Object...)}.
     */
    public static synchronized <T> IExpectationSetters<T> expectPrivate(Object instance, Object... arguments) throws Exception {
        return expectPrivate(instance, null, Whitebox.getType(instance), arguments);
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
        if (instance == null) {
            throw new IllegalArgumentException("Instance or class to expect cannot be null.");
        }
        Method[] methods = null;
        if (methodName != null) {
            if (parameterTypes == null) {
                methods = Whitebox.getMethods(where, methodName);
            } else {
                methods = new Method[] { Whitebox.getMethod(where, methodName, parameterTypes) };
            }
        }
        Method methodToExpect;
        if (methods != null && methods.length == 1) {
            methodToExpect = methods[0];
        } else {
            methodToExpect = WhiteboxImpl.findMethodOrThrowException(instance, null, methodName, arguments);
        }

        return doExpectPrivate(instance, methodToExpect, arguments);
    }

    /**
     * Used to specify expectations on methods using the method name at a
     * specific place in the class hierarchy (specified by the
     * <code>where</code> parameter). Works on for example private or package
     * private methods.
     */
    public static synchronized <T> IExpectationSetters<T> expectPrivate(Object instance, String methodName, Class<?> where, Object... arguments)
            throws Exception {
        return expectPrivate(instance, methodName, where, null, arguments);
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
        MockRepository.putAdditionalState(NICE_REPLAY_AND_VERIFY_KEY, true);
    }

    /**
     * Test if a object is a mock created by EasyMock or not.
     */
    private static boolean isEasyMocked(Object mock) {
        return Enhancer.isEnhanced(mock.getClass()) || Proxy.isProxyClass(mock.getClass());
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
        MockRepository.addObjectsToAutomaticallyReplayAndVerify(additionalMocks);

        for (Object classToReplayOrVerify : MockRepository.getObjectsToAutomaticallyReplayAndVerify()) {
            replay(classToReplayOrVerify);
        }
    }

    /**
     * Reset all classes and mock objects known by PowerMock. This includes all
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
     * 
     * @param additionalMocks
     *            Mocks not created by the PowerMock API. These are typically
     *            those mock objects you have created using pure EasyMock or
     *            EasyMock class extensions.
     */
    public static synchronized void resetAll(Object... additionalMocks) {
        MockRepository.addObjectsToAutomaticallyReplayAndVerify(additionalMocks);

        for (Object classToReplayOrVerify : MockRepository.getObjectsToAutomaticallyReplayAndVerify()) {
            reset(classToReplayOrVerify);
        }
    }

    /**
     * Reset a list of class mocks.
     */
    public static synchronized void reset(Class<?>... classMocks) {
        for (Class<?> type : classMocks) {
            final MethodInvocationControl invocationHandler = MockRepository.getStaticMethodInvocationControl(type);
            if (invocationHandler != null) {
                invocationHandler.reset();
            }
            NewInvocationControl<?> newInvocationControl = MockRepository.getNewInstanceControl(type);
            if (newInvocationControl != null) {
                try {
                    newInvocationControl.reset();
                } catch (AssertionError e) {
                    NewInvocationControlAssertionError.throwAssertionErrorForNewSubstitutionFailure(e, type);
                }
            }
        }
    }

    /**
     * Reset a list of mock objects or classes.
     */
    public static synchronized void reset(Object... mocks) {
        try {
            for (Object mock : mocks) {
                if (mock instanceof Class<?>) {
                    reset((Class<?>) mock);
                } else {
                    MethodInvocationControl invocationControl = MockRepository.getInstanceMethodInvocationControl(mock);
                    if (invocationControl != null) {
                        invocationControl.reset();
                    } else {
                        if (isNiceReplayAndVerifyMode() && !isEasyMocked(mock)) {
                            // ignore non-mock
                        } else {
                            /*
                             * Delegate to easy mock class extension if we have
                             * no handler registered for this object.
                             */
                            try {
                                org.easymock.classextension.EasyMock.reset(mock);
                            } catch (RuntimeException e) {
                                throw new RuntimeException(mock + " is not a mock object", e);
                            }
                        }
                    }
                }
            }
        } catch (Throwable t) {
            MockRepository.putAdditionalState(NICE_REPLAY_AND_VERIFY_KEY, false);
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            } else if (t instanceof Error) {
                throw (Error) t;
            }
            throw new RuntimeException(t);
        }
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
        for (Object classToReplayOrVerify : MockRepository.getObjectsToAutomaticallyReplayAndVerify()) {
            verify(classToReplayOrVerify);
        }
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
        try {
            for (Object mock : mocks) {
                if (mock instanceof Class<?>) {
                    replay((Class<?>) mock);
                } else {
                    MethodInvocationControl invocationControl = MockRepository.getInstanceMethodInvocationControl(mock);
                    if (invocationControl != null) {
                        invocationControl.replay();
                    } else {
                        if (isNiceReplayAndVerifyMode() && !isEasyMocked(mock)) {
                            // ignore non-mock
                        } else {
                            /*
                             * Delegate to easy mock class extension if we have
                             * no handler registered for this object.
                             */
                            try {
                                org.easymock.classextension.EasyMock.replay(mock);
                            } catch (RuntimeException e) {
                                throw new RuntimeException(mock + " is not a mock object", e);
                            }
                        }
                    }
                }
            }
        } catch (Throwable t) {
            MockRepository.putAdditionalState(NICE_REPLAY_AND_VERIFY_KEY, false);
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            } else if (t instanceof Error) {
                throw (Error) t;
            }
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
        for (Object mock : objects) {
            if (mock instanceof Class<?>) {
                verifyClass((Class<?>) mock);
            } else {
                MethodInvocationControl invocationControl = MockRepository.getInstanceMethodInvocationControl(mock);
                if (invocationControl != null) {
                    invocationControl.verify();
                } else {
                    if (isNiceReplayAndVerifyMode() && !isEasyMocked(mock)) {
                        // ignore non-mock
                    } else {
                        /*
                         * Delegate to easy mock class extension if we have no
                         * handler registered for this object.
                         */
                        try {
                            org.easymock.classextension.EasyMock.verify(mock);
                        } catch (RuntimeException e) {
                            throw new RuntimeException(mock + " is not a mock object", e);
                        }
                    }
                }
            }
        }
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
        T mock = createMock(type);
        expectNew(type, arguments).andReturn(mock);
        return mock;
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
        T mock = createMock(type);
        expectNew(type, parameterTypes, arguments).andReturn(mock);
        return mock;
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
        T mock = createNiceMock(type);
        IExpectationSetters<T> expectationSetters = expectNiceNew(type, arguments);
        if (expectationSetters != null) {
            expectationSetters.andReturn(mock);
        }
        return mock;
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
        T mock = createNiceMock(type);
        IExpectationSetters<T> expectationSetters = expectNiceNew(type, parameterTypes, arguments);
        if (expectationSetters != null) {
            expectationSetters.andReturn(mock);
        }
        return mock;
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
        T mock = createStrictMock(type);
        expectStrictNew(type, arguments).andReturn(mock);
        return mock;
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
        T mock = createStrictMock(type);
        expectStrictNew(type, parameterTypes, arguments).andReturn(mock);
        return mock;
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
        return doExpectNew(type, new DefaultMockStrategy(), parameterTypes, arguments);
    }

    @SuppressWarnings("unchecked")
    private static <T> IExpectationSetters<T> doExpectNew(Class<T> type, MockStrategy mockStrategy, Class<?>[] parameterTypes, Object... arguments)
            throws Exception {
        if (type == null) {
            throw new IllegalArgumentException("type cannot be null");
        } else if (mockStrategy == null) {
            throw new IllegalArgumentException("Internal error: Mock strategy cannot be null");
        }

        final boolean isNiceMock = mockStrategy instanceof NiceMockStrategy;

        final Class<T> unmockedType = (Class<T>) WhiteboxImpl.getUnmockedType(type);
        if (!isNiceMock) {
            if (parameterTypes == null) {
                WhiteboxImpl.findConstructorOrThrowException(type, arguments);
            } else {
                WhiteboxImpl.getConstructor(unmockedType, parameterTypes);
            }
        }

        /*
         * Check if this type has been mocked before
         */
        NewInvocationControl<IExpectationSetters<T>> newInvocationControl = (NewInvocationControl<IExpectationSetters<T>>) MockRepository
                .getNewInstanceControl(unmockedType);
        if (newInvocationControl == null) {
            InvocationSubstitute<T> mock = doMock(InvocationSubstitute.class, false, mockStrategy, null, (Method[]) null);
            newInvocationControl = new NewInvocationControlImpl<T>(mock, type);
            MockRepository.putNewInstanceControl(type, newInvocationControl);
            MockRepository.addObjectsToAutomaticallyReplayAndVerify(WhiteboxImpl.getUnmockedType(type));
        }

        if (isNiceMock && (arguments == null || arguments.length == 0)) {
            return null;
        }
        return newInvocationControl.expectSubstitutionLogic(arguments);
    }

    /**
     * Allows specifying expectations on new invocations. For example you might
     * want to throw an exception or return a mock. Note that you must replay
     * the class when using this method since this behavior is part of the class
     * mock.
     */
    public static synchronized <T> IExpectationSetters<T> expectNew(Class<T> type, Object... arguments) throws Exception {
        return doExpectNew(type, new DefaultMockStrategy(), null, arguments);
    }

    /**
     * Allows specifying expectations on new invocations for private member
     * (inner) classes, local or anonymous classes. For example you might want
     * to throw an exception or return a mock. Note that you must replay the
     * class when using this method since this behavior is part of the class
     * mock.
     * 
     * @param fullyQualifiedName
     *            The fully-qualified name of the inner/local/anonymous type to
     *            expect.
     * @param arguments
     *            Optional number of arguments.
     */
    @SuppressWarnings("unchecked")
    public static synchronized <T> IExpectationSetters<T> expectNew(String fullyQualifiedName, Object... arguments) throws Exception {
        final Class<?> forName = Class.forName(fullyQualifiedName);
        return (IExpectationSetters<T>) doExpectNew(forName, new DefaultMockStrategy(), null, arguments);
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
        return doExpectNew(type, new StrictMockStrategy(), null, arguments);
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
        return doExpectNew(type, new StrictMockStrategy(), parameterTypes, arguments);
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
        return doExpectNew(type, new NiceMockStrategy(), null, arguments);
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
        return doExpectNew(type, new NiceMockStrategy(), parameterTypes, arguments);
    }

    /**
     * Suppress constructor calls on specific constructors only.
     */
    public static synchronized void suppressConstructor(Constructor<?>... constructors) {
        SuppressCode.suppressConstructor(constructors);
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
        SuppressCode.suppressSpecificConstructor(clazz, parameterTypes);
    }

    /**
     * Suppress all constructors in the given class and it's super classes.
     * 
     * @param classes
     *            The classes whose constructors will be suppressed.
     */
    public static synchronized void suppressConstructor(Class<?>... classes) {
        SuppressCode.suppressConstructor(classes);
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
        SuppressCode.suppressConstructor(clazz, excludePrivateConstructors);
    }

    /**
     * Suppress specific fields. This works on both instance methods and static
     * methods. Note that replay and verify are not needed as this is not part
     * of a mock behavior.
     */
    public static synchronized void suppressField(Field... fields) {
        SuppressCode.suppressField(fields);
    }

    /**
     * Suppress all fields for these classes.
     */
    public static synchronized void suppressField(Class<?>[] classes) {
        SuppressCode.suppressField(classes);
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
        SuppressCode.suppressField(clazz, fieldNames);
    }

    /**
     * Suppress specific method calls on all types containing this method. This
     * works on both instance methods and static methods. Note that replay and
     * verify are not needed as this is not part of a mock behavior.
     */
    public static synchronized void suppressMethod(Method... methods) {
        SuppressCode.suppressMethod(methods);
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
        SuppressCode.suppressMethod(cls, additionalClasses);
    }

    /**
     * Suppress all methods for these classes.
     * 
     * @param classes
     *            Classes whose methods will be suppressed.
     */
    public static synchronized void suppressMethod(Class<?>[] classes) {
        SuppressCode.suppressMethod(classes);
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
        SuppressCode.suppressMethod(clazz, methodName, additionalMethodNames);
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
        SuppressCode.suppressMethod(clazz, methodNames);
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
        SuppressCode.suppressMethod(clazz, excludePrivateMethods);
    }

    /**
     * Suppress a specific method call. Use this for overloaded methods.
     */
    public static synchronized void suppressMethod(Class<?> clazz, String methodName, Class<?>[] parameterTypes) {
        SuppressCode.suppressMethod(clazz, methodName, parameterTypes);
    }

    private static <T> T doMock(Class<T> type, boolean isStatic, MockStrategy mockStrategy, ConstructorArgs constructorArgs, Method... methods) {
        if (type == null) {
            throw new IllegalArgumentException("The class to mock cannot be null");
        }

        IMocksControl control = mockStrategy.createMockControl(type);
        T mock = null;
        if (type.isInterface()) {
            mock = control.createMock(type);
        } else if (type.getName().startsWith("java.") && Modifier.isFinal(type.getModifiers())) {
            Class<?> replicaType = createReplicaType(type, isStatic, constructorArgs);
            final Object replica = doCreateMock(replicaType, constructorArgs, control, methods);
            control = mockStrategy.createMockControl(replicaType);
            MockInvocationHandler h = new MockInvocationHandler((MocksControl) control);
            final Set<Method> methodsToMock = toSet(methods);
            if (isStatic) {
                MockRepository.putStaticMethodInvocationControl(type, new EasyMockMethodInvocationControl<Object>(h, methodsToMock, replica));
                MockRepository.addObjectsToAutomaticallyReplayAndVerify(type);
                return null;
            } else {
                T newInstance = Whitebox.newInstance(type);
                MockRepository
                        .putInstanceMethodInvocationControl(newInstance, new EasyMockMethodInvocationControl<Object>(h, methodsToMock, replica));
                if (newInstance instanceof InvocationSubstitute<?> == false) {
                    MockRepository.addObjectsToAutomaticallyReplayAndVerify(newInstance);
                }
                return newInstance;
            }
        } else {
            mock = doCreateMock(type, constructorArgs, control, methods);
        }
        MockInvocationHandler h = new MockInvocationHandler((MocksControl) control);
        final Set<Method> methodsToMock = toSet(methods);
        if (isStatic) {
            MockRepository.putStaticMethodInvocationControl(type, new EasyMockMethodInvocationControl<T>(h, methodsToMock, mock));
            MockRepository.addObjectsToAutomaticallyReplayAndVerify(type);
        } else {
            MockRepository.putInstanceMethodInvocationControl(mock, new EasyMockMethodInvocationControl<T>(h, methodsToMock));
            if (mock instanceof InvocationSubstitute<?> == false) {
                MockRepository.addObjectsToAutomaticallyReplayAndVerify(mock);
            }
        }
        return mock;
    }

    private static <T> Class<?> createReplicaType(Class<T> type, boolean isStatic, ConstructorArgs constructorArgs) {
        ClassReplicaCreator classReplicaCreator = new ClassReplicaCreator();
        Class<?> replicaType = null;
        if (isStatic || constructorArgs == null) {
            replicaType = classReplicaCreator.createClassReplica(type);
        } else {
            try {
                replicaType = classReplicaCreator.createInstanceReplica(constructorArgs.getConstructor().newInstance(constructorArgs.getInitArgs()));
            } catch (RuntimeException e) {
                throw e;
            } catch (InvocationTargetException e) {
                Throwable targetException = ((InvocationTargetException) e).getTargetException();
                if (targetException instanceof RuntimeException) {
                    throw (RuntimeException) targetException;
                }
                throw new RuntimeException(e);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
        return replicaType;
    }

    private static <T> T doCreateMock(Class<T> type, ConstructorArgs constructorArgs, final IMocksControl control, Method... methods) {
        T mock;
        MocksClassControl mocksClassControl = ((MocksClassControl) control);
        if (constructorArgs == null) {
            if (methods == null) {
                mock = mocksClassControl.createMock(type);
            } else {
                mock = mocksClassControl.createMock(type, methods);
            }
        } else {
            if (methods == null) {
                mock = mocksClassControl.createMock(type, constructorArgs);
            } else {
                mock = mocksClassControl.createMock(type, constructorArgs, methods);
            }
        }
        return mock;
    }

    private static Set<Method> toSet(Method[] methods) {
        return methods == null ? null : new HashSet<Method>(Arrays.asList(methods));
    }

    private static Class<?>[] mergeArgumentTypes(Class<?> firstArgumentType, Class<?>... additionalArgumentTypes) {
        if (firstArgumentType == null) {
            return additionalArgumentTypes == null ? new Class<?>[0] : additionalArgumentTypes;
        } else if (additionalArgumentTypes == null) {
            additionalArgumentTypes = new Class<?>[0];
        }
        final Class<?>[] argumentTypes = new Class[additionalArgumentTypes.length + 1];
        argumentTypes[0] = firstArgumentType;
        if (additionalArgumentTypes.length != 0) {
            System.arraycopy(additionalArgumentTypes, 0, argumentTypes, 1, additionalArgumentTypes.length);
        }
        return argumentTypes;
    }

    @SuppressWarnings("unchecked")
    private static <T> IExpectationSetters<T> doExpectPrivate(Object instance, Method methodToExpect, Object... arguments) throws Exception {
        WhiteboxImpl.performMethodInvocation(instance, methodToExpect, arguments);
        return (IExpectationSetters<T>) org.easymock.classextension.EasyMock.expectLastCall();
    }

    private static synchronized void replay(Class<?>... types) {
        for (Class<?> type : types) {
            final MethodInvocationControl invocationHandler = MockRepository.getStaticMethodInvocationControl(type);
            if (invocationHandler != null) {
                invocationHandler.replay();
            }

            NewInvocationControl<?> newInvocationControl = MockRepository.getNewInstanceControl(type);
            if (newInvocationControl != null) {
                newInvocationControl.replay();
            }
        }
    }

    /**
     * Note: doesn't clear PowerMock state.
     */
    private static synchronized void verifyClass(Class<?>... types) {
        for (Class<?> type : types) {
            final MethodInvocationControl invocationHandler = MockRepository.getStaticMethodInvocationControl(type);
            if (invocationHandler != null) {
                invocationHandler.verify();
            }
            NewInvocationControl<?> newInvocationControl = MockRepository.getNewInstanceControl(type);
            if (newInvocationControl != null) {
                try {
                    newInvocationControl.verify();
                } catch (AssertionError e) {
                    NewInvocationControlAssertionError.throwAssertionErrorForNewSubstitutionFailure(e, type);
                }
            }
        }
    }

    private static boolean isNiceReplayAndVerifyMode() {
        final Boolean mode = (Boolean) MockRepository.getAdditionalState(NICE_REPLAY_AND_VERIFY_KEY);
        return mode != null && mode;
    }
}
