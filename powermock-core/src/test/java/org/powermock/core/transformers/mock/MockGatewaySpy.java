package org.powermock.core.transformers.mock;

import org.assertj.core.api.Condition;
import org.assertj.core.description.Description;
import org.assertj.core.description.TextDescription;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.powermock.core.MockGateway.SUPPRESS;

public class MockGatewaySpy {
    private static final String ANY = "$*$";
    private static Map<String, Object> returnOnMethodCall = new HashMap<String, Object>();
    
    private final static List<MethodCall> methodCalls = new LinkedList<MethodCall>();
    private final static List<MethodCall> constructorCalls = new LinkedList<MethodCall>();
    private final static List<String> fieldCalls = new LinkedList<String>();
    
    public static Object constructorCall(Class<?> type, Object[] args, Class<?>[] sig) {
        final MethodCall methodCall = new MethodCall();
        methodCall.type = type;
        methodCall.args = args;
        methodCall.sig = sig;
        constructorCalls.add(methodCall);
        return getResult("<init>");
    }
    
    public static boolean suppressConstructorCall(Class<?> type, Object[] args, Class<?>[] sig) {
        return constructorCall(type, args, sig) == SUPPRESS;
    }
    
    public static Object methodCall(Object instance, String methodName, Object[] args, Class<?>[] sig,
                                    String returnTypeAsString) throws Throwable {
        return doMethodCall(instance.getClass(), methodName, args, sig, returnTypeAsString);
    }
    
    public static Object methodCall(Class<?> type, String methodName, Object[] args, Class<?>[] sig,
                                    String returnTypeAsString) throws Throwable {
        return doMethodCall(type, methodName, args, sig, returnTypeAsString);
    }
    
    private static Object doMethodCall(final Class<?> type, final String methodName, final Object[] args, final Class<?>[] sig,
                                       final String returnTypeAsString) {
        final MethodCall methodCall = new MethodCall();
        
        methodCall.type = type;
        methodCall.methodName = methodName;
        methodCall.args = args;
        methodCall.sig = sig;
        methodCall.returnTypeAsString = returnTypeAsString;
        
        registerMethodCall(methodCall);
    
        return getResult(methodName);
    }
    
    private static Object getResult(final String methodName) {
        Object result = returnOnMethodCall.get(methodName);
        if (result == null) {
            result = returnOnMethodCall.get(ANY);
        }
        return result;
    }
    
    private static void registerMethodCall(MethodCall methodName) {
        methodCalls.add(methodName);
    }
    
    private static void registerFieldCall(String fieldName) {
        fieldCalls.add(fieldName);
    }
    
    public static List<MethodCall> methodCalls() {
        return methodCalls;
    }
    
    public static List<String> getFieldCalls() {
        return fieldCalls;
    }
    
    public static List<MethodCall> constructorCalls() {
        return constructorCalls;
    }
    
    public static void returnOnMethodCall(final Object expected) {
        MockGatewaySpy.returnOnMethodCall.put(ANY, expected);
    }
    
    public static void returnOnMethodCall(final String methodName, final Object expected) {
        MockGatewaySpy.returnOnMethodCall.put(methodName, expected);
    }
    
    public static void clear() {
        methodCalls.clear();
        fieldCalls.clear();
        constructorCalls.clear();
        returnOnMethodCall.clear();
    }
    
    public static class MethodCall {
        public Object[] args;
        public Class<?>[] sig;
        public String returnTypeAsString;
        public Class<?> type;
        public String methodName;
    
        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer("MethodCall{\n");
            sb.append("args=").append(args == null ? "null" : Arrays.asList(args).toString());
            sb.append("\n, sig=").append(sig == null ? "null" : Arrays.asList(sig).toString());
            sb.append("\n, returnTypeAsString='").append(returnTypeAsString).append('\'');
            sb.append("\n, type=").append(type);
            sb.append("\n, methodName='").append(methodName).append('\'');
            sb.append("}\n");
            return sb.toString();
        }
    }
    
    public static class ConditionBuilder {
        
        public static ConditionBuilder registered() {
            return new ConditionBuilder();
        }
        
        public Condition<? super List<? extends MethodCall>> forMethod(final String methodName) {
            return new Condition<List<? extends MethodCall>>() {
                @Override
                public boolean matches(final List<? extends MethodCall> value) {
                    for (MethodCall methodCall : value) {
                        if (methodName.equals(methodCall.methodName)) {
                            return true;
                        }
                    }
                    return false;
                }
                
                @Override
                public Description description() {
                    return new TextDescription("The method `%s` has not been called.", methodName);
                }
            };
        }
    }
}
