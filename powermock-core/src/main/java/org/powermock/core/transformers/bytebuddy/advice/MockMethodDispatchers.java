package org.powermock.core.transformers.bytebuddy.advice;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MockMethodDispatchers {
    private static final ConcurrentMap<String, MethodDispatcher> INSTANCE = new ConcurrentHashMap<String, MethodDispatcher>();
    
    public static MethodDispatcher get(String identifier, Object mock) {
        if (mock == INSTANCE) { // Avoid endless loop if ConcurrentHashMap was redefined to check for being a mock.
            return null;
        } else {
            return INSTANCE.get(identifier);
        }
    }
    
    public static void set(String identifier, MethodDispatcher dispatcher) {
        INSTANCE.put(identifier, dispatcher);
    }
}
