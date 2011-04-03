/*
 * Copyright 2011 the original author or authors.
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
package org.powermock.core;

import org.powermock.reflect.Whitebox;
import org.powermock.reflect.internal.TypeUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.Set;

/**
 * Fills the fields with default not-null values. If a field type is an
 * interface a proxy returning default values for each method will be created.
 * If it's an abstract class a new concrete implementation of that type will
 * created and instantiated at run-time.
 * <p>
 * There are two scenarios where a field-type cannot possibly be assigned.
 * <ol>
 * <li>
 * When a class contains a field of it's own type, which would lead to infinite
 * recursion, <code>null</code> is assigned.</li>
 * <li>When the field type is an abstract Java system class with no visible
 * constructor (package-private) <code>null</code> is assigned.</li>
 * </ol>
 */
public class DefaultFieldValueGenerator {

    public static <T> T fillWithDefaultValues(T object) {
        if (object == null) {
            throw new IllegalArgumentException("object to fill cannot be null");
        }
        Set<Field> allInstanceFields = Whitebox.getAllInstanceFields(object);
        for (Field field : allInstanceFields) {
            final Class<?> fieldType = field.getType();
            Object defaultValue = TypeUtils.getDefaultValue(fieldType);
            if (defaultValue == null && fieldType != object.getClass() && !field.isSynthetic()) {
                defaultValue = instantiateFieldType(field);
                if (defaultValue != null) {
                    fillWithDefaultValues(defaultValue);
                }
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
        int modifiers = fieldType.getModifiers();
        if(fieldType.isAssignableFrom(ClassLoader.class) || isClass(fieldType)) {
            defaultValue = null;
        } else if (Modifier.isAbstract(modifiers) && !Modifier.isInterface(modifiers) && !fieldType.isArray()) {
            Class<?> createConcreteSubClass = new ConcreteClassGenerator().createConcreteSubClass(fieldType);
            defaultValue = createConcreteSubClass == null ? null : Whitebox.newInstance(createConcreteSubClass);
        } else {
            fieldType = substituteKnownProblemTypes(fieldType);
            defaultValue = Whitebox.newInstance(fieldType);
        }
        return defaultValue;
    }

    private static boolean isClass(Class<?> fieldType) {
        return fieldType == Class.class;
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
           * constructable without reflection. It's no problem instantiating this
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
