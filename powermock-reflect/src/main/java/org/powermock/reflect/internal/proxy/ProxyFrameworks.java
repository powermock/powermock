package org.powermock.reflect.internal.proxy;

import java.lang.reflect.Proxy;

/**
 *
 */
public class ProxyFrameworks {

    /**
     * The CGLIB class separator character "$$"
     */
    public static final String CGLIB_CLASS_SEPARATOR = "$$";

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
        return (clazz != null && isCglibProxyClassName(clazz.getName()));
    }

    /**
     * Check whether the specified class name is a CGLIB-generated class.
     *
     * @param className the class name to check
     */
    public boolean isCglibProxyClassName(String className) {
        return (className != null && className.contains(CGLIB_CLASS_SEPARATOR));
    }
}
