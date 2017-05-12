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


import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.method.ParameterDescription.InDefinedShape;
import net.bytebuddy.description.method.ParameterList;
import net.bytebuddy.description.type.TypeDescription.Generic;
import net.bytebuddy.implementation.bytecode.StackSize;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.utility.CompoundList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class Frame {
    
    public static Frame beforeConstructorCall(final Iterable<? extends ParameterDescription> constructorParameters) {
        List<LocalVariable> locals = new ArrayList<LocalVariable>();
        
        locals.add(LocalVariable.UNINITIALIZED_THIS);
        
        int maxLocals = 1;
        
        for (ParameterDescription sourceParameter : constructorParameters) {
            Generic type = sourceParameter.getType();
            locals.add(LocalVariable.from(type));
            maxLocals += type.getStackSize().getSize();
        }
        
        return new Frame(locals);
    }
    
    private Deque<Object> stack;
    private List<LocalVariable> locals;
    
    public Frame(final List<LocalVariable> locals) {
        this.locals = Collections.unmodifiableList(locals);
        this.stack = new LinkedList<Object>();
    }
    
    public Frame addTopToLocals(final int count) {
        List<LocalVariable> locals = new ArrayList<LocalVariable>();
        for (int i = 0; i < count; i++) {
            locals.add(LocalVariable.TOP);
        }
        
        return new Frame(
                            CompoundList.of(this.locals, locals)
        );
    }
    
    public Frame addLocalVariable(final LocalVariable localVariable) {
        return new Frame(
                            CompoundList.of(this.locals, localVariable)
        );
    }
    
    public Frame addLocalVariables(final ParameterList<InDefinedShape> types) {
        
        List<LocalVariable> frameLocals = new ArrayList<LocalVariable>();
        
        for (ParameterDescription parameter : types) {
            Generic type = parameter.getType();
            frameLocals.add(LocalVariable.from(type));
        }
        
        return new Frame(CompoundList.of(this.locals, frameLocals));
    }
    
    public Object[] locals() {
        Object[] frameLocals = new Object[this.locals.size()];
        for (int i = 0; i < this.locals.size(); i++) {
            frameLocals[i] = this.locals.get(i).getType();
        }
        return frameLocals;
    }
    
    public int localSize() {
        return locals.size();
    }
    
    public int maxLocalVariableIndex() {
        int localStackSize = 0;
        for (LocalVariable localVariable : locals) {
            localStackSize += localVariable.getStackSize().getSize();
        }
        return localStackSize;
    }
    
    public static class LocalVariable {
        public static LocalVariable from(final Generic type) {
            if (type.represents(double.class)) {
                return DOUBLE;
            } else {
                return new LocalVariable(
                    type.getTypeName().replace('.', '/'),
                    type.getStackSize()
                );
            }
        }
        
        public static final LocalVariable UNINITIALIZED_THIS = new LocalVariable(Opcodes.UNINITIALIZED_THIS, StackSize.SINGLE);
        public static final LocalVariable TOP = new LocalVariable(Opcodes.TOP, StackSize.SINGLE);
        public static final LocalVariable DOUBLE = new LocalVariable(Opcodes.DOUBLE, StackSize.DOUBLE);
        
        private final Object type;
        private final StackSize stackSize;
    
        private LocalVariable(final Object type, StackSize size) {
            this.type = type;
            this.stackSize = size;
        }
        
        public Object getType() {
            return type;
        }
    
        public StackSize getStackSize() {
            return stackSize;
        }
    }
}
