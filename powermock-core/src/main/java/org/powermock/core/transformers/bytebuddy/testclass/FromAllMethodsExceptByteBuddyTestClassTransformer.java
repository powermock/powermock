package org.powermock.core.transformers.bytebuddy.testclass;

import net.bytebuddy.description.method.MethodDescription;
import org.powermock.core.transformers.MethodSignatureWriter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class FromAllMethodsExceptByteBuddyTestClassTransformer extends ByteBuddyTestClassTransformer {
    private final String targetMethodSignature;
    
    public FromAllMethodsExceptByteBuddyTestClassTransformer(final Class<?> testClass,
                                                             final Class<? extends Annotation> testMethodAnnotation,
                                                             final MethodSignatureWriter<MethodDescription> signatureWriter,
                                                             final Method methodToExclude) {
        super(testClass, testMethodAnnotation, signatureWriter);
        this.targetMethodSignature = signatureWriter.signatureForReflection(methodToExclude);
    }
    
    @Override
    public boolean mustHaveTestAnnotationRemoved(MethodDescription method) {
        return !signatureOf(method).equals(targetMethodSignature);
    }
}
