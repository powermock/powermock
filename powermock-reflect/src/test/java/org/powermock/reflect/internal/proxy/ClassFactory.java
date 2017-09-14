package org.powermock.reflect.internal.proxy;

import net.bytebuddy.jar.asm.ClassWriter;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;

class ClassFactory implements Opcodes {
    
    static byte[] create(String className) throws Exception {
        
        ClassWriter cw = new ClassWriter(0);
        MethodVisitor mv;
        
        cw.visit(49,
                 ACC_PUBLIC + ACC_SUPER,
                 className,
                 null,
                 "java/lang/Object",
                 null);
        
        cw.visitSource("Hello.java", null);
        
        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL,
                               "java/lang/Object",
                               "<init>",
                               "()V", false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC,
                                "main",
                                "([Ljava/lang/String;)V",
                                null,
                                null);
            mv.visitFieldInsn(GETSTATIC,
                              "java/lang/System",
                              "out",
                              "Ljava/io/PrintStream;");
            mv.visitLdcInsn("hello");
            mv.visitMethodInsn(INVOKEVIRTUAL,
                               "java/io/PrintStream",
                               "println",
                               "(Ljava/lang/String;)V", false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 1);
            mv.visitEnd();
        }
        cw.visitEnd();
        
        return cw.toByteArray();
    }
}
