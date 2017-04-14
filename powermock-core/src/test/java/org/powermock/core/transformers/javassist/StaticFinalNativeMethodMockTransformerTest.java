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
import javassist.CtNewMethod;
import javassist.NotFoundException;
import javassist.bytecode.AccessFlag;
import org.junit.Test;
import org.powermock.core.transformers.MockTransformer;
import org.powermock.core.transformers.TransformStrategy;
import powermock.test.support.MainMockTransformerTestSupport.CallSpy;
import powermock.test.support.MainMockTransformerTestSupport.SubclassWithBridgeMethod;
import powermock.test.support.MainMockTransformerTestSupport.SuperClassWithObjectMethod;

import java.lang.reflect.Method;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class StaticFinalNativeMethodMockTransformerTest extends AbstractBaseMockTransformerTest {
    
    @Override
    protected MockTransformer createMockTransformer() {
        return new StaticFinalNativeMethodMockTransformer(TransformStrategy.CLASSLOADER);
    }
    
    @Test
    public void shouldIgnoreSyntheticNonBridgeMethods() throws Throwable {
        
        final ClassPool classPool = new ClassPool(true);
        CtClass ctClass = prepareClassesForTest(classPool, "return;");
        
        mockTransformerChain.transform(wrap(ctClass));
        
        runTestWithNewClassLoader(classPool, ShouldIgnoreSyntheticNonBridgeMethods.class.getName());
    }
    
    @Test
    public void shouldIgnoreCallToSyntheticNonBridgeMethods() throws Throwable {
        final ClassPool classPool = new ClassPool(true);
        CtClass ctClass = prepareClassesForTest(classPool, "powermock.test.support.MainMockTransformerTestSupport.CallSpy.registerMethodCall($1);");
    
        mockTransformerChain.transform(wrap(ctClass));
        
        runTestWithNewClassLoader(classPool, ShouldIgnoreCallToSyntheticNonBridgeMethods.class.getName());
    }
    
    
    @Test
    public void shouldModifyBridgeMethods() throws Throwable {
        final ClassPool classPool = new ClassPool(true);
        addCallInterceptorToMockGateway(classPool);
        
        CtClass ctClass = classPool.get(SubclassWithBridgeMethod.class.getName());
        mockTransformerChain.transform(wrap(ctClass));
        
        runTestWithNewClassLoader(classPool, ShouldModifyBridgeMethods.class.getName());
    }
    
    private CtClass prepareClassesForTest(ClassPool classPool,
                                          String body) throws NotFoundException, CannotCompileException {
        addCallInterceptorToMockGateway(classPool);
        
        CtClass ctClass = classPool.getCtClass(SuperClassWithObjectMethod.class.getName());
        addSyntheticMethod(classPool, ctClass, body);
        return ctClass;
    }
    
    private void addSyntheticMethod(ClassPool classPool,
                                    CtClass ctClass, String body) throws NotFoundException, CannotCompileException {
        
        CtMethod ctMethod = CtNewMethod.make(AccessFlag.SYNTHETIC, CtClass.voidType,
                                             SYNTHETIC_METHOD_NAME, new CtClass[]{classPool.get(String.class.getName())},
                                             null, body, ctClass);
        ctClass.addMethod(ctMethod);
        
        for (CtMethod method : ctClass.getDeclaredMethods()) {
            if (!method.getName().equals(SYNTHETIC_METHOD_NAME)) {
                method.insertBefore("$synth(\"" + method.getLongName() + "\");");
            }
        }
    }
    
    public static class ShouldIgnoreSyntheticNonBridgeMethods {
        public static void main(String[] args) throws Exception {
            Class clazz = SuperClassWithObjectMethod.class;
            
            Method method = null;
            for (Method m : clazz.getDeclaredMethods()) {
                if (m.getName().equals(SYNTHETIC_METHOD_NAME)) {
                    method = m;
                    break;
                }
            }
            
            Object instance = clazz.newInstance();
            
            if (method != null) {
                method.setAccessible(true);
            }
            method.invoke(instance, "");
            
            assertThat(CallSpy.getMethodCalls()).isEmpty();
        }
        
    }
    
    @SuppressWarnings("unchecked")
    public static class ShouldIgnoreCallToSyntheticNonBridgeMethods {
        public static void main(String[] args) throws Exception {
            Class clazz = SuperClassWithObjectMethod.class;
            
            Object instance = clazz.newInstance();
            
            clazz.getMethod("doSomething", Object.class).invoke(instance, new Object());
            
            List<String> calls = CallSpy.getMethodCalls();
            assertThat(calls).contains("doSomething").doesNotContain(SYNTHETIC_METHOD_NAME);
        }
        
    }
    
    public static class ShouldModifyBridgeMethods {
        public static void main(String[] args) throws Exception {
            Class clazz = SubclassWithBridgeMethod.class;
            
            Object instance = clazz.newInstance();
            
            clazz.getMethod("doSomething", String.class).invoke(instance, "value");
            
            List<String> calls = CallSpy.getMethodCalls();
            assertThat(calls).contains("doSomething");
        }
    }
}