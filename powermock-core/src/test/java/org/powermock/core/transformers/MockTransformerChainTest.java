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

import static org.assertj.core.api.Assertions.assertThat;

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
    
    
    private static class MockTransformerSpy implements MockTransformer {
        public boolean classTransformed = false;
        
        @Override
        public <T> ClassWrapper<T> transform(final ClassWrapper<T> clazz) throws Exception {
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
    }
}