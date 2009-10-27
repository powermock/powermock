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
package org.powermock.api.support;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

import org.powermock.reflect.Whitebox;

/**
 * The purpose of the deep cloner is to create a deep clone of an object.
 * Classes
 */
public class DeepCloner {
    private static final String IGNORED_PACKAGES = "java.";

    /**
     * Clones an object.
     * 
     * @return A deep clone of the object to clone.
     */
    public static <T> T clone(T objectToClone) {
        assertObjectNotNull(objectToClone);
        return (T) performClone(getType(objectToClone), objectToClone);
    }

    /**
     * Clone an object into an object loaded by the supplied classloader.
     */
    public static <T> T clone(ClassLoader classloader, T objectToClone) {
        assertObjectNotNull(objectToClone);
        return performClone(ClassLoaderUtil.loadClassWithClassloader(classloader, getType(objectToClone)), objectToClone);
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<T> getType(T objectToClone) {
        if (objectToClone == null) {
            return null;
        }
        return (Class<T>) (objectToClone instanceof Class ? objectToClone : objectToClone.getClass());
    }

    private static void assertObjectNotNull(Object object) {
        if (object == null) {
            throw new IllegalArgumentException("Object to clone cannot be null");
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T performClone(Class<T> targetClass, Object source) {
        // TODO Support Enums and primitives and primitive arrays(?)
        Object target = null;
        if (targetClass.isArray()) {
            target = instantiateArray(targetClass, source);
        } else if (targetClass.isPrimitive() || targetClass.getName().startsWith(IGNORED_PACKAGES)) {
            target = source;
        } else if (targetClass.isEnum()) {
            final Class enumClassLoadedByTargetCL = ClassLoaderUtil.loadClassWithClassloader(targetClass.getClassLoader(), source.getClass());
            target = getEnumValue(source, enumClassLoadedByTargetCL);
        } else {
            target = Whitebox.newInstance(targetClass);
        }

        if (!targetClass.isEnum()) {
            Class<?> currentTargetClass = targetClass;
            while (currentTargetClass != null && !currentTargetClass.getName().startsWith(IGNORED_PACKAGES)) {
                for (Field field : currentTargetClass.getDeclaredFields()) {
                    field.setAccessible(true);
                    try {
                        final Field declaredField = source.getClass().getDeclaredField(field.getName());
                        declaredField.setAccessible(true);
                        final Object object = declaredField.get(source);
                        final Object instantiatedValue;
                        final Class<Object> type = getType(object);
                        if (object == null || type.getName().startsWith(IGNORED_PACKAGES)) {
                            instantiatedValue = object;
                        } else {
                            final Class<Object> typeLoadedByCL = ClassLoaderUtil.loadClassWithClassloader(targetClass.getClassLoader(), type);
                            if (type.isEnum()) {
                                instantiatedValue = getEnumValue(object, typeLoadedByCL);
                            } else {
                                instantiatedValue = performClone(typeLoadedByCL, object);
                            }
                        }
                        if (!field.isEnumConstant()) {
                            field.set(target, instantiatedValue);
                        }
                    } catch (RuntimeException e) {
                        throw e;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                currentTargetClass = currentTargetClass.getSuperclass();
            }
        }
        return (T) target;
    }

    @SuppressWarnings("unchecked")
    private static Enum getEnumValue(final Object enumValueOfSourceClassloader, final Class<Object> enumLoadedByTargetCL) {
        return Enum.valueOf((Class) enumLoadedByTargetCL, ((Enum) enumValueOfSourceClassloader).toString());
    }

    private static Object instantiateArray(Class<?> arrayClass, Object objectToClone) {
        final int arrayLength = Array.getLength(objectToClone);
        final Object array = Array.newInstance(arrayClass.getComponentType(), arrayLength);
        for (int i = 0; i < arrayLength; i++) {
            final Object object = Array.get(objectToClone, i);
            final Object performClone = performClone(ClassLoaderUtil.loadClassWithClassloader(arrayClass.getClassLoader(), getType(object)), object);
            Array.set(array, i, performClone);
        }
        return array;
    }
}
