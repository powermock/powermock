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

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeDescription.Generic;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.StackManipulation.Compound;
import net.bytebuddy.implementation.bytecode.member.MethodVariableAccess;

public class Variable {
    
    public static Variable of(final Generic variableType, final int offset) {
        return new Variable(variableType.asErasure(), offset);
    }
    private final TypeDescription typeDefinitions;
    private final int offset;
    
    private Variable(final TypeDescription typeDefinitions, final int offset) {
        this.typeDefinitions = typeDefinitions;
        this.offset = offset;
    }
    
    public static class VariableAccess {
    
        public static StackManipulation load(Variable variable) {return load(variable, false);}
    
        public static StackManipulation load(Variable variable, final boolean boxing) {
            TypeDescription typeDefinitions = variable.typeDefinitions;
            if (typeDefinitions.isPrimitive() && boxing) {
                return new Compound(
                    MethodVariableAccess.of(typeDefinitions).loadFrom(variable.offset),
                    PrimitiveBoxing.forPrimitive(typeDefinitions)
                );
            }else {
                return MethodVariableAccess.of(typeDefinitions).loadFrom(variable.offset);
            }
        }
        
        public static StackManipulation store(Variable variable) {
            return MethodVariableAccess.of(variable.typeDefinitions).storeAt(variable.offset);
        }
    }
}
