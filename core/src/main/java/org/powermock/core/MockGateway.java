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

import org.powermock.core.spi.MethodInvocationControl;
import org.powermock.core.spi.NewInvocationControl;
import org.powermock.reflect.exceptions.MethodNotFoundException;
import org.powermock.reflect.internal.TypeUtils;
import org.powermock.reflect.internal.WhiteboxImpl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * All mock invocations are routed through this gateway. This includes method
 * calls, construction of new instances and more. Do not use this class
 * directly, but always go through the PowerMock facade.
 */
public class MockGateway {

    private static boolean noMockito = Package.getPackage("org.mockito") == null;
    public static final Object PROCEED = new Object();
    public static final Object SUPPRESS = new Object();

    /**
     * Used to tell the MockGateway that the next call should not be mocked
     * regardless if a {@link MethodInvocationControl} is found in the
     * {@link MockRepository}. Used to allow for e.g. recursive partial mocking.
     */
    public static final String DONT_MOCK_NEXT_CALL = "DontMockNextCall";

    /**
     * Tells PowerMock to mock standard methods. These are
     * {@link Object#toString()}, {@link Object#hashCode()} and
     * {@link Object#equals(Object)}. By default this is <code>true</code>.
     */
    public static boolean MOCK_STANDARD_METHODS = true;

    @SuppressWarnings("UnusedDeclaration")
    public static Object newInstanceCall(Class<?> type, Object[] args, Class<?>[] sig) throws Throwable {
        final NewInvocationControl<?> newInvocationControl = MockRepository.getNewInstanceControl(type);
        if (newInvocationControl != null) {
            /*
             * We need to deal with inner, local and anonymous inner classes
			 * specifically. For example when new is invoked on an inner class
			 * it seems like null is passed as an argument even though it
			 * shouldn't. We correct this here.
			 *
			 * Seems with Javassist 3.17.1-GA & Java 7, the 'null' is passed as the last argument.
			 */
            if (type.isMemberClass() && Modifier.isStatic(type.getModifiers())) {
                if (args.length > 0 && (args[0] == null || args[args.length - 1] == null) && sig.length > 0) {
                    args = copyArgumentsForInnerOrLocalOrAnonymousClass(args, false);
                }
            } else if (type.isLocalClass() || type.isAnonymousClass() || type.isMemberClass()) {
                if (args.length > 0 && sig.length > 0 && sig[0].equals(type.getEnclosingClass())) {
                    args = copyArgumentsForInnerOrLocalOrAnonymousClass(args, true);
                }
            }
            return newInvocationControl.invoke(type, args, sig);
        }
        // Check if we should suppress the constructor code
        if (MockRepository.shouldSuppressConstructor(WhiteboxImpl.getConstructor(type, sig))) {
            return WhiteboxImpl.getFirstParentConstructor(type);
        }
        return PROCEED;
    }

    @SuppressWarnings("UnusedDeclaration")
    public static Object fieldCall(Object instanceOrClassContainingTheField, Class<?> classDefiningField,
                                   String fieldName, Class<?> fieldType) {
        if (MockRepository.shouldSuppressField(WhiteboxImpl.getField(classDefiningField, fieldName))) {
            return TypeUtils.getDefaultValue(fieldType);
        }
        return PROCEED;
    }

    public static Object staticConstructorCall(String className) {
        if (MockRepository.shouldSuppressStaticInitializerFor(className)) {
            return "suppress";
        }
        return PROCEED;
    }

    @SuppressWarnings("UnusedDeclaration")
    public static Object constructorCall(Class<?> type, Object[] args, Class<?>[] sig) throws Throwable {
        final Constructor<?> constructor = WhiteboxImpl.getConstructor(type, sig);
        if (MockRepository.shouldSuppressConstructor(constructor)) {
            return null;
        }
        return PROCEED;
    }

    /**
     * Tells PowerMock whether or not to mock
     * {@link java.lang.Object#getClass()}.
     */
    public static boolean MOCK_GET_CLASS_METHOD = false;

    /**
     * Tells PowerMock whether or not to mock
     * {@link java.lang.Class#isAnnotationPresent(Class)} and
     * {@link java.lang.Class#getAnnotation(Class)}.
     */
    public static boolean MOCK_ANNOTATION_METHODS = false;

