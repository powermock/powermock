package org.powermock.core;

import org.powermock.core.spi.MethodInvocationControl;
import org.powermock.reflect.exceptions.MethodNotFoundException;
import org.powermock.reflect.internal.WhiteboxImpl;
import org.powermock.reflect.internal.proxy.UnproxiedType;

import java.lang.reflect.Method;

class MockInvocation {
    private Object object;
    private String methodName;
    private Class<?>[] sig;
    private Class<?> objectType;
    private MethodInvocationControl methodInvocationControl;
    private Method method;
    
    MockInvocation(Object object, String methodName, Class<?>... sig) {
        this.object = object;
        this.methodName = methodName;
        this.sig = sig;
        init();
    }
    
    private void init() {
        if (object instanceof Class<?>) {
            objectType = (Class<?>) object;
            methodInvocationControl = MockRepository.getStaticMethodInvocationControl(objectType);
        } else {
            final Class<?> type = object.getClass();
            UnproxiedType unproxiedType = WhiteboxImpl.getUnproxiedType(type);
            objectType = unproxiedType.getOriginalType();
            methodInvocationControl = MockRepository.getInstanceMethodInvocationControl(object);
        }
        method = findMethodToInvoke(methodName, sig, objectType);
    }
    
    Class<?> getObjectType() {
        return objectType;
    }
    
    MethodInvocationControl getMethodInvocationControl() {
        return methodInvocationControl;
    }
    
    Method getMethod() {
        return method;
    }
    
    private static Method findMethodToInvoke(String methodName, Class<?>[] sig, Class<?> objectType) {
        /*
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
}
