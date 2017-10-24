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

import javassist.CtClass;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.powermock.core.MockGateway;
import org.powermock.core.test.ClassLoaderTestHelper;
import org.powermock.core.test.MockClassLoaderFactory;
import org.powermock.core.transformers.javassist.support.JavaAssistClassWrapperFactory;
import org.powermock.core.transformers.mock.MockGatewaySpy;

import static org.junit.Assume.assumeTrue;

@RunWith(Parameterized.class)
abstract class AbstractBaseMockTransformerTest {
    
    static final String SYNTHETIC_METHOD_NAME = "$synth";
    static final String SYNTH_FIELD = "$_synthField";
    
    protected final TransformStrategy strategy;
    protected final MockTransformerChain mockTransformerChain;
    protected final MockClassLoaderFactory mockClassloaderFactory;
    
    AbstractBaseMockTransformerTest(final TransformStrategy strategy,
                                    final MockTransformerChain mockTransformerChain,
                                    final MockClassLoaderFactory mockClassloaderFactory){
        this.strategy = strategy;
        this.mockTransformerChain = mockTransformerChain;
        this.mockClassloaderFactory = mockClassloaderFactory;
    }
    
    @Before
    public void setUp() throws Exception {
        ClassLoaderTestHelper.clearCache();
        MockGatewaySpy.clear();
        MockGatewaySpy.returnOnMethodCall(MockGateway.PROCEED);
    }
    
    protected Class<?> loadWithMockClassLoader(final String name) throws Exception {
        return ClassLoaderTestHelper.loadWithMockClassLoader(name, mockTransformerChain, mockClassloaderFactory);
    }
    
    protected Class<?> loadWithMockClassLoader(final CtClass ctClass) throws Exception {
        return ClassLoaderTestHelper.loadWithMockClassLoader(ctClass.getName(), ctClass.toBytecode(), mockTransformerChain, mockClassloaderFactory);
    }
    
    ClassWrapper<CtClass> wrap(final CtClass ctClass) {
        return new JavaAssistClassWrapperFactory().wrap(ctClass);
    }
    
    protected void assumeClassLoaderMode() {
        assumeTrue("Supported only by class loader mode.", strategy.isClassloaderMode());
    }
    
    protected void assumeAgentMode() {
        assumeTrue("Supported only by class loader mode.", strategy.isAgentMode());
    }
}
