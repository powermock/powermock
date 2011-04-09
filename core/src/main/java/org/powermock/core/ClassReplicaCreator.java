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

import javassist.*;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class takes care of creating a replica of a class. The class structure
 * is copied to the new class. This is useful in situations where you want to
 * create a mock for a class but it's not possible because of some restrictions
 * (such as the class being loaded by the bootstrap class-loader).
 */
public class ClassReplicaCreator {

    private static final String POWERMOCK_INSTANCE_DELEGATOR_FIELD_NAME = "powerMockInstanceDelegatorField";
    // Used to make each new replica class of a specific type unique.
    private static AtomicInteger counter = new AtomicInteger(0);

    public Class<?> createClassReplica(Class<?> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("clazz cannot be null");
        }
        ClassPool classpool = ClassPool.getDefault();
        final String originalClassName = clazz.getName();
        CtClass originalClassAsCtClass = null;
        final CtClass newClass = classpool.makeClass(generateReplicaClassName(clazz));
        try {
            originalClassAsCtClass = classpool.get(originalClassName);
            CtMethod[] declaredMethods = originalClassAsCtClass.getDeclaredMethods();
            for (CtMethod ctMethod : declaredMethods) {
                final String code = getReplicaMethodDelegationCode(clazz, ctMethod, null);
                CtNewMethod.make(ctMethod.getReturnType(), ctMethod.getName(), ctMethod.getParameterTypes(), ctMethod.getExceptionTypes(),
                        code, newClass);
            }

            return newClass.toClass(this.getClass().getClassLoader(), this.getClass().getProtectionDomain());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create a class that is a replica of type <code>T</code>. To allow for
     * partial mocking all calls to non-mocked methods will be delegated to the
     * <code>delegator</code>.
     * 
     * @param <T>
     *            The type of the replica class to be created.
     * @param delegator
     *            The delegator object that will be invoked to allow for partial
     *            mocking.
     * @return A replica class that can be used to duck-type an instance.
     */
    @SuppressWarnings("unchecked")
    public <T> Class<T> createInstanceReplica(T delegator) {
        if (delegator == null) {
            throw new IllegalArgumentException("delegator cannot be null");
        }
        final Class<T> clazz = (Class<T>) delegator.getClass();
        ClassPool classpool = ClassPool.getDefault();
        final String originalClassName = clazz.getName();
        CtClass originalClassAsCtClass = null;
        final CtClass newClass = classpool.makeClass(generateReplicaClassName(clazz));
        try {
            originalClassAsCtClass = classpool.get(originalClassName);

            copyFields(originalClassAsCtClass, newClass);
            addDelegatorField(delegator, newClass);

            CtMethod[] declaredMethods = originalClassAsCtClass.getDeclaredMethods();
            for (CtMethod ctMethod : declaredMethods) {
                @SuppressWarnings("unused")
				final String code = getReplicaMethodDelegationCode(delegator.getClass(), ctMethod, POWERMOCK_INSTANCE_DELEGATOR_FIELD_NAME);
                // CtMethod make = CtNewMethod.make(ctMethod.getReturnType(),
                // ctMethod.getName(), ctMethod.getParameterTypes(), ctMethod
                // .getExceptionTypes(), code, newClass);
                CtMethod make2 = CtNewMethod.copy(ctMethod, newClass, null);
                // make2.setBody(code);
                newClass.addMethod(make2);
            }

            CtConstructor[] declaredConstructors = originalClassAsCtClass.getDeclaredConstructors();
            for (CtConstructor ctConstructor : declaredConstructors) {
                CtConstructor copy = CtNewConstructor.copy(ctConstructor, newClass, null);
                newClass.addConstructor(copy);
            }
            return newClass.toClass(this.getClass().getClassLoader(), this.getClass().getProtectionDomain());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Add a field to the replica class that holds the instance delegator. I.e.
     * if we're creating a instance replica of <code>java.lang.Long</code> this
     * methods adds a new field of type <code>delegator.getClass()</code> to the
     * replica class.
     */
    private <T> void addDelegatorField(T delegator, final CtClass replicaClass) throws CannotCompileException {
        CtField f = CtField.make(String.format("private %s %s = null;", delegator.getClass().getName(),
                POWERMOCK_INSTANCE_DELEGATOR_FIELD_NAME), replicaClass);
        replicaClass.addField(f);
    }

    private <T> String generateReplicaClassName(final Class<T> clazz) {
        return "replica." + clazz.getName() + "$$PowerMock" + counter.getAndIncrement();
    }

    private void copyFields(CtClass originalClassAsCtClass, final CtClass newClass) throws CannotCompileException, NotFoundException {
        CtField[] declaredFields = originalClassAsCtClass.getDeclaredFields();
        CtField[] undeclaredFields = originalClassAsCtClass.getFields();
        Set<CtField> allFields = new HashSet<CtField>();
        for (CtField ctField : declaredFields) {
            allFields.add(ctField);
        }
        for (CtField ctField : undeclaredFields) {
            allFields.add(ctField);
        }

        for (CtField ctField : allFields) {
            CtField f = new CtField(ctField.getType(), ctField.getName(), newClass);
            newClass.addField(f);
        }
    }

    /*
     * Invokes a instance method of the original instance. This enables partial
     * mocking of system classes.
     */
    private String getReplicaMethodDelegationCode(Class<?> clazz, CtMethod ctMethod, String classOrInstanceToDelegateTo)
            throws NotFoundException {
        StringBuilder builder = new StringBuilder();
        builder.append("{java.lang.reflect.Method originalMethod = ");
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
        final boolean isVoid = returnType.equals(CtClass.voidType);
        if (!isVoid) {
            builder.append("return (");
            builder.append(returnType.getName());
            builder.append(") ");
        }
        builder.append("originalMethod.invoke(");
        if (Modifier.isStatic(ctMethod.getModifiers()) || classOrInstanceToDelegateTo == null) {
            builder.append(clazz.getName());
            builder.append(".class");
        } else {
            builder.append(classOrInstanceToDelegateTo);
        }
        builder.append(", $args);}");
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
        if (types != null && types.length == 0) {
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
