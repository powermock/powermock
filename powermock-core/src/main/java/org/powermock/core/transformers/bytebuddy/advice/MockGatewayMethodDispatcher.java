package org.powermock.core.transformers.bytebuddy.advice;

import org.powermock.core.MockGateway;

import java.util.concurrent.Callable;

public class MockGatewayMethodDispatcher implements MethodDispatcher {
    public Callable<Object> methodCall(Object instance, String methodName, Object[] args, Class<?>[] sig,
                                       String returnTypeAsString) throws Throwable {
        final Object result = handleMethodCall(instance, methodName, args, sig, returnTypeAsString);
        if (result == MockGateway.PROCEED) {
            return null;
        } else {
            return new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    return result;
                }
            };
        }
    }
    
    protected Object handleMethodCall(final Object instance, final String methodName, final Object[] args,
                                      final Class<?>[] sig, final String returnTypeAsString) throws Throwable {
        return MockGateway.methodCall(instance, methodName, args, sig, returnTypeAsString);
    }
}
