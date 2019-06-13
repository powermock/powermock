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
package org.powermock.reflect.internal;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.objenesis.instantiator.ObjectInstantiator;
import org.powermock.reflect.exceptions.ConstructorNotFoundException;
import org.powermock.reflect.exceptions.FieldNotFoundException;
import org.powermock.reflect.exceptions.MethodInvocationException;
import org.powermock.reflect.exceptions.MethodNotFoundException;
import org.powermock.reflect.exceptions.TooManyConstructorsFoundException;
import org.powermock.reflect.exceptions.TooManyFieldsFoundException;
import org.powermock.reflect.exceptions.TooManyMethodsFoundException;
import org.powermock.reflect.internal.comparator.ComparatorFactory;
import org.powermock.reflect.internal.matcherstrategies.AllFieldsMatcherStrategy;
import org.powermock.reflect.internal.matcherstrategies.AssignableFromFieldTypeMatcherStrategy;
import org.powermock.reflect.internal.matcherstrategies.AssignableToFieldTypeMatcherStrategy;
import org.powermock.reflect.internal.matcherstrategies.FieldAnnotationMatcherStrategy;
import org.powermock.reflect.internal.matcherstrategies.FieldMatcherStrategy;
import org.powermock.reflect.internal.matcherstrategies.FieldNameMatcherStrategy;
import org.powermock.reflect.internal.primitivesupport.BoxedWrapper;
import org.powermock.reflect.internal.primitivesupport.PrimitiveWrapper;
import org.powermock.reflect.internal.proxy.ProxyFrameworks;
import org.powermock.reflect.internal.proxy.UnproxiedType;
import org.powermock.reflect.matching.FieldMatchingStrategy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Various utilities for accessing internals of a class. Basically a simplified
 * reflection utility intended for tests.
 */
public class WhiteboxImpl {

    /**
     * The proxy framework.
     */
    private static ProxyFrameworks proxyFrameworks = new ProxyFrameworks();

    /**
     * "Strong" map prevent class and method objects from being GCed and unloaded.
     * TODO replace with ClassValue when Powermock drops Java 6 support.
     */
    private static ConcurrentMap<Class, Method[]> allClassMethodsCache = new ConcurrentHashMap<Class, Method[]>();

    /**
     * Convenience method to get a method from a class type without having to
     * catch the checked exceptions otherwise required. These exceptions are
     * wrapped as runtime exceptions.
     * 
     * The method will first try to look for a declared method in the same
     * class. If the method is not declared in this class it will look for the
     * method in the super class. This will continue throughout the whole class
     * hierarchy. If the method is not found an {@link MethodNotFoundException}
     * is thrown. Since the method name is not specified an
     *
     * @param type           The type of the class where the method is located.
     * @param parameterTypes All parameter types of the method (may be {@code null}).
     * @return A . {@link TooManyMethodsFoundException} is thrown if two or more
     * methods matches the same parameter types in the same class.
     */
    public static Method getMethod(Class<?> type, Class<?>... parameterTypes) {
        Class<?> thisType = type;
        if (parameterTypes == null) {
            parameterTypes = new Class<?>[0];
        }

        List<Method> foundMethods = new LinkedList<Method>();
        while (thisType != null) {
            Method[] methodsToTraverse = null;
            if (thisType.isInterface()) {
                // Interfaces only contain public (and abstract) methods, no
                // need to traverse the hierarchy.
                methodsToTraverse = getAllPublicMethods(thisType);
            } else {
                methodsToTraverse = thisType.getDeclaredMethods();
            }
            for (Method method : methodsToTraverse) {
                if (checkIfParameterTypesAreSame(method.isVarArgs(), parameterTypes, method.getParameterTypes())) {
                    foundMethods.add(method);
                    if (foundMethods.size() == 1) {
                        method.setAccessible(true);
                    }
                }

            }
            if (foundMethods.size() == 1) {
                return foundMethods.get(0);
            } else if (foundMethods.size() > 1) {
                break;
            }
            thisType = thisType.getSuperclass();
        }

        if (foundMethods.isEmpty()) {
            throw new MethodNotFoundException("No method was found with parameter types: [ "
                                                      + getArgumentTypesAsString((Object[]) parameterTypes) + " ] in class "
                                                      + getOriginalUnmockedType(type).getName() + ".");
        } else {
            throwExceptionWhenMultipleMethodMatchesFound("method name",
                    foundMethods.toArray(new Method[foundMethods.size()]));
        }
        // Will never happen
        return null;
    }

    /**
     * Convenience method to get a method from a class type without having to
     * catch the checked exceptions otherwise required. These exceptions are
     * wrapped as runtime exceptions.
     * 
     * The method will first try to look for a declared method in the same
     * class. If the method is not declared in this class it will look for the
     * method in the super class. This will continue throughout the whole class
     * hierarchy. If the method is not found an {@link IllegalArgumentException}
     * is thrown.
     *
     * @param type           The type of the class where the method is located.
     * @param methodName     The method names.
     * @param parameterTypes All parameter types of the method (may be {@code null}).
     * @return A .
     */
    public static Method getMethod(Class<?> type, String methodName, Class<?>... parameterTypes) {
        Class<?> thisType = type;
        if (parameterTypes == null) {
            parameterTypes = new Class<?>[0];
        }
        while (thisType != null) {
            Method[] methodsToTraverse = null;
            if (thisType.isInterface()) {
                // Interfaces only contain public (and abstract) methods, no
                // need to traverse the hierarchy.
                methodsToTraverse = getAllPublicMethods(thisType);
            } else {
                methodsToTraverse = thisType.getDeclaredMethods();
            }
            for (Method method : methodsToTraverse) {
                if (methodName.equals(method.getName())
                            && checkIfParameterTypesAreSame(method.isVarArgs(), parameterTypes, method.getParameterTypes())) {
                    method.setAccessible(true);
                    return method;
                }
            }
            thisType = thisType.getSuperclass();
        }

        throwExceptionIfMethodWasNotFound(type, methodName, null, new Object[]{parameterTypes});
        return null;
    }

