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
package org.powermock.reflect.internal;

/**
 * Utilities for types.
 */
public class TypeUtils {

	/**
	 * Get the default value for a type.
	 * 
	 * @param type
	 *            The type whose default value to get.
	 * @return The default return type of {@code type}.
	 */
	public static Object getDefaultValue(Class<?> type) {
		return getDefaultValue(type.getName());
	}

	/**
	 * Get the default value of a type with based on its fully-qualified name.
	 * 
	 * @param fullyQualifiedTypeName
	 *            The name of the type whose default value to get.
	 * @return The default value of {@code fullyQualifiedTypeName}.
	 */
	public static Object getDefaultValue(String fullyQualifiedTypeName) {
		if (fullyQualifiedTypeName == null) { // Void
			return "";
		} else if (fullyQualifiedTypeName.equals(byte.class.getName())) {
			return (byte) 0;
		} else if (fullyQualifiedTypeName.equals(int.class.getName())) {
			return 0;
		} else if (fullyQualifiedTypeName.equals(short.class.getName())) {
			return (short) 0;
		} else if (fullyQualifiedTypeName.equals(long.class.getName())) {
			return 0L;
		} else if (fullyQualifiedTypeName.equals(float.class.getName())) {
			return 0.0F;
		} else if (fullyQualifiedTypeName.equals(double.class.getName())) {
			return 0.0D;
		} else if (fullyQualifiedTypeName.equals(boolean.class.getName())) {
			return false;
		} else if (fullyQualifiedTypeName.equals(char.class.getName())) {
			return ' ';
		} else {
			return null;
		}
	}

	/**
	 * Get the default value of a type with based on its fully-qualified name.
	 * 
	 * @param fullyQualifiedTypeName
	 *            The name of the type whose default value to get.
	 * @return The default value of {@code fullyQualifiedTypeName}.
	 */
	public static String getDefaultValueAsString(String fullyQualifiedTypeName) {
		if (fullyQualifiedTypeName == null) { // Void
			return "";
		} else if (fullyQualifiedTypeName.equals(byte.class.getName())) {
			return "(byte) 0";
		} else if (fullyQualifiedTypeName.equals(int.class.getName())) {
			return "0";
		} else if (fullyQualifiedTypeName.equals(short.class.getName())) {
			return "(short) 0";
		} else if (fullyQualifiedTypeName.equals(long.class.getName())) {
			return "0L";
		} else if (fullyQualifiedTypeName.equals(float.class.getName())) {
			return "0.0F";
		} else if (fullyQualifiedTypeName.equals(double.class.getName())) {
			return "0.0D";
		} else if (fullyQualifiedTypeName.equals(boolean.class.getName())) {
			return "false";
		} else if (fullyQualifiedTypeName.equals(char.class.getName())) {
			return "' '";
		} else {
			return "null";
		}
	}
}
