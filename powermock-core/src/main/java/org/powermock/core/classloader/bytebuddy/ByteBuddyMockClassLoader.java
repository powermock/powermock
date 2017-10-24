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
import net.bytebuddy.description.type.TypeDescription.Generic;
import net.bytebuddy.dynamic.ClassFileLocator.ForClassLoader;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.pool.TypePool;
import org.powermock.core.classloader.MockClassLoader;
import org.powermock.core.classloader.MockClassLoaderConfiguration;
import org.powermock.core.transformers.ClassWrapper;
import org.powermock.core.transformers.bytebuddy.support.ByteBuddyClass;
import org.powermock.core.transformers.bytebuddy.support.ByteBuddyClassWrapperFactory;

import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ByteBuddyMockClassLoader extends MockClassLoader {
    
    private final TypePool typePool;
    private final ConcurrentMap<String, Class<?>> definedByClassLoadingStrtegy;
    
    public ByteBuddyMockClassLoader(final String[] classesToMock, final String[] packagesToDefer) {
        this(new MockClassLoaderConfiguration(classesToMock, packagesToDefer));
    }
    
    public ByteBuddyMockClassLoader(final MockClassLoaderConfiguration configuration) {
        super(configuration, new ByteBuddyClassWrapperFactory());
        typePool = TypePool.Default.ofClassPath();
        definedByClassLoadingStrtegy = new ConcurrentHashMap<String, Class<?>>();
    }
    
    @Override
    protected Class<?> loadUnmockedClass(final String name,
                                         final ProtectionDomain protectionDomain) throws ClassFormatError, ClassNotFoundException {
        
        final TypeDescription typeDefinitions = getTypeDefinitions(name);
    
        byte[] clazz = createByteBuddyBuilder(typeDefinitions)
                           .make()
                           .load(this, new MockClassLoadingStrategy(protectionDomain))
                           .getBytes();
        
        return defineClass(name, protectionDomain, clazz);
    }
    
    @Override
    public Class<?> defineClass(final String className, final ProtectionDomain protectionDomain, final byte[] clazz) {
        Class<?> defined = definedByClassLoadingStrtegy.get(className);
        if (defined == null){
            defined = super.defineClass(className, protectionDomain, clazz);
            definedByClassLoadingStrtegy.put(className, defined);
        }
        return defined;
    }
    
    protected byte[] defineAndTransformClass(final String name, ProtectionDomain protectionDomain) throws ClassNotFoundException {
        TypeDescription typeDefinitions = getTypeDefinitions(name);
    
        loadParentFirst(typeDefinitions);
        
        Builder<Object> builder = createByteBuddyBuilder(typeDefinitions);
        
        ClassWrapper<ByteBuddyClass> wrap = classWrapperFactory.wrap(new ByteBuddyClass(typeDefinitions, builder));
        
        try {
            wrap = transformClass(wrap);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to transform class with name " + name + ". Reason: " + e.getMessage(), e);
        }
        
        return wrap.unwrap().getBuilder()
                   .make()
                   .load(this, new MockClassLoadingStrategy(protectionDomain))
                   .getBytes();
    }
    
    private void loadParentFirst(final TypeDescription typeDefinitions) throws ClassNotFoundException {
        Generic superClass = typeDefinitions.getSuperClass();
        
        if (parentShouldBeLoaded(typeDefinitions, superClass)) {
            loadClass(superClass.getTypeName());
        }
    }
    
    private boolean parentShouldBeLoaded(final TypeDescription typeDefinitions, final Generic superClass) {
        return !typeDefinitions.isInterface() && !typeDefinitions.isEnum() &&  !"java.lang.Object".equals(superClass.getTypeName());
    }
    
    private Builder<Object> createByteBuddyBuilder(final TypeDescription typeDefinitions) {
        return new ByteBuddy()
                   .rebase(
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
    
    private class MockClassLoadingStrategy implements ClassLoadingStrategy<ByteBuddyMockClassLoader> {
        private final ProtectionDomain protectionDomain;
    
        private MockClassLoadingStrategy(final ProtectionDomain protectionDomain) {
            this.protectionDomain = protectionDomain;
        }
        
        @Override
        public Map<TypeDescription, Class<?>> load(final ByteBuddyMockClassLoader classLoader,
                                                   final Map<TypeDescription, byte[]> types) {
            final Map<TypeDescription, Class<?>> result = new HashMap<TypeDescription, Class<?>>();
            
            for (Entry<TypeDescription, byte[]> entry : types.entrySet()) {
                final TypeDescription typeDescription = entry.getKey();
                final Class<?> loaded = defineClass(typeDescription.getName(), protectionDomain, entry.getValue());
                result.put(typeDescription, loaded);
            }

            return result;
        }
    }
}