    // used for instance methods
    @SuppressWarnings("UnusedDeclaration")
    public static Object methodCall(Object instance, String methodName, Object[] args, Class<?>[] sig,
                                    String returnTypeAsString) throws Throwable {
        return doMethodCall(instance, methodName, args, sig, returnTypeAsString);
    }

    // used for static methods
    @SuppressWarnings("UnusedDeclaration")
    public static Object methodCall(Class<?> type, String methodName, Object[] args, Class<?>[] sig,
                                    String returnTypeAsString) throws Throwable {
        return doMethodCall(type, methodName, args, sig, returnTypeAsString);
    }


    private static Object doMethodCall(Object object, String methodName, Object[] args, Class<?>[] sig,
                                       String returnTypeAsString) throws Throwable {
        if (!shouldMockMethod(methodName, sig)) {
            return PROCEED;
        }

        MockInvocation mockInvocation = new MockInvocation(object, methodName, sig);
        MethodInvocationControl methodInvocationControl = mockInvocation.getMethodInvocationControl();
        Object returnValue = null;

        // The following describes the equals method.

        if (isEqualsMethod(mockInvocation)) {
            returnValue = tryHandleEqualsMethod(mockInvocation);
        }

        if (returnValue != null) {
            return returnValue;
        }
        return doMethodCall(object, args, returnTypeAsString, mockInvocation, methodInvocationControl);
    }

    private static Object doMethodCall(Object object, Object[] args,
                                       String returnTypeAsString,
                                       MockInvocation mockInvocation,
                                       MethodInvocationControl methodInvocationControl) throws Throwable {
        Object returnValue;

        // At first should be checked that method not suppressed/stubbed, because otherwise for spies real
        // method is involved.
        // https://github.com/jayway/powermock/issues/327

        if (MockRepository.shouldSuppressMethod(mockInvocation.getMethod(), mockInvocation.getObjectType())) {
            returnValue = TypeUtils.getDefaultValue(returnTypeAsString);
        } else if (MockRepository.shouldStubMethod(mockInvocation.getMethod())) {
            returnValue = MockRepository.getMethodToStub(mockInvocation.getMethod());
        } else if (methodInvocationControl != null && methodInvocationControl.isMocked(mockInvocation.getMethod()) && shouldMockThisCall()) {
            returnValue = methodInvocationControl.invoke(object, mockInvocation.getMethod(), args);
            if (returnValue == SUPPRESS) {
                returnValue = TypeUtils.getDefaultValue(returnTypeAsString);
            }
        } else if (MockRepository.hasMethodProxy(mockInvocation.getMethod())) {
                /*
                 * We must temporary remove the method proxy when invoking the
                 * invocation handler because if the invocation handler delegates
                 * the call we will end up here again and we'll get a
                 * StackOverflowError.
                 */
            final InvocationHandler invocationHandler = MockRepository.removeMethodProxy(mockInvocation.getMethod());
            try {
                returnValue = invocationHandler.invoke(object, mockInvocation.getMethod(), args);
            } finally {
                // Set the method proxy again after the invocation
                MockRepository.putMethodProxy(mockInvocation.getMethod(), invocationHandler);
            }
        } else {
            returnValue = PROCEED;
        }
        return returnValue;
    }

    /*
     * Method handles exception cases with equals method.
     */
    private static Object tryHandleEqualsMethod(MockInvocation mockInvocation) {

        // Fix for Issue http://code.google.com/p/powermock/issues/detail?id=88
        // For some reason the method call to equals() on final methods is
        // intercepted and during the further processing in Mockito the same
        // equals() method is called on the same instance. A StackOverflowError
        // is the result. The following fix changes this by checking if the
        // method to be called is a final equals() method. In that case the
        // original method is called by returning PROCEED.

        if (mockInvocation.getMethod().getParameterTypes().length == 1
                    && mockInvocation.getMethod().getParameterTypes()[0] == Object.class
                    && Modifier.isFinal(mockInvocation.getMethod().getModifiers())) {
            return PROCEED;
        }

        if (calledFromMockito()){
            return PROCEED;
        }

        return null;
    }


    private static boolean isEqualsMethod(MockInvocation mockInvocation) {
        return "equals".equals(mockInvocation.getMethod().getName());
    }

