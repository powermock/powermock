/*
 *  Copyright (c) 2007 Mockito contributors
 *  This program is made available under the terms of the MIT License.
 */
package org.powermock.api.mockito.repackaged.cglib.transform;

import org.powermock.api.mockito.repackaged.asm.ClassAdapter;
import org.powermock.api.mockito.repackaged.asm.ClassVisitor;

public class ClassTransformerTee extends ClassAdapter implements ClassTransformer {
    private ClassVisitor branch;
    
    public ClassTransformerTee(ClassVisitor branch) {
        super(null);
        this.branch = branch;
    }
    
    public void setTarget(ClassVisitor target) { 
        cv = new ClassVisitorTee(branch, target);
    }
}
