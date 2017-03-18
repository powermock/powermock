/*
 *
 *   Copyright 2017 the original author or authors.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package org.powermock.core.classloader.javassist;

import javassist.ClassPool;
import javassist.CtClass;
import org.powermock.core.classloader.MockClassLoader;
import org.powermock.core.classloader.MockClassLoaderConfiguration;
import org.powermock.core.classloader.annotations.UseClassPathAdjuster;
import org.powermock.core.transformers.ClassWrapper;

import java.security.ProtectionDomain;

public class JavassistMockClassLoader extends MockClassLoader {
    
    public static final String CGLIB_ENHANCER = "net.sf.cglib.proxy.Enhancer$EnhancerKey$$KeyFactoryByCGLIB$$";
    public static final String CGLIB_METHOD_WRAPPER = "net.sf.cglib.core.MethodWrapper$MethodWrapperKey$$KeyFactoryByCGLIB";
    
    private final ClassPool classPool;
    
    public JavassistMockClassLoader(String[] classesToMock) {
        this(classesToMock, new String[0], null);
    }
    
    public JavassistMockClassLoader(String[] classesToMock, String[] packagesToDefer,
                                    UseClassPathAdjuster useClassPathAdjuster) {
        this(new MockClassLoaderConfiguration(classesToMock, packagesToDefer), useClassPathAdjuster);
    }
    
    public JavassistMockClassLoader(MockClassLoaderConfiguration configuration) {
        this(configuration, null);
    }
    
    public JavassistMockClassLoader(MockClassLoaderConfiguration configuration,
                                    UseClassPathAdjuster useClassPathAdjuster) {
        super(configuration);
        classPool = new ClassPoolFactory(useClassPathAdjuster).create();
        classMarker = JavaAssistClassMarkerFactory.createClassMarker(classPool);
    }
    
    
    @Override
    protected Class<?> loadUnmockedClass(String name, ProtectionDomain protectionDomain)
        throws ClassFormatError, ClassNotFoundException {
        byte bytes[] = null;
        try {
            /*
             * TODO This if-statement is a VERY ugly hack to avoid the
             * java.lang.ExceptionInInitializerError caused by
             * "javassist.NotFoundException:
             * net.sf.cglib.proxy.Enhancer$EnhancerKey$$KeyFactoryByCGLIB$$7fb24d72
             * ". This happens after the
             * se.jayway.examples.tests.privatefield.
             * SimplePrivateFieldServiceClassTest#testUseService(..) tests has
             * been run and all other tests will fail if this class is tried to
             * be loaded. Atm I have found no solution other than this ugly hack
             * to make it work. We really need to investigate the real cause of
             * this behavior.
             */
            if (!name.startsWith(CGLIB_ENHANCER) && !name.startsWith(CGLIB_METHOD_WRAPPER)) {
                final CtClass ctClass = classPool.get(name);
                if (ctClass.isFrozen()) {
                    ctClass.defrost();
                }
                bytes = ctClass.toBytecode();
            }
        } catch (Exception e) {
            if (e instanceof javassist.NotFoundException) {
                throw new ClassNotFoundException();
            } else {
                throw new RuntimeException(e);
            }
            
        }
        return bytes == null ? null : defineClass(name, bytes, 0, bytes.length, protectionDomain);
    }
    
    @Override
    protected Class<?> loadMockClass(String name, ProtectionDomain protectionDomain) {
        
        final byte[] clazz = loadAndTransform(name);
        
        return defineClass(name, clazz, 0, clazz.length, protectionDomain);
    }
    
    protected byte[] loadAndTransform(String name) {
        final byte[] clazz;
        
        ClassPool.doPruning = false;
        try {
            CtClass type = classPool.get(name);
            
            ClassWrapper<CtClass> wrappedType = classWrapperFactory.wrap(type);
            
            wrappedType = transformClass(wrappedType);
            
            type = wrappedType.unwrap();

            /*
             * ClassPool may cause huge memory consumption if the number of CtClass
             * objects becomes amazingly large (this rarely happens since Javassist
             * tries to reduce memory consumption in various ways). To avoid this
             * problem, you can explicitly remove an unnecessary CtClass object from
             * the ClassPool. If you call detach() on a CtClass object, then that
             * CtClass object is removed from the ClassPool.
             */
            type.detach();
            
            
            clazz = type.toBytecode();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to transform class with name " + name + ". Reason: "
                                                + e.getMessage(), e);
        }
        return clazz;
    }
    
}
