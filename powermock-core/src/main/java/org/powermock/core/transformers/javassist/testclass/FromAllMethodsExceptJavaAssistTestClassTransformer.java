package org.powermock.core.transformers.javassist.testclass;

import javassist.CtMethod;
import org.powermock.core.transformers.MethodSignatureWriter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class FromAllMethodsExceptJavaAssistTestClassTransformer extends JavaAssistTestClassTransformer {
    private final String targetMethodSignature;
    
    public FromAllMethodsExceptJavaAssistTestClassTransformer(final Class<?> testClass,
                                                              final Class<? extends Annotation> testMethodAnnotation,
                                                              final MethodSignatureWriter<CtMethod> signatureWriter,
                                                              final Method methodToExclude) {
        super(testClass, testMethodAnnotation, signatureWriter);
        this.targetMethodSignature = signatureWriter.signatureForReflection(methodToExclude);
    }
    
    @Override
    protected boolean mustHaveTestAnnotationRemoved(CtMethod method) throws Exception {
        return !signatureOf(method).equals(targetMethodSignature);
    }
}
