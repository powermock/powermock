package org.powermock.core.transformers.mock;

import org.powermock.core.transformers.bytebuddy.advice.MockGatewayMethodDispatcher;

public class MockGatewaySpyMethodDispatcher extends MockGatewayMethodDispatcher {
    protected Object handleMethodCall(final Object instance, final String methodName, final Object[] args,
                                      final Class<?>[] sig, final String returnTypeAsString) throws Throwable {
        return MockGatewaySpy.methodCall(instance, methodName, args, sig, returnTypeAsString);
    }
}
