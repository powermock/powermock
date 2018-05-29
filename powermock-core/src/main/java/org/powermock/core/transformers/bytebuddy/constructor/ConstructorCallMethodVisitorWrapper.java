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

package org.powermock.core.transformers.bytebuddy.constructor;

import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodDescription.InDefinedShape;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.method.ParameterList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.Implementation.Context;
import net.bytebuddy.implementation.bytecode.Duplication;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.StackManipulation.Compound;
import net.bytebuddy.implementation.bytecode.StackSize;
import net.bytebuddy.implementation.bytecode.TypeCreation;
import net.bytebuddy.implementation.bytecode.member.MethodInvocation;
import net.bytebuddy.implementation.bytecode.member.MethodVariableAccess;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.pool.TypePool;
import org.powermock.core.IndicateReloadClass;
import org.powermock.core.bytebuddy.ConditionalStateStackManipulation;
import org.powermock.core.bytebuddy.Frame;
import org.powermock.core.bytebuddy.Frame.LocalVariable;
import org.powermock.core.bytebuddy.MethodMaxLocals;
import org.powermock.core.bytebuddy.MockGetawayCall;
import org.powermock.core.bytebuddy.Variable;
import org.powermock.core.bytebuddy.Variable.VariableAccess;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static net.bytebuddy.matcher.ElementMatchers.hasDescriptor;
import static net.bytebuddy.matcher.ElementMatchers.isConstructor;

public class ConstructorCallMethodVisitorWrapper implements AsmVisitorWrapper.ForDeclaredMethods.MethodVisitorWrapper {
    
    private final MethodDescription defferConstructor;
    private final Class mockGetawayClass;
    private final MethodMaxLocals methodMaxLocals;
    
    public ConstructorCallMethodVisitorWrapper(final MethodDescription defferConstructor,
                                        final Class mockGetawayClass,
                                        final MethodMaxLocals methodMaxLocals) {
        this.defferConstructor = defferConstructor;
        this.mockGetawayClass = mockGetawayClass;
        this.methodMaxLocals = methodMaxLocals;
    }
    
    @Override
    public MethodVisitor wrap(TypeDescription instrumentedType,
                              MethodDescription instrumentedMethod,
                              MethodVisitor methodVisitor,
                              Implementation.Context implementationContext,
                              TypePool typePool,
                              int writerFlags,
                              int readerFlags) {
    
        int maxLocals = methodMaxLocals.getMethodMaxLocal(instrumentedMethod);
        
        return new ConstructorCallMethodVisitor(methodVisitor,
                                                defferConstructor,
                                                implementationContext,
                                                typePool,
                                                mockGetawayClass,
                                                instrumentedType,
                                                instrumentedMethod.getParameters(),
                                                maxLocals
        );
    }
    
    private static class ConstructorCallMethodVisitor extends MethodVisitor {
        
        private final MethodDescription defferConstructor;
        private final Context implementationContext;
        private final TypePool typePool;
        private final TypeDescription instrumentedType;
        private final ParameterList<?> instrumentedMethodParameters;
        private final TypeDescription defferConstructorParam;
        private final Class mockGetawayClass;
        
        private int maxLocalVariables;
        private int stackSizeBuffer;
        
        private ConstructorCallMethodVisitor(final MethodVisitor mv, final MethodDescription defferConstructor,
                                             final Context implementationContext,
                                             final TypePool typePool, final Class mockGetawayClass,
                                             TypeDescription instrumentedType,
                                             ParameterList<?> instrumentedMethodParameters, final int maxLocals) {
            super(Opcodes.ASM5, mv);
            this.defferConstructor = defferConstructor;
            this.implementationContext = implementationContext;
            this.typePool = typePool;
            this.mockGetawayClass = mockGetawayClass;
            this.instrumentedType = instrumentedType;
            this.instrumentedMethodParameters = instrumentedMethodParameters;
            this.stackSizeBuffer = 0;
            this.maxLocalVariables = maxLocals;
            this.defferConstructorParam = typePool.describe(IndicateReloadClass.class.getName()).resolve();
        }
        
        
        @Override
        public void visitMethodInsn(final int opcode, final String owner, final String internalName, final String descriptor,
                                    final boolean isInterface) {
            if (owner.startsWith("java/lang")) {
                callSuper(opcode, owner, internalName, descriptor, isInterface);
                return;
            }
            
            TypePool.Resolution superTypeResolution = typePool.describe(owner.replace('/', '.'));
            
            if (superTypeResolution.isResolved()) {
                TypeDescription superType = superTypeResolution.resolve();
                MethodList<InDefinedShape> methods = superType.getDeclaredMethods().filter(
                    isConstructor().and(hasDescriptor(descriptor))
                );
                
                if (!methods.isEmpty()) {
                    replaceSuperConstructorCall(opcode, owner, internalName, descriptor, isInterface, superType, methods.getOnly());
                    return;
                }
            }
            
            callSuper(opcode, owner, internalName, descriptor, isInterface);
        }
        
