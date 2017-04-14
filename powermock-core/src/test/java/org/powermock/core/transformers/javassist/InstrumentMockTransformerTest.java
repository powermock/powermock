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
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.AccessFlag;
import org.junit.Test;
import org.powermock.core.IndicateReloadClass;
import org.powermock.core.transformers.MockTransformer;
import org.powermock.core.transformers.TransformStrategy;
import powermock.test.support.MainMockTransformerTestSupport.CallSpy;
import powermock.test.support.MainMockTransformerTestSupport.SuperClassWithObjectMethod;
import powermock.test.support.MainMockTransformerTestSupport.SupportClasses;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

public class InstrumentMockTransformerTest extends AbstractBaseMockTransformerTest {
    
    private static final String SYNTHETIC_FIELD_VALUE = "Synthetic Field Value";
    
    @Override
    protected MockTransformer createMockTransformer() {
        return new InstrumentMockTransformer(TransformStrategy.CLASSLOADER);
    }
    
    @Test
    public void subclassShouldNormallyGetAnAdditionalDeferConstructor() throws Exception {
        Class<?> clazz = loadWithMockClassLoader(SupportClasses.SubClass.class.getName());
        
        assertThat(SupportClasses.SubClass.class.getConstructors())
            .as("Original number of constructors")
            .hasSize(1);
        
        assertThat(clazz.getConstructors())
            .as("Number of constructors in modified class")
            .hasSize(2);
        
        assertThat(clazz.getConstructor(IndicateReloadClass.class))
            .as("Defer-constructor expected")
            .isNotNull();
    }
    
    
    @Test
    public void shouldIgnoreCallToSyntheticField() throws Throwable {
        final ClassPool classPool = new ClassPool(true);
        CtClass ctClass = prepareClassesForFieldTests(classPool);
        
        mockTransformerChain.transform(wrap(ctClass));
        
        runTestWithNewClassLoader(classPool, ShouldIgnoreCallToSyntheticField.class.getName());
    }
    
    
    private CtClass prepareClassesForFieldTests(ClassPool classPool) throws NotFoundException, CannotCompileException {
        addCallInterceptorToMockGateway(classPool);
        
        CtClass ctClass = classPool.getCtClass(SuperClassWithObjectMethod.class.getName());
        
        addSyntheticField(classPool, ctClass);
        insertCallSyntheticField(ctClass);
        
        return ctClass;
    }
    
    private void insertCallSyntheticField(CtClass ctClass) throws CannotCompileException {
        for (CtMethod method : ctClass.getDeclaredMethods()) {
            method.insertBefore(
                "String v = "
                    + SYNTH_FIELD
                    + ";"
                    + SYNTH_FIELD
                    + " = \""
                    + method.getName()
                    + "\";"
            );
        }
    }
    
    private void addSyntheticField(ClassPool classPool,
                                   CtClass ctClass) throws CannotCompileException, NotFoundException {
        CtField field = new CtField(classPool.get(String.class.getName()), SYNTH_FIELD, ctClass);
        field.setModifiers(AccessFlag.SYNTHETIC);
        
        ctClass.addField(field, CtField.Initializer.constant(SYNTHETIC_FIELD_VALUE));
    }
    
    
    public static class ShouldIgnoreCallToSyntheticField {
        public static void main(String[] args) throws Exception {
            Class clazz = SuperClassWithObjectMethod.class;
            
            Object instance = clazz.newInstance();
            
            clazz.getMethod("doSomething", Object.class).invoke(instance, new Object());
            
            assertThat(CallSpy.getFieldCalls()).doesNotContain(SYNTH_FIELD);
            
            Field field = clazz.getDeclaredField(SYNTH_FIELD);
            field.setAccessible(true);
            String fieldValue = (String) field.get(instance);
            assertThat(fieldValue).isEqualTo("doSomething");
        }
    }
}