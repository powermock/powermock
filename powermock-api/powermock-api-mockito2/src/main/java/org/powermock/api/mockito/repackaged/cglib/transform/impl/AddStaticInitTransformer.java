/*
 *  Copyright (c) 2007 Mockito contributors
 *  This program is made available under the terms of the MIT License.
 */
package org.powermock.api.mockito.repackaged.cglib.transform.impl;

import org.powermock.api.mockito.repackaged.asm.Type;
import org.powermock.api.mockito.repackaged.cglib.core.CodeEmitter;
import org.powermock.api.mockito.repackaged.cglib.core.Constants;
import org.powermock.api.mockito.repackaged.cglib.core.EmitUtils;
import org.powermock.api.mockito.repackaged.cglib.core.MethodInfo;
import org.powermock.api.mockito.repackaged.cglib.core.ReflectUtils;
import org.powermock.api.mockito.repackaged.cglib.core.TypeUtils;
import org.powermock.api.mockito.repackaged.cglib.transform.ClassEmitterTransformer;

import java.lang.reflect.Method;

/**
 * @author Juozas Baliuka, Chris Nokleberg
 */
public class AddStaticInitTransformer extends ClassEmitterTransformer {
    private MethodInfo info;

    public AddStaticInitTransformer(Method classInit) {
        info = ReflectUtils.getMethodInfo(classInit);
        if (!TypeUtils.isStatic(info.getModifiers())) {
            throw new IllegalArgumentException(classInit + " is not static");
        }
        Type[] types = info.getSignature().getArgumentTypes();
        if (types.length != 1 ||
            !types[0].equals(Constants.TYPE_CLASS) ||
            !info.getSignature().getReturnType().equals(Type.VOID_TYPE)) {
            throw new IllegalArgumentException(classInit + " illegal signature");
        }
    }

    protected void init() {
        if (!TypeUtils.isInterface(getAccess())) {
            CodeEmitter e = getStaticHook();
            EmitUtils.load_class_this(e);
            e.invoke(info);
        }
    }
}
