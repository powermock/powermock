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
import javassist.NotFoundException;
import org.powermock.core.transformers.TransformStrategy;

import static org.powermock.core.transformers.TransformStrategy.INST_REDEFINE;

/**
 * Set class modifier to public to allow for mocking of package private
 * classes. This is needed because we've changed to CgLib naming policy
 * to allow for mocking of signed classes.
 */
public class PackagePrivateClassesMockTransformer extends AbstractJavaAssistMockTransformer {
    
    public PackagePrivateClassesMockTransformer(final TransformStrategy strategy) {
        super(strategy);
    }
    
    @Override
    public CtClass transform(final CtClass clazz) {
        final String name = clazz.getName();
        if (getStrategy() != INST_REDEFINE) {
            transform(clazz, name);
        }
        return clazz;
    }
    
    private static void transform(final CtClass clazz, final String name) {
        try {
            final int modifiers = clazz.getModifiers();
            if (Modifier.isPackage(modifiers)) {
                if (isNotSystemClass(name) && !(clazz.isInterface() && clazz.getDeclaringClass() != null)) {
                    clazz.setModifiers(Modifier.setPublic(modifiers));
                }
            }
        } catch (NotFoundException e) {
            // OK, continue
        }
    }
    
    private static boolean isNotSystemClass(final String name) {
        return !name.startsWith("java.");
    }
}
