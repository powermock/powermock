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

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType.Builder;

public class ByteBuddyClass {
    
    public static ByteBuddyClass from(final TypeDescription typeDefinitions, final Builder builder) {
        return new ByteBuddyClass(typeDefinitions, builder);
    }
    
    private final Builder builder;
    private final TypeDescription typeDescription;
    
    private ByteBuddyClass(final TypeDescription typeDescription, final Builder builder) {
        this.builder = builder;
        this.typeDescription = typeDescription;
    }
    
    public boolean isInterface() {
        return typeDescription.isInterface();
    }
    
    public Builder getBuilder() {
        return builder;
    }
    
    public TypeDescription getTypeDescription() {
        return typeDescription;
    }
    
    @Override
    public String toString() {
        return typeDescription.getName();
    }
}
