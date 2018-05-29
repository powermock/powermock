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
import org.powermock.core.transformers.support.DefaultMockTransformerChain;
import org.powermock.core.transformers.support.FilterPredicates;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class MockTransformerChainTest {
    
    @Test
    public void should_call_all_transformer_when_chain_is_called() throws Exception {
        MockTransformerSpy firstTransformer = new MockTransformerSpy();
        MockTransformerSpy secondTransformer = new MockTransformerSpy();
    
        MockTransformerChain transformerChain = DefaultMockTransformerChain.newBuilder()
                                                                           .append(firstTransformer)
                                                                           .append(secondTransformer)
                                                                           .build();
        
        transformerChain.transform(new DummyClassWrapper());
        
        
        firstTransformer.assertIsCalled();
        secondTransformer.assertIsCalled();
    }
    
    @Test
    public void should_return_collection_of_mock_transformer_which_fit_predicate() {
    
        final FitPredicateMockTransformer expectedTransformer = new FitPredicateMockTransformer();
        
        final MockTransformerChain transformerChain = DefaultMockTransformerChain.newBuilder()
                                                                           .append(new MockTransformerSpy())
                                                                           .append(new MockTransformerSpy())
                                                                           .append(expectedTransformer)
                                                                           .build();
        
        assertThat(transformerChain.filter(FilterPredicates.isInstanceOf(TestClassAwareTransformer.class)))
            .as("Transformer is found.")
            .containsExactly(expectedTransformer);
    }
    
    
    private static class MockTransformerSpy implements MockTransformer<Object> {
        private boolean classTransformed = false;
        
        @Override
        public ClassWrapper<Object> transform(final ClassWrapper<Object> clazz) throws Exception {
            classTransformed = true;
            return clazz;
        }
        
        private void assertIsCalled(){
            assertThat(classTransformed).as("Transformer has not been called").isTrue();
        }
    }
    
    private static class DummyClassWrapper implements ClassWrapper<Object> {
        @Override
        public boolean isInterface() {
            return false;
        }
        
        @Override
        public Object unwrap() {
            return null;
        }
    
        @Override
        public ClassWrapper<Object> wrap(final Object original) {
            return null;
        }
    }
    
    private static class FitPredicateMockTransformer implements MockTransformer, TestClassAwareTransformer {
    
        @Override
        public ClassWrapper transform(final ClassWrapper clazz) {
            return null;
        }
    
        @Override
        public void setTestClass(final Class<?> testClass) {
        
        }
    }
}