package org.powermock.core.transformers.bytebuddy;


import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;
import net.bytebuddy.matcher.ElementMatchers;
import org.powermock.core.transformers.TransformStrategy;
import org.powermock.core.transformers.bytebuddy.advice.MethodDispatcher;
import org.powermock.core.transformers.bytebuddy.advice.MockMethodDispatchers;
import org.powermock.core.transformers.bytebuddy.support.ByteBuddyClass;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import static net.bytebuddy.matcher.ElementMatchers.isNative;
import static net.bytebuddy.matcher.ElementMatchers.not;
import static org.powermock.core.transformers.bytebuddy.advice.MockMethodAdvice.VOID;

public class NativeMethodMockTransformer extends AbstractMethodMockTransformer {
    
    public NativeMethodMockTransformer(final TransformStrategy strategy) {
        super(strategy);
    }
    
    @Override
    protected boolean classShouldTransformed(final TypeDescription typeDefinitions) {
        return getStrategy() == TransformStrategy.CLASSLOADER;
    }
    
    @Override
    public ByteBuddyClass transform(final ByteBuddyClass clazz) throws Exception {
        final Identifier identifier = new DefaultIdentifier(getIdentifier());
        final Builder builder = clazz.getBuilder()
                                     .method(isNative().and(not(ElementMatchers.isStatic())))
                                     .intercept(MethodDelegation.to(InstanceInception.class))
                                     .annotateMethod(identifier)
                                     .method(isNative().and(ElementMatchers.isStatic()))
                                     .intercept(MethodDelegation.to(StaticInception.class))
                                     .annotateMethod(identifier);
        return new ByteBuddyClass(clazz.getTypeDefinitions(), builder);
    }
    
    public static class InstanceInception {
        @RuntimeType
        public static Object intercept(
                                          @This Object instance,
                                          @Origin Method origin,
                                          @AllArguments Object[] arguments
        ) throws Throwable {
            final String returnTypeAsString = getReturnTypeAsString(origin);
    
            final Identifier identifier = origin.getAnnotation(Identifier.class);
            final MethodDispatcher methodDispatcher = MockMethodDispatchers.get(identifier.value(), instance);
    
            return handleCall(origin, arguments, returnTypeAsString, instance, methodDispatcher);
        }
    }
    
    public static class StaticInception {
        
        @RuntimeType
        public static Object intercept(
                                          @Origin Method origin,
                                          @AllArguments Object[] arguments
        ) throws Throwable {
            final String returnTypeAsString = getReturnTypeAsString(origin);
            
            final Class<?> mock = origin.getDeclaringClass();
            final Identifier identifier = origin.getAnnotation(Identifier.class);
            final MethodDispatcher methodDispatcher = MockMethodDispatchers.get(identifier.value(), mock);
    
            return handleCall(origin, arguments, returnTypeAsString, mock, methodDispatcher);
        }
    
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Identifier {
        String value();
    }
    
    private static class DefaultIdentifier implements Identifier {
        private String identifier;
        
        private DefaultIdentifier(final String identifier) {this.identifier = identifier;}
        
        @Override
        public Class<? extends Annotation> annotationType() {
            return Identifier.class;
        }
        
        @Override
        public String value() {
            return identifier;
        }
    }
    
    private static String getReturnTypeAsString(final @Origin Method origin) {
        final Class<?> returnType = origin.getReturnType();
        final String returnTypeAsString;
        if (!returnType.equals(Void.class)) {
            returnTypeAsString = returnType.getName();
        } else {
            returnTypeAsString = VOID;
        }
        return returnTypeAsString;
    }
    
    private static Object handleCall(final @Origin Method origin, final @AllArguments Object[] arguments,
                             final String returnTypeAsString,
                             final Object mock, final MethodDispatcher methodDispatcher) throws Throwable {
        if (methodDispatcher == null) {
            throw new UnsupportedOperationException(origin + " is native");
        } else {
            final Callable<Object> callable = methodDispatcher.methodCall(mock, origin.getName(), arguments, origin.getParameterTypes(), returnTypeAsString);
            if (callable == null) {
                throw new UnsupportedOperationException(origin + " is native");
            }else {
                return callable.call();
            }
        }
    }
}
