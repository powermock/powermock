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

import org.powermock.core.spi.MethodInvocationControl;
import org.powermock.core.spi.NewInvocationControl;
import org.powermock.reflect.internal.WhiteboxImpl;

/**
 * All mock invocations are routed through this gateway. This includes method
 * calls, construction of new instances and more. Do not use this class
 * directly, but always go through the PowerMock facade.
 */
public class MockGateway {

	public static final Object PROCEED = new Object();
	public static final Object SUPPRESS = new Object();

	// used for static methods
	public static synchronized Object methodCall(Class<?> type, String methodName, Object[] args, Class<?>[] sig, String returnTypeAsString)
			throws Throwable {
		return doMethodCall(type, methodName, args, sig, returnTypeAsString);
	}

	private static Object doMethodCall(Object object, String methodName, Object[] args, Class<?>[] sig, String returnTypeAsString) throws Throwable,
			NoSuchMethodException {
		if ((methodName.equals("hashCode") && sig.length == 0) || (methodName.equals("equals") && sig.length == 1)) {
			return PROCEED;
		}
		Object returnValue = null;

		MethodInvocationControl methodInvocationControl = null;
		Class<?> objectType = null;

		if (object instanceof Class<?>) {
			objectType = (Class<?>) object;
			methodInvocationControl = MockRepository.getStaticMethodInvocationControl(objectType);
		} else {
			final Class<? extends Object> type = object.getClass();
			objectType = WhiteboxImpl.getUnmockedType(type);
			methodInvocationControl = MockRepository.getInstanceMethodInvocationControl(object);
		}

		/*
		 * if invocationControl is null or the method is not mocked, invoke
		 * original method or suppress the method code otherwise invoke the
		 * invocation handler.
		 */
		if (methodInvocationControl != null && methodInvocationControl.isMocked(WhiteboxImpl.getMethod(objectType, methodName, sig))) {
			returnValue = methodInvocationControl.invoke(object, WhiteboxImpl.getMethod(objectType, methodName, sig), args);
			if (returnValue == SUPPRESS) {
				returnValue = TypeUtils.getDefaultValue(returnTypeAsString);
			}
		} else {
			final boolean shouldSuppressMethodCode = MockRepository.shouldSuppressMethod(WhiteboxImpl.getMethod(objectType, methodName, sig));
			if (shouldSuppressMethodCode) {
				returnValue = TypeUtils.getDefaultValue(returnTypeAsString);
			} else {
				returnValue = PROCEED;
			}
		}
		return returnValue;
	}

	// used for instance methods
	public static synchronized Object methodCall(Object instance, String methodName, Object[] args, Class<?>[] sig, String returnTypeAsString)
			throws Throwable {
		return doMethodCall(instance, methodName, args, sig, returnTypeAsString);
	}

	public static synchronized Object newInstanceCall(Class<?> type, Object[] args, Class<?>[] sig) throws Throwable {
		final NewInvocationControl<?> newInvocationControl = MockRepository.getNewInstanceControl(type);
		if (newInvocationControl != null) {
			return newInvocationControl.invoke(type, args, sig);
		}
		// Check if we should suppress the constructor code
		if (MockRepository.shouldSuppressConstructor(WhiteboxImpl.getConstructor(type, sig))) {
			return WhiteboxImpl.getFirstParentConstructor(type);
		}
		return PROCEED;
	}

	public static synchronized Object staticConstructorCall(String className) {
		if (MockRepository.shouldSuppressStaticInitializerFor(className)) {
			return "suppress";
		}
		return PROCEED;
	}

	public static synchronized Object constructorCall(Class<?> type, Object[] args, Class<?>[] sig) throws Throwable {
		final Constructor<?> constructor = WhiteboxImpl.getConstructor(type, sig);
		if (MockRepository.shouldSuppressConstructor(constructor)) {
			return null;
		}
		return PROCEED;
	}
}
