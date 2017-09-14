/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.powermock.core.transformers.impl;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.Loader;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.AccessFlag;
import org.junit.Test;
import org.powermock.core.IndicateReloadClass;
import org.powermock.core.MockGateway;
import org.powermock.core.classloader.MockClassLoader;
import org.powermock.core.transformers.MockTransformer;
import powermock.test.support.ClassWithLargeMethods;
import powermock.test.support.MainMockTransformerTestSupport.CallSpy;
import powermock.test.support.MainMockTransformerTestSupport.SubclassWithBridgeMethod;
import powermock.test.support.MainMockTransformerTestSupport.SuperClassWithObjectMethod;
import powermock.test.support.MainMockTransformerTestSupport.SupportClasses;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class ClassMockTransformerTest {

    public static final String SYNTHETIC_METHOD_NAME = "$synth";
    public static final String SYNTH_FIELD = "$_synthField";
    public static final String SYNTHETIC_FIELD_VALUE = "Synthetic Field Value";

    /**
     * This tests that a inner 'public static final class' can be modified to drop the final modifier. Fixes <a
     * href="http://code.google.com/p/powermock/issues/detail?id=95">Issue 95</a>.
     */
    @Test
    public void staticFinalInnerClassesShouldBecomeNonFinal() throws Exception {
        Class<?> clazz = loadWithMockClassLoader(SupportClasses.StaticFinalInnerClass.class.getName());
        assertFalse(Modifier.isFinal(clazz.getModifiers()));
    }

    /**
     * This tests that a inner 'public final class' can be modified to drop the final modifier. Fixes <a
     * href="http://code.google.com/p/powermock/issues/detail?id=95">Issue 95</a>.
     */
    @Test
    public void finalInnerClassesShouldBecomeNonFinal() throws Exception {
        Class<?> clazz = loadWithMockClassLoader(SupportClasses.FinalInnerClass.class.getName());
        assertFalse(Modifier.isFinal(clazz.getModifiers()));
    }

    /**
     * This tests that a inner 'enum' can be modified to drop the final modifier. Fixes <a
     * href="http://code.google.com/p/powermock/issues/detail?id=95">Issue 95</a>.
     */
    @Test
    public void enumClassesShouldBecomeNonFinal() throws Exception {
        Class<?> clazz = loadWithMockClassLoader(SupportClasses.EnumClass.class.getName());
        assertFalse(Modifier.isFinal(clazz.getModifiers()));
    }

    @Test
    public void privateInnerClassesShouldBecomeNonFinal() throws Exception {
        Class<?> clazz = loadWithMockClassLoader(SupportClasses.class.getName() + "$PrivateStaticFinalInnerClass");
        assertFalse(Modifier.isFinal(clazz.getModifiers()));
    }

    @Test
    public void subclassShouldNormallyGetAnAdditionalDeferConstructor() throws Exception {
        Class<?> clazz = loadWithMockClassLoader(SupportClasses.SubClass.class.getName());
        assertEquals("Original number of constructoprs",
                     1, SupportClasses.SubClass.class.getConstructors().length);
        assertEquals("Number of constructors in modified class",
                     2, clazz.getConstructors().length);
        assertNotNull("Defer-constructor expected",
                      clazz.getConstructor(IndicateReloadClass.class));
    }

    @Test
    public void shouldLoadClassWithMethodLowerThanJvmLimit() throws Exception {
        Class<?> clazz = loadWithMockClassLoader(ClassWithLargeMethods.MethodLowerThanLimit.class.getName());
        assertNotNull("Class has been loaded", clazz);
        // There should be no exception since method was not overridden
        clazz.getMethod("init").invoke(clazz);
    }

    @Test
    public void shouldLoadClassAndOverrideMethodGreaterThanJvmLimit() throws Exception {
        Class<?> clazz = loadWithMockClassLoader(ClassWithLargeMethods.MethodGreaterThanLimit.class.getName());
        assertNotNull("Class has been loaded", clazz);
        // There should be exception since method was overridden to satisfy JVM limit
        try {
            clazz.getMethod("init").invoke(clazz);
            fail("Overridden method should throw exception");
        } catch (Exception e) {
            Throwable cause = e.getCause();
            assertThat(cause).isInstanceOf(IllegalAccessException.class);
            assertThat(cause.getMessage()).contains("Method was too large and after instrumentation exceeded JVM limit");
        }
    }

    @Test
    public void shouldIgnoreSyntheticNonBridgeMethods() throws Throwable {

        final ClassPool classPool = new ClassPool(true);
        CtClass ctClass = prepareClassesForTest(classPool, "return;");

        new ClassMockTransformer().transform(ctClass);

        runTestWithNewClassLoader(classPool, ShouldIgnoreSyntheticNonBridgeMethods.class.getName());
    }

    @Test
    public void shouldIgnoreCallToSyntheticNonBridgeMethods() throws Throwable {
        final ClassPool classPool = new ClassPool(true);
        CtClass ctClass = prepareClassesForTest(classPool, "powermock.test.support.MainMockTransformerTestSupport.CallSpy.registerMethodCall($1);");

        new ClassMockTransformer().transform(ctClass);

        runTestWithNewClassLoader(classPool, ShouldIgnoreCallToSyntheticNonBridgeMethods.class.getName());
    }

    @Test
    public void shouldIgnoreCallToSyntheticField() throws Throwable {
        final ClassPool classPool = new ClassPool(true);
        CtClass ctClass = prepareClassesForFieldTests(classPool);

        new ClassMockTransformer().transform(ctClass);

        runTestWithNewClassLoader(classPool, ShouldIgnoreCallToSyntheticField.class.getName());
    }

    @Test
    public void shouldModifyBridgeMethods() throws Throwable {
        final ClassPool classPool = new ClassPool(true);
        addCallInterceptorToMockGateway(classPool);

        CtClass ctClass = classPool.get(SubclassWithBridgeMethod.class.getName());
        new ClassMockTransformer().transform(ctClass);

        runTestWithNewClassLoader(classPool, ShouldModifyBridgeMethods.class.getName());
    }


    private CtClass prepareClassesForFieldTests(ClassPool classPool) throws NotFoundException, CannotCompileException {
        addCallInterceptorToMockGateway(classPool);

        CtClass ctClass = classPool.getCtClass(SuperClassWithObjectMethod.class.getName());
        addSyntheticField(classPool, ctClass);
        insertCallSyntheticMethod(ctClass);
        return ctClass;
    }

    private void insertCallSyntheticMethod(CtClass ctClass) throws CannotCompileException {
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


    private Class<?> loadWithMockClassLoader(String className) throws ClassNotFoundException {
        MockClassLoader loader = new MockClassLoader(new String[]{MockClassLoader.MODIFY_ALL_CLASSES});
        loader.setMockTransformerChain(Collections.<MockTransformer>singletonList(new ClassMockTransformer()));
        return Class.forName(className, true, loader);
    }

    private CtClass prepareClassesForTest(ClassPool classPool,
                                          String body) throws NotFoundException, CannotCompileException {
        addCallInterceptorToMockGateway(classPool);

        CtClass ctClass = classPool.getCtClass(SuperClassWithObjectMethod.class.getName());
        addSyntheticMethod(classPool, ctClass, body);
        return ctClass;
    }

    private void runTestWithNewClassLoader(ClassPool classPool, String name) throws Throwable {
        Loader loader = new Loader(classPool);
        loader.run(name, new String[0]);
    }

    private void addCallInterceptorToMockGateway(ClassPool classPool) throws NotFoundException, CannotCompileException {
        CtClass mockGetawayClass = classPool.get(MockGateway.class.getName());
        for (CtMethod method : mockGetawayClass.getMethods()) {
            String methodName = method.getName();
            if (methodName.equals("methodCall")) {
                method.insertBefore(
                        "powermock.test.support.MainMockTransformerTestSupport.CallSpy.registerMethodCall("
                                + "methodName"
                                + ");"
                );
            }else if(methodName.equals("fieldCall")){
                method.insertBefore(
                        "powermock.test.support.MainMockTransformerTestSupport.CallSpy.registerFieldCall("
                                + "fieldName"
                                + ");"
                );
            }
        }
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
