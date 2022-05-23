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
import javassist.CtNewMethod;
import javassist.NotFoundException;
import javassist.bytecode.AccessFlag;
import net.bytebuddy.utility.RandomString;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.Test;
import org.junit.runners.Parameterized;
import org.powermock.core.MockGateway;
import org.powermock.core.test.MockClassLoaderFactory;
import org.powermock.core.transformers.javassist.StaticFinalNativeMethodMockTransformer;
import org.powermock.core.transformers.mock.MockGatewaySpy;
import org.powermock.core.transformers.support.DefaultMockTransformerChain;
import org.powermock.reflect.internal.WhiteboxImpl;
import powermock.test.support.MainMockTransformerTestSupport.AbstractMethodTestClass;
import powermock.test.support.MainMockTransformerTestSupport.ReturnMethodsTestClass;
import powermock.test.support.MainMockTransformerTestSupport.SubclassWithBridgeMethod;
import powermock.test.support.MainMockTransformerTestSupport.SuperClassWithObjectMethod;
import powermock.test.support.MainMockTransformerTestSupport.VoidMethodsTestClass;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.powermock.core.transformers.mock.MockGatewaySpy.ConditionBuilder.registered;
import static org.powermock.core.transformers.mock.MockGatewaySpy.methodCalls;

public class MethodsMockTransformerTest extends AbstractBaseMockTransformerTest {
    
    @Parameterized.Parameters(name = "strategy: {0}, transformer: {1}")
    public static Iterable<Object[]> data() {
        Collection<Object[]> data = new ArrayList<Object[]>();
        
        data.addAll(MockTransformerTestHelper.createOneTransformerTestData(MockGatewaySpy.class, StaticFinalNativeMethodMockTransformer.class));
        
        return data;
    }
    
    public MethodsMockTransformerTest(final TransformStrategy strategy, final MockTransformer transformer,
                                      final MockClassLoaderFactory mockClassloaderFactory) {
        super(strategy, DefaultMockTransformerChain.newBuilder().append(transformer).build(), mockClassloaderFactory);
    }
    
    @Test
    public void should_skip_call_to_void_private_method_if_getaway_return_not_PROCEED() throws Exception {
        
        MockGatewaySpy.returnOnMethodCall("voidPrivateMethod", "");
        
        final Class<?> clazz = loadWithMockClassLoader(VoidMethodsTestClass.class.getName());
    
        final Object instance = WhiteboxImpl.newInstance(clazz);
        
        WhiteboxImpl.invokeMethod(instance, "voidPrivateMethod", "name");
        
        assertThat(WhiteboxImpl.getInternalState(instance, "lname"))
            .as("Field name is not set")
            .isNull();
        
        assertThat(methodCalls())
            .is(registered().forMethod("voidPrivateMethod"));
    }
    
    @Test
    public void should_skip_call_to_void_public_method_if_getaway_return_not_PROCEED() throws Exception {
        
        MockGatewaySpy.returnOnMethodCall("voidMethod", "");
        
        final Class<?> clazz = loadWithMockClassLoader(VoidMethodsTestClass.class.getName());
    
        final Object instance = WhiteboxImpl.newInstance(clazz);
        
        WhiteboxImpl.invokeMethod(instance, "voidMethod", "name", "field", 100d);
        
        assertThat(WhiteboxImpl.getInternalState(instance, "field"))
            .as("Field name is not set")
            .isNull();
        
        assertThat(methodCalls())
            .is(registered().forMethod("voidMethod"));
        assertThat(methodCalls())
            .isNot(registered().forMethod("voidPrivateMethod"));
    }
    
    @Test
    public void should_skip_call_to_final_void_public_method_if_getaway_return_not_PROCEED() throws Exception {
        
        MockGatewaySpy.returnOnMethodCall("finalVoidMethod", "");
        
        final Class<?> clazz = loadWithMockClassLoader(VoidMethodsTestClass.class.getName());
    
        final Object instance = WhiteboxImpl.newInstance(clazz);
        
        WhiteboxImpl.invokeMethod(instance, "finalVoidMethod", "name", "field", 100d);
        
        assertThat(WhiteboxImpl.getInternalState(instance, "field"))
            .as("Field name is not set")
            .isNull();
        
        assertThat(methodCalls())
            .is(registered().forMethod("finalVoidMethod"));
    }
    
    
    @Test
    public void should_invoke_real_final_void_public_method_if_getaway_return_PROCEED() throws Exception {
        
        MockGatewaySpy.returnOnMethodCall("finalVoidMethod", MockGateway.PROCEED);
        
        final Class<?> clazz = loadWithMockClassLoader(VoidMethodsTestClass.class.getName());
        
        final Object instance = WhiteboxImpl.newInstance(clazz);
    
        final String fieldValue = RandomString.make(10);
        WhiteboxImpl.invokeMethod(instance, "finalVoidMethod", "name", fieldValue, 100d);
        
        assertThat(WhiteboxImpl.getInternalState(instance, "field"))
            .as("Field name is not set")
            .isEqualTo(fieldValue);
        
        assertThat(methodCalls())
            .is(registered().forMethod("finalVoidMethod"));
    }
    
    @Test
    public void should_return_value_from_getaway_for_non_void_methods_is_it_is_not_PROCEED() throws Exception {
        
        final String expected = "mocked";
        MockGatewaySpy.returnOnMethodCall("returnMethod", expected);
        
        final Class<?> clazz = loadWithMockClassLoader(ReturnMethodsTestClass.class.getName());
    
        final Object instance = WhiteboxImpl.newInstance(clazz);
        
        final Object result = WhiteboxImpl.invokeMethod(instance, "returnMethod", "name", "field", 100d);
        
        assertThat(result)
            .isEqualTo(expected);
        
        assertThat(methodCalls())
            .is(registered().forMethod("returnMethod"));
    }
    
