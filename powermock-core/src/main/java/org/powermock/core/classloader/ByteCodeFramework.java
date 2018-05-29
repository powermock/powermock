package org.powermock.core.classloader;

import org.powermock.configuration.GlobalConfiguration;
import org.powermock.core.classloader.annotations.PrepareEverythingForTest;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.core.classloader.annotations.UseClassPathAdjuster;
import org.powermock.core.classloader.bytebuddy.ByteBuddyMockClassLoader;
import org.powermock.core.classloader.javassist.JavassistMockClassLoader;
import org.powermock.core.transformers.MockTransformerChainFactory;
import org.powermock.core.transformers.bytebuddy.ByteBuddyMockTransformerChainFactory;
import org.powermock.core.transformers.javassist.JavassistMockTransformerChainFactory;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

public enum ByteCodeFramework {
    Javassist {
        @Override
        MockClassLoader createClassloader(final MockClassLoaderConfiguration configuration,
                                          final UseClassPathAdjuster useClassPathAdjuster) {
            return new JavassistMockClassLoader(configuration, useClassPathAdjuster);
        }
    
        @Override
        MockTransformerChainFactory createTransformerChainFactory() {
            return new JavassistMockTransformerChainFactory();
        }
    },
    ByteBuddy {
        @Override
        MockClassLoader createClassloader(final MockClassLoaderConfiguration configuration,
                                          final UseClassPathAdjuster useClassPathAdjuster) {
            return new ByteBuddyMockClassLoader(configuration);
        }
    
        @Override
        MockTransformerChainFactory createTransformerChainFactory() {
            return new ByteBuddyMockTransformerChainFactory();
        }
    };
    
    public static ByteCodeFramework getByteCodeFrameworkForMethod(final Class<?> testClass, final Method method) {
        ByteCodeFramework byteCodeFramework = getByteCodeFramework(method);
        if (byteCodeFramework == null) {
            byteCodeFramework = getByteCodeFramework(testClass);
        }
        if (byteCodeFramework == null) {
            throw new IllegalArgumentException(
                String.format(
                    "Either method %s or class %s is annotated by PrepareForTest/PrepareEverythingForTest", method.getName(), testClass.getName()
                )
            );
        }
        return byteCodeFramework;
    }
    
    public static ByteCodeFramework getByteCodeFrameworkForTestClass(final Class<?> testClass) {
        ByteCodeFramework byteCodeFramework = getByteCodeFramework(testClass);
        
        if (byteCodeFramework == null){
            byteCodeFramework = GlobalConfiguration.powerMockConfiguration().getByteCodeFramework();
        }
        
        return byteCodeFramework;
    }
    
    private static ByteCodeFramework getByteCodeFramework(final AnnotatedElement element) {
        if (element.isAnnotationPresent(PrepareForTest.class)) {
            return element.getAnnotation(PrepareForTest.class).byteCodeFramework();
        } else if (element.isAnnotationPresent(PrepareOnlyThisForTest.class)) {
            return element.getAnnotation(PrepareOnlyThisForTest.class).byteCodeFramework();
        } else if (element.isAnnotationPresent(PrepareEverythingForTest.class)) {
            return element.getAnnotation(PrepareEverythingForTest.class).byteCodeFramework();
        } else if (element.isAnnotationPresent(SuppressStaticInitializationFor.class)){
            return element.getAnnotation(SuppressStaticInitializationFor.class).byteCodeFramework();
        }
        return null;
    }
    
    abstract MockClassLoader createClassloader(MockClassLoaderConfiguration configuration, final UseClassPathAdjuster useClassPathAdjuster);
    
    abstract MockTransformerChainFactory createTransformerChainFactory();
}
