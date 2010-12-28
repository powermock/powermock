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
package org.powermock.classloading;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

import org.powermock.api.support.ClassLoaderUtil;
import org.powermock.api.support.SafeExceptionRethrower;
import org.powermock.classloading.spi.DeepClonerSPI;
import org.powermock.classloading.spi.DoNotClone;
import org.powermock.core.ListMap;
import org.powermock.reflect.Whitebox;

import sun.misc.Unsafe;

/**
 * The purpose of the deep cloner is to create a deep clone of an object. An
 * object can also be cloned to a different class-loader.
 * <p>
 */
public class DeepCloner implements DeepClonerSPI {

	private final ClassLoader targetCL;
	private final Map<Object, Object> referenceMap = new ListMap<Object, Object>();
	private final Class<DoNotClone> doNotClone;

	/**
	 * Clone using the supplied ClassLoader.
	 */
	public DeepCloner(ClassLoader classLoader) {
		this.targetCL = classLoader;
		doNotClone = getDoNotClone(targetCL);
	}

	/**
	 * Clone using the current ContextClassLoader.
	 */
	public DeepCloner() {
		this(Thread.currentThread().getContextClassLoader());
	}

	private Class<DoNotClone> getDoNotClone(ClassLoader targetCL) {
		return ClassLoaderUtil.loadClass(DoNotClone.class, targetCL);
	}

	/**
	 * Clones an object.
	 * 
	 * @return A deep clone of the object to clone.
	 */
	public <T> T clone(T objectToClone) {
		return clone(objectToClone, true);
	}

	/**
	 * 
	 * @param includeStandardJavaType
	 *            <code>true</code> also clones standard java types (using
	 *            simple serialization), <code>false</code> simply reference to
	 *            these objects (will be same instance).
	 * @return A deep clone of the object to clone.
	 */
	public <T> T clone(T objectToClone, boolean includeStandardJavaType) {
		assertObjectNotNull(objectToClone);
		return performClone(ClassLoaderUtil.loadClass(getType(objectToClone), targetCL), objectToClone,
				includeStandardJavaType);
	}

	@SuppressWarnings("unchecked")
	private static <T> Class<T> getType(T object) {
		if (object == null) {
			return null;
		}
		return (Class<T>) (object instanceof Class ? object : object.getClass());
	}

	private static boolean isClass(Object object) {
		if (object == null) {
			return false;
		}
		return object instanceof Class<?>;
	}

	private static void assertObjectNotNull(Object object) {
		if (object == null) {
			throw new IllegalArgumentException("Object to clone cannot be null");
		}
	}

	@SuppressWarnings("unchecked")
	private <T> T performClone(Class<T> targetClass, Object source, boolean shouldCloneStandardJavaTypes) {
		Object target = null;
		if (targetClass.isArray() && !isClass(source)) {
			return (T) instantiateArray(targetCL, targetClass, source, referenceMap, shouldCloneStandardJavaTypes);
		} else if (isJavaReflectMethod(targetClass)) {
			return (T) cloneJavaReflectMethod(source);
		} else if (targetClass.isPrimitive() || isSunClass(targetClass) || isJavaReflectClass(targetClass)) {
			return (T) source;
		} else if (isSerializableCandidate(targetClass, source)) {
			return (T) serializationClone(source);
		} else if (targetClass.isEnum()) {
			return (T) cloneEnum(targetCL, source);
		} else if (isClass(source)) {
			return (T) ClassLoaderUtil.loadClass(getType(source), targetCL);
		} else {
			target = isClass(source) ? source : Whitebox.newInstance(targetClass);
		}

		if (target != null) {
			referenceMap.put(source, target);
			cloneFields(targetCL, targetClass, source, target, referenceMap, shouldCloneStandardJavaTypes);
		}
		return (T) target;
	}

	private Object cloneJavaReflectMethod(Object source) {
		Method sourceMethod = (Method) source;
		Class<?> declaringClass = sourceMethod.getDeclaringClass();
		Class<?> targetClassLoadedWithTargetCL = ClassLoaderUtil.loadClass(declaringClass, targetCL);
		Method targetMethod = null;
		try {
			targetMethod = targetClassLoadedWithTargetCL.getDeclaredMethod(sourceMethod.getName(),
					sourceMethod.getParameterTypes());
		} catch (Exception e) {
			SafeExceptionRethrower.safeRethrow(e);
		}
		if (sourceMethod.isAccessible()) {
			targetMethod.setAccessible(true);
		}
		return targetMethod;
	}

	private boolean isJavaReflectMethod(Class<?> cls) {
		return cls.getName().equals(Method.class.getName());
	}

	private boolean isSunClass(Class<?> cls) {
		return cls.getName().startsWith("sun.");
	}

