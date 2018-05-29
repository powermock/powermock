package org.powermock.core.transformers;

import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import net.bytebuddy.description.method.MethodDescription;
import org.powermock.PowerMockInternalException;

import java.lang.reflect.Method;

public enum MethodSignatures {
    
    ByteBuddy {
        @Override
        public MethodSignatureWriter<MethodDescription> methodSignatureWriter() {
            return new ByteBuddyMethodSignatureWriterWriter();
        }
    },
    
    Javassist {
        @Override
        public MethodSignatureWriter<CtMethod> methodSignatureWriter() {
            return new JavassistMethodSignatureWriterWriter();
        }
    };
    
    public abstract <T> MethodSignatureWriter<T> methodSignatureWriter();
    
    private static class ByteBuddyMethodSignatureWriterWriter implements MethodSignatureWriter<MethodDescription> {
        
        @Override
        public String signatureFor(final MethodDescription method) {
            return method.toGenericString();
        }
        
        @Override
        public String signatureForReflection(final Method method) {
            return method.toString();
        }
    }
    
    private static class JavassistMethodSignatureWriterWriter implements MethodSignatureWriter<CtMethod> {
        @Override
        public String signatureFor(final CtMethod m) {
            try {
                CtClass[] paramTypes = m.getParameterTypes();
                String[] paramTypeNames = new String[paramTypes.length];
                for (int i = 0; i < paramTypeNames.length; ++i) {
                    paramTypeNames[i] = paramTypes[i].getSimpleName();
                }
                return createSignature(
                    m.getDeclaringClass().getSimpleName(),
                    m.getReturnType().getSimpleName(),
                    m.getName(), paramTypeNames);
            } catch (NotFoundException e) {
                throw new PowerMockInternalException(e);
            }
        }
        
        @Override
        public String signatureForReflection(final Method m) {
            Class[] paramTypes = m.getParameterTypes();
            String[] paramTypeNames = new String[paramTypes.length];
            for (int i = 0; i < paramTypeNames.length; ++i) {
                paramTypeNames[i] = paramTypes[i].getSimpleName();
            }
            return createSignature(
                m.getDeclaringClass().getSimpleName(),
                m.getReturnType().getSimpleName(),
                m.getName(), paramTypeNames);
        }
        
        private String createSignature(
            String testClass, String returnType, String methodName,
            String[] paramTypes) {
            StringBuilder builder = new StringBuilder(testClass)
                                        .append('\n').append(returnType)
                                        .append('\n').append(methodName);
            for (String param : paramTypes) {
                builder.append('\n').append(param);
            }
            return builder.toString();
        }
    }
}
