/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.powermock.reflect.internal.primitivesupport;

import java.util.HashMap;
import java.util.Map;

/**
 * The purpose of the Primitive Wrapper is to provide methods that deals with
 * translating wrapper types to its related primitive type.
 */
public class BoxedWrapper {
	private static final Map<Class<?>, Class<?>> boxedWrapper = new HashMap<Class<?>, Class<?>>();

	static {
		boxedWrapper.put(int.class, Integer.class);
		boxedWrapper.put(long.class, Long.class);
		boxedWrapper.put(float.class, Float.class);
		boxedWrapper.put(double.class, Double.class);
		boxedWrapper.put(boolean.class, Boolean.class);
		boxedWrapper.put(byte.class, Byte.class);
		boxedWrapper.put(short.class, Short.class);
		boxedWrapper.put(char.class, Character.class);
	}

	/**
	 * Get the wrapped counter part from a primitive type. For example:
	 * <p>
	 * 
	 * {@code getBoxedFromPrimitiveType(int.class)} will return
	 * {@code Integer.class}.
	 * 
	 * 
	 * @param primitiveType
	 *            The primitive type to convert to its wrapper counter part.
	 * @return The boxed counter part or {@code null} if the class did
	 *         not have a boxed counter part.
	 * 
	 */
	public static Class<?> getBoxedFromPrimitiveType(Class<?> primitiveType) {
		return boxedWrapper.get(primitiveType);
	}

	/**
	 * Returns {@code true} if {@code type} has a primitive
	 * counter-part. E.g. if {@code type} if {@code Integer} then this
	 * method will return {@code true}.
	 * 
	 * @param type
	 *            The type to check whether or not it has a primitive
	 *            counter-part.
	 * @return {@code true} if this type has a primitive counter-part.
	 */
	public static boolean hasBoxedCounterPart(Class<?> type) {
		return boxedWrapper.containsKey(type);
	}
}
