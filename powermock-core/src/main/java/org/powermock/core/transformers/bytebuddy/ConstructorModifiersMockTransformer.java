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

import net.bytebuddy.asm.ModifierAdjustment;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.pool.TypePool;
import org.powermock.core.transformers.TestClassAwareTransformer;
import org.powermock.core.transformers.TransformStrategy;
import org.powermock.core.transformers.bytebuddy.support.ByteBuddyClass;

import static net.bytebuddy.matcher.ElementMatchers.isConstructor;

public class ConstructorModifiersMockTransformer extends VisitorByteBuddyMockTransformer implements TestClassAwareTransformer {
    
    public ConstructorModifiersMockTransformer(final TransformStrategy strategy) {
        super(strategy);
    }
    
    @Override
    protected boolean classShouldTransformed(final TypeDescription typeDefinitions) {
        return getStrategy() == TransformStrategy.CLASSLOADER && !isTestClass(typeDefinitions) && !isNestedTestClass(typeDefinitions);
    }
    
    @Override
    public ByteBuddyClass transform(final ByteBuddyClass clazz) throws Exception {
        return visit(clazz, new ModifierAdjustment().withConstructorModifiers(Visibility.PUBLIC));
    }
}
