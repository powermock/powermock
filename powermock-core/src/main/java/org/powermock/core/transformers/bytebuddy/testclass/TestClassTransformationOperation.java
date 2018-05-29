package org.powermock.core.transformers.bytebuddy.testclass;

import org.powermock.core.transformers.bytebuddy.support.ByteBuddyClass;

public interface TestClassTransformationOperation {
    ByteBuddyClass apply(ByteBuddyClass byteBuddyClass);
}
