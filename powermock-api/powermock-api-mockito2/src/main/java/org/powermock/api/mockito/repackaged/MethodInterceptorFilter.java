/*
 *  Copyright (c) 2007 Mockito contributors
 *  This program is made available under the terms of the MIT License.
 */

package org.powermock.api.mockito.repackaged;

import org.mockito.internal.InternalMockHandler;
import org.mockito.internal.creation.DelegatingMethod;
import org.mockito.internal.creation.util.MockitoMethodProxy;
import org.mockito.internal.debugging.LocationImpl;
import org.mockito.internal.invocation.InvocationImpl;
import org.mockito.internal.invocation.MockitoMethod;
import org.mockito.internal.invocation.SerializableMethod;
import org.mockito.internal.invocation.realmethod.CleanTraceRealMethod;
import org.mockito.internal.progress.SequenceNumber;
import org.mockito.invocation.Invocation;
import org.mockito.invocation.MockHandler;
import org.mockito.mock.MockCreationSettings;
import org.powermock.api.mockito.repackaged.cglib.proxy.MethodInterceptor;
import org.powermock.api.mockito.repackaged.cglib.proxy.MethodProxy;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * Should be one instance per mock instance, see CglibMockMaker.
 */
public class MethodInterceptorFilter implements MethodInterceptor, Serializable {

    private static final long serialVersionUID = 6182795666612683784L;
    private final InternalMockHandler handler;
    private final MockCreationSettings mockSettings;
    private final AcrossJVMSerializationFeature acrossJVMSerializationFeature = new AcrossJVMSerializationFeature();

    public MethodInterceptorFilter(InternalMockHandler handler, MockCreationSettings mockSettings) {
        this.handler = handler;
        this.mockSettings = mockSettings;
    }

    public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy)
            throws Throwable {
        if (isEqualsMethod(method)) {
            return proxy == args[0];
        } else if (isHashCodeMethod(method)) {
            return hashCodeForMock(proxy);
        } else if (acrossJVMSerializationFeature.isWriteReplace(method)) {
            return acrossJVMSerializationFeature.writeReplace(proxy);
        }
        
        MockitoMethodProxy mockitoMethodProxy = createMockitoMethodProxy(methodProxy);
        new CGLIBHacker().setMockitoNamingPolicy(methodProxy);
        
        MockitoMethod mockitoMethod = createMockitoMethod(method);
        
        CleanTraceRealMethod realMethod = new CleanTraceRealMethod(mockitoMethodProxy);
        Invocation invocation = new InvocationImpl(proxy, mockitoMethod, args, SequenceNumber.next(), realMethod, new LocationImpl());
        return handler.handle(invocation);
    }

    private static boolean isEqualsMethod(Method method) {
        return method.getName().equals("equals")
                && method.getParameterTypes().length == 1
                && method.getParameterTypes()[0] == Object.class;
    }

    private static boolean isHashCodeMethod(Method method) {
        return method.getName().equals("hashCode")
                && method.getParameterTypes().length == 0;
    }

    public MockHandler getHandler() {
        return handler;
    }

    private int hashCodeForMock(Object mock) {
        return System.identityHashCode(mock);
    }

    public MockitoMethodProxy createMockitoMethodProxy(MethodProxy methodProxy) {
        if (mockSettings.isSerializable())
            return new SerializableMockitoMethodProxy(methodProxy);
        return new DelegatingMockitoMethodProxy(methodProxy);
    }
    
    public MockitoMethod createMockitoMethod(Method method) {
        if (mockSettings.isSerializable()) {
            return new SerializableMethod(method);
        } else {
            return new DelegatingMethod(method);
        }
    }
}