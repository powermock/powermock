/*
 *  Copyright (c) 2007 Mockito contributors
 *  This program is made available under the terms of the MIT License.
 */
package org.powermock.api.mockito.repackaged.cglib.transform;

import org.powermock.api.mockito.repackaged.asm.ClassAdapter;
import org.powermock.api.mockito.repackaged.asm.ClassVisitor;

abstract public class AbstractClassTransformer extends ClassAdapter implements ClassTransformer {
    protected AbstractClassTransformer() {
        super(null);
    }

    public void setTarget(ClassVisitor target) {
        cv = target;
    }
}
