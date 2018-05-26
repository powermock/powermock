package org.powermock.core.transformers.bytebuddy.testclass;


import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import org.powermock.core.transformers.ClassWrapper;
import org.powermock.core.transformers.MethodSignatureWriter;
import org.powermock.core.transformers.TestClassTransformer;
import org.powermock.core.transformers.bytebuddy.support.ByteBuddyClass;

import java.lang.annotation.Annotation;

import static org.powermock.core.transformers.bytebuddy.testclass.TestClassTransformationBuilder.withTestClass;

public abstract class ByteBuddyTestClassTransformer extends TestClassTransformer<ByteBuddyClass, MethodDescription> implements MethodFilter {
    
    private final TypeDescription testClassDescription;
    
    ByteBuddyTestClassTransformer(final Class testClass, final Class<? extends Annotation> testMethodAnnotationType,
                                  final MethodSignatureWriter<MethodDescription> signatureWriter) {
        super(testClass, testMethodAnnotationType, signatureWriter);
        testClassDescription = TypeDescription.Sort.describe(testClass).asErasure();
    }
    
    @Override
    public ClassWrapper<ByteBuddyClass> transform(final ClassWrapper<ByteBuddyClass> clazz) throws Exception {
        if (isTestClass(clazz)) {
            // @formatter:off
            return withTestClass(clazz)
                        .removeTestAnnotationFromMethods(this, getTestMethodAnnotationType())
                   .transform();
            // @formatter:on
        }
        return clazz;
    }
    
    private boolean isTestClass(final ClassWrapper<ByteBuddyClass> clazz) {
        return testClassDescription.equals(clazz.unwrap().getTypeDescription());
    }
    
}
