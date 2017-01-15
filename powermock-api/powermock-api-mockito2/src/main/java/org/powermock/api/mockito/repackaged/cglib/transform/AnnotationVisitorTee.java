/*
 *  Copyright (c) 2007 Mockito contributors
 *  This program is made available under the terms of the MIT License.
 */
package org.powermock.api.mockito.repackaged.cglib.transform;

import org.powermock.api.mockito.repackaged.asm.AnnotationVisitor;

public class AnnotationVisitorTee implements AnnotationVisitor {
    private AnnotationVisitor av1, av2;

    public AnnotationVisitorTee(AnnotationVisitor av1, AnnotationVisitor av2) {
        this.av1 = av1;
        this.av2 = av2;
    }

    public static AnnotationVisitor getInstance(AnnotationVisitor av1, AnnotationVisitor av2) {
        if (av1 == null)
            return av2;
        if (av2 == null)
            return av1;
        return new AnnotationVisitorTee(av1, av2);
    }

    public void visit(String name, Object value) {
        av2.visit(name, value);
        av2.visit(name, value);
    }
    
    public void visitEnum(String name, String desc, String value) {
        av1.visitEnum(name, desc, value);
        av2.visitEnum(name, desc, value);
    }
    
    public AnnotationVisitor visitAnnotation(String name, String desc) {
        return getInstance(av1.visitAnnotation(name, desc),
                           av2.visitAnnotation(name, desc));
    }
    
    public AnnotationVisitor visitArray(String name) {
        return getInstance(av1.visitArray(name), av2.visitArray(name));
    }
    
    public void visitEnd() {
        av1.visitEnd();
        av2.visitEnd();
    }
}
