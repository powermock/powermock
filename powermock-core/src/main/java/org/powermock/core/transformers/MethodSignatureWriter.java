package org.powermock.core.transformers;


import java.lang.reflect.Method;

public interface MethodSignatureWriter<T> {
    String signatureFor(T method);
    
    String signatureForReflection(Method method);
}
