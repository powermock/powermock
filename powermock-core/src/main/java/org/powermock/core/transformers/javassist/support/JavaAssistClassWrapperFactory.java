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

package org.powermock.core.transformers.javassist.support;

import javassist.CtClass;
import org.powermock.core.transformers.ClassWrapper;
import org.powermock.core.transformers.ClassWrapperFactory;

public class JavaAssistClassWrapperFactory implements ClassWrapperFactory<CtClass> {
    @Override
    public ClassWrapper<CtClass> wrap(CtClass ctClass) {
        return new JavaAssistClassWrapper(ctClass);
    }
    
    public static class JavaAssistClassWrapper implements ClassWrapper<CtClass> {
        
        private final CtClass ctClass;
        
        private JavaAssistClassWrapper(CtClass ctClass) {
            this.ctClass = ctClass;
        }
        
        @Override
        public boolean isInterface() {
            return ctClass.isInterface();
        }
        
        @Override
        public CtClass unwrap() {
            return ctClass;
        }

        @Override
        public ClassWrapper<CtClass> wrap(final CtClass original) {
            return new JavaAssistClassWrapper(original);
        }
    }
}
