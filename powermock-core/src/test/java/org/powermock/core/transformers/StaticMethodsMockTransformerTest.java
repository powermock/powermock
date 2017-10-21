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
import org.powermock.core.MockGateway;
import org.powermock.core.test.MockClassLoaderFactory;
import org.powermock.core.transformers.bytebuddy.MethodMockTransformer;
import org.powermock.core.transformers.bytebuddy.advice.MockMethodDispatchers;
import org.powermock.core.transformers.javassist.StaticFinalNativeMethodMockTransformer;
import org.powermock.core.transformers.mock.MockGatewaySpy;
import org.powermock.core.transformers.mock.MockGatewaySpyMethodDispatcher;
import org.powermock.core.transformers.support.DefaultMockTransformerChain;
import org.powermock.reflect.internal.WhiteboxImpl;
import powermock.test.support.MainMockTransformerTestSupport.StaticVoidMethodsTestClass;

import java.util.ArrayList;
import java.util.Collection;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.powermock.core.transformers.mock.MockGatewaySpy.ConditionBuilder.registered;
import static org.powermock.core.transformers.mock.MockGatewaySpy.methodCalls;

public class StaticMethodsMockTransformerTest extends AbstractBaseMockTransformerTest {
    
    @Parameterized.Parameters(name = "strategy: {0}, transformer: {1}")
    public static Iterable<Object[]> data() {
        Collection<Object[]> data = new ArrayList<Object[]>();
    
        data.addAll(MockTransformerTestHelper.createOneTransformerTestData(MockGatewaySpy.class, StaticFinalNativeMethodMockTransformer.class));
        data.addAll(MockTransformerTestHelper.createOneTransformerTestData(MethodMockTransformer.class));
        
        return data;
    }
    
    public StaticMethodsMockTransformerTest(final TransformStrategy strategy, final MockTransformer transformer,
                                            final MockClassLoaderFactory mockClassloaderFactory) {
        super(strategy, DefaultMockTransformerChain.newBuilder().append(transformer).build(), mockClassloaderFactory);
        if (transformer instanceof MethodMockTransformer) {
            MockMethodDispatchers.set(((MethodMockTransformer) transformer).getIdentifier(), new MockGatewaySpyMethodDispatcher());
        }
    }
    
    @Test
    public void should_skip_call_to_void_static_public_method_if_getaway_return_not_PROCEED() throws Exception {
        
        MockGatewaySpy.returnOnMethodCall("voidMethod", "");
        
        final Class<?> clazz = loadWithMockClassLoader(StaticVoidMethodsTestClass.class.getName());
        
        WhiteboxImpl.invokeMethod(clazz, "voidMethod", "name", "field", 100d);
        
        assertThat(WhiteboxImpl.getInternalState(clazz, "field"))
            .as("Field name is not set")
            .isNull();
        
        assertThat(methodCalls())
            .is(registered().forMethod("voidMethod"));
    }
    
    @Test
    public void should_continue_executing_void_static_public_method_if_getaway_return_PROCEED() throws Exception {
        
        MockGatewaySpy.returnOnMethodCall("voidMethod", MockGateway.PROCEED);
        
        final Class<?> clazz = loadWithMockClassLoader(StaticVoidMethodsTestClass.class.getName());
    
        final String expectedFieldValue = "field";
        WhiteboxImpl.invokeMethod(clazz, "voidMethod", "name", expectedFieldValue, 100d);
        
        assertThat(WhiteboxImpl.getInternalState(clazz, "field"))
            .as("Field name is not set")
            .isEqualTo(expectedFieldValue);
        
        assertThat(methodCalls())
            .is(registered().forMethod("voidMethod"));
    }
}