/*
 * Copyright 2015 the original author or authors.
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

import java.util.Arrays;
import org.junit.Test;
import org.powermock.core.classloader.MockClassLoader;
import powermock.test.support.MainMockTransformerTestSupport.SupportClasses;

import org.powermock.core.IndicateReloadClass;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class TestClassTransformerTest {

    @Test
    public void subclassShouldNormallyGetPublicAdditionalDeferConstructor() throws Exception {
        new MainMockTransformerTest()
                .subclassShouldNormallyGetAnAdditionalDeferConstructor();
    }

    @Test
    public void subclassTestClassShouldNotGetPublicDeferConstructor() throws Exception {
        MockClassLoader mockClassLoader = new MockClassLoader(new String[] { MockClassLoader.MODIFY_ALL_CLASSES });
        mockClassLoader.setMockTransformerChain(Arrays.asList(new MainMockTransformer(), TestClassTransformer
                .forTestClass(SupportClasses.SubClass.class)
                .removesTestMethodAnnotation(Test.class)
                .fromAllMethodsExcept(SupportClasses.SubClass.class.getMethods()[0])));
        final Class<?> clazz = Class.forName(SupportClasses.SubClass.class.getName(), true, mockClassLoader);
        assertEquals("Original number of constructoprs",
                1, SupportClasses.SubClass.class.getConstructors().length);
        try {
            fail("A public defer-constructor is not expected: "
                    + clazz.getConstructor(IndicateReloadClass.class));
        } catch (NoSuchMethodException expected) {}
        assertEquals("Number of (public) constructors in modified class",
                1, clazz.getConstructors().length);

        assertNotNull("But there should still be a non-public defer constructor!",
                clazz.getDeclaredConstructor(IndicateReloadClass.class));
    }
}
