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

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.assertj.core.api.iterable.Extractor;
import org.junit.Test;
import org.junit.runners.Parameterized;
import org.powermock.core.IndicateReloadClass;
import org.powermock.core.test.MockClassLoaderFactory;
import org.powermock.core.transformers.bytebuddy.ConstructorCallMockTransformer;
import org.powermock.core.transformers.javassist.InstrumentMockTransformer;
import org.powermock.core.transformers.mock.MockGatewaySpy;
import org.powermock.core.transformers.mock.MockGatewaySpy.MethodCall;
import powermock.test.support.MainMockTransformerTestSupport.ConstructorCall.SupperClassThrowsException;
import powermock.test.support.MainMockTransformerTestSupport.ParameterImpl;
import powermock.test.support.MainMockTransformerTestSupport.ParameterInterface;
import powermock.test.support.MainMockTransformerTestSupport.SomeInterface;
import powermock.test.support.MainMockTransformerTestSupport.SuperClassCallSuperConstructor;
import powermock.test.support.MainMockTransformerTestSupport.SuperClassCallSuperConstructorWithCast;
import powermock.test.support.MainMockTransformerTestSupport.SuperClassCallSuperConstructorWithVararg;
import powermock.test.support.MainMockTransformerTestSupport.SupportClasses;
import powermock.test.support.MainMockTransformerTestSupport.SupportClasses.EnumClass;
import powermock.test.support.MainMockTransformerTestSupport.SupportClasses.PublicSuperClass;
import powermock.test.support.MainMockTransformerTestSupport.SupportClasses.SubClass;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.catchThrowable;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assume.assumeThat;
import static org.junit.Assume.assumeTrue;
import static org.powermock.core.MockGateway.PROCEED;
import static org.powermock.core.MockGateway.SUPPRESS;
import static org.powermock.core.transformers.MockTransformerTestHelper.createTransformerTestDataWithMockGateway;

public class ConstructorCallMockTransformerTest extends AbstractBaseMockTransformerTest {
    
    @Parameterized.Parameters(name = "strategy: {0}, transformerType: {2}")
    public static Iterable<Object[]> data() {
        Collection<Object[]> data = new ArrayList<Object[]>();
    
        data.addAll(createTransformerTestDataWithMockGateway(MockGatewaySpy.class, InstrumentMockTransformer.class));
        data.addAll(createTransformerTestDataWithMockGateway(MockGatewaySpy.class, ConstructorCallMockTransformer.class));
        
        return data;
    }
    
    public ConstructorCallMockTransformerTest(final TransformStrategy strategy,
                                              final MockTransformerChain mockTransformerChain,
                                              final MockClassLoaderFactory mockClassloaderFactory) {
        super(strategy, mockTransformerChain, mockClassloaderFactory);
    }
    
    @Test
    public void should_add_additional_defer_constructor_which_call_default_if_parent_Object_and_strategy_classloader() throws Exception {
        
        assumeClassLoaderMode();
        assumeClassLoaderIsByteBuddy();
        
        Class<?> clazz = reloadClass(PublicSuperClass.class);
        
        assertThat(clazz.getConstructors())
            .as("Number of constructors in modified class")
            .hasSize(2);
        
        assertThat(clazz.getConstructor(IndicateReloadClass.class))
            .as("Defer-constructor returnOnMethodCall")
            .isNotNull();
    }
    
    @Test
    public void should_add_additional_defer_constructor_which_call_default_if_parent_not_Object_and_strategy_classloader() throws Exception {
        
        assumeClassLoaderMode();
        
        Class<?> clazz = reloadClass(SubClass.class);
        
        assertThat(clazz.getConstructors())
            .as("Number of constructors in modified class")
            .hasSize(2);
        
        assertThat(clazz.getConstructor(IndicateReloadClass.class))
            .as("Defer-constructor returnOnMethodCall")
            .isNotNull();
    }
    
    @Test
    public void should_not_add_additional_defer_constructor_if_strategy_is_not_classloader() throws Exception {
        assumeAgentMode();
        
        Class<?> clazz = loadWithMockClassLoader(SupportClasses.SubClass.class.getName());
        
        assertThat(clazz.getConstructors())
            .as("Number of constructors in modified class")
            .hasSameSizeAs(SupportClasses.SubClass.class.getConstructors());
    }
    
    @Test
    public void should_not_add_defer_constructor_to_interface() throws Exception {
        Class<?> clazz = loadWithMockClassLoader(SomeInterface.class.getName());
        
        assertThat(clazz.getConstructors())
            .as("Number of constructors in modified interface same as in original")
            .hasSameSizeAs(SomeInterface.class.getConstructors());
    }
    
    @Test
    public void should_not_add_defer_constructor_to_enum() throws Exception {
        Class<?> clazz = loadWithMockClassLoader(EnumClass.class.getName());
        
        assertThat(clazz.getConstructors())
            .as("Number of constructors in modified class same as in original")
            .hasSameSizeAs(EnumClass.class.getConstructors());
    }
    
