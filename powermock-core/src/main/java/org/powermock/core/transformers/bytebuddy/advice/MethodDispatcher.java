package org.powermock.core.transformers.bytebuddy.advice;

import java.util.concurrent.Callable;

public interface MethodDispatcher {
    Callable<Object> methodCall(Object instance, String methodName, Object[] args, Class<?>[] sig, String returnTypeAsString) throws Throwable;
}
