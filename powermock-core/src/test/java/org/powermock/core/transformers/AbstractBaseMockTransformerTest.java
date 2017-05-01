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

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.powermock.core.MockGateway;
import org.powermock.core.test.ClassLoaderTestHelper;
import org.powermock.core.test.MockClassLoaderFactory;
import org.powermock.core.transformers.javassist.support.JavaAssistClassWrapperFactory;

import static org.junit.Assert.assertNotNull;

@RunWith(Parameterized.class)
abstract class AbstractBaseMockTransformerTest {
    
    static final String SYNTHETIC_METHOD_NAME = "$synth";
    static final String SYNTH_FIELD = "$_synthField";
    
    protected final TransformStrategy strategy;
    protected final MockTransformerChain mockTransformerChain;
    private final MockClassLoaderFactory mockClassloaderFactory;
    
    AbstractBaseMockTransformerTest(final TransformStrategy strategy,
                                    final MockTransformerChain mockTransformerChain,
                                    final MockClassLoaderFactory mockClassloaderFactory){
        this.strategy = strategy;
        this.mockTransformerChain = mockTransformerChain;
        this.mockClassloaderFactory = mockClassloaderFactory;
    }
    
    protected Class<?> loadWithMockClassLoader(final String name) throws Exception {
        return ClassLoaderTestHelper.loadWithMockClassLoader(name, mockTransformerChain, mockClassloaderFactory);
    }
    
    void addCallInterceptorToMockGateway(ClassPool classPool) throws NotFoundException, CannotCompileException {
        CtClass mockGetawayClass = classPool.get(MockGateway.class.getName());
        for (CtMethod method : mockGetawayClass.getMethods()) {
            String methodName = method.getName();
            if (methodName.equals("methodCall")) {
                method.insertBefore(
                    "powermock.test.support.MainMockTransformerTestSupport.CallSpy.registerMethodCall("
                        + "methodName"
                        + ");"
                );
            } else if (methodName.equals("fieldCall")) {
                method.insertBefore(
                    "powermock.test.support.MainMockTransformerTestSupport.CallSpy.registerFieldCall("
                        + "fieldName"
                        + ");"
                );
            }
        }
    }
    
    ClassWrapper<CtClass> wrap(final CtClass ctClass) {
        return new JavaAssistClassWrapperFactory().wrap(ctClass);
    }
}
