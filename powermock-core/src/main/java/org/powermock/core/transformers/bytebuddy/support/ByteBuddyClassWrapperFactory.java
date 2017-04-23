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

package org.powermock.core.transformers.bytebuddy.support;

import org.powermock.core.transformers.ClassWrapper;
import org.powermock.core.transformers.ClassWrapperFactory;

public class ByteBuddyClassWrapperFactory implements ClassWrapperFactory<ByteBuddyClass> {
    
    @Override
    public ClassWrapper<ByteBuddyClass> wrap(final ByteBuddyClass original) {
        return new ByteBuddyWrapper(original);
    }
    
    public static class ByteBuddyWrapper implements ClassWrapper<ByteBuddyClass> {
        
        private ByteBuddyClass byteBuddyClass;
        
        private ByteBuddyWrapper(final ByteBuddyClass byteBuddyClass) {
            this.byteBuddyClass = byteBuddyClass;
        }
        
        @Override
        public boolean isInterface() {
            return byteBuddyClass.isInterface();
        }
        
        @Override
        public ByteBuddyClass unwrap() {
            return byteBuddyClass;
        }
        
        @Override
        public ClassWrapper<ByteBuddyClass> wrap(final ByteBuddyClass original) {
            return new ByteBuddyWrapper(original);
        }
    }
}