    @Test
    public void should_return_value_from_getaway_for_final_non_void_methods_is_it_is_not_PROCEED() throws Exception {
        
        final String expected = "mocked";
        MockGatewaySpy.returnOnMethodCall("finalReturnMethod", expected);
        
        final Class<?> clazz = loadWithMockClassLoader(ReturnMethodsTestClass.class.getName());
    
        final Object instance = WhiteboxImpl.newInstance(clazz);
        
        final Object result = WhiteboxImpl.invokeMethod(instance, "finalReturnMethod", "name", "field", 100d);
        
        assertThat(result)
            .isEqualTo(expected);
        
        assertThat(methodCalls())
            .is(registered().forMethod("finalReturnMethod"));
    }
    
    @Test
    public void should_return_value_from_getaway_for_final_private_non_void_methods_is_it_is_not_PROCEED() throws Exception {
        
        final String expected = "mocked";
        MockGatewaySpy.returnOnMethodCall("privateReturnMethod", expected);
        
        final Class<?> clazz = loadWithMockClassLoader(ReturnMethodsTestClass.class.getName());
    
        final Object instance = WhiteboxImpl.newInstance(clazz);
        
        final Object result = WhiteboxImpl.invokeMethod(instance, "privateReturnMethod", "name");
        
        assertThat(result)
            .isEqualTo(expected);
        
        assertThat(methodCalls())
            .is(registered().forMethod("privateReturnMethod"));
    }
    
    @Test
    public void should_return_real_method_return_value_for_non_void_methods_if_getaway_returns_PROCEED() throws Exception {
        
        MockGatewaySpy.returnOnMethodCall("returnMethod", MockGateway.PROCEED);
        
        final Class<?> clazz = loadWithMockClassLoader(ReturnMethodsTestClass.class.getName());
    
        final Object instance = WhiteboxImpl.newInstance(clazz);
        
        final String name = "name";
        final Object result = WhiteboxImpl.invokeMethod(instance, "returnMethod", name, "field", 100d);
        
        String expected = name + "(a)";
        assertThat(result)
            .isEqualTo(expected);
        
        assertThat(methodCalls())
            .is(registered().forMethod("returnMethod"));
    }
    
    @Test
    public void should_return_real_method_return_value_for_final_non_void_methods_if_getaway_returns_PROCEED() throws Exception {
        
        MockGatewaySpy.returnOnMethodCall("finalReturnMethod", MockGateway.PROCEED);
        
        final Class<?> clazz = loadWithMockClassLoader(ReturnMethodsTestClass.class.getName());
    
        final Object instance = WhiteboxImpl.newInstance(clazz);
        
        final String name = "name";
        final Object result = WhiteboxImpl.invokeMethod(instance, "finalReturnMethod", name, "field", 100d);
        
        String expected = name + "(a)";
        assertThat(result)
            .isEqualTo(expected);
        
        assertThat(methodCalls())
            .is(registered().forMethod("finalReturnMethod"));
    }
    
    @Test
    public void should_ignore_abstract_methods() throws Exception {
    
        final Throwable throwable = catchThrowable(new ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                loadWithMockClassLoader(AbstractMethodTestClass.class.getName());
            }
        });
        
        assertThat(throwable)
            .as("Abstract class is transformed")
            .isNull();
    }
    
    @Test
    public void should_modify_bridge_methods() throws Throwable {
        
        final Class<?> clazz = loadWithMockClassLoader(SubclassWithBridgeMethod.class.getName());
    
        final Object instance = WhiteboxImpl.newInstance(clazz);
        
        clazz.getMethod("doSomething", String.class).invoke(instance, "value");
        
        assertThat(methodCalls())
            .is(registered().forMethod("doSomething"));
    }
    
    @Test
    public void should_ignore_synthetic_non_bridge_methods() throws Throwable {
        
        final ClassPool classPool = new ClassPool(true);
        CtClass ctClass = prepareClassesForTest(classPool, "return;");
        
        final Class<?> clazz = loadWithMockClassLoader(ctClass);
        
        Method method = null;
        for (Method m : clazz.getDeclaredMethods()) {
            if (m.getName().equals(SYNTHETIC_METHOD_NAME)) {
                method = m;
                break;
            }
        }
    
        final Object instance = WhiteboxImpl.newInstance(clazz);
    
        assertThat(method)
            .isNotNull();
    
        method.setAccessible(true);
        method.invoke(instance, "");
        
        assertThat(methodCalls())
            .isNot(registered().forMethod(SYNTHETIC_METHOD_NAME));
    }
    
    
    @Test
    public void should_ignore_call_to_synthetic_non_bridge_methods() throws Throwable {
        
        final ClassPool classPool = new ClassPool(true);
        CtClass ctClass = prepareClassesForTest(classPool, "syntheticMethodIsCalled = true;");
        
        final Class<?> clazz = loadWithMockClassLoader(ctClass);
    
        final Object instance = WhiteboxImpl.newInstance(clazz);
        
        clazz.getMethod("doSomething", Object.class).invoke(instance, new Object());
        
        assertThat(methodCalls())
            .isNot(registered().forMethod(SYNTHETIC_METHOD_NAME));
        
        assertThat(WhiteboxImpl.getInternalState(clazz, "syntheticMethodIsCalled"))
            .isEqualTo(true);
    }
    
    
    private CtClass prepareClassesForTest(ClassPool classPool,
                                          String bodyOfSyntheticMethod) throws NotFoundException, CannotCompileException {
        
        CtClass ctClass = classPool.getCtClass(SuperClassWithObjectMethod.class.getName());
        addSyntheticMethod(classPool, ctClass, bodyOfSyntheticMethod);
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
    
}