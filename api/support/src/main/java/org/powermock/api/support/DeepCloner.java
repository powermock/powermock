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
import java.lang.reflect.Modifier;
import java.util.Collection;

import org.powermock.reflect.Whitebox;

/**
 * The purpose of the deep cloner is to create a deep clone of an object. An
 * object can also be cloned to a different class-loader.
 * <p>
 * Note that fields with static <i>and</i> final modifiers cannot be cloned.
 * 
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
        final Class<T> objectType = getType(objectToClone);
        return (T) performClone(objectType.getClassLoader(), objectType, objectToClone);
    }

    /**
     * Clone an object into an object loaded by the supplied classloader.
     */
    public static <T> T clone(ClassLoader classloader, T objectToClone) {
        assertObjectNotNull(objectToClone);
        return performClone(classloader, ClassLoaderUtil.loadClassWithClassloader(classloader, getType(objectToClone)), objectToClone);
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
    private static <T> T performClone(ClassLoader targetCL, Class<T> targetClass, Object source) {
        Object target = null;
        if (targetClass.isArray()) {
            target = instantiateArray(targetCL, targetClass, source);
        } else if (isCollection(targetClass)) {
            target = cloneCollection(targetCL, source);
        } else if (isStandardJavaType(targetClass)) {
            target = source;
        } else if (targetClass.isEnum()) {
            target = cloneEnum(targetCL, source);
        } else {
            target = Whitebox.newInstance(targetClass);
        }

        if (!targetClass.isEnum()) {
            cloneFields(targetCL, targetClass, source, target);
        }
        return (T) target;
    }

    @SuppressWarnings("unchecked")
    private static Object cloneEnum(ClassLoader targetCL, Object source) {
        Object target;
        final Class enumClassLoadedByTargetCL = ClassLoaderUtil.loadClassWithClassloader(targetCL, source.getClass());
        target = getEnumValue(source, enumClassLoadedByTargetCL);
        return target;
    }

    private static <T> void cloneFields(ClassLoader targetCL, Class<T> targetClass, Object source, Object target) {
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
                    if (object == null || (type.getName().startsWith(IGNORED_PACKAGES) && !isCollection(object))) {
                        instantiatedValue = object;
                    } else {
                        final Class<Object> typeLoadedByCL = ClassLoaderUtil.loadClassWithClassloader(targetCL, type);
                        if (type.isEnum()) {
                            instantiatedValue = getEnumValue(object, typeLoadedByCL);
                        } else {
                            instantiatedValue = performClone(targetCL, typeLoadedByCL, object);
                        }
                    }
                    if (!field.isEnumConstant() && !isStaticFinalModifier(field)) {
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

    private static <T> boolean isStandardJavaType(Class<T> targetClass) {
        return targetClass.isPrimitive() || targetClass.getName().startsWith(IGNORED_PACKAGES);
    }

    private static boolean isStaticFinalModifier(final Field field) {
        final int modifiers = field.getModifiers();
        return (Modifier.isFinal(modifiers) && Modifier.isStatic(modifiers));
    }

    @SuppressWarnings("unchecked")
    private static Object cloneCollection(ClassLoader targetCL, Object source) {
        Object target;
        Collection sourceCollection = (Collection) source;
        final Class<Collection<?>> collectionClass = (Class<Collection<?>>) ClassLoaderUtil.loadClassWithClassloader(targetCL, source.getClass());
        Collection newInstance = null;
        try {
            newInstance = collectionClass.newInstance();
        } catch (Exception e) {
            // Should never happen for collections
            throw new RuntimeException(e);
        }
        for (Object collectionValue : sourceCollection) {
            final Class<? extends Object> typeLoadedByTargetCL = ClassLoaderUtil.loadClassWithClassloader(targetCL, collectionValue.getClass());
            newInstance.add(performClone(targetCL, typeLoadedByTargetCL, collectionValue));
        }
        target = newInstance;
        return target;
    }

    private static boolean isCollection(final Object object) {
        return isCollection(object.getClass());
    }

    private static boolean isCollection(final Class<?> cls) {
        return Collection.class.isAssignableFrom(cls);
    }

    @SuppressWarnings("unchecked")
    private static Enum getEnumValue(final Object enumValueOfSourceClassloader, final Class<Object> enumTypeLoadedByTargetCL) {
        return Enum.valueOf((Class) enumTypeLoadedByTargetCL, ((Enum) enumValueOfSourceClassloader).toString());
    }

    private static Object instantiateArray(ClassLoader targetCL, Class<?> arrayClass, Object objectToClone) {
        final int arrayLength = Array.getLength(objectToClone);
        final Object array = Array.newInstance(arrayClass.getComponentType(), arrayLength);
        for (int i = 0; i < arrayLength; i++) {
            final Object object = Array.get(objectToClone, i);
            final Object performClone = performClone(targetCL, ClassLoaderUtil.loadClassWithClassloader(targetCL, getType(object)), object);
            Array.set(array, i, performClone);
        }
        return array;
    }
}
