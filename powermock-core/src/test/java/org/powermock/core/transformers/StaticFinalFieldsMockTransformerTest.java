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

import org.junit.Test;
import org.junit.runners.Parameterized;
import org.powermock.core.test.MockClassLoaderFactory;
import org.powermock.core.transformers.javassist.StaticFinalFieldsMockTransformer;
import org.powermock.reflect.internal.WhiteboxImpl;
import powermock.test.support.MainMockTransformerTestSupport.SupportClasses;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

import static java.lang.reflect.Modifier.isFinal;
import static java.lang.reflect.Modifier.isTransient;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assume.assumeThat;

public class StaticFinalFieldsMockTransformerTest extends AbstractBaseMockTransformerTest {
    
    @Parameterized.Parameters(name = "strategy: {0}, transformerType: {2}")
    public static Iterable<Object[]> data() {
        Collection<Object[]> data = new ArrayList<Object[]>();
        
        data.addAll(MockTransformerTestHelper.createTransformerTestData(StaticFinalFieldsMockTransformer.class));
        
        return data;
    }
    
    public StaticFinalFieldsMockTransformerTest(final TransformStrategy strategy,
                                                final MockTransformerChain mockTransformerChain,
                                                final MockClassLoaderFactory mockClassloaderFactory) {
        super(strategy, mockTransformerChain, mockClassloaderFactory);
    }
    
    @Test
    public void should_remove_final_modifier_from_static_final_field_if_strategy_not_redefine() throws Exception {
        
        assumeThat(strategy, not(equalTo(TransformStrategy.INST_REDEFINE)));
        
        Class<?> clazz = loadWithMockClassLoader(SupportClasses.class.getName());
        
        assertThatFieldIsNotFinal(clazz, "finalStaticField");
        assertThatFieldIsFinal(clazz, "finalField");
    }
    
    @Test
    public void should_not_remove_final_modifier_from_static_final_field_if_strategy_redefine() throws Exception {
        
        assumeThat(strategy, equalTo(TransformStrategy.INST_REDEFINE));
        
        Class<?> clazz = loadWithMockClassLoader(SupportClasses.class.getName());
        
        assertThatFieldIsFinal(clazz, "finalStaticField");
        assertThatFieldIsFinal(clazz, "finalField");
    }
    
    @Test
    public void should_remove_only_final_modifier_but_keep_transient() throws Exception {
        
        assumeThat(strategy, not(equalTo(TransformStrategy.INST_REDEFINE)));
        
        Class<?> clazz = loadWithMockClassLoader(SupportClasses.class.getName());
        
        Field finalTransientStaticField = WhiteboxImpl.getField(clazz, "finalStaticTransientField");
        
        assertThat(isFinal(finalTransientStaticField.getModifiers())).isFalse();
        assertThat(isTransient(finalTransientStaticField.getModifiers())).isTrue();
        
    }
    
    private void assertThatFieldIsFinal(final Class<?> clazz, final String fieldName) {
        Field field = WhiteboxImpl.getField(clazz, fieldName);
        
        assertThat(field)
            .as("Final field is exist.")
            .isNotNull();
        
        assertThat(isFinal(field.getModifiers()))
            .as("Field is final.")
            .isTrue();
    }
    
    private void assertThatFieldIsNotFinal(final Class<?> clazz, final String fieldName) {
        Field field = WhiteboxImpl.getField(clazz, fieldName);
        
        assertThat(field)
            .as("Final field is exist.")
            .isNotNull();
        
        assertThat(isFinal(field.getModifiers()))
            .as("Field is not final.")
            .isFalse();
    }
}