package org.powermock.modules.agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

public class DefinalizingClassTransformer extends AbstractClassTransformer implements ClassFileTransformer {

     
    
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (loader == null || shouldIgnore(className)) {
            return null;
        }
        final ClassReader reader = new ClassReader(classfileBuffer);
        final ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        reader.accept(new PowerMockClassVisitor(writer),
                ClassReader.SKIP_FRAMES);
        return writer.toByteArray();
    }

}
