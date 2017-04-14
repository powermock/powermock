/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.powermock.core.classloader;

import javassist.ByteArrayClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import org.junit.Before;
import org.junit.Test;
import org.powermock.core.classloader.annotations.UseClassPathAdjuster;
import org.powermock.core.classloader.javassist.ClassPathAdjuster;
import org.powermock.core.classloader.javassist.JavassistMockClassLoader;
import org.powermock.core.transformers.MockTransformer;
import org.powermock.core.transformers.MockTransformerChain;
import org.powermock.core.transformers.javassist.JavassistMockTransformerChainFactory;
import org.powermock.reflect.Whitebox;
import org.powermock.reflect.internal.WhiteboxImpl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.powermock.core.classloader.MockClassLoader.MODIFY_ALL_CLASSES;

public class MockClassLoaderTest {
    
    private MockClassLoaderFactory<JavassistMockClassLoader> mockClassLoaderFactory =
        new MockClassLoaderFactory<JavassistMockClassLoader>(JavassistMockClassLoader.class);
    
    
    private MockTransformerChain transformerChain;
    
    @Before
    public void setUp() throws Exception {
        transformerChain = new JavassistMockTransformerChainFactory().createDefaultChain(Collections.<MockTransformer>emptyList());
    }
    
    @Test
    public void autoboxingWorks() throws Exception {
        String name = this.getClass().getPackage().getName() + ".HardToTransform";
        final MockClassLoader mockClassLoader = mockClassLoaderFactory.getInstance(new String[]{name});
        mockClassLoader.setMockTransformerChain(transformerChain);
        
        Class<?> c = mockClassLoader.loadClass(name);
        
        Object object = c.newInstance();
        Whitebox.invokeMethod(object, "run");
        
        assertThat(5).isEqualTo(Whitebox.invokeMethod(object, "testInt"));
        assertThat(5L).isEqualTo(Whitebox.invokeMethod(object, "testLong"));
        assertThat(5f).isEqualTo(Whitebox.invokeMethod(object, "testFloat"));
        assertThat(5.0).isEqualTo(Whitebox.invokeMethod(object, "testDouble"));
        assertThat(new Short("5")).isEqualTo(Whitebox.invokeMethod(object, "testShort"));
        assertThat(new Byte("5")).isEqualTo(Whitebox.invokeMethod(object, "testByte"));
        assertThat(true).isEqualTo(Whitebox.invokeMethod(object, "testBoolean"));
        assertThat('5').isEqualTo(Whitebox.invokeMethod(object, "testChar"));
        assertThat("5").isEqualTo(Whitebox.invokeMethod(object, "testString"));
    }
    
    @Test
    public void callFindClassWorks() throws Exception {
        MyClassloader myClassloader = new MyClassloader(new String[]{"org.mytest.myclass"});
        assertEquals(String.class, myClassloader.findClassPublic("java.lang.String"));
    }
    
    @Test
    public void powerMockIgnoreAnnotatedPackagesAreIgnored() throws Exception {
        MockClassLoader mockClassLoader = mockClassLoaderFactory.getInstance(new String[]{"org.ikk.Jux"});
        
        MockClassLoaderConfiguration configuration = mockClassLoader.getConfiguration();
        
        Whitebox.setInternalState(configuration, "deferPackages", new String[]{"*mytest*"}, MockClassLoaderConfiguration.class);
        
        assertFalse(configuration.shouldModify("org.mytest.myclass"));
    }
    
    @Test
    public void powerMockIgnoreAnnotatedPackagesHavePrecedenceOverPrepareEverythingForTest() throws Exception {
        MockClassLoader mockClassLoader = mockClassLoaderFactory.getInstance(new String[]{MODIFY_ALL_CLASSES});
        MockClassLoaderConfiguration configuration = mockClassLoader.getConfiguration();
        
        Whitebox.setInternalState(configuration, "deferPackages", new String[]{"*mytest*"}, MockClassLoaderConfiguration.class);
        
        assertFalse(configuration.shouldModify("org.mytest.myclass"));
    }
    
