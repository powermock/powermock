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

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import org.powermock.reflect.internal.TypeUtils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class takes care of creating a concrete sub-class implementing all
 * abstract methods in the parent.
 */
public class ConcreteClassGenerator {

	// Used to make each new subclass of a specific type unique.
	private static AtomicInteger counter = new AtomicInteger(0);

	public Class<?> createConcreteSubClass(Class<?> clazz) {
		if (clazz == null) {
			throw new IllegalArgumentException("clazz cannot be null");
		}
		if (!java.lang.reflect.Modifier.isAbstract(clazz.getModifiers())) {
			throw new IllegalArgumentException("clazz must be abstract");
		}
		ClassPool classpool = ClassPool.getDefault();
		final String originalClassName = clazz.getName();
		final CtClass originalClassAsCtClass;
		final CtClass newClass = classpool.makeClass(generateClassName(clazz));
		try {
			newClass.setSuperclass(classpool.get(clazz.getName()));
		} catch (Exception e1) {
			throw new RuntimeException(e1);
		}
		try {
			originalClassAsCtClass = classpool.get(originalClassName);
			CtMethod[] declaredMethods = originalClassAsCtClass.getDeclaredMethods();
			for (CtMethod ctMethod : declaredMethods) {
				if (Modifier.isAbstract(ctMethod.getModifiers())) {
					final String code = getReturnCode(ctMethod.getReturnType());
					CtNewMethod.make(ctMethod.getReturnType(), ctMethod.getName(), ctMethod.getParameterTypes(),
							ctMethod.getExceptionTypes(), code, newClass);
				}
			}
			if (!hasInheritableConstructor(originalClassAsCtClass)) {
				return null;
			}
			return newClass.toClass(this.getClass().getClassLoader(), this.getClass().getProtectionDomain());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private boolean hasInheritableConstructor(CtClass cls) throws NotFoundException {
		CtConstructor[] constructors = cls.getDeclaredConstructors();
		if (constructors.length == 0) {
			return true;
		}
		for (CtConstructor ctConstructor : constructors) {
			int modifiers = ctConstructor.getModifiers();
			if (!Modifier.isPackage(modifiers) && !Modifier.isPrivate(modifiers)) {
				return true;
			}
		}
		return false;

	}

	private String getReturnCode(CtClass returnType) {
		if (returnType.equals(CtClass.voidType)) {
			return "{}";
		}
		return "{return " + TypeUtils.getDefaultValueAsString(returnType.getName()) + ";}";
	}

	private <T> String generateClassName(final Class<T> clazz) {
		return "subclass." + clazz.getName() + "$$PowerMock" + counter.getAndIncrement();
	}
}
