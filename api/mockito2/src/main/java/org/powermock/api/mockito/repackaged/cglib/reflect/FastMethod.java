/*
 *  Copyright (c) 2007 Mockito contributors
 *  This program is made available under the terms of the MIT License.
 */
package org.powermock.api.mockito.repackaged.cglib.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class FastMethod extends FastMember
{
    FastMethod(FastClass fc, Method method) {
        super(fc, method, helper(fc, method));
    }

    private static int helper(FastClass fc, Method method) {
        int index = fc.getIndex(method.getName(), method.getParameterTypes());
        if (index < 0) {
            Class[] types = method.getParameterTypes();
            System.err.println("hash=" + method.getName().hashCode() + " size=" + types.length);
            for (int i = 0; i < types.length; i++) {
                System.err.println("  types[" + i + "]=" + types[i].getName());
            }
            throw new IllegalArgumentException("Cannot find method " + method);
        }
        return index;
    }

    public Class getReturnType() {
        return ((Method)member).getReturnType();
    }

    public Class[] getParameterTypes() {
        return ((Method)member).getParameterTypes();
    }

    public Class[] getExceptionTypes() {
        return ((Method)member).getExceptionTypes();
    }

    public Object invoke(Object obj, Object[] args) throws InvocationTargetException {
        return fc.invoke(index, obj, args);
    }

    public Method getJavaMethod() {
        return (Method)member;
    }
}
