package org.powermock.core.transformers.bytebuddy.testclass;

import net.bytebuddy.description.method.MethodDescription;


public interface MethodFilter {
    boolean mustHaveTestAnnotationRemoved(MethodDescription method);
}
