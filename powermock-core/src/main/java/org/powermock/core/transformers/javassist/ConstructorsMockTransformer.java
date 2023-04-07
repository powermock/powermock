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
import javassist.CtConstructor;
import javassist.Modifier;
import javassist.NotFoundException;
import org.powermock.core.transformers.TransformStrategy;

import static org.powermock.core.transformers.TransformStrategy.CLASSLOADER;

/**
 * Convert all constructors to public
 */
public class ConstructorsMockTransformer extends AbstractJavaAssistMockTransformer {
    
    public ConstructorsMockTransformer(final TransformStrategy strategy) {
        super(strategy);
    }
    
    @Override
    public CtClass transform(final CtClass clazz) {
        if (clazz.isInterface()) {
            return clazz;
        }
        
        if (getStrategy() == CLASSLOADER) {
            transform(new CtClass[]{clazz});
            // we also need to transform nested class at this time due to JEP181 since JDK11
            // otherwise, we might have trouble during further transformation
            // see github #958
            try {
                CtClass[] nestedClasses = clazz.getDeclaredClasses();
                transform(nestedClasses);
            } catch (NotFoundException ignored) {
                // ignored
            }
        }
        return clazz;
    }

    private static void transform(final CtClass[] clazzArray) {
        for (CtClass nestedClazz : clazzArray) {
            // nestedClazz could be already loaded hence frozen
            // e.g. in case of a complex structure of classes with several
            // levels of nesting. This nestedClazz might have been used e.g.
            // in a sibling of the outer class (which is also nested)
            // so mocking that sibling class might cause a class load of
            // this nestedClazz as a side-effect.
            // Workaround this by checking the frozen state and defrost.
            if (nestedClazz.isFrozen()) {
              nestedClazz.defrost();
            }
            for (CtConstructor c : nestedClazz.getDeclaredConstructors()) {
                final int modifiers = c.getModifiers();
                if (!Modifier.isPublic(modifiers)) {
                    c.setModifiers(Modifier.setPublic(modifiers));
                }
            }
        }
    }
}
