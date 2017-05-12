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

import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeDescription.ForLoadedType;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.StackSize;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;

public enum PrimitiveBoxing implements StackManipulation{
    
    BOOLEAN(Boolean.class, StackSize.ZERO, "valueOf", "(Z)Ljava/lang/Boolean;"),
    
    BYTE(Byte.class, StackSize.ZERO, "valueOf", "(B)Ljava/lang/Byte;"),
    
    SHORT(Short.class, StackSize.ZERO, "valueOf", "(S)Ljava/lang/Short;"),
    
    CHARACTER(Character.class, StackSize.ZERO, "valueOf", "(C)Ljava/lang/Character;"),
    
    INTEGER(Integer.class, StackSize.ZERO, "valueOf", "(I)Ljava/lang/Integer;"),
    
    LONG(Long.class, StackSize.SINGLE, "valueOf", "(J)Ljava/lang/Long;"),
    
    FLOAT(Float.class, StackSize.ZERO, "valueOf", "(F)Ljava/lang/Float;"),
    
    DOUBLE(Double.class, StackSize.SINGLE, "valueOf", "(D)Ljava/lang/Double;");
    
    private final ForLoadedType wrapperType;
    private final Size size;
    private final String boxingMethodName;
    private final String boxingMethodDescriptor;
    
    PrimitiveBoxing(Class<?> wrapperType,
                    StackSize sizeDifference,
                    String boxingMethodName,
                    String boxingMethodDescriptor) {
        this.wrapperType = new TypeDescription.ForLoadedType(wrapperType);
        this.size = sizeDifference.toDecreasingSize();
        this.boxingMethodName = boxingMethodName;
        this.boxingMethodDescriptor = boxingMethodDescriptor;
    }
    
    public static PrimitiveBoxing forPrimitive(TypeDefinition typeDefinition) {
        if (typeDefinition.represents(boolean.class)) {
            return BOOLEAN;
        } else if (typeDefinition.represents(byte.class)) {
            return BYTE;
        } else if (typeDefinition.represents(short.class)) {
            return SHORT;
        } else if (typeDefinition.represents(char.class)) {
            return CHARACTER;
        } else if (typeDefinition.represents(int.class)) {
            return INTEGER;
        } else if (typeDefinition.represents(long.class)) {
            return LONG;
        } else if (typeDefinition.represents(float.class)) {
            return FLOAT;
        } else if (typeDefinition.represents(double.class)) {
            return DOUBLE;
        } else {
            throw new IllegalArgumentException("Not a non-void, primitive type: " + typeDefinition);
        }
    }
    
    @Override
    public boolean isValid() {
        return true;
    }
    
    @Override
    public Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
        methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC,
                                      wrapperType.getInternalName(),
                                      boxingMethodName,
                                      boxingMethodDescriptor,
                                      false);
        return size;
    }
}