    private static boolean calledFromMockito() {
        if (noMockito) {
            return false;
        }
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace) {
            if (stackTraceElement.getClassName().startsWith("org.mockito.")){
                return true;
            }
        }
        return false;
    }

    private static boolean shouldMockMethod(String methodName, Class<?>[] sig) {
        if (isJavaStandardMethod(methodName, sig) && !MOCK_STANDARD_METHODS) {
            return false;
        } else if (isGetClassMethod(methodName, sig) && !MOCK_GET_CLASS_METHOD) {
            return false;
        } else { return !(isAnnotationMethod(methodName, sig) && !MOCK_ANNOTATION_METHODS); }
    }

    private static boolean isJavaStandardMethod(String methodName, Class<?>[] sig) {
        return (methodName.equals("equals") && sig.length == 1) || (methodName.equals("hashCode") && sig.length == 0)
                       || (methodName.equals("toString") && sig.length == 0);
    }

    private static boolean isGetClassMethod(String methodName, Class<?>[] sig) {
        return methodName.equals("getClass") && sig.length == 0;
    }

    private static boolean isAnnotationMethod(String methodName, Class<?>[] sig) {
        return (methodName.equals("isAnnotationPresent") && sig.length == 1) || (methodName.equals("getAnnotation") && sig.length == 1);
    }

    private static boolean shouldMockThisCall() {
        Object shouldSkipMockingOfNextCall = MockRepository.getAdditionalState(DONT_MOCK_NEXT_CALL);
        final boolean shouldMockThisCall = shouldSkipMockingOfNextCall == null;
        MockRepository.removeAdditionalState(DONT_MOCK_NEXT_CALL);
        return shouldMockThisCall;
    }

    /**
     * The first parameter of an inner, local or anonymous inner class is
     * <code>null</code> or the enclosing instance. This should not be included
     * in the substitute invocation since it is never expected by the user.
     * <p/>
     * Seems with Javassist 3.17.1-GA & Java 7, the '<code>null</code>' is passed as the last argument.
     */
    private static Object[] copyArgumentsForInnerOrLocalOrAnonymousClass(Object[] args,
                                                                         boolean excludeEnclosingInstance) {
        Object[] newArgs = new Object[args.length - 1];
        final int start;
        final int end;
        int j = 0;

        if (args[0] == null || excludeEnclosingInstance) {
            start = 1;
            end = args.length;
        } else {
            start = 0;
            end = args.length - 1;
        }

        for (int i = start; i < end; i++) {
            newArgs[j++] = args[i];
        }
        args = newArgs;
        return args;
    }

    private static class MockInvocation {
        private Object object;
        private String methodName;
        private Class<?>[] sig;
        private Class<?> objectType;
        private MethodInvocationControl methodInvocationControl;
        private Method method;

        public MockInvocation(Object object, String methodName, Class<?>... sig) {
            this.object = object;
            this.methodName = methodName;
            this.sig = sig;
            init();
        }

        private static Method findMethodToInvoke(String methodName, Class<?>[] sig, Class<?> objectType) {/*
            * if invocationControl is null or the method is not mocked, invoke
            * original method or suppress the method code otherwise invoke the
            * invocation handler.
            */
            Method method;
            try {
                method = WhiteboxImpl.getBestMethodCandidate(objectType, methodName, sig, true);
            } catch (MethodNotFoundException e) {
                /*
                 * Dirty hack to get around issue 110
                 * (http://code.google.com/p/powermock/issues/detail?id=110). Review
                 * this! What we do here is to try to find a reflective method on
                 * class. This has begun to fail since version 1.2 when we supported
                 * mocking static methods in system classes.
                 */
                try {
                    method = WhiteboxImpl.getMethod(Class.class, methodName, sig);
                } catch (MethodNotFoundException e2) {
                    throw e;
                }
            }
            return method;
        }

        public Class<?> getObjectType() {
            return objectType;
        }

        public MethodInvocationControl getMethodInvocationControl() {
            return methodInvocationControl;
        }

        public Method getMethod() {
            return method;
        }

        private void init() {
            if (object instanceof Class<?>) {
                objectType = (Class<?>) object;
                methodInvocationControl = MockRepository.getStaticMethodInvocationControl(objectType);
            } else {
                final Class<?> type = object.getClass();
                objectType = WhiteboxImpl.getUnmockedType(type);
                methodInvocationControl = MockRepository.getInstanceMethodInvocationControl(object);
            }
            method = findMethodToInvoke(methodName, sig, objectType);
        }
    }
}
