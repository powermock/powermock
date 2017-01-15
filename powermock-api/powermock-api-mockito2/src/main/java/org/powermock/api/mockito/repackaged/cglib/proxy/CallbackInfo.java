/*
 *  Copyright (c) 2007 Mockito contributors
 *  This program is made available under the terms of the MIT License.
 */
package org.powermock.api.mockito.repackaged.cglib.proxy;

import org.powermock.api.mockito.repackaged.asm.Type;

class CallbackInfo
{
    private static final CallbackInfo[] CALLBACKS = {
        new CallbackInfo(NoOp.class, NoOpGenerator.INSTANCE),
        new CallbackInfo(MethodInterceptor.class, MethodInterceptorGenerator.INSTANCE),
        new CallbackInfo(InvocationHandler.class, InvocationHandlerGenerator.INSTANCE),
        new CallbackInfo(LazyLoader.class, LazyLoaderGenerator.INSTANCE),
        new CallbackInfo(Dispatcher.class, DispatcherGenerator.INSTANCE),
        new CallbackInfo(FixedValue.class, FixedValueGenerator.INSTANCE),
        new CallbackInfo(ProxyRefDispatcher.class, DispatcherGenerator.PROXY_REF_INSTANCE),
    };
    private Class cls;
    private CallbackGenerator generator;

    //////////////////// PRIVATE ////////////////////
    private Type type;
    private CallbackInfo(Class cls, CallbackGenerator generator) {
        this.cls = cls;
        this.generator = generator;
        type = Type.getType(cls);
    }

    public static Type[] determineTypes(Class[] callbackTypes) {
        Type[] types = new Type[callbackTypes.length];
        for (int i = 0; i < types.length; i++) {
            types[i] = determineType(callbackTypes[i]);
        }
        return types;
    }
    
    public static Type[] determineTypes(Callback[] callbacks) {
        Type[] types = new Type[callbacks.length];
        for (int i = 0; i < types.length; i++) {
            types[i] = determineType(callbacks[i]);
        }
        return types;
    }

    public static CallbackGenerator[] getGenerators(Type[] callbackTypes) {
        CallbackGenerator[] generators = new CallbackGenerator[callbackTypes.length];
        for (int i = 0; i < generators.length; i++) {
            generators[i] = getGenerator(callbackTypes[i]);
        }
        return generators;
    }

    private static Type determineType(Callback callback) {
        if (callback == null) {
            throw new IllegalStateException("Callback is null");
        }
        return determineType(callback.getClass());
    }

    private static Type determineType(Class callbackType) {
        Class cur = null;
        for (int i = 0; i < CALLBACKS.length; i++) {
            CallbackInfo info = CALLBACKS[i];
            if (info.cls.isAssignableFrom(callbackType)) {
                if (cur != null) {
                    throw new IllegalStateException("Callback implements both " + cur + " and " + info.cls);
                }
                cur = info.cls;
            }
        }
        if (cur == null) {
            throw new IllegalStateException("Unknown callback type " + callbackType);
        }
        return Type.getType(cur);
    }

    private static CallbackGenerator getGenerator(Type callbackType) {
        for (int i = 0; i < CALLBACKS.length; i++) {
            CallbackInfo info = CALLBACKS[i];
            if (info.type.equals(callbackType)) {
                return info.generator;
            }
        }
        throw new IllegalStateException("Unknown callback type " + callbackType);
    }
}
    

