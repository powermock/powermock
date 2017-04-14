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
import org.powermock.core.transformers.ClassWrapper;
import org.powermock.core.transformers.MockTransformer;
import org.powermock.core.transformers.TransformStrategy;

public abstract class AbstractJavaAssistMockTransformer implements MockTransformer {
    
    private final TransformStrategy strategy;
    
    public AbstractJavaAssistMockTransformer(TransformStrategy strategy) {
        this.strategy = strategy;
    }
    
    @Override
    public <T> ClassWrapper<T> transform(final ClassWrapper<T> clazz) throws Exception {
        T classImpl = clazz.unwrap();
        
        if (classImpl instanceof CtClass) {
            transform((CtClass) classImpl);
        }
        
        return clazz;
    }
    
    public abstract CtClass transform(CtClass clazz) throws Exception;
    
    protected TransformStrategy getStrategy() {
        return strategy;
    }
}
