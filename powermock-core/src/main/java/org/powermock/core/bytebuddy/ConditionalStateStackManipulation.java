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

import net.bytebuddy.implementation.Implementation.Context;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.jar.asm.Label;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;

public class ConditionalStateStackManipulation implements StackManipulation {
    
    
    private final StackManipulation condition;
    private final StackManipulation action;
    private final StackManipulation otherwise;
    private final Frame frame;
    
    public ConditionalStateStackManipulation(final StackManipulation condition,
                                             final StackManipulation action,
                                             final StackManipulation otherwise,
                                             final Frame frame) {
        
        this.condition = condition;
        this.action = action;
        this.otherwise = otherwise;
        this.frame = frame;
    }
    
    @Override
    public boolean isValid() {
        return true;
    }
    
    @Override
    public Size apply(final MethodVisitor mv, final Context implementationContext) {
    
        Size size = new Size(0, 0);
        
        Label proceed = new Label();
        Label exit = new Label();
    
        size = size.aggregate(condition.apply(mv, implementationContext));
    
        mv.visitJumpInsn(Opcodes.IFEQ, proceed);
    
        size = size.aggregate(action.apply(mv, implementationContext));
    
        mv.visitJumpInsn(Opcodes.GOTO, exit);
    
        mv.visitLabel(proceed);
        mv.visitFrame(Opcodes.F_FULL, frame.localSize(), frame.locals(), 0, null);
    
        size = size.aggregate(otherwise.apply(mv, implementationContext));
        
        mv.visitLabel(exit);
    
        mv.visitFrame(Opcodes.F_FULL, 0, null, 0, null);
        
        return size;
    }
}
