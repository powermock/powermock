/*
 *  Copyright (c) 2007 Mockito contributors
 *  This program is made available under the terms of the MIT License.
 */
package org.powermock.api.mockito.repackaged.cglib.core;

import org.powermock.api.mockito.repackaged.asm.Type;

import java.lang.reflect.Member;
import java.lang.reflect.Modifier;

public class VisibilityPredicate implements Predicate {
    private boolean protectedOk;
    private String pkg;

    public VisibilityPredicate(Class source, boolean protectedOk) {
        this.protectedOk = protectedOk;
        pkg = TypeUtils.getPackageName(Type.getType(source));
    }

    public boolean evaluate(Object arg) {
        int mod = (arg instanceof Member) ? ((Member)arg).getModifiers() : ((Integer)arg).intValue();
        if (Modifier.isPrivate(mod)) {
            return false;
        } else if (Modifier.isPublic(mod)) {
            return true;
        } else if (Modifier.isProtected(mod)) {
            return protectedOk;
        } else {
            return pkg.equals(TypeUtils.getPackageName(Type.getType(((Member)arg).getDeclaringClass())));
        }
    }
}

