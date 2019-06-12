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

import net.bytebuddy.description.method.MethodDescription.ForLoadedMethod;
import net.bytebuddy.description.method.ParameterDescription.InDefinedShape;
import net.bytebuddy.description.method.ParameterList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation.Context;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.collection.ArrayFactory;
import net.bytebuddy.implementation.bytecode.constant.ClassConstant;
import net.bytebuddy.implementation.bytecode.member.MethodInvocation;
import net.bytebuddy.jar.asm.MethodVisitor;
import org.powermock.core.bytebuddy.Variable.VariableAccess;
import org.powermock.reflect.internal.WhiteboxImpl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public class MockGetawayCall {
    
    private final Method getawayMethod;
    
    public MockGetawayCall(final Class mockGetawayClass) {
        getawayMethod = WhiteboxImpl.getMethod(
            mockGetawayClass, "suppressConstructorCall", Class.class, Object[].class, Class[].class
        );
    }
    
    public ForType forType(final TypeDescription targetType) {
        return new ForType(this, targetType);
    }
    
    public static class ForType {
        private final TypeDescription targetType;
        private final MockGetawayCall mockGetawayCall;
    
        private ForType(final MockGetawayCall mockGetawayCall, final TypeDescription targetType) {
            this.mockGetawayCall = mockGetawayCall;
            this.targetType = targetType;
        }
        
        
        public WithArguments withArguments(final List<Variable> parameters) {
            return new WithArguments(this, parameters);
        }
        
    }
    
    public static class WithArguments {
        private final ForType forType;
        private final List<Variable> arguments;
    
        private WithArguments(final ForType forType, final List<Variable> arguments) {
            this.forType = forType;
            this.arguments = arguments;
        }
        
        public ConstructorMockGetawayCall withParameterTypes(final ParameterList<InDefinedShape> targetParameters) {
            return new ConstructorMockGetawayCall(
                forType.mockGetawayCall.getawayMethod,
                forType.targetType,
                arguments,
                targetParameters
            );
        }
    }
    
    private static class ConstructorMockGetawayCall implements StackManipulation {
        
        private final Method getawayMethod;
        private final TypeDescription targetType;
        private final List<Variable> arguments;
        private final ParameterList<InDefinedShape> targetParameters;
    
        private ConstructorMockGetawayCall(final Method getawayMethod,
                                           final TypeDescription targetType,
                                           final List<Variable> arguments,
                                           final ParameterList<InDefinedShape> targetParameters
        ) {
            this.getawayMethod = getawayMethod;
            this.targetType = targetType;
            this.arguments = arguments;
            this.targetParameters = targetParameters;
        }
        
        private List<StackManipulation> loadSignatureParametersClasses() {
            List<StackManipulation> constructorSignature = new ArrayList<StackManipulation>();
            
            for (InDefinedShape targetParameter : targetParameters) {
                constructorSignature.add(
                    ClassConstant.of(targetParameter.getType().asErasure())
                );
            }
            return constructorSignature;
        }
        
        private List<StackManipulation> loadArgumentsFromVariable() {
            List<StackManipulation> loadTargetParameters = new ArrayList<StackManipulation>();
            for (Variable argument : arguments) {
                loadTargetParameters.add(
                    VariableAccess.load(argument, true)
                );
            }
            return loadTargetParameters;
        }
        
        @Override
        public boolean isValid() {
            return true;
        }
        
        
        @Override
        public Size apply(final MethodVisitor mv, final Context implementationContext) {
            List<StackManipulation> loadTargetParameters = loadArgumentsFromVariable();
            List<StackManipulation> constructorSignature = loadSignatureParametersClasses();
            
            return new Compound(
                                   ClassConstant.of(targetType),
                                   ArrayFactory.forType(TypeDescription.OBJECT.asGenericType()).withValues(loadTargetParameters),
                                   ArrayFactory.forType(TypeDescription.CLASS.asGenericType()).withValues(constructorSignature),
                                   MethodInvocation.invoke(new ForLoadedMethod(getawayMethod))
            ).apply(mv, implementationContext);
        }
    }
}
