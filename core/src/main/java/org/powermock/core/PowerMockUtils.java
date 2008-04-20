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

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Vector;

public class PowerMockUtils {

	/**
	 * Get an iterator of all classes loaded by the specific classloader.
	 * 
	 * @param classLoader
	 * @return
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("unchecked")
	public static Iterator<Class<?>> getClassIterator(ClassLoader classLoader)
			throws NoSuchFieldException, IllegalAccessException {
		Class<?> classLoaderClass = classLoader.getClass();
		while (classLoaderClass != ClassLoader.class) {
			classLoaderClass = classLoaderClass.getSuperclass();
		}
		Field classesField = classLoaderClass.getDeclaredField("classes");
		classesField.setAccessible(true);
		Vector<Class<?>> classes = (Vector<Class<?>>) classesField
				.get(classLoader);
		return classes.iterator();
	}

	/**
	 * 
	 * @param classLoader
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 */
	public static void printClassesLoadedByClassloader(ClassLoader classLoader,
			boolean includeParent) throws NoSuchFieldException,
			IllegalAccessException {
		while (classLoader != null) {
			System.out.println("ClassLoader: " + classLoader);
			for (Iterator<?> iter = PowerMockUtils
					.getClassIterator(classLoader); iter.hasNext();) {
				System.out.println("\t" + iter.next());
			}
			if (includeParent) {
				classLoader = classLoader.getParent();
			} else {
				classLoader = null;
			}
		}
	}

	public static void throwAssertionErrorForNewSubstitutionFailure(
			AssertionError oldError, Class<?> type) {
		/*
		 * We failed to verify the new substitution mock. This happens when, for
		 * example, the user has done something like
		 * expectNew(MyClass.class).andReturn(myMock).times(3) when in fact an
		 * instance of MyClass has been created less or more times than 3.
		 */
		String message = oldError.getMessage();
		final String expectedString = "expected:";
		final String actualString = "actual:";
		final int expectedTimesStartIndex = message.indexOf(expectedString)
				+ expectedString.length() + 1;
		final int indexOfActualString = message.indexOf(actualString);
		String expectedTimes = message.substring(expectedTimesStartIndex,
				indexOfActualString - 2);
		String actualTimes = message.substring(indexOfActualString
				+ actualString.length() + 1);
		StringBuilder errorMessage = new StringBuilder();
		errorMessage
				.append("\nExpectation failure on verify:\nExpected a new instance call on ");
		errorMessage.append(type.toString()).append(" ").append(expectedTimes)
				.append(" times but was actually ").append(actualTimes).append(
						" times.");
		throw new AssertionError(errorMessage);
	}
}
