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

import net.bytebuddy.description.type.TypeDescription;
import org.powermock.core.transformers.ClassWrapper;
import org.powermock.core.transformers.MockTransformer;
import org.powermock.core.transformers.TransformStrategy;
import org.powermock.core.transformers.bytebuddy.support.ByteBuddyClass;

public abstract class AbstractByteBuddyMockTransformer implements MockTransformer<ByteBuddyClass> {
    
    private final TransformStrategy strategy;
    
    public AbstractByteBuddyMockTransformer(TransformStrategy strategy) {
        this.strategy = strategy;
    }
    
    @Override
    public ClassWrapper<ByteBuddyClass> transform(final ClassWrapper<ByteBuddyClass> clazz) throws Exception {
        ByteBuddyClass byteBuddyClass = clazz.unwrap();
    
        if (byteBuddyClass != null) {
            TypeDescription typeDefinitions = byteBuddyClass.getTypeDescription();
    
            if (!classShouldTransformed(typeDefinitions)) {
                return clazz;
            }
            
            byteBuddyClass = transform(byteBuddyClass);
        }
        
        return clazz.wrap(byteBuddyClass);
    }
    
    protected abstract boolean classShouldTransformed(final TypeDescription typeDefinitions);
    
    public abstract ByteBuddyClass transform(ByteBuddyClass clazz) throws Exception;
    
    protected TransformStrategy getStrategy() {
        return strategy;
    }
}
