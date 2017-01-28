package org.powermock.reflect.internal.proxy;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ProxyFrameworks {
    
    private static final UnproxiedTypeFactory UNPROXIED_TYPE_FACTORY = new UnproxiedTypeFactory();

    public UnproxiedType getUnproxiedType(Class<?> type) {

        if (type == null){
            return null;
        }

        if (isJavaProxy(type)){
            return UNPROXIED_TYPE_FACTORY.createFromInterfaces(type.getInterfaces());
        }

        if (isCglibProxyClass(type)) {
            return UNPROXIED_TYPE_FACTORY.createFromSuperclassAndInterfaces(type.getSuperclass(), type.getInterfaces());
        }

        return UNPROXIED_TYPE_FACTORY.createFromType(type);
    }
    
    public UnproxiedType getUnproxiedType(Object o) {
        if (o == null) {
            return null;
        }
        return getUnproxiedType(o.getClass());
    }

    private boolean isJavaProxy(Class<?> clazz) {
        return (clazz != null && Proxy.isProxyClass(clazz));
    }

    private boolean isCglibProxyClass(Class<?> clazz) {
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
