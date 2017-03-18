/*
 *   Copyright 2016 the original author or authors.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package org.powermock.core.classloader.javassist;

import javassist.ClassPool;
import javassist.CtClass;
import org.powermock.core.classloader.ClassMarker;
import org.powermock.core.classloader.PowerMockModified;
import org.powermock.core.transformers.ClassWrapper;

class JavaAssistClassMarkerFactory {
    
    static ClassMarker createClassMarker(ClassPool classPool) {
        return new InterfaceClassMarker(classPool);
    }
    
    /**
     * The implementation of the {@link ClassMarker} which use an interface to mark type.
     *
     * @see PowerMockModified
     */
    private static class InterfaceClassMarker implements ClassMarker {
        
        private final ClassPool classPool;
        
        InterfaceClassMarker(ClassPool classPool) {
            this.classPool = classPool;
        }
        
        /**
         * Mark type as loaded by PowerMock
         *
         * @param type to mark.
         */
        @Override
        public <T> void mark(ClassWrapper<T> type) {
            T unwrapped = type.unwrap();
            if (unwrapped instanceof CtClass) {
                mark((CtClass) unwrapped);
            }
        }
        
        public void mark(CtClass type) {
            CtClass powerMockInterface = classPool.makeInterface("org.powermock.core.classloader.PowerMockModified");
            
            type.addInterface(powerMockInterface);
            
            powerMockInterface.detach();
            
        }
    }
}