    /**
     * Convenience method to get a field from a class type.
     * 
     * The method will first try to look for a declared field in the same class.
     * If the method is not declared in this class it will look for the field in
     * the super class. This will continue throughout the whole class hierarchy.
     * If the field is not found an {@link IllegalArgumentException} is thrown.
     *
     * @param type      The type of the class where the method is located.
     * @param fieldName The method names.
     * @return A .
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Field getField(Class<?> type, String fieldName) {
        LinkedList<Class<?>> examine = new LinkedList<Class<?>>();
        examine.add(type);
        Set<Class<?>> done = new HashSet<Class<?>>();
        while (!examine.isEmpty()) {
            Class<?> thisType = examine.removeFirst();
            done.add(thisType);
            final Field[] declaredField = thisType.getDeclaredFields();
            for (Field field : declaredField) {
                if (fieldName.equals(field.getName())) {
                    field.setAccessible(true);
                    return field;
                }
            }
            Set<Class<?>> potential = new HashSet<Class<?>>();
            final Class<?> clazz = thisType.getSuperclass();
            if (clazz != null) {
                potential.add(thisType.getSuperclass());
            }
            potential.addAll((Collection) Arrays.asList(thisType.getInterfaces()));
            potential.removeAll(done);
            examine.addAll(potential);
        }

        throwExceptionIfFieldWasNotFound(type, fieldName, null);
        return null;
    }

    /**
     * Create a new instance of a class without invoking its constructor.
     * 
     * No byte-code manipulation is needed to perform this operation and thus
     * it's not necessary use the {@code PowerMockRunner} or
     * {@code PrepareForTest} annotation to use this functionality.
     *
     * @param <T>                The type of the instance to create.
     * @param classToInstantiate The type of the instance to create.
     * @return A new instance of type T, created without invoking the
     * constructor.
     */
    @SuppressWarnings("unchecked")
    public static <T> T newInstance(Class<T> classToInstantiate) {
        int modifiers = classToInstantiate.getModifiers();

        final Object object;
        if (Modifier.isInterface(modifiers)) {
            object = Proxy.newProxyInstance(WhiteboxImpl.class.getClassLoader(), new Class<?>[]{classToInstantiate},
                    new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            return TypeUtils.getDefaultValue(method.getReturnType());
                        }
                    });
        } else if (classToInstantiate.isArray()) {
            object = Array.newInstance(classToInstantiate.getComponentType(), 0);
        } else if (Modifier.isAbstract(modifiers)) {
            throw new IllegalArgumentException(
                                                      "Cannot instantiate an abstract class. Please use the ConcreteClassGenerator in PowerMock support to generate a concrete class first.");
        } else {
            Objenesis objenesis = new ObjenesisStd();
            ObjectInstantiator thingyInstantiator = objenesis.getInstantiatorOf(classToInstantiate);
            object = thingyInstantiator.newInstance();
        }
        return (T) object;
    }

    /**
     * Convenience method to get a (declared) constructor from a class type
     * without having to catch the checked exceptions otherwise required. These
     * exceptions are wrapped as runtime exceptions. The constructor is also set
     * to accessible.
     *
     * @param type           The type of the class where the constructor is located.
     * @param parameterTypes All parameter types of the constructor (may be
     *                       {@code null}).
     * @return A .
     */
    public static Constructor<?> getConstructor(Class<?> type, Class<?>... parameterTypes) {
        Class<?> unmockedType = WhiteboxImpl.getOriginalUnmockedType(type);
        try {
            final Constructor<?> constructor = unmockedType.getDeclaredConstructor(parameterTypes);
            constructor.setAccessible(true);
            return constructor;
        } catch (RuntimeException e) {
            throw e;
        } catch (Error e) {
            throw e;
        } catch (Throwable e) {
            throw new ConstructorNotFoundException(String.format(
                    "Failed to lookup constructor with parameter types [ %s ] in class %s.",
                    getArgumentTypesAsString((Object[]) parameterTypes), unmockedType.getName()), e);
        }
    }

    /**
     * Set the value of a field using reflection. This method will traverse the
     * super class hierarchy until a field with name <tt>fieldName</tt> is
     * found.
     *
     * @param object    the object whose field to modify
     * @param fieldName the name of the field
     * @param value     the new value of the field
     */
    public static void setInternalState(Object object, String fieldName, Object value) {
        Field foundField = findFieldInHierarchy(object, fieldName);
        setField(object, value, foundField);
    }

    /**
     * Set the value of a field using reflection. This method will traverse the
     * super class hierarchy until a field with name <tt>fieldName</tt> is
     * found.
     *
     * @param object    the object to modify
     * @param fieldName the name of the field
     * @param value     the new value of the field
     */
    public static void setInternalState(Object object, String fieldName, Object[] value) {
        setInternalState(object, fieldName, (Object) value);
    }

    /**
     * Set the value of a field using reflection. This method will traverse the
     * super class hierarchy until the first field of type <tt>fieldType</tt> is
     * found. The <tt>value</tt> will then be assigned to this field.
     *
     * @param object    the object to modify
     * @param fieldType the type of the field
     * @param value     the new value of the field
     */
    public static void setInternalState(Object object, Class<?> fieldType, Object value) {
        setField(object, value, findFieldInHierarchy(object, new AssignableFromFieldTypeMatcherStrategy(fieldType)));
    }

    /**
     * Set the value of a field using reflection. This method will traverse the
     * super class hierarchy until the first field assignable to the
     * <tt>value</tt> type is found. The <tt>value</tt> (or
     * <tt>additionalValues</tt> if present) will then be assigned to this field.
     *
     * @param object           the object to modify
     * @param value            the new value of the field
     * @param additionalValues Additional values to set on the object
     */
    public static void setInternalState(Object object, Object value, Object... additionalValues) {
        setField(object, value,
                findFieldInHierarchy(object, new AssignableFromFieldTypeMatcherStrategy(getType(value))));
        if (additionalValues != null && additionalValues.length > 0) {
            for (Object additionalValue : additionalValues) {
                setField(
                        object,
                        additionalValue,
                        findFieldInHierarchy(object, new AssignableFromFieldTypeMatcherStrategy(
                                                                                                       getType(additionalValue))));
            }
        }
    }

    /**
     * Set the value of a field using reflection at at specific place in the
     * class hierarchy (<tt>where</tt>). This first field assignable to
     * <tt>object</tt> will then be set to <tt>value</tt>.
     *
     * @param object the object to modify
     * @param value  the new value of the field
     * @param where  the class in the hierarchy where the field is defined
     */
    public static void setInternalState(Object object, Object value, Class<?> where) {
        setField(object, value, findField(object, new AssignableFromFieldTypeMatcherStrategy(getType(value)), where));
    }

    /**
     * Set the value of a field using reflection at a specific location (
     * <tt>where</tt>) in the class hierarchy. The <tt>value</tt> will then be
     * assigned to this field.
     *
     * @param object    the object to modify
     * @param fieldType the type of the field the should be set.
     * @param value     the new value of the field
     * @param where     which class in the hierarchy defining the field
     */
    public static void setInternalState(Object object, Class<?> fieldType, Object value, Class<?> where) {
        if (fieldType == null || where == null) {
            throw new IllegalArgumentException("fieldType and where cannot be null");
        }

        setField(object, value, findFieldOrThrowException(fieldType, where));
    }

    /**
     * Set the value of a field using reflection. Use this method when you need
     * to specify in which class the field is declared. This is useful if you
     * have two fields in a class hierarchy that has the same name but you like
     * to modify the latter.
     *
     * @param object    the object to modify
     * @param fieldName the name of the field
     * @param value     the new value of the field
     * @param where     which class the field is defined
     */
    public static void setInternalState(Object object, String fieldName, Object value, Class<?> where) {
        if (object == null || fieldName == null || fieldName.equals("") || fieldName.startsWith(" ")) {
            throw new IllegalArgumentException("object, field name, and \"where\" must not be empty or null.");
        }

        final Field field = getField(fieldName, where);
        try {
            field.set(object, value);
        } catch (Exception e) {
            throw new RuntimeException("Internal Error: Failed to set field in method setInternalState.", e);
        }
    }

    /**
     * Get the value of a field using reflection. This method will iterate
     * through the entire class hierarchy and return the value of the first
     * field named <tt>fieldName</tt>. If you want to get a specific field value
     * at specific place in the class hierarchy please refer to
     *
     * @param <T>       the generic type
     * @param object    the object to modify
     * @param fieldName the name of the field
     * @return the internal state
     * {@link #getInternalState(Object, String, Class)}.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getInternalState(Object object, String fieldName) {
        Field foundField = findFieldInHierarchy(object, fieldName);
        try {
            return (T) foundField.get(object);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Internal error: Failed to get field in method getInternalState.", e);
        }
    }

    /**
     * Find field in hierarchy.
     *
     * @param object    the object
     * @param fieldName the field name
     * @return the field
     */
    private static Field findFieldInHierarchy(Object object, String fieldName) {
        return findFieldInHierarchy(object, new FieldNameMatcherStrategy(fieldName));
    }

    /**
     * Find field in hierarchy.
     *
     * @param object   the object
     * @param strategy the strategy
     * @return the field
     */
    private static Field findFieldInHierarchy(Object object, FieldMatcherStrategy strategy) {
        assertObjectInGetInternalStateIsNotNull(object);
        return findSingleFieldUsingStrategy(strategy, object, true, getType(object));
    }

    /**
     * Find field.
     *
     * @param object   the object
     * @param strategy the strategy
     * @param where    the where
     * @return the field
     */
    private static Field findField(Object object, FieldMatcherStrategy strategy, Class<?> where) {
        return findSingleFieldUsingStrategy(strategy, object, false, where);
    }

    /**
     * Find single field using strategy.
     *
     * @param strategy       the strategy
     * @param object         the object
     * @param checkHierarchy the check hierarchy
     * @param startClass     the start class
     * @return the field
     */
    private static Field findSingleFieldUsingStrategy(FieldMatcherStrategy strategy, Object object,
                                                      boolean checkHierarchy, Class<?> startClass) {
        assertObjectInGetInternalStateIsNotNull(object);
        Field foundField = null;
        final Class<?> originalStartClass = startClass;
        while (startClass != null) {
            final Field[] declaredFields = startClass.getDeclaredFields();
            for (Field field : declaredFields) {
                if (strategy.matches(field) && hasFieldProperModifier(object, field)) {
                    if (foundField != null) {
                        throw new TooManyFieldsFoundException("Two or more fields matching " + strategy + ".");
                    }
                    foundField = field;
                }
            }
            if (foundField != null) {
                break;
            } else if (!checkHierarchy) {
                break;
            }
            startClass = startClass.getSuperclass();
        }
        if (foundField == null) {
            strategy.notFound(originalStartClass, !isClass(object));
        }
        foundField.setAccessible(true);
        return foundField;
    }

    /**
     * Find all fields using strategy.
     *
     * @param strategy       the strategy
     * @param object         the object
     * @param checkHierarchy the check hierarchy
     * @param startClass     the start class
     * @return the set
     */
    private static Set<Field> findAllFieldsUsingStrategy(FieldMatcherStrategy strategy, Object object,
                                                         boolean checkHierarchy, Class<?> startClass) {
        assertObjectInGetInternalStateIsNotNull(object);
        final Set<Field> foundFields = new LinkedHashSet<Field>();
        while (startClass != null) {
            final Field[] declaredFields = startClass.getDeclaredFields();
            for (Field field : declaredFields) {
                if (strategy.matches(field) && hasFieldProperModifier(object, field)) {
                    // TODO replace by the class 
                    try {
                        field.setAccessible(true);
                        foundFields.add(field);
                    } catch (Exception ignored) {
                        // the InaccessibleObjectException is thrown in Java 9 in case
                        // if a field is private and a module is not open
                    }
                }
            }
            if (!checkHierarchy) {
                break;
            }
            startClass = startClass.getSuperclass();
        }

        return Collections.unmodifiableSet(foundFields);
    }

    /**
     * Checks for field proper modifier.
     *
     * @param object the object
     * @param field  the field
     * @return true, if successful
     */
    private static boolean hasFieldProperModifier(Object object, Field field) {
        return ((object instanceof Class<?> && Modifier.isStatic(field.getModifiers())) || ((object instanceof Class<?> == false && Modifier
                                                                                                                                            .isStatic(field.getModifiers()) == false)));
    }

    /**
     * Get the value of a field using reflection. This method will traverse the
     * super class hierarchy until the first field of type <tt>fieldType</tt> is
     * found. The value of this field will be returned.
     *
     * @param <T>       the generic type
     * @param object    the object to modify
     * @param fieldType the type of the field
     * @return the internal state
     */
    @SuppressWarnings("unchecked")
    public static <T> T getInternalState(Object object, Class<T> fieldType) {
        Field foundField = findFieldInHierarchy(object, new AssignableToFieldTypeMatcherStrategy(fieldType));
        try {
            return (T) foundField.get(object);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Internal error: Failed to get field in method getInternalState.", e);
        }
    }

    /**
     * Get the value of a field using reflection. Use this method when you need
     * to specify in which class the field is declared. The first field matching
     * the <tt>fieldType</tt> in <tt>where</tt> will is the field whose value
     * will be returned.
     *
     * @param <T>       the expected type of the field
     * @param object    the object to modify
     * @param fieldType the type of the field
     * @param where     which class the field is defined
     * @return the internal state
     */
    @SuppressWarnings("unchecked")
    public static <T> T getInternalState(Object object, Class<T> fieldType, Class<?> where) {
        if (object == null) {
            throw new IllegalArgumentException("object and type are not allowed to be null");
        }

        try {
            return (T) findFieldOrThrowException(fieldType, where).get(object);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Internal error: Failed to get field in method getInternalState.", e);
        }
    }

    /**
     * Get the value of a field using reflection. Use this method when you need
     * to specify in which class the field is declared. This might be useful
     * when you have mocked the instance you are trying to access. Use this
     * method to avoid casting.
     *
     * @param <T>       the expected type of the field
     * @param object    the object to modify
     * @param fieldName the name of the field
     * @param where     which class the field is defined
     * @return the internal state
     */
    @SuppressWarnings("unchecked")
    public static <T> T getInternalState(Object object, String fieldName, Class<?> where) {
        if (object == null || fieldName == null || fieldName.equals("") || fieldName.startsWith(" ")) {
            throw new IllegalArgumentException("object, field name, and \"where\" must not be empty or null.");
        }

        Field field = null;
        try {
            field = where.getDeclaredField(fieldName);
            field.setAccessible(true);
            return (T) field.get(object);
        } catch (NoSuchFieldException e) {
            throw new FieldNotFoundException("Field '" + fieldName + "' was not found in class " + where.getName()
                                                     + ".");
        } catch (Exception e) {
            throw new RuntimeException("Internal error: Failed to get field in method getInternalState.", e);
        }
    }

    /**
     * Invoke a private or inner class method without the need to specify the
     * method name. This is thus a more refactor friendly version of the
     *
     * @param <T>       the generic type
     * @param tested    the tested
     * @param arguments the arguments
     * @return the t
     * @throws Exception the exception
     *                   {@link #invokeMethod(Object, String, Object...)} method and
     *                   is recommend over this method for that reason. This method
     *                   might be useful to test private methods.
     */
    @SuppressWarnings("unchecked")
    public static synchronized <T> T invokeMethod(Object tested, Object... arguments) throws Exception {
        return (T) doInvokeMethod(tested, null, null, arguments);
    }

    /**
     * Invoke a private or inner class method without the need to specify the
     * method name. This is thus a more refactor friendly version of the
     *
     * @param <T>       the generic type
     * @param tested    the tested
     * @param arguments the arguments
     * @return the t
     * @throws Exception the exception
     *                   {@link #invokeMethod(Object, String, Object...)} method and
     *                   is recommend over this method for that reason. This method
     *                   might be useful to test private methods.
     */
    @SuppressWarnings("unchecked")
    public static synchronized <T> T invokeMethod(Class<?> tested, Object... arguments) throws Exception {
        return (T) doInvokeMethod(tested, null, null, arguments);
    }

    /**
     * Invoke a private or inner class method. This might be useful to test
     * private methods.
     *
     * @param <T>             the generic type
     * @param tested          the tested
     * @param methodToExecute the method to execute
     * @param arguments       the arguments
     * @return the t
     * @throws Exception the exception
     */
    @SuppressWarnings("unchecked")
    public static synchronized <T> T invokeMethod(Object tested, String methodToExecute, Object... arguments)
            throws Exception {
        return (T) doInvokeMethod(tested, null, methodToExecute, arguments);
    }

    /**
     * Invoke a private or inner class method in cases where power mock cannot
     * automatically determine the type of the parameters, for example when
     * mixing primitive types and wrapper types in the same method. For most
     * situations use {@link #invokeMethod(Class, String, Object...)} instead.
     *
     * @param <T>             the generic type
     * @param tested          the tested
     * @param methodToExecute the method to execute
     * @param argumentTypes   the argument types
     * @param arguments       the arguments
     * @return the t
     * @throws Exception Exception that may occur when invoking this method.
     */
    @SuppressWarnings("unchecked")
    public static synchronized <T> T invokeMethod(Object tested, String methodToExecute, Class<?>[] argumentTypes,
                                                  Object... arguments) throws Exception {
        final Class<?> unmockedType = getType(tested);
        Method method = getMethod(unmockedType, methodToExecute, argumentTypes);
        if (method == null) {
            throwExceptionIfMethodWasNotFound(unmockedType, methodToExecute, null, arguments);
        }
        return (T) performMethodInvocation(tested, method, arguments);
    }

    /**
     * Invoke a private or inner class method in a subclass (defined by
     * {@code definedIn}) in cases where power mock cannot automatically
     * determine the type of the parameters, for example when mixing primitive
     * types and wrapper types in the same method. For most situations use
     *
     * @param <T>             the generic type
     * @param tested          the tested
     * @param methodToExecute the method to execute
     * @param definedIn       the defined in
     * @param argumentTypes   the argument types
     * @param arguments       the arguments
     * @return the t
     * @throws Exception Exception that may occur when invoking this method.
     *                   {@link #invokeMethod(Class, String, Object...)} instead.
     */
    @SuppressWarnings("unchecked")
    public static synchronized <T> T invokeMethod(Object tested, String methodToExecute, Class<?> definedIn,
                                                  Class<?>[] argumentTypes, Object... arguments) throws Exception {
        Method method = getMethod(definedIn, methodToExecute, argumentTypes);
        if (method == null) {
            throwExceptionIfMethodWasNotFound(definedIn, methodToExecute, null, arguments);
        }
        return (T) performMethodInvocation(tested, method, arguments);
    }

    /**
     * Invoke a private or inner class method in that is located in a subclass
     * of the tested instance. This might be useful to test private methods.
     *
     * @param <T>             the generic type
     * @param tested          the tested
     * @param declaringClass  the declaring class
     * @param methodToExecute the method to execute
     * @param arguments       the arguments
     * @return the t
     * @throws Exception Exception that may occur when invoking this method.
     */
    @SuppressWarnings("unchecked")
    public static synchronized <T> T invokeMethod(Object tested, Class<?> declaringClass, String methodToExecute,
                                                  Object... arguments) throws Exception {
        return (T) doInvokeMethod(tested, declaringClass, methodToExecute, arguments);
    }

    /**
     * Invoke a private method in that is located in a subclass of an instance.
     * This might be useful to test overloaded private methods.
     * 
     * Use this for overloaded methods only, if possible use
     *
     * @param <T>             the generic type
     * @param object          the object
     * @param declaringClass  the declaring class
     * @param methodToExecute the method to execute
     * @param parameterTypes  the parameter types
     * @param arguments       the arguments
     * @return the t
     * @throws Exception Exception that may occur when invoking this method.
     *                   {@link #invokeMethod(Object, Object...)} or
     *                   {@link #invokeMethod(Object, String, Object...)} instead.
     */
    @SuppressWarnings("unchecked")
    public static synchronized <T> T invokeMethod(Object object, Class<?> declaringClass, String methodToExecute,
                                                  Class<?>[] parameterTypes, Object... arguments) throws Exception {
        if (object == null) {
            throw new IllegalArgumentException("object cannot be null");
        }

        final Method methodToInvoke = getMethod(declaringClass, methodToExecute, parameterTypes);
        // Invoke method
        return (T) performMethodInvocation(object, methodToInvoke, arguments);
    }

    /**
     * Invoke a private or inner class method. This might be useful to test
     * private methods.
     *
     * @param <T>             the generic type
     * @param clazz           the clazz
     * @param methodToExecute the method to execute
     * @param arguments       the arguments
     * @return the t
     * @throws Exception the exception
     */
    @SuppressWarnings("unchecked")
    public static synchronized <T> T invokeMethod(Class<?> clazz, String methodToExecute, Object... arguments)
            throws Exception {
        return (T) doInvokeMethod(clazz, null, methodToExecute, arguments);
    }

    /**
     * Do invoke method.
     *
     * @param <T>             the generic type
     * @param tested          the tested
     * @param declaringClass  the declaring class
     * @param methodToExecute the method to execute
     * @param arguments       the arguments
     * @return the t
     * @throws Exception the exception
     */
    @SuppressWarnings("unchecked")
    private static <T> T doInvokeMethod(Object tested, Class<?> declaringClass, String methodToExecute,
                                        Object... arguments) throws Exception {
        Method methodToInvoke = findMethodOrThrowException(tested, declaringClass, methodToExecute, arguments);

        // Invoke test
        return (T) performMethodInvocation(tested, methodToInvoke, arguments);
    }

    /**
     * Finds and returns a certain method. If the method couldn't be found this
     * method delegates to
     *
     * @param tested          The instance or class containing the method.
     * @param declaringClass  The class where the method is supposed to be declared (may be
     *                        {@code null}).
     * @param methodToExecute The method name. If {@code null} then method will be
     *                        looked up based on the argument types only.
     * @param arguments       The arguments of the methods.
     * @return A single method.
     * @throws MethodNotFoundException if no method was found.
     * @throws TooManyMethodsFoundException if too methods matched.
     * @throws IllegalArgumentException if {@code tested} is null.
     */
    public static Method findMethodOrThrowException(Object tested, Class<?> declaringClass, String methodToExecute,
                                                    Object[] arguments){
        if (tested == null) {
            throw new IllegalArgumentException("The object to perform the operation on cannot be null.");
        }

        /*
           * Get methods from the type if it's not mocked or from the super type
           * if the tested object is mocked.
           */
        Class<?> testedType = null;
        if (isClass(tested)) {
            testedType = (Class<?>) tested;
        } else {
            testedType = tested.getClass();
        }

        Method[] methods = null;
        if (declaringClass == null) {
            methods = getAllMethods(testedType);
        } else {
            methods = declaringClass.getDeclaredMethods();
        }
        Method potentialMethodToInvoke = null;
        for (Method method : methods) {
            if (methodToExecute == null || method.getName().equals(methodToExecute)) {
                Class<?>[] paramTypes = method.getParameterTypes();
                if ((arguments != null && (paramTypes.length == arguments.length))) {
                    if (paramTypes.length == 0) {
                        potentialMethodToInvoke = method;
                        break;
                    }
                    boolean methodFound = checkArgumentTypesMatchParameterTypes(method.isVarArgs(), paramTypes, arguments);
                    if (methodFound) {
                        if (potentialMethodToInvoke == null) {
                            potentialMethodToInvoke = method;
                        } else if (potentialMethodToInvoke.getName().equals(method.getName())) {
                            if (areAllArgumentsOfSameType(arguments) && potentialMethodToInvoke.getDeclaringClass() != method.getDeclaringClass()) {
                                //  We've already found the method which means that "potentialMethodToInvoke" overrides "method".
                                return potentialMethodToInvoke;
                            } else {
                                // We've found an overloaded method
                                return getBestMethodCandidate(getType(tested), method.getName(), getTypes(arguments), false);
                            }
                        } else {
                            // A special case to be backward compatible
                            Method bestCandidateMethod = getMethodWithMostSpecificParameterTypes(method, potentialMethodToInvoke);
                            if (bestCandidateMethod != null) {
                                potentialMethodToInvoke = bestCandidateMethod;
                                continue;
                            }
                            /*
                            * We've already found a method match before, this
                            * means that PowerMock cannot determine which
                            * method to expect since there are two methods with
                            * the same name and the same number of arguments
                            * but one is using wrapper types.
                            */
                            throwExceptionWhenMultipleMethodMatchesFound("argument parameter types", new Method[]{
                                    potentialMethodToInvoke, method});
                        }
                    }
                } else if (isPotentialVarArgsMethod(method, arguments)) {
                    if (potentialMethodToInvoke == null) {
                        potentialMethodToInvoke = method;
                    } else {
                        /*
                               * We've already found a method match before, this means
                               * that PowerMock cannot determine which method to
                               * expect since there are two methods with the same name
                               * and the same number of arguments but one is using
                               * wrapper types.
                               */
                        throwExceptionWhenMultipleMethodMatchesFound("argument parameter types", new Method[]{
                                potentialMethodToInvoke, method});
                    }
                    break;
                } else if (arguments != null && (paramTypes.length != arguments.length)) {
                    continue;
                } else if (arguments == null && paramTypes.length == 1 && !paramTypes[0].isPrimitive()) {
                    potentialMethodToInvoke = method;
                }
            }
        }

        WhiteboxImpl.throwExceptionIfMethodWasNotFound(getType(tested), methodToExecute, potentialMethodToInvoke,
                arguments);
        return potentialMethodToInvoke;
    }

    /**
     * Find the method whose parameter types most closely matches the {@code types}.
     *
     * @param firstMethodCandidate  The first method candidate
     * @param secondMethodCandidate The second method candidate
     * @return The method that most closely matches the provided types or {@code null} if no method match.
     */
    private static Method getMethodWithMostSpecificParameterTypes(Method firstMethodCandidate, Method secondMethodCandidate) {
        Class<?>[] firstMethodCandidateParameterTypes = firstMethodCandidate.getParameterTypes();
        Class<?>[] secondMethodCandidateParameterTypes = secondMethodCandidate.getParameterTypes();

        Method bestMatch = null;
        for (int i = 0; i < firstMethodCandidateParameterTypes.length; i++) {
            Class<?> candidateType1 = toBoxedIfPrimitive(firstMethodCandidateParameterTypes[i]);
            Class<?> candidateType2 = toBoxedIfPrimitive(secondMethodCandidateParameterTypes[i]);

            if (!candidateType1.equals(candidateType2)) {
                Method potentialMatch = null;
                if (candidateType1.isAssignableFrom(candidateType2)) {
                    potentialMatch = secondMethodCandidate;
                } else if (candidateType2.isAssignableFrom(candidateType1)) {
                    potentialMatch = firstMethodCandidate;
                }

                if (potentialMatch != null) {
                    if (bestMatch != null && !potentialMatch.equals(bestMatch)) {
                        /*
                         * We cannot determine which method is the most specific because one parameter of the first candidate
                         * was more specific and another parameter of the second candidate was more specific.
                         */
                        return null;
                    } else {
                        bestMatch = potentialMatch;
                    }
                }
            }
        }

        return bestMatch;
    }

    private static Class<?> toBoxedIfPrimitive(Class<?> type) {
        return type.isPrimitive() ? BoxedWrapper.getBoxedFromPrimitiveType(type) : type;
    }

    /**
     * Gets the types.
     *
     * @param arguments the arguments
     * @return the types
     */
    private static Class<?>[] getTypes(Object[] arguments) {
        Class<?>[] classes = new Class<?>[arguments.length];
        for (int i = 0; i < arguments.length; i++) {
            classes[i] = getType(arguments[i]);
        }
        return classes;
    }

    /**
     * Gets the best method candidate.
     *
     * @param cls                     the cls
     * @param methodName              the method name
     * @param signature               the signature
     * @param exactParameterTypeMatch {@code true} if the {@code expectedTypes} must match
     *                                the parameter types must match exactly, {@code false} if
     *                                the {@code expectedTypes} are allowed to be converted
     *                                into primitive types if they are of a wrapped type and still
     *                                match.
     * @return the best method candidate
     */
    public static Method getBestMethodCandidate(Class<?> cls, String methodName, Class<?>[] signature,
                                                boolean exactParameterTypeMatch) {
        final Method foundMethod;
        final Method[] methods = getMethods(cls, methodName, signature, exactParameterTypeMatch);
        if (methods.length == 1) {
            foundMethod = methods[0];
        } else {
            // We've found overloaded methods, we need to find the best one to invoke.
            Arrays.sort(methods, ComparatorFactory.createMethodComparator());
            foundMethod = methods[0];
        }
        return foundMethod;
    }

    /**
     * Finds and returns the default constructor. If the constructor couldn't be
     * found this method delegates to {@link #throwExceptionWhenMultipleConstructorMatchesFound(java.lang.reflect.Constructor[])}.
     *
     * @param type The type where the constructor should be located.
     * @return The found constructor.
     * @throws ConstructorNotFoundException if too many constructors was found.
     */
    public static Constructor<?> findDefaultConstructorOrThrowException(Class<?> type) throws
            ConstructorNotFoundException {
        if (type == null) {
            throw new IllegalArgumentException("type cannot be null");
        }

        final Constructor<?> declaredConstructor;
        try {
            declaredConstructor = type.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new ConstructorNotFoundException(String.format("Couldn't find a default constructor in %s.", type.getName()));
        }
        return declaredConstructor;
    }

    /**
     * Finds and returns any constructor. If the constructor couldn't be
     * found this method delegates to {@link #throwExceptionWhenMultipleConstructorMatchesFound(java.lang.reflect.Constructor[])}.
     *
     * @param type The type where the constructor should be located.
     * @return The found constructor.
     * @throws TooManyConstructorsFoundException if too many constructors was found.
     */
    public static Constructor<?> findConstructorOrThrowException(Class<?> type) {
        final Constructor<?>[] declaredConstructors = filterPowerMockConstructor(type.getDeclaredConstructors());
        if (declaredConstructors.length > 1) {
            throwExceptionWhenMultipleConstructorMatchesFound(declaredConstructors);
        }
        return declaredConstructors[0];
    }

    /**
     * Filter power mock constructor.
     *
     * @param declaredConstructors the declared constructors
     * @return the constructor[]
     */
    static Constructor<?>[] filterPowerMockConstructor(Constructor<?>[] declaredConstructors) {
        Set<Constructor<?>> constructors = new HashSet<Constructor<?>>();
        for (Constructor<?> constructor : declaredConstructors) {
            final Class<?>[] parameterTypes = constructor.getParameterTypes();
            if (parameterTypes.length >= 1
                        && parameterTypes[parameterTypes.length - 1].getName().equals(
                    "org.powermock.core.IndicateReloadClass")) {
                continue;
            } else {
                constructors.add(constructor);
            }
        }
        return constructors.toArray(new Constructor<?>[constructors.size()]);
    }

    /**
     * Finds and returns a certain constructor. If the constructor couldn't be
     * found this method delegates to
     *
     * @param type      The type where the constructor should be located.
     * @param arguments The arguments passed to the constructor.
     * @return The found constructor.
     * @throws ConstructorNotFoundException if no constructor was found.
     * @throws TooManyConstructorsFoundException if too constructors matched.
     * @throws IllegalArgumentException if {@code type} is null.
     */
    public static Constructor<?> findUniqueConstructorOrThrowException(Class<?> type, Object... arguments) {
        return new ConstructorFinder(type, arguments).findConstructor();
    }

    /**
     * Convert argument types to primitive.
     *
     * @param paramTypes the param types
     * @param arguments  the arguments
     * @return the class[]
     */
    private static Class<?>[] convertArgumentTypesToPrimitive(Class<?>[] paramTypes, Object[] arguments) {
        Class<?>[] types = new Class<?>[arguments.length];
        for (int i = 0; i < arguments.length; i++) {
            Class<?> argumentType = null;
            if (arguments[i] == null) {
                argumentType = paramTypes[i];
            } else {
                argumentType = getType(arguments[i]);
            }
            Class<?> primitiveWrapperType = PrimitiveWrapper.getPrimitiveFromWrapperType(argumentType);
            if (primitiveWrapperType == null) {
                types[i] = argumentType;
            } else {
                types[i] = primitiveWrapperType;
            }
        }
        return types;
    }

    /**
     * Throw exception if method was not found.
     *
     * @param type         the type
     * @param methodName   the method name
     * @param methodToMock the method to mock
     * @param arguments    the arguments
     */
    public static void throwExceptionIfMethodWasNotFound(Class<?> type, String methodName, Method methodToMock,
                                                         Object... arguments) {
        if (methodToMock == null) {
            String methodNameData = "";
            if (methodName != null) {
                methodNameData = "with name '" + methodName + "' ";
            }
            throw new MethodNotFoundException("No method found " + methodNameData + "with parameter types: [ "
                                                      + getArgumentTypesAsString(arguments) + " ] in class " + getOriginalUnmockedType(type)
                                                                                                                       .getName() + ".");
        }
    }

    /**
     * Throw exception if field was not found.
     *
     * @param type      the type
     * @param fieldName the field name
     * @param field     the field
     */
    public static void throwExceptionIfFieldWasNotFound(Class<?> type, String fieldName, Field field) {
        if (field == null) {
            throw new FieldNotFoundException("No field was found with name '" + fieldName + "' in class "
                                                     + getOriginalUnmockedType(type).getName() + ".");
        }
    }

    /**
     * Throw exception if constructor was not found.
     *
     * @param type                 the type
     * @param potentialConstructor the potential constructor
     * @param arguments            the arguments
     */
    static void throwExceptionIfConstructorWasNotFound(Class<?> type, Constructor<?> potentialConstructor,
                                                       Object... arguments) {
        if (potentialConstructor == null) {
            String message = "No constructor found in class '" + getOriginalUnmockedType(type).getName() + "' with "
                                     + "parameter types: [ " + getArgumentTypesAsString(arguments) + " ].";
            throw new ConstructorNotFoundException(message);
        }
    }

    /**
     * Gets the argument types as string.
     *
     * @param arguments the arguments
     * @return the argument types as string
     */
    static String getArgumentTypesAsString(Object... arguments) {
        StringBuilder argumentsAsString = new StringBuilder();
        final String noParameters = "<none>";
        if (arguments != null && arguments.length != 0) {
            for (int i = 0; i < arguments.length; i++) {
                String argumentName = null;
                Object argument = arguments[i];

                if (argument instanceof Class<?>) {
                    argumentName = ((Class<?>) argument).getName();
                } else if (argument instanceof Class<?>[] && arguments.length == 1) {
                    Class<?>[] argumentArray = (Class<?>[]) argument;
                    if (argumentArray.length > 0) {
                        for (int j = 0; j < argumentArray.length; j++) {
                            appendArgument(argumentsAsString, j,
                                    argumentArray[j] == null ? "null" : getUnproxyType(argumentArray[j]).getName(),
                                    argumentArray);
                        }
                        return argumentsAsString.toString();
                    } else {
                        argumentName = noParameters;
                    }
                } else if (argument == null) {
                    argumentName = "null";
                } else {
                    argumentName = getUnproxyType(argument).getName();
                }
                appendArgument(argumentsAsString, i, argumentName, arguments);
            }
        } else {
            argumentsAsString.append("<none>");
        }
        return argumentsAsString.toString();
    }

    /**
     * Append argument.
     *
     * @param argumentsAsString the arguments as string
     * @param index             the index
     * @param argumentName      the argument name
     * @param arguments         the arguments
     */
    private static void appendArgument(StringBuilder argumentsAsString, int index, String argumentName,
                                       Object[] arguments) {
        argumentsAsString.append(argumentName);
        if (index != arguments.length - 1) {
            argumentsAsString.append(", ");
        }
    }

    /**
     * Invoke a constructor. Useful for testing classes with a private
     * constructor when PowerMock cannot determine which constructor to invoke.
     * This only happens if you have two constructors with the same number of
     * arguments where one is using primitive data types and the other is using
     * the wrapped counter part. For example:
     * 
     * <pre>
     * public class MyClass {
     * private MyClass(Integer i) {
     * ...
     * }
     *
     * private MyClass(int i) {
     * ...
     * }
     * </pre>
     * 
     * This ought to be a really rare case. So for most situation, use
     *
     * @param <T>                                   the generic type
     * @param classThatContainsTheConstructorToTest the class that contains the constructor to test
     * @param parameterTypes                        the parameter types
     * @param arguments                             the arguments
     * @return The object created after the constructor has been invoked.
     * @throws Exception If an exception occur when invoking the constructor.
     *                   {@link #invokeConstructor(Class, Object...)} instead.
     */
    public static <T> T invokeConstructor(Class<T> classThatContainsTheConstructorToTest, Class<?>[] parameterTypes,
                                          Object[] arguments) throws Exception {
        if (parameterTypes != null && arguments != null) {
            if (parameterTypes.length != arguments.length) {
                throw new IllegalArgumentException("parameterTypes and arguments must have the same length");
            }
        }

        Constructor<T> constructor = null;
        try {
            constructor = classThatContainsTheConstructorToTest.getDeclaredConstructor(parameterTypes);
        } catch (Exception e) {
            throw new ConstructorNotFoundException("Could not lookup the constructor", e);
        }

        return createInstance(constructor, arguments);
    }

    /**
     * Invoke a constructor. Useful for testing classes with a private
     * constructor.
     *
     * @param <T>                                   the generic type
     * @param classThatContainsTheConstructorToTest the class that contains the constructor to test
     * @param arguments                             the arguments
     * @return The object created after the constructor has been invoked.
     * @throws Exception If an exception occur when invoking the constructor.
     */
    public static <T> T invokeConstructor(Class<T> classThatContainsTheConstructorToTest, Object... arguments)
            throws Exception {

        if (classThatContainsTheConstructorToTest == null) {
            throw new IllegalArgumentException("The class should contain the constructor cannot be null.");
        }

        Class<?>[] argumentTypes = null;
        if (arguments == null) {
            argumentTypes = new Class<?>[0];
        } else {
            argumentTypes = new Class<?>[arguments.length];
            for (int i = 0; i < arguments.length; i++) {
                argumentTypes[i] = getType(arguments[i]);
            }
        }

        Constructor<T> constructor = null;

        constructor = getBestCandidateConstructor(classThatContainsTheConstructorToTest, argumentTypes, arguments);

        return createInstance(constructor, arguments);
    }

    private static <T> Constructor<T> getBestCandidateConstructor(Class<T> classThatContainsTheConstructorToTest, Class<?>[] argumentTypes, Object[] arguments) {
        Constructor<T> constructor;


        Constructor<T> potentialConstructorWrapped = getPotentialConstructorWrapped(classThatContainsTheConstructorToTest, argumentTypes);

        Constructor<T> potentialConstructorPrimitive = getPotentialConstructorPrimitive(classThatContainsTheConstructorToTest, argumentTypes);

        if (potentialConstructorPrimitive == null && potentialConstructorWrapped == null) {
            // Check if we can find a matching var args constructor.
            constructor = getPotentialVarArgsConstructor(classThatContainsTheConstructorToTest, arguments);
            if (constructor == null) {
                throw new ConstructorNotFoundException("Failed to find a constructor with parameter types: ["
                                                               + getArgumentTypesAsString(arguments) + "]");
            }
        } else if (potentialConstructorPrimitive == null) {
            constructor = potentialConstructorWrapped;
        } else if (potentialConstructorWrapped == null) {
            constructor = potentialConstructorPrimitive;
        } else if (arguments == null || arguments.length == 0 && potentialConstructorPrimitive != null) {
            constructor = potentialConstructorPrimitive;
        } else {
            throw new TooManyConstructorsFoundException(
                                                               "Could not determine which constructor to execute. Please specify the parameter types by hand.");
        }
        return constructor;
    }

    private static <T> Constructor<T> getPotentialConstructorWrapped(Class<T> classThatContainsTheConstructorToTest, Class<?>[] argumentTypes) {
        return new CandidateConstructorSearcher<T>(classThatContainsTheConstructorToTest, argumentTypes)
                       .findConstructor();
    }

    private static <T> Constructor<T> getPotentialConstructorPrimitive(Class<T> classThatContainsTheConstructorToTest, Class<?>[] argumentTypes) {

        Constructor<T> potentialConstructorPrimitive = null;
        try {
            Class<?>[] primitiveType = PrimitiveWrapper.toPrimitiveType(argumentTypes);

            if (!argumentTypesEqualsPrimitiveTypes(argumentTypes, primitiveType)) {

                potentialConstructorPrimitive = new CandidateConstructorSearcher<T>(classThatContainsTheConstructorToTest, primitiveType)
                                                        .findConstructor();
            }
        } catch (Exception e) {
            // Do nothing
        }
        return potentialConstructorPrimitive;
    }

    private static boolean argumentTypesEqualsPrimitiveTypes(Class<?>[] argumentTypes, Class<?>[] primitiveType) {
        for (int index = 0; index < argumentTypes.length; index++) {
            if (!argumentTypes[index].equals(primitiveType[index])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Gets the potential var args constructor.
     *
     * @param <T>                                   the generic type
     * @param classThatContainsTheConstructorToTest the class that contains the constructor to test
     * @param arguments                             the arguments
     * @return the potential var args constructor
     */
    @SuppressWarnings("unchecked")
    private static <T> Constructor<T> getPotentialVarArgsConstructor(Class<T> classThatContainsTheConstructorToTest,
                                                                     Object... arguments) {
        Constructor<T>[] declaredConstructors = (Constructor<T>[]) classThatContainsTheConstructorToTest
                                                                           .getDeclaredConstructors();
        for (Constructor<T> possibleVarArgsConstructor : declaredConstructors) {
            if (possibleVarArgsConstructor.isVarArgs()) {
                if (arguments == null || arguments.length == 0) {
                    return possibleVarArgsConstructor;
                } else {
                    Class<?>[] parameterTypes = possibleVarArgsConstructor.getParameterTypes();
                    if (parameterTypes[parameterTypes.length - 1].getComponentType().isAssignableFrom(
                            getType(arguments[0]))) {
                        return possibleVarArgsConstructor;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Creates the instance.
     *
     * @param <T>         the generic type
     * @param constructor the constructor
     * @param arguments   the arguments
     * @return the t
     * @throws Exception the exception
     */
    private static <T> T createInstance(Constructor<T> constructor, Object... arguments) throws Exception {
        if (constructor == null) {
            throw new IllegalArgumentException("Constructor cannot be null");
        }
        constructor.setAccessible(true);

        T createdObject = null;
        try {
            if (constructor.isVarArgs()) {
                Class<?>[] parameterTypes = constructor.getParameterTypes();
                final int varArgsIndex = parameterTypes.length - 1;
                Class<?> varArgsType = parameterTypes[varArgsIndex].getComponentType();
                Object varArgsArrayInstance = createAndPopulateVarArgsArray(varArgsType, varArgsIndex, arguments);
                Object[] completeArgumentList = new Object[parameterTypes.length];
                System.arraycopy(arguments, 0, completeArgumentList, 0, varArgsIndex);
                completeArgumentList[completeArgumentList.length - 1] = varArgsArrayInstance;
                createdObject = constructor.newInstance(completeArgumentList);
            } else {
                createdObject = constructor.newInstance(arguments);
            }
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof Exception) {
                throw (Exception) cause;
            } else if (cause instanceof Error) {
                throw (Error) cause;
            }
        }
        return createdObject;
    }

    /**
     * Creates the and populate var args array.
     *
     * @param varArgsType          the var args type
     * @param varArgsStartPosition the var args start position
     * @param arguments            the arguments
     * @return the object
     */
    private static Object createAndPopulateVarArgsArray(Class<?> varArgsType, int varArgsStartPosition,
                                                        Object... arguments) {
        Object arrayInstance = Array.newInstance(varArgsType, arguments.length - varArgsStartPosition);
        for (int i = varArgsStartPosition; i < arguments.length; i++) {
            Array.set(arrayInstance, i - varArgsStartPosition, arguments[i]);
        }
        return arrayInstance;
    }

    /**
     * Get all declared constructors in the class and set accessible to
     * {@code true}.
     *
     * @param clazz The class whose constructors to get.
     * @return All constructors declared in this class hierarchy.
     */
    public static Constructor<?>[] getAllConstructors(Class<?> clazz) {
        Constructor<?>[] declaredConstructors = clazz.getDeclaredConstructors();
        for (Constructor<?> constructor : declaredConstructors) {
            if (!constructor.isAccessible()) {
                constructor.setAccessible(true);
            }
        }
        return declaredConstructors;
    }

    /**
     * Get all methods in a class hierarchy! Both declared an non-declared (no
     * duplicates).
     *
     * @param clazz The class whose methods to get.
     * @return All methods declared in this class hierarchy.
     */
    public static Method[] getAllMethods(Class<?> clazz) {
        Method[] allMethods = allClassMethodsCache.get(clazz);
        if (allMethods == null) {
            // Allows a race between concurrent threads coming for clazz's methods at the same time,
            // but the race seems to be harmless.
            allMethods = doGetAllMethods(clazz);
            allClassMethodsCache.put(clazz, allMethods);
        }
        return allMethods;
    }

    private static Method[] doGetAllMethods(Class<?> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("You must specify a class in order to get the methods.");
        }
        Set<Method> methods = new LinkedHashSet<Method>();

        Class<?> thisType = clazz;

        while (thisType != null) {
            final Class<?> type = thisType;
            final Method[] declaredMethods = AccessController.doPrivileged(new PrivilegedAction<Method[]>() {

                @Override
                public Method[] run() {
                    return type.getDeclaredMethods();
                }

            });
            for (Method method : declaredMethods) {
                if(!"finalize".equals(method.getName())) {
                    method.setAccessible(true);
                    methods.add(method);
                }
            }
            Collections.addAll(methods, type.getMethods());
            thisType = thisType.getSuperclass();
        }
        return methods.toArray(new Method[methods.size()]);
    }

    /**
     * Get all public methods for a class (no duplicates)! Note that the
     * class-hierarchy will not be traversed.
     *
     * @param clazz The class whose methods to get.
     * @return All public methods declared in class.
     */
    private static Method[] getAllPublicMethods(Class<?> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("You must specify a class in order to get the methods.");
        }
        Set<Method> methods = new LinkedHashSet<Method>();

        for (Method method : clazz.getMethods()) {
            method.setAccessible(true);
            methods.add(method);
        }
        return methods.toArray(new Method[0]);
    }

    /**
     * Get all fields in a class hierarchy! Both declared an non-declared (no
     * duplicates).
     *
     * @param clazz The class whose fields to get.
     * @return All fields declared in this class hierarchy.
     */
    public static Field[] getAllFields(Class<?> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("You must specify the class that contains the fields");
        }
        Set<Field> fields = new LinkedHashSet<Field>();

        Class<?> thisType = clazz;

        while (thisType != null) {
            final Field[] declaredFields = thisType.getDeclaredFields();
            for (Field field : declaredFields) {
                field.setAccessible(true);
                fields.add(field);
            }
            thisType = thisType.getSuperclass();
        }
        return fields.toArray(new Field[fields.size()]);
    }

    /**
     * Get the first parent constructor defined in a super class of
     * {@code klass}.
     *
     * @param klass The class where the constructor is located. {@code null}
     *              ).
     * @return A .
     */
    public static Constructor<?> getFirstParentConstructor(Class<?> klass) {
        try {
            return getOriginalUnmockedType(klass).getSuperclass().getDeclaredConstructors()[0];
        } catch (Exception e) {
            throw new ConstructorNotFoundException("Failed to lookup constructor.", e);
        }
    }

    /**
     * Finds and returns a method based on the input parameters. If no
     * {@code parameterTypes} are present the method will return the first
     * method with name {@code methodNameToMock}. If no method was found,
     * {@code null} will be returned. If no {@code methodName} is
     * specified the method will be found based on the parameter types. If
     * neither method name nor parameters are specified an
     *
     * @param <T>            the generic type
     * @param type           the type
     * @param methodName     the method name
     * @param parameterTypes the parameter types
     * @return the method {@link IllegalArgumentException} will be thrown.
     */
    public static <T> Method findMethod(Class<T> type, String methodName, Class<?>... parameterTypes) {
        if (methodName == null && parameterTypes == null) {
            throw new IllegalArgumentException("You must specify a method name or parameter types.");
        }
        List<Method> matchingMethodsList = new LinkedList<Method>();
        for (Method method : getAllMethods(type)) {
            if (methodName == null || method.getName().equals(methodName)) {
                if (parameterTypes != null && parameterTypes.length > 0) {
                    // If argument types was supplied, make sure that they
                    // match.
                    Class<?>[] paramTypes = method.getParameterTypes();
                    if (!checkIfParameterTypesAreSame(method.isVarArgs(), parameterTypes, paramTypes)) {
                        continue;
                    }
                }
                // Add the method to the matching methods list.
                matchingMethodsList.add(method);
            }
        }

        Method methodToMock = null;
        if (matchingMethodsList.size() > 0) {
            if (matchingMethodsList.size() == 1) {
                // We've found a unique method match.
                methodToMock = matchingMethodsList.get(0);
            } else if ((parameterTypes != null ? parameterTypes.length : 0) == 0) {
                /*
                     * If we've found several matches and we've supplied no
                     * parameter types, go through the list of found methods and see
                     * if we have a method with no parameters. In that case return
                     * that method.
                     */
                for (Method method : matchingMethodsList) {
                    if (method.getParameterTypes().length == 0) {
                        methodToMock = method;
                        break;
                    }
                }

                if (methodToMock == null) {
                    WhiteboxImpl.throwExceptionWhenMultipleMethodMatchesFound("argument parameter types",
                            matchingMethodsList.toArray(new Method[matchingMethodsList.size()]));
                }
            } else {
                // We've found several matching methods.
                WhiteboxImpl.throwExceptionWhenMultipleMethodMatchesFound("argument parameter types",
                        matchingMethodsList.toArray(new Method[matchingMethodsList.size()]));
            }
        }

        return methodToMock;
    }


    /**
     * Gets the unmocked type.
     *
     * @param <T>  the generic type
     * @param type the type
     * @return the unmocked type
     */
    public static <T> Class<?> getOriginalUnmockedType(Class<T> type) {
        return getUnproxiedType(type).getOriginalType();
    }
    
    public static <T> UnproxiedType getUnproxiedType(Class<T> type) {
        return proxyFrameworks.getUnproxiedType(type);
    }
    
    /**
     * Throw exception when multiple method matches found.
     *
     * @param helpInfo the help info
     * @param methods  the methods
     */
    static void throwExceptionWhenMultipleMethodMatchesFound(String helpInfo, Method[] methods) {
        if (methods == null || methods.length < 2) {
            throw new IllegalArgumentException(
                                                      "Internal error: throwExceptionWhenMultipleMethodMatchesFound needs at least two methods.");
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Several matching methods found, please specify the ");
        sb.append(helpInfo);
        sb.append(" so that PowerMock can determine which method you're referring to.\n");
        sb.append("Matching methods in class ").append(methods[0].getDeclaringClass().getName()).append(" were:\n");

        for (Method method : methods) {
            sb.append(method.getReturnType().getName()).append(" ");
            sb.append(method.getName()).append("( ");
            final Class<?>[] parameterTypes = method.getParameterTypes();
            for (Class<?> paramType : parameterTypes) {
                sb.append(paramType.getName()).append(".class ");
            }
            sb.append(")\n");
        }
        throw new TooManyMethodsFoundException(sb.toString());
    }

    /**
     * Throw exception when multiple constructor matches found.
     *
     * @param constructors the constructors
     */
    static void throwExceptionWhenMultipleConstructorMatchesFound(Constructor<?>[] constructors) {
        if (constructors == null || constructors.length < 2) {
            throw new IllegalArgumentException(
                                                      "Internal error: throwExceptionWhenMultipleConstructorMatchesFound needs at least two constructors.");
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Several matching constructors found, please specify the argument parameter types so that PowerMock can determine which method you're referring to.\n");
        sb.append("Matching constructors in class ").append(constructors[0].getDeclaringClass().getName())
          .append(" were:\n");

        for (Constructor<?> constructor : constructors) {
            sb.append(constructor.getName()).append("( ");
            final Class<?>[] parameterTypes = constructor.getParameterTypes();
            for (Class<?> paramType : parameterTypes) {
                sb.append(paramType.getName()).append(".class ");
            }
            sb.append(")\n");
        }
        throw new TooManyConstructorsFoundException(sb.toString());
    }

    /**
     * Find method or throw exception.
     *
     * @param type           the type
     * @param methodName     the method name
     * @param parameterTypes the parameter types
     * @return the method
     */
    @SuppressWarnings("all")
    public static Method findMethodOrThrowException(Class<?> type, String methodName, Class<?>... parameterTypes) {
        Method methodToMock = findMethod(type, methodName, parameterTypes);
        throwExceptionIfMethodWasNotFound(type, methodName, methodToMock, (Object[]) parameterTypes);
        return methodToMock;
    }

    /**
     * Get an array of {@link Method}'s that matches the supplied list of method
     * names. Both instance and static methods are taken into account.
     *
     * @param clazz       The class that should contain the methods.
     * @param methodNames Names of the methods that will be returned.
     * @return An array of Method's.
     */
    public static Method[] getMethods(Class<?> clazz, String... methodNames) {
        if (methodNames == null || methodNames.length == 0) {
            throw new IllegalArgumentException("You must supply at least one method name.");
        }
        final List<Method> methodsToMock = new LinkedList<Method>();
        Method[] allMethods = null;
        if (clazz.isInterface()) {
            allMethods = getAllPublicMethods(clazz);
        } else {
            allMethods = getAllMethods(clazz);
        }

        for (Method method : allMethods) {
            for (String methodName : methodNames) {
                if (method.getName().equals(methodName)) {
                    method.setAccessible(true);
                    methodsToMock.add(method);
                }
            }
        }

        final Method[] methodArray = methodsToMock.toArray(new Method[0]);
        if (methodArray.length == 0) {
            throw new MethodNotFoundException(String.format(
                    "No methods matching the name(s) %s were found in the class hierarchy of %s.",
                    concatenateStrings(methodNames), getType(clazz)));
        }
        return methodArray;
    }

    /**
     * Get an array of {@link Method}'s that matches the method name and whose
     * argument types are assignable from {@code expectedTypes}. Both
     * instance and static methods are taken into account.
     *
     * @param clazz                   The class that should contain the methods.
     * @param methodName              Names of the methods that will be returned.
     * @param expectedTypes           The methods must match
     * @param exactParameterTypeMatch {@code true} if the {@code expectedTypes} must match
     *                                the parameter types must match exactly, {@code false} if
     *                                the {@code expectedTypes} are allowed to be converted
     *                                into primitive types if they are of a wrapped type and still
     *                                match.
     * @return An array of Method's.
     */
    public static Method[] getMethods(Class<?> clazz, String methodName, Class<?>[] expectedTypes,
                                      boolean exactParameterTypeMatch) {
        List<Method> matchingArgumentTypes = new LinkedList<Method>();
        Method[] methods = getMethods(clazz, methodName);
        for (Method method : methods) {
            final Class<?>[] parameterTypes = method.getParameterTypes();
            if (checkIfParameterTypesAreSame(method.isVarArgs(), expectedTypes, parameterTypes)
                        || (!exactParameterTypeMatch && checkIfParameterTypesAreSame(method.isVarArgs(),
                    convertParameterTypesToPrimitive(expectedTypes), parameterTypes))) {
                matchingArgumentTypes.add(method);
            }
        }
        final Method[] methodArray = matchingArgumentTypes.toArray(new Method[0]);
        if (methodArray.length == 0) {
            throw new MethodNotFoundException(String.format(
                    "No methods matching the name(s) %s were found in the class hierarchy of %s.",
                    concatenateStrings(methodName), getType(clazz)));
        }
        return matchingArgumentTypes.toArray(new Method[matchingArgumentTypes.size()]);
    }

    /**
     * Get an array of {@link Field}'s that matches the supplied list of field
     * names. Both instance and static fields are taken into account.
     *
     * @param clazz      The class that should contain the fields.
     * @param fieldNames Names of the fields that will be returned.
     * @return An array of Field's. May be of length 0 but not .
     */
    public static Field[] getFields(Class<?> clazz, String... fieldNames) {
        final List<Field> fields = new LinkedList<Field>();

        for (Field field : getAllFields(clazz)) {
            for (String fieldName : fieldNames) {
                if (field.getName().equals(fieldName)) {
                    fields.add(field);
                }
            }
        }

        final Field[] fieldArray = fields.toArray(new Field[fields.size()]);
        if (fieldArray.length == 0) {
            throw new FieldNotFoundException(String.format(
                    "No fields matching the name(s) %s were found in the class hierarchy of %s.",
                    concatenateStrings(fieldNames), getType(clazz)));
        }
        return fieldArray;
    }

    /**
     * Perform method invocation.
     *
     * @param <T>            the generic type
     * @param tested         the tested
     * @param methodToInvoke the method to invoke
     * @param arguments      the arguments
     * @return the t
     * @throws Exception the exception
     */
    @SuppressWarnings("unchecked")
    public static <T> T performMethodInvocation(Object tested, Method methodToInvoke, Object... arguments)
            throws Exception {
        final boolean accessible = methodToInvoke.isAccessible();
        if (!accessible) {
            methodToInvoke.setAccessible(true);
        }
        try {
            if (isPotentialVarArgsMethod(methodToInvoke, arguments)) {
                Class<?>[] parameterTypes = methodToInvoke.getParameterTypes();
                final int varArgsIndex = parameterTypes.length - 1;
                Class<?> varArgsType = parameterTypes[varArgsIndex].getComponentType();
                Object varArgsArrayInstance = createAndPopulateVarArgsArray(varArgsType, varArgsIndex, arguments);
                Object[] completeArgumentList = new Object[parameterTypes.length];
                System.arraycopy(arguments, 0, completeArgumentList, 0, varArgsIndex);
                completeArgumentList[completeArgumentList.length - 1] = varArgsArrayInstance;
                return (T) methodToInvoke.invoke(tested, completeArgumentList);
            } else {
                return (T) methodToInvoke.invoke(tested, arguments == null ? new Object[]{arguments} : arguments);
            }
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof Exception) {
                throw (Exception) cause;
            } else if (cause instanceof Error) {
                throw (Error) cause;
            } else {
                throw new MethodInvocationException(cause);
            }
        } finally {
            if (!accessible) {
                methodToInvoke.setAccessible(false);
            }
        }
    }

    /**
     * Gets the all method except.
     *
     * @param <T>         the generic type
     * @param type        the type
     * @param methodNames the method names
     * @return the all method except
     */
    public static <T> Method[] getAllMethodExcept(Class<T> type, String... methodNames) {
        List<Method> methodsToMock = new LinkedList<Method>();
        Method[] methods = getAllMethods(type);
        iterateMethods:
        for (Method method : methods) {
            for (String methodName : methodNames) {
                if (method.getName().equals(methodName)) {
                    continue iterateMethods;
                }
            }
            methodsToMock.add(method);
        }
        return methodsToMock.toArray(new Method[0]);
    }

    /**
     * Gets the all methods except.
     *
     * @param <T>                 the generic type
     * @param type                the type
     * @param methodNameToExclude the method name to exclude
     * @param argumentTypes       the argument types
     * @return the all methods except
     */
    public static <T> Method[] getAllMethodsExcept(Class<T> type, String methodNameToExclude, Class<?>[] argumentTypes) {
        Method[] methods = getAllMethods(type);
        List<Method> methodList = new ArrayList<Method>();
        outer:
        for (Method method : methods) {
            if (method.getName().equals(methodNameToExclude)) {
                if (argumentTypes != null && argumentTypes.length > 0) {
                    final Class<?>[] args = method.getParameterTypes();
                    if (args != null && args.length == argumentTypes.length) {
                        for (int i = 0; i < args.length; i++) {
                            if (args[i].isAssignableFrom(getOriginalUnmockedType(argumentTypes[i]))) {
                                /*
                                         * Method was not found thus it should not be
                                         * mocked. Continue to investigate the next
                                         * method.
                                         */
                                continue outer;
                            }
                        }
                    }
                } else {
                    continue;
                }
            }
            methodList.add(method);
        }
        return methodList.toArray(new Method[0]);
    }

    /**
     * Are all methods static.
     *
     * @param methods the methods
     * @return true, if successful
     */
    public static boolean areAllMethodsStatic(Method... methods) {
        for (Method method : methods) {
            if (!Modifier.isStatic(method.getModifiers())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if all arguments are of the same type.
     *
     * @param arguments the arguments
     * @return true, if successful
     */
    static boolean areAllArgumentsOfSameType(Object[] arguments) {
        if (arguments == null || arguments.length <= 1) {
            return true;
        }

        // Handle null values
        int index = 0;
        Object object = null;
        while (object == null && index < arguments.length) {
            object = arguments[index++];
        }

        if (object == null) {
            return true;
        }
        // End of handling null values

        final Class<?> firstArgumentType = getType(object);
        for (int i = index; i < arguments.length; i++) {
            final Object argument = arguments[i];
            if (argument != null && !getType(argument).isAssignableFrom(firstArgumentType)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Check argument types match parameter types.
     *
     * @param isVarArgs      If the last parameter is a var args.
     * @param parameterTypes the parameter types
     * @param arguments      the arguments
     * @return if all actual parameter types are assignable from the expected
     * arguments, otherwise.
     */
     static boolean checkArgumentTypesMatchParameterTypes(boolean isVarArgs, Class<?>[] parameterTypes,
                                                                 Object[] arguments) {
        if (parameterTypes == null) {
            throw new IllegalArgumentException("parameter types cannot be null");
        } else if (!isVarArgs && arguments.length != parameterTypes.length) {
            return false;
        }
        for (int i = 0; i < arguments.length; i++) {
            Object argument = arguments[i];
            if (argument == null) {
                final int index;
                if (i >= parameterTypes.length) {
                    index = parameterTypes.length - 1;
                } else {
                    index = i;
                }
                final Class<?> type = parameterTypes[index];
                if (type.isPrimitive()) {
                    // Primitives cannot be null
                    return false;
                } else {
                    continue;
                }
            } else if (i >= parameterTypes.length) {
                if (isAssignableFrom(parameterTypes[parameterTypes.length - 1], getType(argument))) {
                    continue;
                } else {
                    return false;
                }
            } else {
                boolean assignableFrom = isAssignableFrom(parameterTypes[i], getType(argument));
                final boolean isClass = parameterTypes[i].equals(Class.class) && isClass(argument);
                if (!assignableFrom && !isClass) {
                    return false;
                }
            }
        }
        return true;
    }

    static boolean isAssignableFrom(Class<?> type, Class<?> from) {
        boolean assignableFrom;
        Class<?> theType = getComponentType(type);
        Class<?> theFrom = getComponentType(from);
        assignableFrom = theType.isAssignableFrom(theFrom);
        if (!assignableFrom && PrimitiveWrapper.hasPrimitiveCounterPart(theFrom)) {
            final Class<?> primitiveFromWrapperType = PrimitiveWrapper.getPrimitiveFromWrapperType(theFrom);
            if (primitiveFromWrapperType != null) {
                assignableFrom = theType.isAssignableFrom(primitiveFromWrapperType);
            }
        }
        return assignableFrom;
    }

    private static Class<?> getComponentType(Class<?> type) {
        Class<?> theType = type;
        while (theType.isArray()) {
            theType = theType.getComponentType();
        }
        return theType;
    }

    /**
     * Gets the type.
     *
     * @param object the object
     * @return The type of the of an object.
     */
    public static Class<?> getType(Object object) {
        Class<?> type = null;
        if (isClass(object)) {
            type = (Class<?>) object;
        } else if (object != null) {
            type = object.getClass();
        }
        return type;
    }

    /**
     * Gets the type.
     *
     * @param object the object
     * @return The type of the of an object.
     */
    public static Class<?> getUnproxyType(Object object) {
        Class<?> type = null;
        if (isClass(object)) {
            type = (Class<?>) object;
        } else if (object != null) {
            type = object.getClass();
        }
        return type == null ? null : getOriginalUnmockedType(type);
    }

    /**
     * Get an inner class type.
     *
     * @param declaringClass The class in which the inner class is declared.
     * @param name           The unqualified name (simple name) of the inner class.
     * @return The type.
     * @throws ClassNotFoundException the class not found exception
     */
    @SuppressWarnings("unchecked")
    public static Class<Object> getInnerClassType(Class<?> declaringClass, String name) throws ClassNotFoundException {
        return (Class<Object>) Class.forName(declaringClass.getName() + "$" + name);
    }

    /**
     * Get the type of a local inner class.
     *
     * @param declaringClass The class in which the local inner class is declared.
     * @param occurrence     The occurrence of the local class. For example if you have two
     *                       local classes in the {@code declaringClass} you must pass
     *                       in {@code 1} if you want to get the type for the first
     *                       one or {@code 2} if you want the second one.
     * @param name           The unqualified name (simple name) of the local class.
     * @return The type.
     * @throws ClassNotFoundException the class not found exception
     */
    @SuppressWarnings("unchecked")
    public static Class<Object> getLocalClassType(Class<?> declaringClass, int occurrence, String name)
            throws ClassNotFoundException {
        return (Class<Object>) Class.forName(declaringClass.getName() + "$" + occurrence + name);
    }

    /**
     * Get the type of an anonymous inner class.
     *
     * @param declaringClass The class in which the anonymous inner class is declared.
     * @param occurrence     The occurrence of the anonymous inner class. For example if
     *                       you have two anonymous inner classes classes in the
     *                       {@code declaringClass} you must pass in {@code 1} if
     *                       you want to get the type for the first one or {@code 2}
     *                       if you want the second one.
     * @return The type.
     * @throws ClassNotFoundException the class not found exception
     */
    @SuppressWarnings("unchecked")
    public static Class<Object> getAnonymousInnerClassType(Class<?> declaringClass, int occurrence)
            throws ClassNotFoundException {
        return (Class<Object>) Class.forName(declaringClass.getName() + "$" + occurrence);
    }

    /**
     * Get all fields annotated with a particular annotation. This method
     * traverses the class hierarchy when checking for the annotation.
     *
     * @param object                The object to look for annotations. Note that if're you're
     *                              passing an object only instance fields are checked, passing a
     *                              class will only check static fields.
     * @param annotation            The annotation type to look for.
     * @param additionalAnnotations Optionally more annotations to look for. If any of the
     *                              annotations are associated with a particular field it will be
     *                              added to the resulting {@code Set}.
     * @return A set of all fields containing the particular annotation.
     */
    @SuppressWarnings("unchecked")
    public static Set<Field> getFieldsAnnotatedWith(Object object, Class<? extends Annotation> annotation,
                                                    Class<? extends Annotation>... additionalAnnotations) {
        Class<? extends Annotation>[] annotations = null;
        if (additionalAnnotations == null || additionalAnnotations.length == 0) {
            annotations = (Class<? extends Annotation>[]) new Class<?>[]{annotation};
        } else {
            annotations = (Class<? extends Annotation>[]) new Class<?>[additionalAnnotations.length + 1];
            annotations[0] = annotation;
            System.arraycopy(additionalAnnotations, 0, annotations, 1, additionalAnnotations.length);
        }
        return getFieldsAnnotatedWith(object, annotations);
    }

    /**
     * Get all fields annotated with a particular annotation. This method
     * traverses the class hierarchy when checking for the annotation.
     *
     * @param object          The object to look for annotations. Note that if're you're
     *                        passing an object only instance fields are checked, passing a
     *                        class will only check static fields.
     * @param annotationTypes The annotation types to look for
     * @return A set of all fields containing the particular annotation(s).
     * @since 1.3
     */
    public static Set<Field> getFieldsAnnotatedWith(Object object, Class<? extends Annotation>[] annotationTypes) {
        return findAllFieldsUsingStrategy(new FieldAnnotationMatcherStrategy(annotationTypes), object, true,
                getType(object));
    }

    /**
     * Get all fields assignable from a particular type. This method traverses
     * the class hierarchy when checking for the type.
     *
     * @param object The object to look for type. Note that if're you're passing an
     *               object only instance fields are checked, passing a class will
     *               only check static fields.
     * @param type   The type to look for.
     * @return A set of all fields of the particular type.
     */
    public static Set<Field> getFieldsOfType(Object object, Class<?> type) {
        return findAllFieldsUsingStrategy(new AssignableFromFieldTypeMatcherStrategy(type), object, true,
                getType(object));
    }

    /**
     * Get all instance fields for a particular object. It returns all fields
     * regardless of the field modifier and regardless of where in the class
     * hierarchy a field is located.
     *
     * @param object The object whose instance fields to get.
     * @return All instance fields in the hierarchy. All fields are set to
     * accessible
     */
    public static Set<Field> getAllInstanceFields(Object object) {
        return findAllFieldsUsingStrategy(new AllFieldsMatcherStrategy(), object, true, getUnproxyType(object));
    }

    /**
     * Get all static fields for a particular type.
     *
     * @param type The class whose static fields to get.
     * @return All static fields in . All fields are set to accessible.
     */
    public static Set<Field> getAllStaticFields(Class<?> type) {
        final Set<Field> fields = new LinkedHashSet<Field>();
        final Field[] declaredFields = type.getDeclaredFields();
        for (Field field : declaredFields) {
            if (Modifier.isStatic(field.getModifiers())) {
                field.setAccessible(true);
                fields.add(field);
            }
        }
        return fields;
    }

    /**
     * Checks if is class.
     *
     * @param argument the argument
     * @return true, if is class
     */
    public static boolean isClass(Object argument) {
        return argument instanceof Class<?>;
    }

    /**
     * Check if parameter types are same.
     *
     * @param isVarArgs              Whether or not the method or constructor contains var args.
     * @param expectedParameterTypes the expected parameter types
     * @param actualParameterTypes   the actual parameter types
     * @return if all actual parameter types are assignable from the expected
     * parameter types, otherwise.
     */
    public static boolean checkIfParameterTypesAreSame(boolean isVarArgs, Class<?>[] expectedParameterTypes,
                                                       Class<?>[] actualParameterTypes) {
        return new ParameterTypesMatcher(isVarArgs, expectedParameterTypes, actualParameterTypes).match();
    }

    /**
     * Gets the field.
     *
     * @param fieldName the field name
     * @param where     the where
     * @return the field
     */
    private static Field getField(String fieldName, Class<?> where) {
        if (where == null) {
            throw new IllegalArgumentException("where cannot be null");
        }

        Field field = null;
        try {
            field = where.getDeclaredField(fieldName);
            field.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new FieldNotFoundException("Field '" + fieldName + "' was not found in class " + where.getName()
                                                     + ".");
        }
        return field;
    }

    /**
     * Find field or throw exception.
     *
     * @param fieldType the field type
     * @param where     the where
     * @return the field
     */
    private static Field findFieldOrThrowException(Class<?> fieldType, Class<?> where) {
        if (fieldType == null || where == null) {
            throw new IllegalArgumentException("fieldType and where cannot be null");
        }
        Field field = null;
        for (Field currentField : where.getDeclaredFields()) {
            currentField.setAccessible(true);
            if (currentField.getType().equals(fieldType)) {
                field = currentField;
                break;
            }
        }
        if (field == null) {
            throw new FieldNotFoundException("Cannot find a field of type " + fieldType + "in where.");
        }
        return field;
    }

    /**
     * Sets the field.
     *
     * @param object     the object
     * @param value      the value
     * @param foundField the found field
     */
    private static void setField(Object object, Object value, Field foundField) {
        foundField.setAccessible(true);
        try {
            int fieldModifiersMask = foundField.getModifiers();
            removeFinalModifierIfPresent(foundField);
            foundField.set(object, value);
            restoreModifiersToFieldIfChanged(fieldModifiersMask, foundField);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Internal error: Failed to set field in method setInternalState.", e);
        }
    }

    private static void removeFinalModifierIfPresent(Field fieldToRemoveFinalFrom) throws IllegalAccessException {
        int fieldModifiersMask = fieldToRemoveFinalFrom.getModifiers();
        boolean isFinalModifierPresent = (fieldModifiersMask & Modifier.FINAL) == Modifier.FINAL;
        if (isFinalModifierPresent) {
            checkIfCanSetNewValue(fieldToRemoveFinalFrom);
            int fieldModifiersMaskWithoutFinal = fieldModifiersMask & ~Modifier.FINAL;
            sedModifiersToField(fieldToRemoveFinalFrom, fieldModifiersMaskWithoutFinal);
        }
    }

    private static void checkIfCanSetNewValue(Field fieldToSetNewValueTo) {
        int fieldModifiersMask = fieldToSetNewValueTo.getModifiers();
        boolean isFinalModifierPresent = (fieldModifiersMask & Modifier.FINAL) == Modifier.FINAL;
        boolean isStaticModifierPresent = (fieldModifiersMask & Modifier.STATIC) == Modifier.STATIC;

        if(isFinalModifierPresent && isStaticModifierPresent){
            boolean fieldTypeIsPrimitive = fieldToSetNewValueTo.getType().isPrimitive();
            if (fieldTypeIsPrimitive) {
                throw new IllegalArgumentException("You are trying to set a private static final primitive. Try using an object like Integer instead of int!");
            }
            boolean fieldTypeIsString = fieldToSetNewValueTo.getType().equals(String.class);
            if (fieldTypeIsString) {
                throw new IllegalArgumentException("You are trying to set a private static final String. Cannot set such fields!");
            }
        }
    }

    private static void restoreModifiersToFieldIfChanged(int initialFieldModifiersMask, Field fieldToRestoreModifiersTo) throws IllegalAccessException {
        int newFieldModifiersMask = fieldToRestoreModifiersTo.getModifiers();
        if(initialFieldModifiersMask != newFieldModifiersMask){
            sedModifiersToField(fieldToRestoreModifiersTo, initialFieldModifiersMask);
        }
    }

    private static void sedModifiersToField(Field fieldToRemoveFinalFrom, int fieldModifiersMaskWithoutFinal) throws IllegalAccessException {
        try {
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            boolean accessibleBeforeSet = modifiersField.isAccessible();
            modifiersField.setAccessible(true);
            modifiersField.setInt(fieldToRemoveFinalFrom, fieldModifiersMaskWithoutFinal);
            modifiersField.setAccessible(accessibleBeforeSet);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Internal error: Failed to find the \"modifiers\" field in method setInternalState.", e);
        }
    }

    /**
     * Concatenate strings.
     *
     * @param stringsToConcatenate the strings to concatenate
     * @return the string
     */
    private static String concatenateStrings(String... stringsToConcatenate) {
        StringBuilder builder = new StringBuilder();
        final int stringsLength = stringsToConcatenate.length;
        for (int i = 0; i < stringsLength; i++) {
            if (i == stringsLength - 1 && stringsLength != 1) {
                builder.append(" or ");
            } else if (i != 0) {
                builder.append(", ");
            }
            builder.append(stringsToConcatenate[i]);
        }
        return builder.toString();
    }

    /**
     * Checks if is potential var args method.
     *
     * @param method    the method
     * @param arguments the arguments
     * @return true, if is potential var args method
     */
    private static boolean isPotentialVarArgsMethod(Method method, Object[] arguments) {
        return doesParameterTypesMatchForVarArgsInvocation(method.isVarArgs(), method.getParameterTypes(), arguments);
    }

    /**
     * Does parameter types match for var args invocation.
     *
     * @param isVarArgs      the is var args
     * @param parameterTypes the parameter types
     * @param arguments      the arguments
     * @return true, if successful
     */
     static boolean doesParameterTypesMatchForVarArgsInvocation(boolean isVarArgs, Class<?>[] parameterTypes,
                                                                       Object[] arguments) {
        if (isVarArgs && arguments != null && arguments.length >= 1 && parameterTypes != null
                    && parameterTypes.length >= 1) {
            final Class<?> componentType = parameterTypes[parameterTypes.length - 1].getComponentType();
            final Object lastArgument = arguments[arguments.length - 1];
            if (lastArgument != null) {
                final Class<?> lastArgumentTypeAsPrimitive = getTypeAsPrimitiveIfWrapped(lastArgument);
                final Class<?> varArgsParameterTypeAsPrimitive = getTypeAsPrimitiveIfWrapped(componentType);
                isVarArgs = varArgsParameterTypeAsPrimitive.isAssignableFrom(lastArgumentTypeAsPrimitive);
            }
        }
        return isVarArgs && checkArgumentTypesMatchParameterTypes(isVarArgs, parameterTypes, arguments);
    }

    /**
     * Get the type of an object and convert it to primitive if the type has a
     * primitive counter-part. E.g. if object is an instance of
     * {@code java.lang.Integer} this method will return
     * {@code int.class}.
     *
     * @param object The object whose type to get.
     * @return the type as primitive if wrapped
     */
    static Class<?> getTypeAsPrimitiveIfWrapped(Object object) {
        if (object != null) {
            final Class<?> firstArgumentType = getType(object);
            final Class<?> firstArgumentTypeAsPrimitive = PrimitiveWrapper.hasPrimitiveCounterPart(firstArgumentType) ? PrimitiveWrapper
                                                                                                                                .getPrimitiveFromWrapperType(firstArgumentType) : firstArgumentType;
            return firstArgumentTypeAsPrimitive;
        }
        return null;
    }

    /**
     * Set the values of multiple instance fields defined in a context using
     * reflection. The values in the context will be assigned to values on the
     * {@code instance}. This method will traverse the class hierarchy when
     * searching for the fields. Example usage:
     * 
     * Given:
     * 
     * <pre>
     * public class MyContext {
     * 	private String myString = &quot;myString&quot;;
     * 	protected int myInt = 9;
     * }
     *
     * public class MyInstance {
     * 	private String myInstanceString;
     * 	private int myInstanceInt;
     *
     * }
     * </pre>
     * 
     * then
     * 
     * <pre>
     * Whitebox.setInternalStateFromContext(new MyInstance(), new MyContext());
     * </pre>
     * 
     * will set the instance variables of {@code myInstance} to the values
     * specified in {@code MyContext}.
     *
     * @param object             the object
     * @param context            The context where the fields are defined.
     * @param additionalContexts Optionally more additional contexts.
     */
    public static void setInternalStateFromContext(Object object, Object context, Object[] additionalContexts) {
        setInternalStateFromContext(object, context, FieldMatchingStrategy.MATCHING);
        if (additionalContexts != null && additionalContexts.length > 0) {
            for (Object additionContext : additionalContexts) {
                setInternalStateFromContext(object, additionContext, FieldMatchingStrategy.MATCHING);
            }
        }
    }

    public static void setInternalStateFromContext(Object object, Object context, FieldMatchingStrategy strategy) {
        if (isClass(context)) {
            copyState(object, getType(context), strategy);
        } else {
            copyState(object, context, strategy);
        }
    }

    /**
     * Set the values of multiple static fields defined in a context using
     * reflection. The values in the context will be assigned to values on the
     * {@code classOrInstance}. This method will traverse the class
     * hierarchy when searching for the fields. Example usage:
     * 
     * Given:
     * 
     * <pre>
     * public class MyContext {
     * 	private static String myString = &quot;myString&quot;;
     * 	protected static int myInt = 9;
     * }
     *
     * public class MyInstance {
     * 	private static String myInstanceString;
     * 	private static int myInstanceInt;
     *
     * }
     * </pre>
     * 
     * then
     * 
     * <pre>
     * Whitebox.setInternalStateFromContext(MyInstance.class, MyContext.class);
     * </pre>
     * 
     * will set the static variables of {@code MyInstance} to the values
     * specified in {@code MyContext}.
     *
     * @param object             the object
     * @param context            The context where the fields are defined.
     * @param additionalContexts Optionally more additional contexts.
     */
    public static void setInternalStateFromContext(Object object, Class<?> context, Class<?>[] additionalContexts) {
        setInternalStateFromContext(object, context, FieldMatchingStrategy.MATCHING);
        if (additionalContexts != null && additionalContexts.length > 0) {
            for (Class<?> additionContext : additionalContexts) {
                setInternalStateFromContext(object, additionContext, FieldMatchingStrategy.MATCHING);
            }
        }
    }

    /**
     * Copy state.
     *
     * @param object   the object
     * @param context  the context
     * @param strategy The field matching strategy.
     */
    static void copyState(Object object, Object context, FieldMatchingStrategy strategy) {
        if (object == null) {
            throw new IllegalArgumentException("object to set state cannot be null");
        } else if (context == null) {
            throw new IllegalArgumentException("context cannot be null");
        } else if (strategy == null) {
            throw new IllegalArgumentException("strategy cannot be null");
        }

        Set<Field> allFields = isClass(context) ? getAllStaticFields(getType(context)) : getAllInstanceFields(context);
        for (Field field : allFields) {
            try {
                final boolean isStaticField = Modifier.isStatic(field.getModifiers());
                setInternalState(isStaticField ? getType(object) : object, field.getType(), field.get(context));
            } catch (FieldNotFoundException e) {
                if (strategy == FieldMatchingStrategy.STRICT) {
                    throw e;
                }
            } catch (IllegalAccessException e) {
                // Should never happen
                throw new RuntimeException(
                                                  "Internal Error: Failed to get the field value in method setInternalStateFromContext.", e);
            }
        }
    }

    /**
     * Assert object in get internal state is not null.
     *
     * @param object the object
     */
    private static void assertObjectInGetInternalStateIsNotNull(Object object) {
        if (object == null) {
            throw new IllegalArgumentException("The object containing the field cannot be null");
        }
    }

    /**
     * Convert parameter types to primitive.
     *
     * @param parameterTypes the parameter types
     * @return the class[]
     */
    private static Class<?>[] convertParameterTypesToPrimitive(Class<?>[] parameterTypes) {
        Class<?>[] converted = new Class<?>[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> primitiveWrapperType = PrimitiveWrapper.getPrimitiveFromWrapperType(parameterTypes[i]);
            if (primitiveWrapperType == null) {
                converted[i] = parameterTypes[i];
            } else {
                converted[i] = primitiveWrapperType;
            }
        }
        return converted;
    }
    
    public static <T> void copyToMock(T from, T mock) {
        copy(from, mock, from.getClass());
    }
    
    public static<T> void copyToRealObject(T from, T to) {
        copy(from, to, from.getClass());
    }
    
    private static<T> void copy(T from, T to, Class<?> fromClazz) {
        while (fromClazz != Object.class) {
            copyValues(from, to, fromClazz);
            fromClazz = fromClazz.getSuperclass();
        }
    }
    
    private static<T> void copyValues(T from, T mock, Class<?> classFrom) {
        Field[] fields = classFrom.getDeclaredFields();
        
        for (Field field : fields) {
            // ignore static fields
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            boolean accessible = field.isAccessible();
            try {
                field.setAccessible(true);
                copyValue(from, mock, field);
            } catch (Exception ignored) {
                //Ignore - be lenient - if some field cannot be copied then let's be it
            } finally {
                field.setAccessible(accessible);
            }
        }
    }
    
    private static <T> void copyValue(T from, T to, Field field) throws IllegalAccessException {
        Object value = field.get(from);
        field.set(to, value);
    }

}
