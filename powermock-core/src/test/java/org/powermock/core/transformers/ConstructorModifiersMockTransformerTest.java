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
import org.powermock.core.transformers.bytebuddy.ConstructorModifiersMockTransformer;
import org.powermock.core.transformers.javassist.ConstructorsMockTransformer;
import powermock.test.support.MainMockTransformerTestSupport.ParentTestClass;
import powermock.test.support.MainMockTransformerTestSupport.ParentTestClass.NestedTestClass;
import powermock.test.support.MainMockTransformerTestSupport.SupportClasses;
import powermock.test.support.MainMockTransformerTestSupport.SupportClasses.MultipleConstructors;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assume.assumeThat;

public class ConstructorModifiersMockTransformerTest extends AbstractBaseMockTransformerTest {
    
    @Parameterized.Parameters(name = "strategy: {0}, transformerType: {2}")
    public static Iterable<Object[]> data() {
        Collection<Object[]> data = new ArrayList<Object[]>();
        
        data.addAll(MockTransformerTestHelper.createTransformerTestData(ConstructorsMockTransformer.class));
        data.addAll(MockTransformerTestHelper.createTransformerTestData(ConstructorModifiersMockTransformer.class));
        
        return data;
    }
    
    public ConstructorModifiersMockTransformerTest(final TransformStrategy strategy,
                                                   final MockTransformerChain mockTransformerChain,
                                                   final MockClassLoaderFactory mockClassloaderFactory
    ) {
        super(strategy, mockTransformerChain, mockClassloaderFactory);
    }
    
    @Test
    public void should_make_all_constructor_public_if_strategy_is_classloader() throws Exception {
        
        assumeThat(strategy, equalTo(TransformStrategy.CLASSLOADER));
        
        Class<?> clazz = loadWithMockClassLoader(SupportClasses.MultipleConstructors.class.getName());
        
        assertThat(clazz.getConstructors())
            .as("All constructor must be public")
            .hasSize(5)
            .extracting("modifiers")
            .contains(Modifier.PUBLIC);
    }
    
    @Test
    public void should_leave_constructor_unchanged_if_strategy_is_not_classloader() throws Exception {
        
        assumeThat(strategy, not(equalTo(TransformStrategy.CLASSLOADER)));
        
        Class<?> clazz = SupportClasses.MultipleConstructors.class;
        Class<?> modifiedClass = loadWithMockClassLoader(SupportClasses.MultipleConstructors.class.getName());
    
        assertThatAllConstructorsHaveSameModifier(clazz, modifiedClass);
    }
    
    @Test
    public void should_not_change_constructors_of_test_class() throws Exception {
        assumeClassLoaderMode();
        assumeClassLoaderIsByteBuddy();
        
        final Class<MultipleConstructors> testClass = MultipleConstructors.class;
        
        setTestClassToTransformers(testClass);
        
        Class<?> modifiedClass = loadWithMockClassLoader(testClass.getName());
        
        assertThatAllConstructorsHaveSameModifier(testClass, modifiedClass);
    }
    
    @Test
    public void should_not_change_constructors_of_nested_test_classes() throws Exception {
        assumeClassLoaderMode();
        assumeClassLoaderIsByteBuddy();
        
        setTestClassToTransformers(ParentTestClass.class);
        
        final Class<?> originalClazz = NestedTestClass.class;
        
        Class<?> modifiedClass = loadWithMockClassLoader(originalClazz.getName());
        
        assertThatAllConstructorsHaveSameModifier(originalClazz, modifiedClass);
    }
    
    private void assertThatAllConstructorsHaveSameModifier(final Class<?> clazz, final Class<?> modifiedClass) {
        assertThat(modifiedClass.getConstructors())
            .as("All constructor has same modifiers")
            .hasSameSizeAs(clazz.getConstructors())
            .usingElementComparator(new Comparator<Constructor<?>>() {
                @Override
                public int compare(final Constructor<?> o1, final Constructor<?> o2) {
                    return o1.getModifiers() == o2.getModifiers()
                               ? o1.getParameterTypes().length - o2.getParameterTypes().length : o1.getModifiers() - o2.getModifiers();
                }
            })
            .contains(clazz.getConstructors());
    }
    
}