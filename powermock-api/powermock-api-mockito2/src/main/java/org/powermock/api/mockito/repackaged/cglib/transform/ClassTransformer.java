/*
 *  Copyright (c) 2007 Mockito contributors
 *  This program is made available under the terms of the MIT License.
 */
package org.powermock.api.mockito.repackaged.cglib.transform;

import org.powermock.api.mockito.repackaged.asm.ClassVisitor;

public interface ClassTransformer extends ClassVisitor {
    public void setTarget(ClassVisitor target);
}
