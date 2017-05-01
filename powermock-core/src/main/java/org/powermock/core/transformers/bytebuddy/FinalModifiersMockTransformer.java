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

package org.powermock.core.transformers.bytebuddy;

import net.bytebuddy.asm.AsmVisitorWrapper.AbstractBase;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.field.FieldDescription.InDefinedShape;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.modifier.TypeManifestation;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.jar.asm.ClassVisitor;
import net.bytebuddy.jar.asm.FieldVisitor;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.pool.TypePool;
import org.powermock.core.transformers.TransformStrategy;
import org.powermock.core.transformers.bytebuddy.support.ByteBuddyClass;

import java.util.HashMap;
import java.util.Map;

import static org.powermock.core.transformers.TransformStrategy.INST_REDEFINE;

public class FinalModifiersMockTransformer extends VisitorByteBuddyMockTransformer {
    
    public FinalModifiersMockTransformer(final TransformStrategy strategy) {
        super(strategy);
    }
    
    @Override
    public ByteBuddyClass transform(final ByteBuddyClass clazz) throws Exception {
        return visit(clazz, new RemoveFinalModifier());
    }
    
    protected boolean classShouldTransformed(final TypeDescription typeDefinitions) {
        return getStrategy() != INST_REDEFINE && !typeDefinitions.isInterface();
    }
    
    private static class RemoveFinalModifier extends AbstractBase {
        
        @Override
        public ClassVisitor wrap(TypeDescription instrumentedType,
                                 ClassVisitor classVisitor,
                                 Implementation.Context implementationContext,
                                 TypePool typePool,
                                 FieldList<InDefinedShape> fields,
                                 MethodList<?> methods,
                                 int writerFlags,
                                 int readerFlags
        ) {
            Map<String, InDefinedShape> mappedFields = new HashMap<String, InDefinedShape>();
            for (FieldDescription.InDefinedShape fieldDescription : fields) {
                mappedFields.put(fieldDescription.getInternalName() + fieldDescription.getDescriptor(), fieldDescription);
            }
            return new RemoveFinalModifierClassVisitor(
                                                          classVisitor,
                                                          instrumentedType,
                                                          mappedFields
            );
        }
        
    }
    
    private static class RemoveFinalModifierClassVisitor extends ClassVisitor {
        private final TypeDescription instrumentedType;
        private final Map<String, InDefinedShape> fields;
    
        private RemoveFinalModifierClassVisitor(final ClassVisitor classVisitor,
                                               final TypeDescription instrumentedType,
                                               final Map<String, InDefinedShape> mappedFields) {
            super(Opcodes.ASM5, classVisitor);
            this.instrumentedType = instrumentedType;
            this.fields = mappedFields;
        }
        
        @Override
        public void visit(int version, int modifiers, String internalName, String signature, String superClassName,
                          String[] interfaceName) {
            modifiers = removeFinalModifier(modifiers);
            super.visit(version, modifiers, internalName, signature, superClassName, interfaceName);
        }
        
        @Override
        public void visitInnerClass(String internalName, String outerName, String innerName, int modifiers) {
            if (instrumentedType.getInternalName().equals(internalName)) {
                modifiers = removeFinalModifier(modifiers);
            }
            super.visitInnerClass(internalName, outerName, innerName, modifiers);
        }
        
        @Override
        public FieldVisitor visitField(int modifiers, String internalName, String descriptor, String signature, Object value) {
            FieldDescription.InDefinedShape fieldDescription = fields.get(internalName + descriptor);
            if (fieldDescription != null) {
                modifiers = removeFinalModifier(modifiers);
            }
            return super.visitField(modifiers, internalName, descriptor, signature, value);
        }
        
        private int removeFinalModifier(int modifiers) {
            modifiers = modifiers ^ TypeManifestation.FINAL.getMask();
            return modifiers;
        }
    }
}
