package org.powermock.core.transformers.bytebuddy;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.utility.RandomString;
import org.powermock.core.transformers.TransformStrategy;
import org.powermock.core.transformers.bytebuddy.advice.MockGatewayMethodDispatcher;
import org.powermock.core.transformers.bytebuddy.advice.MockMethodDispatchers;

abstract class AbstractMethodMockTransformer extends AbstractByteBuddyMockTransformer {
    protected final String identifier;
    
    AbstractMethodMockTransformer(final TransformStrategy strategy) {
        super(strategy);
        identifier = RandomString.make();
        MockMethodDispatchers.set(identifier, new MockGatewayMethodDispatcher());
    }
    
    public String getIdentifier() {
        return identifier;
    }
    
    @Override
    protected boolean classShouldTransformed(final TypeDescription typeDefinitions) {
        return true;
    }
}
