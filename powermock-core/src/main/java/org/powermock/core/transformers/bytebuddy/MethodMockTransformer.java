package org.powermock.core.transformers.bytebuddy;

import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.AsmVisitorWrapper.ForDeclaredMethods;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.matcher.ElementMatchers;
import org.powermock.core.transformers.TransformStrategy;
import org.powermock.core.transformers.bytebuddy.advice.MockMethodAdvice;
import org.powermock.core.transformers.bytebuddy.advice.MockStaticMethodAdvice;
import org.powermock.core.transformers.bytebuddy.support.ByteBuddyClass;

import static net.bytebuddy.matcher.ElementMatchers.isMethod;
import static net.bytebuddy.matcher.ElementMatchers.not;

public class MethodMockTransformer extends AbstractMethodMockTransformer {
    
    public MethodMockTransformer(final TransformStrategy strategy) {
        super(strategy);
    }
    
    @Override
    public ByteBuddyClass transform(final ByteBuddyClass clazz) throws Exception {
        final Builder builder = clazz.getBuilder()
                                     .visit(instanceMethods())
                                     .visit(staticMethods());
        return ByteBuddyClass.from(clazz.getTypeDescription(), builder);
    }
    
    private ForDeclaredMethods staticMethods() {
        return Advice.withCustomMapping()
                     .bind(MockMethodAdvice.Identifier.class, identifier)
                     .to(MockStaticMethodAdvice.class)
                     .on(
                         isMethod().and(ElementMatchers.<MethodDescription>isStatic()).and(not(ElementMatchers.<MethodDescription>isNative()))
                     );
    }
    
    private ForDeclaredMethods instanceMethods() {
        return Advice.withCustomMapping()
                     .bind(MockMethodAdvice.Identifier.class, identifier)
                     .to(MockMethodAdvice.class)
                     .on(
                         isMethod().and(
                             not(
                                 ElementMatchers.<MethodDescription>isStatic().or(ElementMatchers.<MethodDescription>isSynthetic()).or(ElementMatchers.<MethodDescription>isNative())
                             )
                         )
                     );
    }
}