	private boolean isJavaReflectClass(Class<?> cls) {
		return cls.getName().startsWith("java.lang.reflect");
	}

	private <T> boolean isSerializableCandidate(Class<T> targetClass, Object source) {
		return isStandardJavaType(targetClass)
				&& (isSerializable(targetClass) || isImpliticlySerializable(targetClass))
				&& !Map.class.isAssignableFrom(source.getClass())
				&& !Iterable.class.isAssignableFrom(source.getClass());
	}

	private static boolean isImpliticlySerializable(Class<?> cls) {
		return cls.isPrimitive();
	}

	private static boolean isSerializable(Class<?> cls) {
		return Serializable.class.isAssignableFrom(cls);
	}

	/*
	 * Perform simple serialization
	 */
	private Object serializationClone(Object source) {
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(bos);
			oos.writeObject(source);
			oos.flush();
			ByteArrayInputStream bin = new ByteArrayInputStream(bos.toByteArray());
			ois = new ObjectInputStream(bin);
			return ois.readObject();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			close(oos);
			close(ois);
		}
	}

	private void close(Closeable closeable) {
		try {
			if (closeable != null) {
				closeable.close();
			}
		} catch (IOException e) {
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Object cloneEnum(ClassLoader targetCL, Object source) {
		Object target;
		final Class enumClassLoadedByTargetCL = ClassLoaderUtil.loadClass(getType(source), targetCL);
		target = getEnumValue(source, enumClassLoadedByTargetCL);
		return target;
	}

	@SuppressWarnings("unchecked")
	private <T> void cloneFields(ClassLoader targetCL, Class<T> targetClass, Object source, Object target,
			Map<Object, Object> referenceMap, boolean cloneStandardJavaTypes) {
		Class<?> currentTargetClass = targetClass;
		while (currentTargetClass != null) {
			for (Field field : currentTargetClass.getDeclaredFields()) {
				if (field.getAnnotation(doNotClone) != null) {
					continue;
				}
				field.setAccessible(true);
				try {
					final Field declaredField = Whitebox.getField(getType(source), field.getName());
					declaredField.setAccessible(true);
					final Object object = declaredField.get(source);
					final Object instantiatedValue;
					if (object == source) {
						instantiatedValue = target;
					} else if (referenceMap.containsKey(object)) {
						instantiatedValue = referenceMap.get(object);
					} else {
						if (object == null && !isIterable(object)) {
							instantiatedValue = object;
						} else {
							Class<Object> type = getType(object);
							if (type.getName() == "void") {
								type = Class.class.cast(Class.class);
							}
							final Class<Object> typeLoadedByCL = ClassLoaderUtil.loadClass(type, targetCL
                            );
							if (type.isEnum()) {
								instantiatedValue = getEnumValue(object, typeLoadedByCL);
							} else {
								instantiatedValue = performClone(typeLoadedByCL, object, cloneStandardJavaTypes);
							}
						}
					}

					final boolean needsUnsafeWrite = field.isEnumConstant() || isStaticFinalModifier(field);
					if (needsUnsafeWrite) {
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
		return targetClass.getName().startsWith("java.");
	}

	private static boolean isStaticFinalModifier(final Field field) {
		final int modifiers = field.getModifiers();
		return Modifier.isFinal(modifiers) && Modifier.isStatic(modifiers)
				|| field.getDeclaringClass().equals(Character.class) && field.getName().equals("MIN_RADIX");
	}

	private static boolean isIterable(final Object object) {
		return object == null ? false : isIterable(object.getClass());
	}

	private static boolean isIterable(final Class<?> cls) {
		return Iterable.class.isAssignableFrom(cls);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Enum getEnumValue(final Object enumValueOfSourceClassloader,
			final Class<Object> enumTypeLoadedByTargetCL) {
		return Enum.valueOf((Class) enumTypeLoadedByTargetCL, ((Enum) enumValueOfSourceClassloader).toString());
	}

	private Object instantiateArray(ClassLoader targetCL, Class<?> arrayClass, Object objectToClone,
			Map<Object, Object> referenceMap, boolean cloneStandardJavaTypes) {
		final int arrayLength = Array.getLength(objectToClone);
		final Object array = Array.newInstance(arrayClass.getComponentType(), arrayLength);
		for (int i = 0; i < arrayLength; i++) {
			final Object object = Array.get(objectToClone, i);
			final Object performClone;
			if (object == null) {
				performClone = null;
			} else {
				performClone = performClone(ClassLoaderUtil.loadClass(getType(object), targetCL),
						object, cloneStandardJavaTypes);
			}
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
				throw new RuntimeException("Could not set field " + object.getClass() + "." + field.getName(),
						exception);
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
						throw new RuntimeException("Could not set field " + object.getClass() + "." + field.getName()
								+ ": Unknown type " + type);
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