        @Override
        public void visitMaxs(int maxStack, int maxLocals) {
            super.visitMaxs(maxStack + stackSizeBuffer, Math.max(maxLocals, maxLocalVariables));
        }
        
        private void replaceSuperConstructorCall(final int opcode, final String owner, final String internalName, final String descriptor,
                                                 final boolean isInterface, final TypeDescription superType,
                                                 final InDefinedShape targetConstructor) {
            
            ParameterList<ParameterDescription.InDefinedShape> targetParameters = targetConstructor.getParameters();
            
            Frame frame = Frame.beforeConstructorCall(instrumentedMethodParameters)
                               .addTopToLocals(maxLocalVariables - instrumentedMethodParameters.size())
                               .addLocalVariable(LocalVariable.UNINITIALIZED_THIS)
                               .addLocalVariables(targetParameters);
            
            maxLocalVariables = frame.maxLocalVariableIndex();
            
            final LinkedList<Variable> targetParamsAsVariables = new LinkedList<Variable>();
            
            final StackManipulation condition = createConstructorConditional(superType, targetParameters, targetParamsAsVariables);
            
            final StackManipulation action = createDeferConstructorCall();
            
            final StackManipulation otherwise = createOriginalConstructorCall(opcode, owner, internalName, descriptor, isInterface, targetParamsAsVariables);
            
            stackSizeBuffer += new Compound(
                storeParamsAsLocalVariables(targetParameters, targetParamsAsVariables, maxLocalVariables),
                new ConditionalStateStackManipulation(condition, action, otherwise, frame)
            ).apply(mv, implementationContext).getMaximalSize() + StackSize.SINGLE.getSize();
        }
        
        private Compound storeParamsAsLocalVariables(final ParameterList<ParameterDescription.InDefinedShape> targetParameters,
                                                     final LinkedList<Variable> targetParamsAsVariables, int offset) {
            List<StackManipulation> storeToLocalVariable = new ArrayList<StackManipulation>();
            
            for (int index = targetParameters.size() - 1; index >= 0; index--) {
                ParameterDescription.InDefinedShape targetParameter = targetParameters.get(index);
                
                offset = offset - targetParameter.getType().getStackSize().getSize();
                Variable variable = Variable.of(targetParameter.getType(), offset);
                
                storeToLocalVariable.add(VariableAccess.store(variable));
                
                targetParamsAsVariables.addFirst(variable);
            }
            
            storeToLocalVariable.add(MethodVariableAccess.of(instrumentedType).storeAt(--offset));
            
            return new Compound(storeToLocalVariable);
        }
        
        private StackManipulation createOriginalConstructorCall(final int opcode, final String owner, final String internalName,
                                                                final String descriptor, final boolean isInterface,
                                                                final LinkedList<Variable> targetParamsAsVariables) {
            final ConstructorCallMethodVisitor constructorCallMethodVisitor = this;
            
            return new StackManipulation() {
                @Override
                public boolean isValid() {
                    return true;
                }
                
                @Override
                public Size apply(final MethodVisitor methodVisitor, final Context implementationContext) {
                    Size size = new Size(0, 0);
                    
                    size = size.aggregate(MethodVariableAccess.loadThis().apply(mv, implementationContext));
                    
                    for (Variable variable : targetParamsAsVariables) {
                        size = size.aggregate(VariableAccess.load(variable).apply(mv, implementationContext));
                    }
                    
                    constructorCallMethodVisitor.callSuper(opcode, owner, internalName, descriptor, isInterface);
                    
                    return size;
                }
            };
        }
        
        private StackManipulation createDeferConstructorCall() {
            return new Compound(
                                   MethodVariableAccess.loadThis(),
                                   TypeCreation.of(defferConstructorParam),
                                   Duplication.SINGLE,
                                   MethodInvocation.invoke(
                                       defferConstructorParam.getDeclaredMethods().filter(ElementMatchers.isConstructor()).getOnly()
                                   ),
                                   MethodInvocation.invoke(defferConstructor)
            );
        }
        
        private StackManipulation createConstructorConditional(final TypeDescription superType,
                                                               final ParameterList<ParameterDescription.InDefinedShape> targetParameters,
                                                               final LinkedList<Variable> targetParamsAsVariables) {
            return new MockGetawayCall(mockGetawayClass)
                       .forType(superType)
                       .withArguments(targetParamsAsVariables)
                       .withParameterTypes(targetParameters);
        }
        
        private void callSuper(final int opcode, final String owner, final String internalName, final String descriptor,
                               final boolean isInterface) {
            super.visitMethodInsn(opcode, owner, internalName, descriptor, isInterface);
        }
    }
    
    
}