    @Test
    public void should_suppress_call_to_super_constructor_if_getaway_return_SUPPRESS() throws Exception {
        assumeClassLoaderMode();
        
        MockGatewaySpy.returnOnMethodCall(SUPPRESS);
        
        Class<?> clazz = loadWithMockClassLoader(SuperClassCallSuperConstructor.class.getName());
    
        final Constructor<?> constructor = clazz.getConstructor(String.class, String.class, double.class);
        
        Throwable throwable = catchThrowable(new ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                try {
                    constructor.newInstance("name", "field", 100);
                } catch (Exception e) {
                    throw e.getCause() == null ? e : e.getCause();
                }
            }
        });
        
        assertThat(throwable)
            .as("Call to super is suppressed")
            .isNull();
    }
    
    @Test
    public void should_not_suppress_call_to_super_constructor_if_getaway_return_PROCEED() throws Exception {
        assumeClassLoaderMode();
        
        MockGatewaySpy.returnOnMethodCall(PROCEED);
        
        Class<?> clazz = loadWithMockClassLoader(SuperClassCallSuperConstructor.class.getName());
    
        final Constructor<?> constructor = clazz.getConstructor(String.class, String.class, double.class);
        
        Throwable throwable = catchThrowable(new ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                try {
                    constructor.newInstance("name", "field", 100);
                } catch (Exception e) {
                    throw e.getCause() == null ? e : e.getCause();
                }
            }
        });
        
        assertThat(throwable)
            .as("Call to super is not suppressed")
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage(SupperClassThrowsException.MESSAGE);
    }
    
    @Test
    public void should_provide_correct_constructor_param_and_arguments() throws Exception {
        assumeClassLoaderMode();
        
        MockGatewaySpy.returnOnMethodCall(SUPPRESS);
        
        Class<?> clazz = loadWithMockClassLoader(SuperClassCallSuperConstructor.class.getName());
    
        final Constructor<?> constructor = clazz.getConstructor(String.class, String.class, double.class);
        
        constructor.newInstance("name", "field", 100);
    
        assertThatCorrectConstructorTypeProvided();
    
        final MethodCall methodCall = MockGatewaySpy.constructorCalls().get(0);
        
        assertThat(methodCall.args)
            .as("Correct constructor arguments are provided")
            .containsExactly("name", 100.0);
        
        assertThat(methodCall.sig)
            .as("Correct constructor signature is provided")
            .containsExactly(String.class, double.class);
    }
    
    @Test
    public void should_provide_correct_constructor_param_and_arguments_when_cast_required() throws Exception {
        assumeClassLoaderMode();
        
        MockGatewaySpy.returnOnMethodCall(SUPPRESS);
        
        final Class<?> clazz = loadWithMockClassLoader(SuperClassCallSuperConstructorWithCast.class.getName());
        
        final Class<?> paramClass = loadWithMockClassLoader(ParameterInterface.class.getName());
        final Object param = loadWithMockClassLoader(ParameterImpl.class.getName()).newInstance();
        
        final Constructor<?> constructor = clazz.getConstructor(paramClass);
        
        constructor.newInstance(param);
    
        assertThatCorrectConstructorTypeProvided();
    
        final MethodCall methodCall = MockGatewaySpy.constructorCalls().get(0);
    
        assertThat(methodCall.args)
            .as("Correct constructor arguments are provided")
            .containsExactly(param);
        
        assertThat(methodCall.sig)
            .as("Correct constructor signature is provided")
            .hasSize(1)
            .extracting(new Extractor<Class<?>, Object>() {
                @Override
                public Object extract(final Class<?> input) {
                    return input.getName();
                }
            })
            .containsExactly(ParameterImpl.class.getName());
    }
    
    @Test
    public void should_provide_correct_constructor_param_and_arguments_when_parameters_vararg() throws Exception {
        assumeClassLoaderMode();
        
        MockGatewaySpy.returnOnMethodCall(SUPPRESS);
        
        final Class<?> clazz = loadWithMockClassLoader(SuperClassCallSuperConstructorWithVararg.class.getName());
        
        final Class<?> paramClass = long[].class;
        
        final Constructor<?> constructor = clazz.getConstructor(paramClass);
    
        long[] params = {1, 5, 6};
        constructor.newInstance(new Object[]{
            params
        });
    
        assertThatCorrectConstructorTypeProvided();
    
        final MethodCall methodCall = MockGatewaySpy.constructorCalls().get(0);
        
        assertThat(methodCall.args)
            .as("Constructor arguments have correct size")
            .hasSize(1);
        
        assertThat((long[]) methodCall.args[0])
            .as("Correct constructor arguments are provided")
            .containsExactly(params);
        
        assertThat(methodCall.sig)
            .as("Correct constructor signature is provided")
            .hasSize(1)
            .containsExactly(long[].class);
    }
    
    private void assertThatCorrectConstructorTypeProvided() {
        final MethodCall methodCall = MockGatewaySpy.constructorCalls().get(0);
        assertThat(methodCall.type.getName())
            .as("Correct constructor type is provided")
            .isEqualTo(SupperClassThrowsException.class.getName());
    }
    
    private void assumeClassLoaderIsByteBuddy() {
        assumeTrue(
            "ByteBuddy implantation MockClassLoader should always add defer constructor," +
                " because ByteBuddy cannot add constructor to super class ad-hoc.",
            mockClassloaderFactory.isByteBuddy()
        );
    }
    
    private Class<?> reloadClass(final Class<?> originalClazz) throws Exception {
        assumeThat("Original number of constructors equals to 1", originalClazz.getConstructors().length, equalTo(1));
        return loadWithMockClassLoader(originalClazz.getName());
    }
}
