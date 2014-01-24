package org.powermock.modules.agent;

import org.powermock.objectweb.asm.ClassAdapter;
import org.powermock.objectweb.asm.ClassVisitor;
import org.powermock.objectweb.asm.MethodVisitor;
import org.powermock.objectweb.asm.Opcodes;

import java.lang.reflect.Modifier;

class PowerMockClassVisitor extends ClassAdapter {

    public PowerMockClassVisitor(ClassVisitor classVisitor) {
        super(classVisitor);
    }

    @Override
    public void visit(final int version, final int access, final String name, final String signature, final String superName,
            final String[] interfaces) {
        final int accessModifiersWithFinalRemoved = removeFinal(access);
        super.visit(version, accessModifiersWithFinalRemoved, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, final String name, final String desc, final String signature,
            final String[] exceptions) {
//        if(isConstructor(name)) {
//            access = setConstructorToPublic(access);
//        }
        return super.visitMethod(removeFinal(access), name, desc, signature, exceptions);
    }

    private int setConstructorToPublic(int access) {
        return Modifier.isPublic(access) ? access : access & Opcodes.ACC_PUBLIC;
    }

    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        super.visitInnerClass(name, outerName, innerName, removeFinal(access));
    }

    private int removeFinal(int access) {
        return access & ~Opcodes.ACC_FINAL;
    }

    private boolean isConstructor(String name) {
        return name.equals("<init>");
    }
}
