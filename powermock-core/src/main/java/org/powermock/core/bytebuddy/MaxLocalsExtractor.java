/*
 *
 *   Copyright 2017 the original author or authors.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package org.powermock.core.bytebuddy;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.jar.asm.ClassVisitor;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;

public class MaxLocalsExtractor extends ClassVisitor {
    
    private MethodMaxLocals methodMaxLocals;
    
    public MaxLocalsExtractor() {
        super(Opcodes.ASM5);
    }
    
    @Override
    public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature,
                                     final String[] exceptions) {
        if (MethodDescription.CONSTRUCTOR_INTERNAL_NAME.equals(name)) {
            methodMaxLocals = new MethodMaxLocals();
            return new MaxLocalsMethodVisitor(name, desc, methodMaxLocals);
        }
        return super.visitMethod(access, name, desc, signature, exceptions);
    }
    
    public MethodMaxLocals getMethods() {
        return methodMaxLocals;
    }
    
    private static class MaxLocalsMethodVisitor extends MethodVisitor {
        
        private final String name;
        private final String signature;
        private final MethodMaxLocals methodMaxLocals;
        private int maxLocals;
        
        private MaxLocalsMethodVisitor(final String name, final String signature,
                                       final MethodMaxLocals methodMaxLocals) {
            super(Opcodes.ASM5);
            this.name = name;
            this.signature = signature;
            this.methodMaxLocals = methodMaxLocals;
        }
        
        @Override
        public void visitMaxs(final int maxStack, final int maxLocals) {
            this.maxLocals = maxLocals;
            super.visitMaxs(maxStack, maxLocals);
        }
        
        @Override
        public void visitEnd() {
            methodMaxLocals.add(name, signature, maxLocals);
            super.visitEnd();
        }
    }
    
}
