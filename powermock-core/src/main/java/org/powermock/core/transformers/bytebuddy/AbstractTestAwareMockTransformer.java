package org.powermock.core.transformers.bytebuddy;

import net.bytebuddy.description.type.TypeDescription;
import org.powermock.core.transformers.TransformStrategy;

public abstract class AbstractTestAwareMockTransformer extends AbstractByteBuddyMockTransformer {
    private TypeDescription testClass;
    
    AbstractTestAwareMockTransformer(final TransformStrategy strategy) {super(strategy);}
    
    boolean isNestedTestClass(final TypeDescription td) {
        final TypeDescription enclosingType = td.getEnclosingType();
        if(enclosingType == null){
            return false;
        }
        return enclosingType.equals(testClass);
    }
    
    boolean isTestClass(final TypeDescription td) {
        return td.equals(testClass);
    }
    
    public void setTestClass(final Class<?> testClass) {
        this.testClass = TypeDescription.Sort.describe(testClass).asErasure();
    }
}
