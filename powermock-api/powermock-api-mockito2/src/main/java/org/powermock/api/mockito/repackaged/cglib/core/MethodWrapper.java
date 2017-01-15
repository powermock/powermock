/*
 *  Copyright (c) 2007 Mockito contributors
 *  This program is made available under the terms of the MIT License.
 */
package org.powermock.api.mockito.repackaged.cglib.core;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MethodWrapper {
    private static final MethodWrapperKey KEY_FACTORY =
      (MethodWrapperKey)KeyFactory.create(MethodWrapperKey.class);

    private MethodWrapper() {
    }
    
    public static Object create(Method method) {
        return KEY_FACTORY.newInstance(method.getName(),
                                       ReflectUtils.getNames(method.getParameterTypes()),
                                       method.getReturnType().getName());
    }

    public static Set createSet(Collection methods) {
        Set set = new HashSet();
        for (Iterator it = methods.iterator(); it.hasNext();) {
            set.add(create((Method)it.next()));
        }
        return set;
    }

    /** Internal interface, only public due to ClassLoader issues. */
    public interface MethodWrapperKey {
        public Object newInstance(String name, String[] parameterTypes, String returnType);
    }
}
