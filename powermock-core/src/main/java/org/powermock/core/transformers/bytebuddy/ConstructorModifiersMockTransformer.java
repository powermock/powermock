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

import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.asm.AsmVisitorWrapper.AbstractBase;
import net.bytebuddy.description.field.FieldDescription.InDefinedShape;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.jar.asm.ClassVisitor;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.pool.TypePool;
import org.powermock.core.transformers.TransformStrategy;
import org.powermock.core.transformers.bytebuddy.support.ByteBuddyClass;

public class ConstructorModifiersMockTransformer extends VisitorByteBuddyMockTransformer {
    
    public ConstructorModifiersMockTransformer(final TransformStrategy strategy) {
        super(strategy);
    }
    
    @Override
    protected boolean classShouldBeIgnored(final TypeDescription typeDefinitions) {
        return getStrategy() != TransformStrategy.CLASSLOADER;
    }
    
    @Override
    public ByteBuddyClass transform(final ByteBuddyClass clazz) throws Exception {
        return visit(clazz, new ConstructorModifiers(Visibility.PUBLIC));
    }
    
    private static class ConstructorModifiers extends AbstractBase {
        
        private final Visibility visibility;
    
        private ConstructorModifiers(final Visibility visibility) {this.visibility = visibility;}
        
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
            return new ConstructorModifiersClassVisitor(classVisitor, visibility);
        }
        
    }
    
    private static class ConstructorModifiersClassVisitor extends ClassVisitor {
        
        private final Visibility visibility;
        
        private ConstructorModifiersClassVisitor(final ClassVisitor classVisitor, final Visibility visibility) {
            super(Opcodes.ASM5, classVisitor);
            this.visibility = visibility;
        }
        
        @Override
        public MethodVisitor visitMethod(int modifiers, String internalName, String descriptor, String signature, String[] exception) {
            if (MethodDescription.CONSTRUCTOR_INTERNAL_NAME.equals(internalName)) {
                modifiers = (modifiers & ~visibility.getRange()) | visibility.getMask();
            }
            return super.visitMethod(modifiers, internalName, descriptor, signature, exception);
        }
    }
}
