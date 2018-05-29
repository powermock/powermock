package org.powermock.core.transformers.bytebuddy.testclass;

import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.implementation.Implementation.Context;
import net.bytebuddy.jar.asm.AnnotationVisitor;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.jar.asm.Type;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.pool.TypePool;
import org.powermock.core.transformers.bytebuddy.support.ByteBuddyClass;

import java.lang.annotation.Annotation;

class RemoveTestAnnotationFromMethods implements TestClassTransformationOperation {
    private final MethodFilter methodFilter;
    private final Class<? extends Annotation> testMethodAnnotationType;
    
    RemoveTestAnnotationFromMethods(final MethodFilter methodFilter, final Class<? extends Annotation> testMethodAnnotationType) {
        this.methodFilter = methodFilter;
        this.testMethodAnnotationType = testMethodAnnotationType;
    }
    
    @Override
    public ByteBuddyClass apply(final ByteBuddyClass byteBuddyClass) {
        final Builder builder = byteBuddyClass.getBuilder()
                                              .visit(new AsmVisitorWrapper.ForDeclaredMethods()
                                                         .method(
                                                             ElementMatchers.not(ElementMatchers.isConstructor()).and(
                                                                 new MethodFilterAdapter(methodFilter)
                                                             ),
                                                             new AnnotationRemovingMethodVisitorWrapper(testMethodAnnotationType)
                                                         ));
        
        
        return ByteBuddyClass.from(byteBuddyClass.getTypeDescription(), builder);
    }
    
    private static class MethodFilterAdapter implements ElementMatcher<MethodDescription> {
        private final MethodFilter methodFilter;
        
        private MethodFilterAdapter(final MethodFilter methodFilter) {
            this.methodFilter = methodFilter;
        }
        
        @Override
        public boolean matches(final MethodDescription target) {
            return methodFilter.mustHaveTestAnnotationRemoved(target);
        }
    }
    
    private static class AnnotationRemovingMethodVisitorWrapper implements AsmVisitorWrapper.ForDeclaredMethods.MethodVisitorWrapper {
        private final Class<? extends Annotation> testMethodAnnotationType;
        
        private AnnotationRemovingMethodVisitorWrapper(final Class<? extends Annotation> testMethodAnnotationType) {
            this.testMethodAnnotationType = testMethodAnnotationType;
        }
        
        @Override
        public MethodVisitor wrap(final TypeDescription instrumentedType,
                                  final MethodDescription instrumentedMethod,
                                  final MethodVisitor methodVisitor,
                                  final Context implementationContext,
                                  final TypePool typePool,
                                  final int writerFlags,
                                  final int readerFlags) {
            return new MethodVisitor(Opcodes.ASM5, methodVisitor) {
                @Override
                public AnnotationVisitor visitAnnotation(final String descriptor, final boolean visible) {
                    if (Type.getDescriptor(testMethodAnnotationType).equals(descriptor)) {
                        return null;
                    }
                    return super.visitAnnotation(descriptor, visible);
                }
            };
        }
    }
}
