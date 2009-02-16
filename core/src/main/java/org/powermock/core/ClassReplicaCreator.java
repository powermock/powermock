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
package org.powermock.core;

import java.util.concurrent.atomic.AtomicInteger;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;

/**
 * This class takes care of creating a replica of a class. The class structure
 * is copied to the new class. This is useful in situations where you want to
 * create a mock for a class but it's not possible because of some restrictions
 * (such as the class being loaded by the bootstrap class-loader).
 */
public class ClassReplicaCreator {

	// Used to make each new replica class of a specific type unique.
	private static AtomicInteger counter = new AtomicInteger(0);

	public Class<?> createClassReplica(Class<?> clazz) {
		if (clazz == null) {
			throw new IllegalArgumentException("clazz cannot be null");
		}
		ClassPool classpool = ClassPool.getDefault();
		final String originalClassName = clazz.getName();
		CtClass originalClassAsCtClass = null;
		final String replicaClassName = "replica." + clazz.getName() + "$$PowerMock" + counter.getAndIncrement();
		final CtClass newClass = classpool.makeClass(replicaClassName);
		try {
			originalClassAsCtClass = classpool.get(originalClassName);
			CtMethod[] declaredMethods = originalClassAsCtClass.getDeclaredMethods();
			for (CtMethod ctMethod : declaredMethods) {
				final String code = getInvocationCode(clazz, ctMethod);
				CtNewMethod.make(ctMethod.getReturnType(), ctMethod.getName(), ctMethod.getParameterTypes(), ctMethod
						.getExceptionTypes(), code, newClass);
			}

			return newClass.toClass(this.getClass().getClassLoader(), this.getClass().getProtectionDomain());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// Invokes the method of the original class. This enables partial mocking of
	// system classes.
	private String getInvocationCode(Class<?> clazz, CtMethod ctMethod) throws NotFoundException {
		StringBuilder builder = new StringBuilder();
		builder.append("{");
		builder.append("java.lang.reflect.Method originalMethod = ");
		builder.append(clazz.getName());
		builder.append(".class.getDeclaredMethod(\"");
		builder.append(ctMethod.getName());
		builder.append("\", ");
		final String parametersAsString = getParametersAsString(getParameterTypes(ctMethod));
		if ("".equals(parametersAsString)) {
			builder.append("null");
		} else {
			builder.append(parametersAsString);
		}
		builder.append(");\n");
		builder.append("originalMethod.setAccessible(true);\n");
		final CtClass returnType = ctMethod.getReturnType();
		if (!returnType.equals(CtClass.voidType)) {
			builder.append("return ");
		}
		builder.append("originalMethod.invoke(");
		builder.append(clazz.getName());
		builder.append(".class, $args);");
		builder.append("}");
		return builder.toString();
	}

	private String[] getParameterTypes(CtMethod ctMethod) throws NotFoundException {
		final CtClass[] parameterTypesAsCtClass = ctMethod.getParameterTypes();
		final String[] parameterTypes = new String[parameterTypesAsCtClass.length];
		for (int i = 0; i < parameterTypes.length; i++) {
			parameterTypes[i] = parameterTypesAsCtClass[i].getName() + ".class";
		}
		return parameterTypes;
	}

	private static String getParametersAsString(String[] types) {
		StringBuilder parametersAsString = new StringBuilder();
		if (types.length == 0) {
			parametersAsString.append("new Class[0]");
		} else {
			parametersAsString.append("new Class[] {");
			if (types != null && types.length != 0) {
				for (int i = 0; i < types.length; i++) {
					parametersAsString.append(types[i]);
					if (i != types.length - 1) {
						parametersAsString.append(", ");
					}
				}
			}
			parametersAsString.append("}");
		}
		return parametersAsString.toString();
	}
}
