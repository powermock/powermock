package org.powermock.core.transformers.bytebuddy.advice;

import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bytecode.assign.Assigner;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public class MockMethodAdvice {
    
    public static final String VOID = "";
    
    @SuppressWarnings("unused")
    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
    private static Callable<?> enter(@Identifier String identifier,
                                     @Advice.This Object instance,
                                     @Advice.Origin Method origin,
                                     @Advice.AllArguments Object[] arguments) throws Throwable {
        final Class<?> returnType = origin.getReturnType();
        final String returnTypeAsString;
        if (!returnType.equals(Void.class)) {
            returnTypeAsString = returnType.getName();
        } else {
            returnTypeAsString = VOID;
        }
    
        final MethodDispatcher methodDispatcher = MockMethodDispatchers.get(identifier, instance);
        
        if (methodDispatcher == null){
            return null;
        }else {
            return methodDispatcher.methodCall(instance, origin.getName(), arguments, origin.getParameterTypes(), returnTypeAsString);
        }
    }
    
    @SuppressWarnings({"unused", "UnusedAssignment"})
    @Advice.OnMethodExit
    private static void exit(@Advice.Return(readOnly = false, typing = Assigner.Typing.DYNAMIC) Object returned,
                             @Advice.Enter Callable<?> mocked) throws Throwable {
        if (mocked != null) {
            returned = mocked.call();
        }
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Identifier {
    
    }
    
}
