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

package org.powermock.core.classloader.bytebuddy;


import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator.ForClassLoader;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.pool.TypePool;
import org.powermock.core.classloader.MockClassLoader;
import org.powermock.core.classloader.MockClassLoaderConfiguration;
import org.powermock.core.transformers.ClassWrapper;
import org.powermock.core.transformers.bytebuddy.support.ByteBuddyClass;
import org.powermock.core.transformers.bytebuddy.support.ByteBuddyClassWrapperFactory;

import java.security.ProtectionDomain;

import static net.bytebuddy.matcher.ElementMatchers.isDeclaredBy;

public class ByteBuddyMockClassLoader extends MockClassLoader {
    
    private final TypePool typePool;
    
    public ByteBuddyMockClassLoader(final String[] classesToMock, final String[] packagesToDefer) {
        super(classesToMock, packagesToDefer);
        typePool = TypePool.Default.ofClassPath();
    }
    
    public ByteBuddyMockClassLoader(final MockClassLoaderConfiguration configuration) {
        super(configuration, new ByteBuddyClassWrapperFactory());
        typePool = TypePool.Default.ofClassPath();
    }
    
    @Override
    protected Class<?> loadUnmockedClass(final String name,
                                         final ProtectionDomain protectionDomain) throws ClassFormatError, ClassNotFoundException {
        
        final TypeDescription typeDefinitions = getTypeDefinitions(name);
        
        byte[] clazz = createByteBuddyBuilder(typeDefinitions)
                           .make()
                           .getBytes();
        
        return defineClass(name, protectionDomain, clazz);
    }
    
    protected byte[] defineAndTransformClass(final String name) throws ClassNotFoundException {
        TypeDescription typeDefinitions = getTypeDefinitions(name);
    
        Builder<Object> builder = createByteBuddyBuilder(typeDefinitions);
    
        ClassWrapper<ByteBuddyClass> wrap = classWrapperFactory.wrap(new ByteBuddyClass(typeDefinitions, builder));
        
        try {
            wrap = transformClass(wrap);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to transform class with name " + name + ". Reason: " + e.getMessage(), e);
        }
        
        return wrap.unwrap().getBuilder()
                   .method(isDeclaredBy(typeDefinitions))
                   .intercept(FixedValue.nullValue())
                   .make()
                   .getBytes();
    }
    
    private Builder<Object> createByteBuddyBuilder(final TypeDescription typeDefinitions) {
        return new ByteBuddy()
                   .redefine(
                       typeDefinitions,
                       ForClassLoader.ofClassPath()
                   );
    }
    
    private TypeDescription getTypeDefinitions(final String name) throws ClassNotFoundException {
        final TypeDescription typeDefinitions;
        try {
            typeDefinitions = typePool.describe(name).resolve();
        } catch (IllegalStateException e) {
            throw new ClassNotFoundException(e.getMessage(), e);
        }
        return typeDefinitions;
    }
}
