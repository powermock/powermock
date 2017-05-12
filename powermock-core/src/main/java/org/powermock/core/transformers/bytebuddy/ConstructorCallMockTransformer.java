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

import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodDescription.Latent;
import net.bytebuddy.description.method.MethodDescription.Token;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeDescription.Generic;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.jar.asm.ClassReader;
import net.bytebuddy.pool.TypePool;
import org.powermock.core.IndicateReloadClass;
import org.powermock.core.MockGateway;
import org.powermock.core.transformers.TransformStrategy;
import org.powermock.core.transformers.bytebuddy.constructor.ConstructorCallMethodVisitorWrapper;
import org.powermock.core.transformers.bytebuddy.support.ByteBuddyClass;
import org.powermock.core.bytebuddy.MaxLocalsExtractor;
import org.powermock.core.bytebuddy.MethodMaxLocals;

import java.io.IOException;
import java.util.Collections;

import static net.bytebuddy.jar.asm.ClassReader.SKIP_DEBUG;
import static net.bytebuddy.matcher.ElementMatchers.isConstructor;

public class ConstructorCallMockTransformer extends AbstractByteBuddyMockTransformer {
    
    private final TypePool typePool;
    private Class<?> mockGetawayClass;
    
    public ConstructorCallMockTransformer(final TransformStrategy strategy) {
        super(strategy);
        this.typePool = TypePool.Default.ofClassPath();
        this.mockGetawayClass = MockGateway.class;
    }
    
    @Override
    protected boolean classShouldTransformed(final TypeDescription td) {
        return getStrategy().isClassloaderMode() && !td.isInterface() && !td.isEnum();
    }
    
    @Override
    public ByteBuddyClass transform(final ByteBuddyClass clazz) throws Exception {
        final TypeDescription td = clazz.getTypeDefinitions();
    
        final MethodMaxLocals methods = extractConstructorMaxLocals(td);
    
        final Generic superClass = td.getSuperClass();
    
        final DeferConstructor deferConstructor = new DeferConstructor(clazz, superClass).create();
    
        return new ByteBuddyClass(clazz.getTypeDefinitions(),
                                  deferConstructor.getBuilder().visit(
                                      new AsmVisitorWrapper.ForDeclaredMethods().method(
                                          isConstructor(),
                                          new ConstructorCallMethodVisitorWrapper(
                                              deferConstructor.getSupperClassDefferConstructor(),
                                              mockGetawayClass,
                                              methods
                                          )
                                      )
                                  ));
        
        
    }
    
    private MethodMaxLocals extractConstructorMaxLocals(final TypeDescription td) throws IOException {
        final ClassFileLocator classFileLocator = ClassFileLocator.ForClassLoader.of(this.getClass().getClassLoader());
        final ClassReader classReader = new ClassReader(classFileLocator.locate(td.getName()).resolve());
        
        final MaxLocalsExtractor maxLocalsExtractor = new MaxLocalsExtractor();
        classReader.accept(maxLocalsExtractor, SKIP_DEBUG);
        return maxLocalsExtractor.getMethods();
    }
    
    private Token deferConstructorToken() {
        Generic parameter = typePool.describe(IndicateReloadClass.class.getName())
                                    .resolve()
                                    .asGenericType();
        
        return new Token(
                            MethodDescription.CONSTRUCTOR_INTERNAL_NAME,
                            Visibility.PUBLIC.getMask(),
                            Generic.VOID,
                            Collections.singletonList(parameter)
        );
    }
    
    private static class NewConstructorWithSuperDeferConstructorCall {
        
        private final Builder builder;
        private final MethodDescription deferConstructor;
        
        private NewConstructorWithSuperDeferConstructorCall(final ByteBuddyClass clazz,
                                                            final MethodDescription deferConstructor) {
            this.builder = clazz.getBuilder();
            this.deferConstructor = deferConstructor;
        }
        
        public Builder apply() {
            return builder
                       .defineConstructor(Visibility.PUBLIC)
                       .withParameters(IndicateReloadClass.class)
                       .intercept(
                           MethodCall.invoke(deferConstructor)
                                     .withAllArguments()
                       );
        }
        
    }
    
    private static class NewConstructorWithDefaultConstructorCall {
        private final Builder builder;
        private final MethodDescription deferConstructor;
        
        private NewConstructorWithDefaultConstructorCall(final ByteBuddyClass clazz,
                                                         final MethodDescription deferConstructor) {
            this.builder = clazz.getBuilder();
            this.deferConstructor = deferConstructor;
        }
        
        public Builder apply() {
            return builder
                       .defineConstructor(Visibility.PUBLIC)
                       .withParameters(IndicateReloadClass.class)
                       .intercept(MethodCall.invoke(deferConstructor));
        }
    }
    
    
    private class DeferConstructor {
        private final ByteBuddyClass clazz;
        private final Generic superClass;
        private MethodDescription supperClassDefferConstructor;
        private Builder builder;
        
        private DeferConstructor(final ByteBuddyClass clazz, final Generic superClass) {
            this.clazz = clazz;
            this.superClass = superClass;
        }
    
        private MethodDescription getSupperClassDefferConstructor() {
            return supperClassDefferConstructor;
        }
        
        public Builder getBuilder() {
            return builder;
        }
        
        public DeferConstructor create() {
            
            if ("java.lang.Object".equals(superClass.getTypeName())) {
                supperClassDefferConstructor = new MethodDescription.ForLoadedConstructor(Object.class.getDeclaredConstructors()[0]);
                builder = new NewConstructorWithDefaultConstructorCall(clazz, supperClassDefferConstructor).apply();
            } else {
                supperClassDefferConstructor = deferConstructor(superClass);
                builder = new NewConstructorWithSuperDeferConstructorCall(clazz, supperClassDefferConstructor).apply();
            }
            return this;
        }
        
        private MethodDescription deferConstructor(final Generic superClass) {
            return new Latent(superClass.asErasure(), deferConstructorToken());
        }
    }
}