    @Test
    public void canFindResource() throws Exception {
        final MockClassLoader mockClassLoader = mockClassLoaderFactory.getInstance(new String[0]);
        List<MockTransformer> list = new LinkedList<MockTransformer>();
        mockClassLoader.setMockTransformerChain(transformerChain);
        
        // Force a ClassLoader that can find 'foo/bar/baz/test.txt' into
        // mockClassLoader.deferTo.
        mockClassLoader.deferTo = new ResourcePrefixClassLoader(getClass().getClassLoader(), "org/powermock/core/classloader/");
        
        // MockClassLoader will only be able to find 'foo/bar/baz/test.txt' if it
        // properly defers the resource lookup to its deferTo ClassLoader.
        URL resource = mockClassLoader.getResource("foo/bar/baz/test.txt");
        assertThat(resource).isNotNull();
        assertThat(resource.getPath()).endsWith("test.txt");
    }
    
    @Test
    public void canFindResources() throws Exception {
        final MockClassLoader mockClassLoader = mockClassLoaderFactory.getInstance(new String[0]);
        mockClassLoader.setMockTransformerChain(transformerChain);
        
        // Force a ClassLoader that can find 'foo/bar/baz/test.txt' into
        // mockClassLoader.deferTo.
        mockClassLoader.deferTo = new ResourcePrefixClassLoader(getClass().getClassLoader(), "org/powermock/core/classloader/");
        
        // MockClassLoader will only be able to find 'foo/bar/baz/test.txt' if it
        // properly defers the resources lookup to its deferTo ClassLoader.
        Enumeration<URL> resources = mockClassLoader.getResources("foo/bar/baz/test.txt");
        
        assertThat(resources.nextElement().getPath()).endsWith("test.txt");
    }
    
    @Test
    public void resourcesNotDoubled() throws Exception {
        final MockClassLoader mockClassLoader = mockClassLoaderFactory.getInstance(new String[0]);
        mockClassLoader.setMockTransformerChain(transformerChain);
        
        // MockClassLoader will only be able to find 'foo/bar/baz/test.txt' if it
        // properly defers the resources lookup to its deferTo ClassLoader.
        Enumeration<URL> resources = mockClassLoader.getResources("org/powermock/core/classloader/foo/bar/baz/test.txt");
        
        assertThat(resources.nextElement().getPath()).endsWith("test.txt");
        assertThat(resources.hasMoreElements()).isFalse();
    }
    
    @Test
    public void canFindDynamicClassFromAdjustedClasspath() throws Exception {
        // Construct MockClassLoader with @UseClassPathAdjuster annotation.
        // It activates our MyClassPathAdjuster class which appends our dynamic
        // class to the MockClassLoader's classpool.
        UseClassPathAdjuster useClassPathAdjuster = new TestUseClassPathAdjuster();
        final MockClassLoader mockClassLoader = mockClassLoaderFactory.getInstance(new String[0], useClassPathAdjuster);
        mockClassLoader.setMockTransformerChain(transformerChain);
        
        // setup custom classloader providing our dynamic class, for MockClassLoader to defer to
        mockClassLoader.deferTo = new ClassLoader(getClass().getClassLoader()) {
            @Override
            public Class<?> loadClass(String name)
                throws ClassNotFoundException {
                if (name.equals(DynamicClassHolder.clazz.getName())) {
                    return DynamicClassHolder.clazz;
                }
                return super.loadClass(name);
            }
        };
        
        // verify that MockClassLoader can successfully load the class
        Class<?> dynamicTestClass = Class.forName(DynamicClassHolder.clazz.getName(), false, mockClassLoader);
        
        assertThat(dynamicTestClass).isNotSameAs(DynamicClassHolder.clazz);
    }
    
    @Test(expected = ClassNotFoundException.class)
    @Ignore
    public void cannotFindDynamicClassInDeferredClassLoader() throws Exception {
        
        MockClassLoader mockClassLoader = mockClassLoaderFactory.getInstance(new String[0]);
        List<MockTransformer> list = new LinkedList<MockTransformer>();
        mockClassLoader.setMockTransformerChain(transformerChain);
        
        // setup custom classloader providing our dynamic class, for MockClassLoader to defer to
        mockClassLoader.deferTo = new ClassLoader(getClass().getClassLoader()) {
            
            @Override
            public Class<?> loadClass(String name)
                throws ClassNotFoundException {
                return super.loadClass(name);
            }
        };
        
        //Try to locate and load a class that is not in MockClassLoader.
        Class.forName(DynamicClassHolder.clazz.getName(), false, mockClassLoader);
    }
    
