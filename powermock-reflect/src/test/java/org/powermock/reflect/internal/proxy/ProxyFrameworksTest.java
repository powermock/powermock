package org.powermock.reflect.internal.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;
import net.sf.cglib.proxy.InvocationHandler;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URLClassLoader;

import static org.assertj.core.api.Assertions.assertThat;

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
    
    @Test
    public void should_return_null_if_object_is_null() throws Exception {
        assertThat(proxyFrameworks.getUnproxiedType((Object) null)).isNull();
    }
    
    @Test
    public void should_return_original_class_if_object_not_proxy() throws Exception {
        SomeClass someClass = new SomeClass();
        
        UnproxiedType unproxiedType = proxyFrameworks.getUnproxiedType(someClass);
        
        assertThatOriginalTypeInstanceOf(unproxiedType, SomeClass.class);
    }
    
    @Test
    public void should_return_original_class_if_proxy_created_with_java() {
        SomeInterface someInterface = createJavaProxy(new Class[]{ SomeInterface.class });
        
        UnproxiedType unproxiedType = proxyFrameworks.getUnproxiedType(someInterface);
        
        assertThatOriginalTypeInstanceOf(unproxiedType, SomeInterface.class);
    }
    
    @Test
    public void should_return_original_class_if_proxy_created_with_cglib() {
        SomeClass someClass = (SomeClass) createCglibProxy(SomeClass.class);
        
        UnproxiedType unproxiedType = proxyFrameworks.getUnproxiedType(someClass);
        
        assertThatOriginalTypeInstanceOf(unproxiedType, SomeClass.class);
    }
    
    @Test
    public void should_not_detect_synthetic_classes_as_cglib_proxy() throws Exception {
        String className = "Some$$SyntheticClass$$Lambda";
        byte[] bytes = ClassFactory.create(className);
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        CustomClassLoader customClassLoader = new CustomClassLoader(classLoader);
        
        Class<?> defineClass = customClassLoader.defineClass(className, bytes);
        
        UnproxiedType unproxiedType = proxyFrameworks.getUnproxiedType(defineClass.newInstance());
        
        assertThatOriginalTypeInstanceOf(unproxiedType, defineClass);
    }
    
    @Test
    public void should_return_object_as_original_class_if_no_non_no_mocking_interfaces() {
        Factory someClass = (Factory) createCglibProxy(Factory.class);
        
        UnproxiedType unproxiedType = proxyFrameworks.getUnproxiedType(someClass);
        
        assertThatOriginalTypeInstanceOf(unproxiedType, Object.class);
    }
    
    @Test
    public void should_return_interface_as_original_type_if_only_one_non_mocking_interface() {
        Factory someClass = (Factory) createCglibProxy(Factory.class, SomeInterface.class);
        
        UnproxiedType unproxiedType = proxyFrameworks.getUnproxiedType(someClass);
        
        assertThatOriginalTypeInstanceOf(unproxiedType, SomeInterface.class);
    }
    
    @Test
    public void should_return_interface_and_original_type_if_proxy_has_interface_and_superclass() {
        SomeClass someClass = (SomeClass) createCglibProxy(SomeClass.class, SomeInterface.class, AnotherInterface.class);
        
        UnproxiedType unproxiedType = proxyFrameworks.getUnproxiedType(someClass);
    
        assertThatOriginalTypeInstanceOfAndInterfaces(unproxiedType, SomeClass.class, new Class[]{
                SomeInterface.class,
                AnotherInterface.class
        });
    }
    
    @Test
    public void should_return_interfaces_if_proxy_create_from_several_interfaces() {
        
        Class[] interfaces = {SomeInterface.class, AnotherInterface.class};
        
        SomeInterface someInterface = createJavaProxy(interfaces);
        
        UnproxiedType unproxiedType = proxyFrameworks.getUnproxiedType(someInterface);
        
        assertThatOriginalIsNullAndInterfaces(
                unproxiedType, interfaces
        );
    }
    
    private Object createCglibProxy(Class<?> superclass, Class... interfaces) {
        if (interfaces.length == 0){
            return Enhancer.create(
                    superclass,
                    new InvocationHandler() {
                        @Override
                        public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                            return method.invoke(o, objects);
                        }
                    });
        }else {
            return Enhancer.create(
                    superclass,
                    interfaces,
                    new InvocationHandler() {
                        @Override
                        public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                            return method.invoke(o, objects);
                        }
                    });
        }
    }
    
    private SomeInterface createJavaProxy(Class[] interfaces) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return (SomeInterface) Proxy.newProxyInstance(
                classLoader,
                interfaces,
                new java.lang.reflect.InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        return method.invoke(proxy, args);
                    }
                });
    }
    
    private void assertThatOriginalIsNullAndInterfaces(UnproxiedType unproxiedType, Class[] expectedInterfaces) {
        assertThat(unproxiedType.getOriginalType()).isNull();
        assertThat(unproxiedType.getInterfaces())
                .containsExactlyInAnyOrder(expectedInterfaces);
    }
    
    private void assertThatOriginalTypeInstanceOf(UnproxiedType unproxiedType, Class<?> expectedClass) {
        assertThat(unproxiedType.getOriginalType()).isEqualTo(expectedClass);
        assertThat(unproxiedType.getInterfaces()).isEmpty();
    }
    
    private void assertThatOriginalTypeInstanceOfAndInterfaces(UnproxiedType unproxiedType, Class<?> expectedClass,Class[] expectedInterfaces ) {
        assertThat(unproxiedType.getOriginalType()).isEqualTo(expectedClass);
        assertThat(unproxiedType.getInterfaces())
                .containsExactlyInAnyOrder(expectedInterfaces);
    }
    
    private static class CustomClassLoader extends URLClassLoader {
        
        private CustomClassLoader(ClassLoader parent) {
            super(((URLClassLoader) parent).getURLs(), parent);
        }
        
        Class<?> defineClass(String name, byte[] b) {
            return defineClass(name, b, 0, b.length);
        }
    }
    
}