package org.powermock.reflect.internal.proxy;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ProxyFrameworks {

    public Class<?> getUnproxiedType(Class<?> type) {

        if (type == null){
            return null;
        }

        if (isJavaProxy(type)){
            return type.getInterfaces()[0];
        }

        if (isCglibProxyClass(type)) {
            return type.getSuperclass();
        }


        return type;
    }

    private boolean isJavaProxy(Class<?> clazz) {
        return (clazz != null && Proxy.isProxyClass(clazz));
    }

    public Class<?> getUnproxiedType(Object o) {
        if (o == null) {
            throw new IllegalArgumentException("type cannot be null");
        }
        return getUnproxiedType(o.getClass());
    }

    /**
     * Check whether the specified class is a CGLIB-generated class.
     *
     * @param clazz the class to check
     */
    public boolean isCglibProxyClass(Class<?> clazz) {
        if (clazz == null){
            return false;
        }
        Method[] methods = clazz.getDeclaredMethods();
        for(Method m: methods){
            if(isCglibCallbackMethod(m)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isCglibCallbackMethod(Method m) {
        return "CGLIB$SET_THREAD_CALLBACKS".equals(m.getName()) && m.getParameterTypes().length == 1;
    }
}
