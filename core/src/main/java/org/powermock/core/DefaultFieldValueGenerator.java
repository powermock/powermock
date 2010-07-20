/*
 * Copyright 2010 the original author or authors.
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

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.powermock.reflect.Whitebox;

public class DefaultFieldValueGenerator {
	public static <T> T fillWithDefaultValues(T object) {
		if (object == null) {
			throw new IllegalArgumentException("object to fill cannot be null");
		}
		Set<Field> allInstanceFields = Whitebox.getAllInstanceFields(object);
		for (Field field : allInstanceFields) {
			Object defaultValue = TypeUtils.getDefaultValue(field.getType());
			if (defaultValue == null) {
				defaultValue = instantiateFieldType(field);
				fillWithDefaultValues(defaultValue);
			}
			try {
				field.set(object, defaultValue);
			} catch (Exception e) {
				throw new RuntimeException("Internal error: Failed to set field.", e);
			}
		}
		return object;
	}

	private static Object instantiateFieldType(Field field) {
		Class<?> fieldType = field.getType();
		Object defaultValue;
		if (fieldType.isArray()) {
			defaultValue = Array.newInstance(fieldType.getComponentType(), 0);
		} else if (Modifier.isAbstract(fieldType.getModifiers())) {
			defaultValue = Whitebox.newInstance(new ConcreteClassGenerator().createConcreteSubClass(fieldType));
		} else {
			fieldType = substituteKnownProblemTypes(fieldType);
			defaultValue = Whitebox.newInstance(fieldType);
		}
		return defaultValue;
	}

	/**
	 * Substitute class types that are known to cause problems when generating
	 * them.
	 * 
	 * @param fieldType
	 * @return A field-type substitute or the original class.
	 */
	private static Class<?> substituteKnownProblemTypes(Class<?> fieldType) {
		/*
		 * InetAddress has a private constructor and is normally not
		 * constructible without reflection. It's no problem instantiating this
		 * class using reflection or with Whitebox#newInstance but the problem
		 * lies in the equals method since it _always_ returns false even though
		 * it's the same instance! So in cases where classes containing an
		 * InetAddress field and uses it in the equals method (such as
		 * java.net.URL) then may return false since InetAddress#equals()
		 * returns false all the time. As a work-around we return an
		 * Inet4Address instead which has a proper equals method.
		 */
		if (fieldType == InetAddress.class) {
			return Inet4Address.class;
		}
		return fieldType;
	}

}
