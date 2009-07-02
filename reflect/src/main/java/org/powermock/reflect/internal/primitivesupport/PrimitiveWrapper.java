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
package org.powermock.reflect.internal.primitivesupport;

import java.util.HashMap;
import java.util.Map;

/**
 * The purpose of the Primitive Wrapper is to provide methods that deals with
 * translating wrapper types to its related primitive type.
 */
public class PrimitiveWrapper {
	private static final Map<Class<?>, Class<?>> primitiveWrapper = new HashMap<Class<?>, Class<?>>();

	static {
		primitiveWrapper.put(Integer.class, int.class);
		primitiveWrapper.put(Long.class, long.class);
		primitiveWrapper.put(Float.class, float.class);
		primitiveWrapper.put(Double.class, double.class);
		primitiveWrapper.put(Boolean.class, boolean.class);
		primitiveWrapper.put(Byte.class, byte.class);
		primitiveWrapper.put(Short.class, short.class);
		primitiveWrapper.put(Character.class, char.class);
	}

	/**
	 * Convert all wrapper types in <code>types</code> to their primitive
	 * counter parts.
	 * 
	 * @param types
	 *            The array of types that should be converted.
	 * @return A new array where all wrapped types have been converted to their
	 *         primitive counter part.
	 */
	public static Class<?>[] toPrimitiveType(Class<?>[] types) {
		if (types == null) {
			throw new IllegalArgumentException("types cannot be null");
		}

		Class<?>[] convertedTypes = new Class<?>[types.length];
		for (int i = 0; i < types.length; i++) {
			final Class<?> originalType = types[i];
			Class<?> primitiveType = primitiveWrapper.get(originalType);
			if (primitiveType == null) {
				convertedTypes[i] = originalType;
			} else {
				convertedTypes[i] = primitiveType;
			}
		}
		return convertedTypes;
	}

	/**
	 * Get the primitive counter part from a wrapped type. For example:
	 * <p>
	 * 
	 * <code>getPrimitiveFromWrapperType(Integer.class)</code> will return
	 * <code>int.class</code>.
	 * 
	 * 
	 * @param wrapperType
	 *            The wrapper type to convert to its primitive counter part.
	 * @return The primitive counter part or <code>null</code> if the class did
	 *         not have a primitive counter part.
	 * 
	 */
	public static Class<?> getPrimitiveFromWrapperType(Class<?> wrapperType) {
		return primitiveWrapper.get(wrapperType);
	}

	/**
	 * Returns <code>true</code> if <code>type</code> has a primitive
	 * counter-part. E.g. if <code>type</code> if <code>Integer</code> then this
	 * method will return <code>true</code>.
	 * 
	 * @param type
	 *            The type to check whether or not it has a primitive
	 *            counter-part.
	 * @return <code>true</code> if this type has a primitive counter-part.
	 */
	public static boolean hasPrimitiveCounterPart(Class<?> type) {
		return primitiveWrapper.containsKey(type);
	}
}
