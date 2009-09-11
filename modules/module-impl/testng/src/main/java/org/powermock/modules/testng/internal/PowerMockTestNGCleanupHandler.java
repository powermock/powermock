package org.powermock.modules.testng.internal;

import java.lang.reflect.Method;

import javassist.util.proxy.MethodHandler;

import org.powermock.core.MockRepository;
import org.testng.annotations.Test;

/**
 * Javassist handler that takes care of cleaing up {@link MockRepository} state
 * after each method annotated with {@link Test}.
 */
public class PowerMockTestNGCleanupHandler implements MethodHandler {

    public PowerMockTestNGCleanupHandler() throws ClassNotFoundException {
    }

    public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
        final Object result = proceed.invoke(self, args);
        if (thisMethod.isAnnotationPresent(Test.class)) {
            MockRepository.clear();
        }
        return result;
    }
}