    @Test
    public void canLoadDefinedClass() throws Exception {
        final String className = "my.ABCTestClass";
        final MockClassLoader mockClassLoader = mockClassLoaderFactory.getInstance(new String[]{className});
        
        
        Whitebox.invokeMethod(mockClassLoader, "defineClass", className, DynamicClassHolder.classBytes,
                              0, DynamicClassHolder.classBytes.length, this.getClass().getProtectionDomain());
        Class.forName(className, false, mockClassLoader);
        
        mockClassLoader.loadClass(className);
    }
    
    // helper class for canFindDynamicClassFromAdjustedClasspath()
    public static class MyClassPathAdjuster implements ClassPathAdjuster {
        public void adjustClassPath(ClassPool classPool) {
            classPool.appendClassPath(new ByteArrayClassPath(DynamicClassHolder.clazz.getName(), DynamicClassHolder.classBytes));
        }
    }
    
    // helper class for canFindDynamicClassFromAdjustedClasspath()
    static class DynamicClassHolder {
        final static byte[] classBytes;
        final static Class<?> clazz;
        
        static {
            try {
                // construct a new class dynamically
                ClassPool cp = ClassPool.getDefault();
                final CtClass ctClass = cp.makeClass("my.ABCTestClass");
                classBytes = ctClass.toBytecode();
                clazz = ctClass.toClass();
            } catch (Exception e) {
                throw new RuntimeException("Problem constructing custom class", e);
            }
        }
    }
    
    @SuppressWarnings("SameParameterValue")
    static class MyClassloader extends JavassistMockClassLoader {
        
        private final ClassPool classPool = new ClassPool();
        
        public MyClassloader(String[] classesToMock) {
            super(classesToMock);
        }
        
        public Class<?> findClassPublic(String s) throws ClassNotFoundException {
            return findClass(s);
        }
        
        @Override
        protected Class findClass(String name) throws ClassNotFoundException {
            if (name.startsWith("java.lang")) {
                return this.getClass().getClassLoader().loadClass(name);
            }
            return super.findClass(name);
        }
        @Override
        protected Class<?> loadMockClass(String name, ProtectionDomain protectionDomain) {
            final byte[] clazz = loadAndTransform(name);
            
            return defineClass(name, clazz, 0, clazz.length, protectionDomain);
        }
    }
    
    private static class MockClassLoaderFactory<T extends MockClassLoader> {
        
        private final Class<T> classLoaderClass;
        
        private MockClassLoaderFactory(Class<T> classLoaderClass) {
            this.classLoaderClass = classLoaderClass;
        }
        
        T getInstance(String[] param, UseClassPathAdjuster useClassPathAdjuster) throws IllegalAccessException, InvocationTargetException, InstantiationException {
            Constructor<?> constructor = WhiteboxImpl.getConstructor(classLoaderClass, param.getClass(), param.getClass(), UseClassPathAdjuster.class);
            return (T) constructor.newInstance(param, new String[0], useClassPathAdjuster);
        }
        
        public T getInstance(MockClassLoaderConfiguration configuration) throws IllegalAccessException, InvocationTargetException, InstantiationException {
            Constructor<?> constructor = WhiteboxImpl.getConstructor(classLoaderClass, configuration.getClass());
            return (T) constructor.newInstance(new Object[]{configuration});
        }
        
        private T getInstance(String[] param) throws IllegalAccessException, InvocationTargetException, InstantiationException {
            MockClassLoaderConfiguration configuration = new MockClassLoaderConfiguration(param, new String[0]);
            return getInstance(configuration);
        }
    }
    
    public static class TestUseClassPathAdjuster implements UseClassPathAdjuster {
        public Class<? extends Annotation> annotationType() {
            return UseClassPathAdjuster.class;
        }
        
        public Class<? extends ClassPathAdjuster> value() {
            return MyClassPathAdjuster.class;
        }
    }
}
