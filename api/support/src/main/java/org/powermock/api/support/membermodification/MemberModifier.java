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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.powermock.api.support.SuppressCode;
import org.powermock.api.support.membermodification.strategy.MethodReplaceStrategy;
import org.powermock.api.support.membermodification.strategy.MethodStubStrategy;
import org.powermock.api.support.membermodification.strategy.impl.MethodReplaceStrategyImpl;
import org.powermock.api.support.membermodification.strategy.impl.MethodStubStrategyImpl;

/**
 * Contains various utilities for modifying members of classes such as
 * constructors, fields and methods. Modifying means e.g. changing return value
 * of method invocations or suppressing a constructor.
 */
public class MemberModifier extends MemberMatcher {

	/**
	 * Suppress a specific method. This works on both instance methods and
	 * static methods.
	 */
	public static void suppress(Method method) {
		SuppressCode.suppressMethod(method);
	}

	/**
	 * Suppress multiple methods. This works on both instance methods and static
	 * methods.
	 */
	public static void suppress(Method[] methods) {
		SuppressCode.suppressMethod(methods);
	}

	/**
	 * Suppress a constructor.
	 */
	public static void suppress(Constructor<?> constructor) {
		SuppressCode.suppressConstructor(constructor);
	}

	/**
	 * Suppress multiple constructors.
	 */
	public static void suppress(Constructor<?>[] constructors) {
		SuppressCode.suppressConstructor(constructors);
	}

	/**
	 * Suppress a field.
	 */
	public static void suppress(Field field) {
		SuppressCode.suppressField(field);
	}

	/**
	 * Suppress multiple fields.
	 */
	public static void suppress(Field[] fields) {
		SuppressCode.suppressField(fields);
	}

	/**
	 * Add a method that should be intercepted and return another value (i.e.
	 * the method is stubbed).
	 */
	public static <T> MethodStubStrategy<T> stub(Method method) {
		return new MethodStubStrategyImpl<T>(method);
	}

	/**
	 * Replace a method invocation.
	 */
	public static MethodReplaceStrategy replace(Method method) {
		return new MethodReplaceStrategyImpl(method);
	}
}
