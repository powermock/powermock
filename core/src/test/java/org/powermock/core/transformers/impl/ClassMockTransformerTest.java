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

import javassist.Modifier;
import org.junit.Test;
import org.powermock.core.IndicateReloadClass;
import org.powermock.core.classloader.MockClassLoader;
import org.powermock.core.transformers.MockTransformer;
import powermock.test.support.ClassWithLargeMethods;
import powermock.test.support.MainMockTransformerTestSupport.SupportClasses;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class ClassMockTransformerTest {
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
            assertThat(cause, instanceOf(IllegalAccessException.class));
            assertThat(cause.getMessage(),
                    containsString("Method was too large and after instrumentation exceeded JVM limit"));
        }
    }

    private Class<?> loadWithMockClassLoader(String className) throws ClassNotFoundException {
        MockClassLoader loader = new MockClassLoader(new String[]{MockClassLoader.MODIFY_ALL_CLASSES});
        loader.setMockTransformerChain(Collections.<MockTransformer>singletonList(new ClassMockTransformer()));
        return Class.forName(className, true, loader);
    }
}
