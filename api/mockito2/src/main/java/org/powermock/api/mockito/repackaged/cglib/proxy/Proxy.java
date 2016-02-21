/*
 *  Copyright (c) 2007 Mockito contributors
 *  This program is made available under the terms of the MIT License.
 */
package org.powermock.api.mockito.repackaged.cglib.proxy;


import org.powermock.api.mockito.repackaged.cglib.core.CodeGenerationException;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;

/**
 * This class is meant to be used as replacement for
 * <code>java.lang.reflect.Proxy</code> under JDK 1.2. There are some known
 * subtle differences:
 * <ul>
 * <li>The exceptions returned by invoking <code>getExceptionTypes</code>
 * on the <code>Method</code> passed to the <code>invoke</code> method
 * <b>are</b> the exact set that can be thrown without resulting in an
 * <code>UndeclaredThrowableException</code> being thrown.
 * <li>{@link UndeclaredThrowableException} is used instead
 * of <code>java.lang.reflect.UndeclaredThrowableException</code>.
 * </ul>
 * <p>
 * @version $Id: Proxy.java,v 1.6 2004/06/24 21:15:19 herbyderby Exp $
 */
public class Proxy implements Serializable {
    private static final CallbackFilter BAD_OBJECT_METHOD_FILTER = new CallbackFilter() {
        public int accept(Method method, List<Method> allMethods) {
            if (method.getDeclaringClass().getName().equals("java.lang.Object")) {
                String name = method.getName();
                if (!(name.equals("hashCode") ||
                      name.equals("equals") ||
                      name.equals("toString"))) {
                    return 1;
                }
            }
            return 0;
        }
    };
    protected InvocationHandler h;

    protected Proxy(InvocationHandler h) {
        Enhancer.registerCallbacks(getClass(), new Callback[]{ h, null });
        this.h = h;
    }

    public static InvocationHandler getInvocationHandler(Object proxy) {
        if (!(proxy instanceof ProxyImpl)) {
            throw new IllegalArgumentException("Object is not a proxy");
        }
        return ((Proxy)proxy).h;
    }

    public static Class getProxyClass(ClassLoader loader, Class[] interfaces) {
        Enhancer e = new Enhancer();
        e.setSuperclass(ProxyImpl.class);
        e.setInterfaces(interfaces);
        e.setCallbackTypes(new Class[]{
            InvocationHandler.class,
            NoOp.class,
        });
        e.setCallbackFilter(BAD_OBJECT_METHOD_FILTER);
        e.setUseFactory(false);
        return e.createClass();
    }

    public static boolean isProxyClass(Class cl) {
        return cl.getSuperclass().equals(ProxyImpl.class);
    }

    public static Object newProxyInstance(ClassLoader loader, Class[] interfaces, InvocationHandler h) {
        try {
            Class clazz = getProxyClass(loader, interfaces);
            return clazz.getConstructor(new Class[]{ InvocationHandler.class }).newInstance(new Object[]{ h });
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new CodeGenerationException(e);
        }
    }

    // private for security of isProxyClass
    private static class ProxyImpl extends Proxy {
        protected ProxyImpl(InvocationHandler h) {
            super(h);
        }
    }
}
