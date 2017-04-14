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
import javassist.CtField;
import javassist.Modifier;
import org.powermock.core.transformers.TransformStrategy;

import static org.powermock.core.transformers.TransformStrategy.INST_REDEFINE;


/**
 * Remove final from all static final fields. Not possible if using a java agent.
 */
public class StaticFinalFieldsMockTransformer extends AbstractJavaAssistMockTransformer {
    
    public StaticFinalFieldsMockTransformer(final TransformStrategy strategy) {
        super(strategy);
    }
    
    public CtClass transform(final CtClass clazz) {
        if (clazz.isInterface()) {
            return clazz;
        }
        
        if (getStrategy() != INST_REDEFINE) {
            for (CtField f : clazz.getDeclaredFields()) {
                final int modifiers = f.getModifiers();
                if (Modifier.isFinal(modifiers) && Modifier.isStatic(modifiers)) {
                    f.setModifiers(modifiers ^ Modifier.FINAL);
                }
            }
        }
        return clazz;
    }
    
}
