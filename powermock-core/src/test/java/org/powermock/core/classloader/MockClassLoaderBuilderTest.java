package org.powermock.core.classloader;


import javassist.CtClass;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.Test;
import org.powermock.PowerMockInternalException;
import org.powermock.core.classloader.bytebuddy.ByteBuddyMockClassLoader;
import org.powermock.core.classloader.javassist.JavassistMockClassLoader;
import org.powermock.core.transformers.ClassWrapper;
import org.powermock.core.transformers.MockTransformer;
import org.powermock.core.transformers.MockTransformerChain.FilterPredicate;
import org.powermock.core.transformers.TestClassAwareTransformer;
import org.powermock.core.transformers.bytebuddy.support.ByteBuddyClass;
import org.powermock.core.transformers.support.DefaultMockTransformerChain;
import org.powermock.reflect.internal.WhiteboxImpl;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MockClassLoaderBuilderTest {
    
    @Test
    public void should_create_instance_of_MockClassLoader_depends_on_provided_bytecode_framework() {
        MockClassLoader classLoader = MockClassLoaderBuilder
                                          .create(ByteCodeFramework.Javassist)
                                          .forTestClass(getClass())
                                          .build();
        
        assertThat(classLoader).isExactlyInstanceOf(JavassistMockClassLoader.class);
        
        classLoader = MockClassLoaderBuilder
                          .create(ByteCodeFramework.ByteBuddy)
                          .forTestClass(getClass())
                          .build();
        
        assertThat(classLoader).isExactlyInstanceOf(ByteBuddyMockClassLoader.class);
    }
    
    @Test
    public void should_create_transformer_chain_depends_on_provided_bytecode_framework() {
        
        MockClassLoader classLoader = MockClassLoaderBuilder
                                          .create(ByteCodeFramework.Javassist)
                                          .forTestClass(getClass())
                                          .build();
        
        assertThatJavassistMockTransformerChainCreated(classLoader);
        
        
        classLoader = MockClassLoaderBuilder
                          .create(ByteCodeFramework.ByteBuddy)
                          .forTestClass(getClass())
                          .build();
        
        assertThatByteBuddyMockTransformerChainCreated(classLoader);
    }
    
    @Test
    public void should_set_test_class_to_TestClassAwareTransformers() {
        
        final SpyMockTransformer extraMockTransformer = new SpyMockTransformer();
        
        MockClassLoaderBuilder
            .create(ByteCodeFramework.Javassist)
            .forTestClass(MockClassLoaderBuilderTest.class)
            .addExtraMockTransformers(extraMockTransformer)
            .build();
        
        assertThat(extraMockTransformer.testClass)
            .as("Test class is set ")
            .isEqualTo(MockClassLoaderBuilderTest.class);
    }
    
    @Test
    public void should_throw_internal_exception_if_test_class_is_null() {
        
        assertThatThrownBy(new ThrowingCallable() {
            @Override
            public void call() {
                MockClassLoaderBuilder
                    .create(ByteCodeFramework.Javassist)
                    .build();
            }
        }).as("Internal exception has been thrown.")
          .isExactlyInstanceOf(PowerMockInternalException.class);
    }
    
    private void assertThatJavassistMockTransformerChainCreated(final MockClassLoader classLoader) {
        final DefaultMockTransformerChain mockTransformerChain = (DefaultMockTransformerChain) classLoader.getMockTransformerChain();
        assertThatMockTransformerChainWorksWithExpectedClassRepresentation(CtClass.class, mockTransformerChain);
    }
    
    private void assertThatByteBuddyMockTransformerChainCreated(final MockClassLoader classLoader) {
        final DefaultMockTransformerChain mockTransformerChain = (DefaultMockTransformerChain) classLoader.getMockTransformerChain();
        assertThatMockTransformerChainWorksWithExpectedClassRepresentation(ByteBuddyClass.class, mockTransformerChain);
    }
    
    private void assertThatMockTransformerChainWorksWithExpectedClassRepresentation(final Class<?> expectedParameterClass,
                                                                                    final DefaultMockTransformerChain mockTransformerChain) {
        final Method method = WhiteboxImpl.findMethod(
            mockTransformerChain.filter(new FilterPredicate() {
                @Override
                public boolean test(final MockTransformer<?> mockTransformer) {
                    return true;
                }
            }).iterator().next().getClass(),
            "transform",
            ClassWrapper.class
        );
        
        final ParameterizedType returnType = (ParameterizedType) method.getGenericReturnType();
        assertThat(returnType.getActualTypeArguments())
            .withFailMessage(
                "Expected that transformer chain works with %s, however actually it works with %s",
                expectedParameterClass, ((Class) returnType.getActualTypeArguments()[0]).getName()
            )
            .containsExactly(expectedParameterClass);
    }
    
    private static class SpyMockTransformer<T> implements MockTransformer<T>, TestClassAwareTransformer {
        
        private Class<?> testClass;
        
        @Override
        public ClassWrapper<T> transform(final ClassWrapper<T> clazz) {
            return null;
        }
        
        @Override
        public void setTestClass(final Class<?> testClass) {
            
            this.testClass = testClass;
        }
    }
}