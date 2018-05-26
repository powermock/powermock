package org.powermock.core.transformers;

import javassist.CtMethod;
import net.bytebuddy.description.method.MethodDescription;
import org.powermock.core.classloader.ByteCodeFramework;
import org.powermock.core.transformers.bytebuddy.testclass.ForMethodsByteBuddyTestClassTransformer;
import org.powermock.core.transformers.bytebuddy.testclass.FromAllMethodsExceptByteBuddyTestClassTransformer;
import org.powermock.core.transformers.javassist.testclass.ForMethodsJavaAssistTestClassTransformer;
import org.powermock.core.transformers.javassist.testclass.FromAllMethodsExceptJavaAssistTestClassTransformer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;

public class TestClassTransformerBuilder {
    
    public static TestClassTransformerBuilder forTestClass(final Class<?> testClass) {
        return new TestClassTransformerBuilder(testClass);
    }
    
    private final Class<?> testClass;
    
    private TestClassTransformerBuilder(final Class<?> testClass) {
        this.testClass = testClass;
    }
    
    public RemovesTestMethodAnnotation removesTestMethodAnnotation(final Class<? extends Annotation> testMethodAnnotation) {
        return new RemovesTestMethodAnnotation(testClass, testMethodAnnotation, ByteCodeFramework.getByteCodeFrameworkForTestClass(testClass));
    }
    
    public TestClassTransformerBuilderWithClue bytecodeFrameworkClue(final Method method) {
        return new TestClassTransformerBuilderWithClue(testClass, method);
    }
    
    public static class TestClassTransformerBuilderWithClue {
        
        private final Class<?> testClass;
        private final Method method;
        
        private TestClassTransformerBuilderWithClue(final Class<?> testClass, final Method method) {
            this.testClass = testClass;
            this.method = method;
        }
        
        public RemovesTestMethodAnnotation removesTestMethodAnnotation(final Class<? extends Annotation> testMethodAnnotation) {
            return new RemovesTestMethodAnnotation(testClass, testMethodAnnotation, ByteCodeFramework.getByteCodeFrameworkForMethod(testClass, method));
        }
    }
    
    public static class RemovesTestMethodAnnotation {
        private final Class<? extends Annotation> testMethodAnnotation;
        private final Class<?> testClass;
        private final ByteCodeFramework byteCodeFramework;
        
        private RemovesTestMethodAnnotation(final Class<?> testClass, final Class<? extends Annotation> testMethodAnnotation,
                                            final ByteCodeFramework byteCodeFramework) {
            this.testClass = testClass;
            this.testMethodAnnotation = testMethodAnnotation;
            this.byteCodeFramework = byteCodeFramework;
        }
        
        public TestClassTransformer fromMethods(final Collection<Method> testMethodsThatRunOnOtherClassLoaders) {
            switch (byteCodeFramework) {
                case Javassist:
                    return new ForMethodsJavaAssistTestClassTransformer(
                        testClass, testMethodAnnotation, MethodSignatures.Javassist.<CtMethod>methodSignatureWriter(), testMethodsThatRunOnOtherClassLoaders
                    );
                case ByteBuddy:
                    return new ForMethodsByteBuddyTestClassTransformer(
                        testClass, testMethodAnnotation, MethodSignatures.ByteBuddy.<MethodDescription>methodSignatureWriter(), testMethodsThatRunOnOtherClassLoaders
                    );
                default:
                    throw new IllegalArgumentException(String.format("Unknown bytecode framework `%s`", byteCodeFramework));
            }
        }
        
        public TestClassTransformer fromAllMethodsExcept(Method singleMethodToRunOnTargetClassLoader) {
            switch (byteCodeFramework) {
                case Javassist:
                    return new FromAllMethodsExceptJavaAssistTestClassTransformer(
                        testClass, testMethodAnnotation, MethodSignatures.Javassist.<CtMethod>methodSignatureWriter(), singleMethodToRunOnTargetClassLoader
                    );
                case ByteBuddy:
                    return new FromAllMethodsExceptByteBuddyTestClassTransformer(
                        testClass, testMethodAnnotation, MethodSignatures.ByteBuddy.<MethodDescription>methodSignatureWriter(), singleMethodToRunOnTargetClassLoader
                    );
                default:
                    throw new IllegalArgumentException(String.format("Unknown bytecode framework `%s`", byteCodeFramework));
            }
        }
    }
}
