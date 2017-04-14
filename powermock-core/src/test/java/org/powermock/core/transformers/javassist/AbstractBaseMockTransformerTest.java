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

package org.powermock.core.transformers.javassist;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Loader;
import javassist.NotFoundException;
import org.junit.Before;
import org.powermock.core.MockGateway;
import org.powermock.core.classloader.MockClassLoader;
import org.powermock.core.classloader.javassist.JavassistMockClassLoader;
import org.powermock.core.transformers.ClassWrapper;
import org.powermock.core.transformers.MockTransformer;
import org.powermock.core.transformers.MockTransformerChain;
import org.powermock.core.transformers.javassist.support.ClassWrapperFactoryImpl;
import org.powermock.core.transformers.support.DefaultMockTransformerChain;

import static org.junit.Assert.assertNotNull;

public abstract class AbstractBaseMockTransformerTest {
    
    public static final String SYNTHETIC_METHOD_NAME = "$synth";
    public static final String SYNTH_FIELD = "$_synthField";
    
    protected MockTransformerChain mockTransformerChain;
    
    @Before
    public void setUp() throws Exception {
        mockTransformerChain = createMockTransformerChain();
    }
    
    protected Class<?> loadWithMockClassLoader(String className) throws ClassNotFoundException {
        MockClassLoader loader = new JavassistMockClassLoader(new String[]{MockClassLoader.MODIFY_ALL_CLASSES});
        loader.setMockTransformerChain(mockTransformerChain);
        
        Class<?> clazz = Class.forName(className, true, loader);
    
        assertNotNull("Class has been loaded", clazz);
        
        return clazz;
    }
    
    void runTestWithNewClassLoader(ClassPool classPool, String name) throws Throwable {
        Loader loader = new Loader(classPool);
        loader.run(name, new String[0]);
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
    
    
    protected MockTransformerChain createMockTransformerChain() {
        return DefaultMockTransformerChain.newBuilder().append(createMockTransformer()).build();
    }
    
    protected MockTransformer createMockTransformer(){
        return null;
    }
    
    ClassWrapper<CtClass> wrap(final CtClass ctClass) {
        return new ClassWrapperFactoryImpl().wrap(ctClass);
    }
}
