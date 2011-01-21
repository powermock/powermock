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
import org.powermock.core.classloader.MockClassLoader;
import org.powermock.core.transformers.MockTransformer;
import powermock.test.support.MainMockTransformerTestSupport.SupportClasses;

import java.util.Collections;

import static org.junit.Assert.assertFalse;

public class MainMockTransformerTest {
    /**
     * This tests that a inner 'public static final class' can be modified to drop the final modifier. Fixes <a
     * href="http://code.google.com/p/powermock/issues/detail?id=95">Issue 95</a>.
     */
    @Test
    public void staticFinalInnerClassesShouldBecomeNonFinal() throws Exception {
        MockClassLoader mockClassLoader = new MockClassLoader(new String[] { MockClassLoader.MODIFY_ALL_CLASSES });
        mockClassLoader.setMockTransformerChain(Collections.<MockTransformer> singletonList(new MainMockTransformer()));
        Class<?> clazz = Class.forName(SupportClasses.StaticFinalInnerClass.class.getName(), true, mockClassLoader);
        assertFalse(Modifier.isFinal(clazz.getModifiers()));
    }

    /**
     * This tests that a inner 'public final class' can be modified to drop the final modifier. Fixes <a
     * href="http://code.google.com/p/powermock/issues/detail?id=95">Issue 95</a>.
     */
    @Test
    public void finalInnerClassesShouldBecomeNonFinal() throws Exception {
        MockClassLoader mockClassLoader = new MockClassLoader(new String[] { MockClassLoader.MODIFY_ALL_CLASSES });
        mockClassLoader.setMockTransformerChain(Collections.<MockTransformer> singletonList(new MainMockTransformer()));
        Class<?> clazz = Class.forName(SupportClasses.FinalInnerClass.class.getName(), true, mockClassLoader);
        assertFalse(Modifier.isFinal(clazz.getModifiers()));
    }

    /**
     * This tests that a inner 'enum' can be modified to drop the final modifier. Fixes <a
     * href="http://code.google.com/p/powermock/issues/detail?id=95">Issue 95</a>.
     */
    @Test
    public void enumClassesShouldBecomeNonFinal() throws Exception {
        MockClassLoader mockClassLoader = new MockClassLoader(new String[] { MockClassLoader.MODIFY_ALL_CLASSES });
        mockClassLoader.setMockTransformerChain(Collections.<MockTransformer> singletonList(new MainMockTransformer()));
        Class<?> clazz = Class.forName(SupportClasses.EnumClass.class.getName(), true, mockClassLoader);
        assertFalse(Modifier.isFinal(clazz.getModifiers()));
    }

    @Test
    public void privateInnerClassesShouldBecomeNonFinal() throws Exception {
        MockClassLoader mockClassLoader = new MockClassLoader(new String[] { MockClassLoader.MODIFY_ALL_CLASSES });
        mockClassLoader.setMockTransformerChain(Collections.<MockTransformer> singletonList(new MainMockTransformer()));
        final Class<?> clazz = Class.forName(SupportClasses.class.getName() + "$PrivateStaticFinalInnerClass", true, mockClassLoader);
        assertFalse(Modifier.isFinal(clazz.getModifiers()));
    }
}
