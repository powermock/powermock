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

import org.powermock.core.ListMap;
import org.powermock.reflect.Whitebox;

import sun.misc.Unsafe;

/**
 * The purpose of the deep cloner is to create a deep clone of an object. An
 * object can also be cloned to a different class-loader.
 * <p>
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
		return (T) performClone(objectType.getClassLoader(), objectType, objectToClone, new ListMap<Object, Object>());
	}

	/**
	 * Clone an object into an object loaded by the supplied classloader.
	 */
	public static <T> T clone(ClassLoader classloader, T objectToClone) {
		assertObjectNotNull(objectToClone);
		return performClone(classloader, ClassLoaderUtil.loadClassWithClassloader(classloader, getType(objectToClone)), objectToClone,
				new ListMap<Object, Object>());
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
	private static <T> T performClone(ClassLoader targetCL, Class<T> targetClass, Object source, ListMap<Object, Object> referenceMap) {
		Object target = null;
		if (targetClass.isArray()) {
			target = instantiateArray(targetCL, targetClass, source, referenceMap);
		} else if (isCollection(targetClass)) {
			target = cloneCollection(targetCL, source, referenceMap);
		} else if (isStandardJavaType(targetClass)) {
			target = source;
		} else if (targetClass.isEnum()) {
			target = cloneEnum(targetCL, source);
		} else {
			target = Whitebox.newInstance(targetClass);
		}

		if (!targetClass.isEnum()) {
			cloneFields(targetCL, targetClass, source, target, referenceMap);
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

	private static <T> void cloneFields(ClassLoader targetCL, Class<T> targetClass, Object source, Object target, ListMap<Object, Object> referenceMap) {
		Class<?> currentTargetClass = targetClass;
		while (currentTargetClass != null && !currentTargetClass.getName().startsWith(IGNORED_PACKAGES)) {
			for (Field field : currentTargetClass.getDeclaredFields()) {
				field.setAccessible(true);
				try {
					final Field declaredField = source.getClass().getDeclaredField(field.getName());
					declaredField.setAccessible(true);
					final Object object = declaredField.get(source);
					final Object instantiatedValue;
					if (referenceMap.containsKey(object)) {
						instantiatedValue = referenceMap.get(object);
					} else {
						final Class<Object> type = getType(object);
						if (object == null || (type.getName().startsWith(IGNORED_PACKAGES) && !isCollection(object))) {
							instantiatedValue = object;
						} else {
							final Class<Object> typeLoadedByCL = ClassLoaderUtil.loadClassWithClassloader(targetCL, type);
							if (type.isEnum()) {
								instantiatedValue = getEnumValue(object, typeLoadedByCL);
							} else {
								instantiatedValue = performClone(targetCL, typeLoadedByCL, object, referenceMap);
							}
						}
						referenceMap.put(object, instantiatedValue);
					}

					if (field.isEnumConstant() || isStaticFinalModifier(field)) {
						UnsafeFieldWriter.write(field, target, instantiatedValue);
					} else {
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
	private static Object cloneCollection(ClassLoader targetCL, Object source, ListMap<Object, Object> referenceMap) {
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
			newInstance.add(performClone(targetCL, typeLoadedByTargetCL, collectionValue, referenceMap));
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

	private static Object instantiateArray(ClassLoader targetCL, Class<?> arrayClass, Object objectToClone, ListMap<Object, Object> referenceMap) {
		final int arrayLength = Array.getLength(objectToClone);
		final Object array = Array.newInstance(arrayClass.getComponentType(), arrayLength);
		for (int i = 0; i < arrayLength; i++) {
			final Object object = Array.get(objectToClone, i);
			final Object performClone = performClone(targetCL, ClassLoaderUtil.loadClassWithClassloader(targetCL, getType(object)), object,
					referenceMap);
			Array.set(array, i, performClone);
		}
		return array;
	}

	/**
	 * Most of this code has been copied from the Sun14ReflectionProvider in the
	 * XStream project. Some changes has been made, namely if the field is
	 * static final then the {@link Unsafe#staticFieldOffset(Field)} method is
	 * used instead of {@link Unsafe#objectFieldOffset(Field)}.
	 * 
	 * @author Joe Walnes
	 * @author Brian Slesinsky
	 * @author Johan Haleby
	 */
	private static class UnsafeFieldWriter {
		private final static Unsafe unsafe;
		private final static Exception exception;
		static {
			Unsafe u = null;
			Exception ex = null;
			try {
				Class<?> objectStreamClass = Class.forName("sun.misc.Unsafe");
				Field unsafeField = objectStreamClass.getDeclaredField("theUnsafe");
				unsafeField.setAccessible(true);
				u = (Unsafe) unsafeField.get(null);
			} catch (Exception e) {
				ex = e;
			}
			exception = ex;
			unsafe = u;
		}

		public static void write(Field field, Object object, Object value) {
			if (exception != null) {
				throw new RuntimeException("Could not set field " + object.getClass() + "." + field.getName(), exception);
			}
			try {
				final long offset;
				if (DeepCloner.isStaticFinalModifier(field)) {
					offset = unsafe.staticFieldOffset(field);
				} else {
					offset = unsafe.objectFieldOffset(field);
				}
				Class<?> type = field.getType();
				if (type.isPrimitive()) {
					if (type.equals(Integer.TYPE)) {
						unsafe.putInt(object, offset, ((Integer) value).intValue());
					} else if (type.equals(Long.TYPE)) {
						unsafe.putLong(object, offset, ((Long) value).longValue());
					} else if (type.equals(Short.TYPE)) {
						unsafe.putShort(object, offset, ((Short) value).shortValue());
					} else if (type.equals(Character.TYPE)) {
						unsafe.putChar(object, offset, ((Character) value).charValue());
					} else if (type.equals(Byte.TYPE)) {
						unsafe.putByte(object, offset, ((Byte) value).byteValue());
					} else if (type.equals(Float.TYPE)) {
						unsafe.putFloat(object, offset, ((Float) value).floatValue());
					} else if (type.equals(Double.TYPE)) {
						unsafe.putDouble(object, offset, ((Double) value).doubleValue());
					} else if (type.equals(Boolean.TYPE)) {
						unsafe.putBoolean(object, offset, ((Boolean) value).booleanValue());
					} else {
						throw new RuntimeException("Could not set field " + object.getClass() + "." + field.getName() + ": Unknown type " + type);
					}
				} else {
					unsafe.putObject(object, offset, value);
				}
			} catch (IllegalArgumentException e) {
				throw new RuntimeException("Could not set field " + object.getClass() + "." + field.getName(), e);
			}
		}
	}
}
