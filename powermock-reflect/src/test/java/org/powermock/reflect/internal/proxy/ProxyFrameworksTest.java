package org.powermock.reflect.internal.proxy;

import net.sf.cglib.asm.AnnotationVisitor;
import net.sf.cglib.asm.ClassWriter;
import net.sf.cglib.asm.FieldVisitor;
import net.sf.cglib.asm.MethodVisitor;
import net.sf.cglib.asm.Opcodes;
import net.sf.cglib.proxy.Enhancer;
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
    
    @Test
    public void should_not_detect_synthetic_classes_as_cglib_proxy() throws Exception {
        String className = "Some$$SyntheticClass$$Lambda";
        byte[] bytes = ClassFactory.create(className);
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        CustomClassLoader customClassLoader = new CustomClassLoader(classLoader);
    
        Class<?> defineClass = customClassLoader.defineClass(className, bytes);
    
        assertThat(proxyFrameworks.getUnproxiedType(defineClass.newInstance())).isEqualTo(defineClass);
    }
    
    private static class CustomClassLoader extends URLClassLoader{
    
        public CustomClassLoader(ClassLoader parent) {
            super(((URLClassLoader)parent).getURLs(), parent);
        }
    
        public Class<?> defineClass(String name, byte[] b) {
            return defineClass(name, b, 0, b.length);
        }
    }
    
    private static class ClassFactory implements Opcodes {
    
        private static byte[] create(String className) throws Exception {
            
            ClassWriter cw = new ClassWriter(0);
            FieldVisitor fv;
            MethodVisitor mv;
            AnnotationVisitor av0;
            
            cw.visit(49,
                     ACC_PUBLIC + ACC_SUPER,
                     className,
                     null,
                     "java/lang/Object",
                     null);
            
            cw.visitSource("Hello.java", null);
            
            {
                mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKESPECIAL,
                                   "java/lang/Object",
                                   "<init>",
                                   "()V");
                mv.visitInsn(RETURN);
                mv.visitMaxs(1, 1);
                mv.visitEnd();
            }
            {
                mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC,
                                    "main",
                                    "([Ljava/lang/String;)V",
                                    null,
                                    null);
                mv.visitFieldInsn(GETSTATIC,
                                  "java/lang/System",
                                  "out",
                                  "Ljava/io/PrintStream;");
                mv.visitLdcInsn("hello");
                mv.visitMethodInsn(INVOKEVIRTUAL,
                                   "java/io/PrintStream",
                                   "println",
                                   "(Ljava/lang/String;)V");
                mv.visitInsn(RETURN);
                mv.visitMaxs(2, 1);
                mv.visitEnd();
            }
            cw.visitEnd();
            
            return cw.toByteArray();
        }
    }
    
}