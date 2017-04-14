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

package org.powermock.core.transformers.javassist;

import javassist.CtClass;
import javassist.Modifier;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.ClassFile;
import javassist.bytecode.InnerClassesAttribute;
import org.powermock.core.transformers.TransformStrategy;

import static org.powermock.core.transformers.TransformStrategy.INST_REDEFINE;

public class ClassFinalModifierMockTransformer extends AbstractJavaAssistMockTransformer {
    
    public ClassFinalModifierMockTransformer(final TransformStrategy strategy) {
        super(strategy);
    }
    
    @Override
    public CtClass transform(final CtClass clazz) {
        if (clazz.isInterface()) {
            return clazz;
        }
        
        if (getStrategy() != INST_REDEFINE) {
            if (Modifier.isFinal(clazz.getModifiers())) {
                clazz.setModifiers(clazz.getModifiers() ^ Modifier.FINAL);
            }
    
            ClassFile classFile = clazz.getClassFile2();
            AttributeInfo attribute = classFile.getAttribute(InnerClassesAttribute.tag);
            if (attribute != null && attribute instanceof InnerClassesAttribute) {
                InnerClassesAttribute ica = (InnerClassesAttribute) attribute;
                String name = classFile.getName();
                int n = ica.tableLength();
                for (int i = 0; i < n; ++i) {
                    if (name.equals(ica.innerClass(i))) {
                        int accessFlags = ica.accessFlags(i);
                        if (Modifier.isFinal(accessFlags)) {
                            ica.setAccessFlags(i, accessFlags ^ Modifier.FINAL);
                        }
                    }
                }
            }
        }
        
        return clazz;
    }
}
