/*
 *  Copyright (c) 2007 Mockito contributors
 *  This program is made available under the terms of the MIT License.
 */
package org.powermock.api.mockito.repackaged.cglib.proxy;

import org.powermock.api.mockito.repackaged.cglib.core.ClassEmitter;
import org.powermock.api.mockito.repackaged.cglib.core.CodeEmitter;
import org.powermock.api.mockito.repackaged.cglib.core.MethodInfo;
import org.powermock.api.mockito.repackaged.cglib.core.Signature;

import java.util.List;

interface CallbackGenerator
{
    void generate(ClassEmitter ce, Context context, List methods) throws Exception;
    void generateStatic(CodeEmitter e, Context context, List methods) throws Exception;

    interface Context
    {
        ClassLoader getClassLoader();
        CodeEmitter beginMethod(ClassEmitter ce, MethodInfo method);
        int getOriginalModifiers(MethodInfo method);
        int getIndex(MethodInfo method);
        void emitCallback(CodeEmitter ce, int index);
        Signature getImplSignature(MethodInfo method);
    }
}
