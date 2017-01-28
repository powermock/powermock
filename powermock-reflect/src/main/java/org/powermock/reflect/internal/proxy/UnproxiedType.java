package org.powermock.reflect.internal.proxy;

public interface UnproxiedType {
    Class<?> getOriginalType();
    
    Class<?>[] getInterfaces();
}
