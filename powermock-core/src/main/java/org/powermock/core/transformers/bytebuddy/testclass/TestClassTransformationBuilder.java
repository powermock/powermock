package org.powermock.core.transformers.bytebuddy.testclass;

import org.powermock.core.transformers.ClassWrapper;
import org.powermock.core.transformers.bytebuddy.support.ByteBuddyClass;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class TestClassTransformationBuilder {
    static TestClassTransformation withTestClass(final ClassWrapper<ByteBuddyClass> clazz) {
        return new TestClassTransformation(clazz);
    }
    
    static class TestClassTransformation {
        private final ClassWrapper<ByteBuddyClass> transformedClass;
        
        private TestClassTransformation(final ClassWrapper<ByteBuddyClass> transformedClass) {
            this.transformedClass = transformedClass;
        }
    
        TerminationOperation removeTestAnnotationFromMethods(final MethodFilter methodFilter,
                                                             final Class<? extends Annotation> testMethodAnnotationType) {
            final List<TestClassTransformationOperation> newOperations = new ArrayList<TestClassTransformationOperation>();
        
            newOperations.add(new RemoveTestAnnotationFromMethods(methodFilter, testMethodAnnotationType));
            return new TerminationOperation(transformedClass, newOperations);
        }
    }
    
    static class TerminationOperation extends BuilderUnit {
        
        private TerminationOperation(
            final ClassWrapper<ByteBuddyClass> clazz,
            final List<TestClassTransformationOperation> operations) {
            super(clazz, operations);
        }
        
        public ClassWrapper<ByteBuddyClass> transform() {
            
            ByteBuddyClass buddyClass = clazz.unwrap();
            
            for (TestClassTransformationOperation operation : operations) {
                buddyClass = operation.apply(buddyClass);
            }
            
            return clazz.wrap(buddyClass);
        }
    }
    
    private static class BuilderUnit {
        final List<TestClassTransformationOperation> operations;
        final ClassWrapper<ByteBuddyClass> clazz;
        
        private BuilderUnit(final ClassWrapper<ByteBuddyClass> clazz,
                            final List<TestClassTransformationOperation> operations) {
            this.clazz = clazz;
            this.operations = operations;
        }
    }
}
