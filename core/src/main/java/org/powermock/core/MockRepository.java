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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.powermock.core.spi.MethodInvocationControl;
import org.powermock.core.spi.NewInvocationControl;

/**
 * Hold mock objects that should be used instead of the concrete implementation.
 * Mock transformers may use this class to gather information on which classes
 * and methods that are mocked.
 */
public class MockRepository {

	private static Set<Object> objectsToAutomaticallyReplayAndVerify = new HashSet<Object>();

	private static Map<Class<?>, NewInvocationControl<?>> newSubstitutions = new HashMap<Class<?>, NewInvocationControl<?>>();

	/**
	 * Holds info about general method invocation mocks for classes.
	 */
	private static Map<Class<?>, MethodInvocationControl> classMocks = new HashMap<Class<?>, MethodInvocationControl>();

	/**
	 * Holds info about general method invocation mocks for instances.
	 */
	private static Map<Object, MethodInvocationControl> instanceMocks = new HashMap<Object, MethodInvocationControl>();

	/**
	 * Holds info about which class that should have their static initializers
	 * suppressed.
	 */
	private static Set<String> suppressStaticInitializers = new HashSet<String>();

	/**
	 * Sometimes mock frameworks needs to store additional state. They can to
	 * this key/value based approach.
	 */
	private static Map<String, Object> additionalState = new HashMap<String, Object>();

	/**
	 * Set of constructors that should be suppressed.
	 */
	private static final Set<Constructor<?>> suppressConstructor = new HashSet<Constructor<?>>();

	/**
	 * Set of methods that should be suppressed.
	 */
	private static final Set<Method> suppressMethod = new HashSet<Method>();

	/**
	 * Clear all state of the mock repository
	 */
	public synchronized static void clear() {
		newSubstitutions.clear();
		classMocks.clear();
		instanceMocks.clear();
		objectsToAutomaticallyReplayAndVerify.clear();
		suppressStaticInitializers.clear();
		additionalState.clear();
		suppressConstructor.clear();
		suppressMethod.clear();
	}

	/**
	 * Removes an object from the MockRepository if it exists.
	 */
	public static void remove(Object mock) {
		if (mock instanceof Class<?>) {
			if (newSubstitutions.containsKey(mock)) {
				newSubstitutions.remove(mock);
			}
			if (classMocks.containsKey(mock)) {
				classMocks.remove(mock);
			}
		} else if (instanceMocks.containsKey(mock)) {
			instanceMocks.remove(mock);
		}
	}

	public static synchronized MethodInvocationControl getStaticMethodInvocationControl(Class<?> type) {
		return classMocks.get(type);
	}

	public static synchronized MethodInvocationControl putStaticMethodInvocationControl(Class<?> type, MethodInvocationControl invocationControl) {
		return classMocks.put(type, invocationControl);
	}

	public static synchronized MethodInvocationControl removeClassMethodInvocationControl(Class<?> type) {
		return classMocks.remove(type);
	}

	public static synchronized MethodInvocationControl getInstanceMethodInvocationControl(Object instance) {
		return instanceMocks.get(instance);
	}

	public static synchronized MethodInvocationControl putInstanceMethodInvocationControl(Object instance, MethodInvocationControl invocationControl) {
		return instanceMocks.put(instance, invocationControl);
	}

	public static synchronized MethodInvocationControl removeInstanceMethodInvocationControl(Class<?> type) {
		return classMocks.remove(type);
	}

	public static synchronized NewInvocationControl<?> getNewInstanceControl(Class<?> type) {
		return newSubstitutions.get(type);
	}

	public static synchronized NewInvocationControl<?> putNewInstanceControl(Class<?> type, NewInvocationControl<?> control) {
		return newSubstitutions.put(type, control);
	}

	/**
	 * Add a fully qualified class name for a class that should have its static
	 * initializers suppressed.
	 * 
	 * @param className
	 *            The fully qualified class name for a class that should have
	 *            its static initializers suppressed.
	 */
	public static synchronized void addSuppressStaticInitializer(String className) {
		suppressStaticInitializers.add(className);
	}

	/**
	 * Remove a fully qualified class name for a class that should no longer
	 * have its static initializers suppressed.
	 * 
	 * @param className
	 *            The fully qualified class name for a class that should no
	 *            longer have its static initializers suppressed.
	 */
	public static synchronized void removeSuppressStaticInitializer(String className) {
		suppressStaticInitializers.remove(className);
	}

	/**
	 * Check whether or not a class with the fully qualified name should have
	 * its static initializers suppressed.
	 * 
	 * @param className
	 *            <code>true</code> if class with the fully qualified name
	 *            <code>className</code> should have its static initializers
	 *            suppressed, <code>false</code> otherwise.
	 */
	public static synchronized boolean shouldSuppressStaticInitializerFor(String className) {
		return suppressStaticInitializers.contains(className);
	}

	/**
	 * @return All classes that should be automatically replayed or verified.
	 */
	public static synchronized Set<Object> getObjectsToAutomaticallyReplayAndVerify() {
		return Collections.unmodifiableSet(objectsToAutomaticallyReplayAndVerify);
	}

	/**
	 * Add classes that should be automatically replayed or verified.
	 */
	public static synchronized void addObjectsToAutomaticallyReplayAndVerify(Object... objects) {
		for (Object mock : objects) {
			objectsToAutomaticallyReplayAndVerify.add(mock);
		}
	}

	/**
	 * When a mock framework API needs to store additional state not applicable
	 * for the other methods, it may you this method to do so.
	 * 
	 * @param key
	 *            The key under which the <tt>value</tt> is stored.
	 * @param value
	 *            The value to store under the specified <tt>key</tt>.
	 * @return The previous object under the specified <tt>key</tt> or
	 *         <code>null</code>.
	 */
	public static synchronized Object putAdditionalState(String key, Object value) {
		return additionalState.put(key, value);
	}

	/**
	 * Retrieve state based on the supplied key.
	 */
	public static synchronized Object getAdditionalState(String key) {
		return additionalState.get(key);
	}

	/**
	 * Add a method to suppress.
	 * 
	 * @param method
	 *            The method to suppress.
	 */
	public static synchronized void addMethodToSuppress(Method method) {
		suppressMethod.add(method);
	}

	/**
	 * Add a constructor to suppress.
	 * 
	 * @param constructor
	 *            The constructor to suppress.
	 */
	public static synchronized void addConstructorToSuppress(Constructor<?> constructor) {
		suppressConstructor.add(constructor);
	}

	/**
	 * @return <code>true</code> if the <tt>method</tt> should be suppressed.
	 */
	public static synchronized boolean shouldSuppressMethod(Method method) {
		return suppressMethod.contains(method);
	}

	/**
	 * @return <code>true</code> if the <tt>constructor</tt> should be
	 *         suppressed.
	 */
	public static synchronized boolean shouldSuppressConstructor(Constructor<?> constructor) {
		return suppressConstructor.contains(constructor);
	}
}
