package org.powermock.core.transformers.bytebuddy;

import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.AsmVisitorWrapper.ForDeclaredMethods;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.RandomString;
import org.powermock.core.transformers.TransformStrategy;
import org.powermock.core.transformers.bytebuddy.advice.MockMethodAdvice;
import org.powermock.core.transformers.bytebuddy.advice.MockGatewayMethodDispatcher;
import org.powermock.core.transformers.bytebuddy.advice.MockMethodDispatchers;
import org.powermock.core.transformers.bytebuddy.advice.MockStaticMethodAdvice;
import org.powermock.core.transformers.bytebuddy.support.ByteBuddyClass;

import static net.bytebuddy.matcher.ElementMatchers.isDeclaredBy;
import static net.bytebuddy.matcher.ElementMatchers.isMethod;
import static net.bytebuddy.matcher.ElementMatchers.isVirtual;

public class MethodMockTransformer extends AbstractByteBuddyMockTransformer {
    
    private final String identifier;
    
    public MethodMockTransformer(final TransformStrategy strategy) {
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
    
    @Override
    public ByteBuddyClass transform(final ByteBuddyClass clazz) throws Exception {
        final Builder builder = clazz.getBuilder()
                                     .visit(virtualMethods())
                                     .visit(staticMethods());
        return new ByteBuddyClass(clazz.getTypeDefinitions(), builder);
    }
    
    private ForDeclaredMethods staticMethods() {
        return Advice.withCustomMapping()
                     .bind(MockMethodAdvice.Identifier.class, identifier)
                     .to(MockStaticMethodAdvice.class)
                     .on(
                         isMethod().and(ElementMatchers.<MethodDescription>isStatic())
                     );
    }
    
    private ForDeclaredMethods virtualMethods() {
        return Advice.withCustomMapping()
                     .bind(MockMethodAdvice.Identifier.class, identifier)
                     .to(MockMethodAdvice.class)
                     .on(isVirtual());
    }
}
