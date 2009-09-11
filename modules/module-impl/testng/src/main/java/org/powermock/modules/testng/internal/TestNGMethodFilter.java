package org.powermock.modules.testng.internal;

import java.lang.reflect.Method;

import javassist.util.proxy.MethodFilter;

/**
 *Javassit method filter that ignores the toString method otherwise the test
 * output in Maven looks strange.
 */
public class TestNGMethodFilter implements MethodFilter {
    public boolean isHandled(Method method) {
        return !isToString(method) && !isHashCode(method) && !isFinalize(method);
    }

    private boolean isFinalize(Method method) {
        return method.getName().equals("finalize") && isZeroArgumentMethod(method);
    }

    private boolean isHashCode(Method method) {
        return method.getName().equals("hashCode") && isZeroArgumentMethod(method);
    }

    private boolean isToString(Method method) {
        return (method.getName().equals("toString") && isZeroArgumentMethod(method));
    }

    private boolean isZeroArgumentMethod(Method method) {
        return method.getParameterTypes().length == 0;
    }
}