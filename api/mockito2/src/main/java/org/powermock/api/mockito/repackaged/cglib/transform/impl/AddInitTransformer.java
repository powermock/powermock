/*
 *  Copyright (c) 2007 Mockito contributors
 *  This program is made available under the terms of the MIT License.
 */
package org.powermock.api.mockito.repackaged.cglib.transform.impl;

import org.powermock.api.mockito.repackaged.asm.Type;
import org.powermock.api.mockito.repackaged.cglib.core.CodeEmitter;
import org.powermock.api.mockito.repackaged.cglib.core.Constants;
import org.powermock.api.mockito.repackaged.cglib.core.MethodInfo;
import org.powermock.api.mockito.repackaged.cglib.core.ReflectUtils;
import org.powermock.api.mockito.repackaged.cglib.core.Signature;
import org.powermock.api.mockito.repackaged.cglib.transform.ClassEmitterTransformer;

import java.lang.reflect.Method;

/**
 * @author	Mark Hobson
 */
public class AddInitTransformer extends ClassEmitterTransformer {
    private MethodInfo info;
    
    public AddInitTransformer(Method method) {
        info = ReflectUtils.getMethodInfo(method);
        
        Type[] types = info.getSignature().getArgumentTypes();
        if (types.length != 1 ||
        !types[0].equals(Constants.TYPE_OBJECT) ||
        !info.getSignature().getReturnType().equals(Type.VOID_TYPE)) {
            throw new IllegalArgumentException(method + " illegal signature");
        }
    }
    
    public CodeEmitter begin_method(int access, Signature sig, Type[] exceptions) {
        final CodeEmitter emitter = super.begin_method(access, sig, exceptions);
        if (sig.getName().equals(Constants.CONSTRUCTOR_NAME)) {
            return new CodeEmitter(emitter) {
                public void visitInsn(int opcode) {
                    if (opcode == Constants.RETURN) {
                        load_this();
                        invoke(info);
                    }
                    super.visitInsn(opcode);
                }
            };
        }
        return emitter;
    }
}

