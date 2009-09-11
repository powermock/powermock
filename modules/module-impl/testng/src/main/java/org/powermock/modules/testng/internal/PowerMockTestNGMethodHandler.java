package org.powermock.modules.testng.internal;

import java.lang.reflect.Method;

import javassist.util.proxy.MethodHandler;

import org.powermock.core.MockRepository;
import org.powermock.reflect.Whitebox;
import org.testng.annotations.Test;

/**
 * Javassist handler that takes care of cleaing up {@link MockRepository} state
 * after each method annotated with {@link Test}.
 */
public class PowerMockTestNGMethodHandler implements MethodHandler {

    private Object annotationEnabler;

    public PowerMockTestNGMethodHandler(Class<?> testClass) {
        try {
            Class<?> annotationEnablerClass = Class.forName("org.powermock.api.extensions.listener.AnnotationEnabler");
            annotationEnabler = Whitebox.newInstance(annotationEnablerClass);
        } catch (ClassNotFoundException e) {
            annotationEnabler = null;
        }
    }

    public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
        injectMocksUsingAnnotationEnabler(self);
        final Object result = proceed.invoke(self, args);
        if (thisMethod.isAnnotationPresent(Test.class)) {
            MockRepository.clear();
        }
        return result;
    }

    private void injectMocksUsingAnnotationEnabler(Object self) throws Exception {
        if (annotationEnabler != null) {
            Whitebox.invokeMethod(annotationEnabler, "beforeTestMethod", new Class<?>[] { Object.class, Method.class, Object[].class }, self, null,
                    null);
        }
    }
}
