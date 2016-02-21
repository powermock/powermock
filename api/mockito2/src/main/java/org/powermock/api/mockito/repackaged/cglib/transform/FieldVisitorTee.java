/*
 *  Copyright (c) 2007 Mockito contributors
 *  This program is made available under the terms of the MIT License.
 */
package org.powermock.api.mockito.repackaged.cglib.transform;

import org.powermock.api.mockito.repackaged.asm.AnnotationVisitor;
import org.powermock.api.mockito.repackaged.asm.Attribute;
import org.powermock.api.mockito.repackaged.asm.FieldVisitor;

public class FieldVisitorTee implements FieldVisitor {
    private FieldVisitor fv1, fv2;
    
    public FieldVisitorTee(FieldVisitor fv1, FieldVisitor fv2) {
        this.fv1 = fv1;
        this.fv2 = fv2;
    }

    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        return AnnotationVisitorTee.getInstance(fv1.visitAnnotation(desc, visible),
                                                fv2.visitAnnotation(desc, visible));
    }
    
    public void visitAttribute(Attribute attr) {
        fv1.visitAttribute(attr);
        fv2.visitAttribute(attr);
    }

    public void visitEnd() {
        fv1.visitEnd();
        fv2.visitEnd();
    }
}

