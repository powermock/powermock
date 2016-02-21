/*
 *  Copyright (c) 2007 Mockito contributors
 *  This program is made available under the terms of the MIT License.
 */
package org.powermock.api.mockito.repackaged.cglib.transform;

import org.powermock.api.mockito.repackaged.asm.AnnotationVisitor;
import org.powermock.api.mockito.repackaged.asm.Attribute;
import org.powermock.api.mockito.repackaged.asm.ClassVisitor;
import org.powermock.api.mockito.repackaged.asm.FieldVisitor;
import org.powermock.api.mockito.repackaged.asm.MethodVisitor;

abstract public class AbstractClassFilterTransformer extends AbstractClassTransformer {
    private ClassTransformer pass;
    private ClassVisitor target;

    protected AbstractClassFilterTransformer(ClassTransformer pass) {
        this.pass = pass;
    }

    public void setTarget(ClassVisitor target) {
        super.setTarget(target);
        pass.setTarget(target);
    }

    abstract protected boolean accept(int version, int access, String name, String signature, String superName, String[] interfaces);

    public void visit(int version,
                      int access,
                      String name,
                      String signature,
                      String superName,
                      String[] interfaces) {
        target = accept(version, access, name, signature, superName, interfaces) ? pass : cv;
        target.visit(version, access, name, signature, superName, interfaces);
    }
    
    public void visitSource(String source, String debug) {
        target.visitSource(source, debug);
    }
    
    public void visitOuterClass(String owner, String name, String desc) {
        target.visitOuterClass(owner, name, desc);
    }
    
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        return target.visitAnnotation(desc, visible);
    }
    
    public void visitAttribute(Attribute attr) {
        target.visitAttribute(attr);
    }
    
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        target.visitInnerClass(name, outerName, innerName, access);
    }

    public FieldVisitor visitField(int access,
                                   String name,
                                   String desc,
                                   String signature,
                                   Object value) {
        return target.visitField(access, name, desc, signature, value);
    }
    
    public MethodVisitor visitMethod(int access,
                                     String name,
                                     String desc,
                                     String signature,
                                     String[] exceptions) {
        return target.visitMethod(access, name, desc, signature, exceptions);
    }

    public void visitEnd() {
        target.visitEnd();
        target = null; // just to be safe
    }
}
