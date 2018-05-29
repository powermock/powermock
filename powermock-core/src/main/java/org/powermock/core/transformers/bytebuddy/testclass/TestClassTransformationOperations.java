package org.powermock.core.transformers.bytebuddy.testclass;


import org.powermock.core.transformers.bytebuddy.support.ByteBuddyClass;

public enum TestClassTransformationOperations implements TestClassTransformationOperation {
    
     Nothing{
         @Override
         public ByteBuddyClass apply(final ByteBuddyClass byteBuddyClass) {
             return byteBuddyClass;
         }
     }
}
