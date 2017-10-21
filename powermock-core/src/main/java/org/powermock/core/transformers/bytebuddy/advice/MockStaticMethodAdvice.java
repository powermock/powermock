package org.powermock.core.transformers.bytebuddy.advice;

import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import org.powermock.core.transformers.bytebuddy.advice.MockMethodAdvice.Identifier;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import static org.powermock.core.transformers.bytebuddy.advice.MockMethodAdvice.VOID;

public class MockStaticMethodAdvice {
    
    @SuppressWarnings("unused")
    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
    private static Callable<?> enter(@Identifier String identifier,
                                     @Advice.Origin Method origin,
                                     @Advice.AllArguments Object[] arguments) throws Throwable {
        final Class<?> returnType = origin.getReturnType();
        final String returnTypeAsString;
        if (!returnType.equals(Void.class)) {
            returnTypeAsString = returnType.getName();
        } else {
            returnTypeAsString = VOID;
        }
    
        final Class<?> mock = origin.getDeclaringClass();
        final MethodDispatcher methodDispatcher = MockMethodDispatchers.get(identifier, mock);
    
        if (methodDispatcher == null){
            return null;
        }else {
            return methodDispatcher.methodCall(mock, origin.getName(), arguments, origin.getParameterTypes(), returnTypeAsString);
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
    
}
