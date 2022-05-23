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

package org.powermock.core.transformers;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.Test;
import org.junit.runners.Parameterized;
import org.powermock.core.test.MockClassLoaderFactory;
import powermock.test.support.MainMockTransformerTestSupport.SomeInterface;
import powermock.test.support.MainMockTransformerTestSupport.SupportClasses;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;

import static java.lang.reflect.Modifier.isFinal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assume.assumeThat;

public class ClassFinalModifierMockTransformerTest extends AbstractBaseMockTransformerTest {
    
    @Parameterized.Parameters(name = "strategy: {0}, transformerType: {2}")
    public static Iterable<Object[]> data() {
        Collection<Object[]> data = new ArrayList<Object[]>();
        
        data.addAll(MockTransformerTestHelper.createTransformerTestData(org.powermock.core.transformers.javassist.ClassFinalModifierMockTransformer.class));
        
        return data;
    }
    
    public ClassFinalModifierMockTransformerTest(
                                                    final TransformStrategy strategy,
                                                    final MockTransformerChain mockTransformerChain,
                                                    final MockClassLoaderFactory mockClassloaderFactory
    ) {
        super(strategy, mockTransformerChain, mockClassloaderFactory);
    }
    
    @Test
    public void should_remove_final_modifier_from_static_final_inner_classes_strategy_not_equals_to_inst_redefine() throws Exception {
    
        assumeThat(strategy, not(equalTo(TransformStrategy.INST_REDEFINE)));
        
        Class<?> clazz = loadWithMockClassLoader(SupportClasses.StaticFinalInnerClass.class.getName());
    
        assertFalse(Modifier.isFinal(clazz.getModifiers()));
    }
    
    @Test
    public void should_nit_remove_final_modifier_from_static_final_inner_classes_equals_to_inst_redefine() throws Exception {
        
        assumeThat(strategy, equalTo(TransformStrategy.INST_REDEFINE));
        
        Class<?> clazz = loadWithMockClassLoader(SupportClasses.StaticFinalInnerClass.class.getName());
        
        assertThat(isFinal(clazz.getModifiers())).isTrue();
    }
    
    @Test
    public void should_remove_final_modifier_from_final_inner_classes() throws Exception {
    
        assumeThat(strategy, not(equalTo(TransformStrategy.INST_REDEFINE)));
        
        Class<?> clazz = loadWithMockClassLoader(SupportClasses.FinalInnerClass.class.getName());
        assertFalse(Modifier.isFinal(clazz.getModifiers()));
    }
    
    @Test
    public void should_remove_final_modifier_from_enums() throws Exception {
    
        assumeThat(strategy, not(equalTo(TransformStrategy.INST_REDEFINE)));
        
        Class<?> clazz = loadWithMockClassLoader(SupportClasses.EnumClass.class.getName());
        assertFalse(Modifier.isFinal(clazz.getModifiers()));
    }
    
    @Test
    public void should_remove_final_modifier_from_private_static_final_inner_classes() throws Exception {
    
        assumeThat(strategy, not(equalTo(TransformStrategy.INST_REDEFINE)));
        
        Class<?> clazz = loadWithMockClassLoader(SupportClasses.class.getName() + "$PrivateStaticFinalInnerClass");
        assertFalse(Modifier.isFinal(clazz.getModifiers()));
    }
    
    @Test
    public void should_ignore_interfaces() throws Exception {
    
        Throwable throwable = catchThrowable(new ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                loadWithMockClassLoader(SomeInterface.class.getName());
            }
        });
        
        assertThat(throwable).isNull();
    }
    
}