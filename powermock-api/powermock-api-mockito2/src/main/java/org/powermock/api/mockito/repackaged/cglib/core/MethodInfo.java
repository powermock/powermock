/*
 *  Copyright (c) 2007 Mockito contributors
 *  This program is made available under the terms of the MIT License.
 */
package org.powermock.api.mockito.repackaged.cglib.core;

import org.powermock.api.mockito.repackaged.asm.Type;

abstract public class MethodInfo {

    protected MethodInfo() {
    }
    
    abstract public ClassInfo getClassInfo();
    abstract public int getModifiers();
    abstract public Signature getSignature();
    abstract public Type[] getExceptionTypes();

    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (!(o instanceof MethodInfo))
            return false;
        return getSignature().equals(((MethodInfo)o).getSignature());
    }

    public int hashCode() {
        return getSignature().hashCode();
    }

    public String toString() {
        // TODO: include modifiers, exceptions
        return getSignature().toString();
    }
}
