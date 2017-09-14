package org.powermock.modules.agent;

import net.bytebuddy.jar.asm.ClassVisitor;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;

class PowerMockClassVisitor extends ClassVisitor {

    public PowerMockClassVisitor(ClassVisitor classVisitor) {
        super(Opcodes.ASM5, classVisitor);
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
        return super.visitMethod(removeFinal(access), name, desc, signature, exceptions);
    }

    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        super.visitInnerClass(name, outerName, innerName, removeFinal(access));
    }

    private int removeFinal(int access) {
        return access & ~Opcodes.ACC_FINAL;
    }

}
