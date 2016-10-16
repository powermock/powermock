package org.powermock.reflect.internal.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.InvocationHandler;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 */
public class ProxyFrameworksTest {
    
    private ProxyFrameworks proxyFrameworks;

    @Before
    public void setUp() throws Exception {
        proxyFrameworks = new ProxyFrameworks();
    }

    @Test
    public void should_throw_illegal_argument_exception_if_class_is_null() throws Exception {
        assertThat(proxyFrameworks.getUnproxiedType(null)).isNull();
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_return_null_if_object_is_null() throws Exception {
        assertThat(proxyFrameworks.getUnproxiedType((Object) null)).isNull();
    }

    @Test
    public void should_return_original_class_if_object_not_proxy() throws Exception {
        SomeClass someClass = new SomeClass();
        assertThat(proxyFrameworks.getUnproxiedType(someClass)).isEqualTo(SomeClass.class);
    }

    @Test
    public void should_return_original_class_if_proxy_created_with_java() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        SomeInterface someInterface = (SomeInterface) Proxy.newProxyInstance(classLoader, new Class[]{SomeInterface.class}, new java.lang.reflect.InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return method.invoke(proxy, args);
            }
        });
        assertThat(proxyFrameworks.getUnproxiedType(someInterface)).isEqualTo(SomeInterface.class);
    }

    @Test
    public void should_return_original_class_if_proxy_created_with_cglib() {
        SomeClass someClass = (SomeClass) Enhancer.create(SomeClass.class, new InvocationHandler() {
            @Override
            public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                return method.invoke(o, objects);
            }
        });
        assertThat(proxyFrameworks.getUnproxiedType(someClass)).isEqualTo(SomeClass.class);
    }
}