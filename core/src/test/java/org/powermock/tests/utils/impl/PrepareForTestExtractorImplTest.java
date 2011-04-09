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
package org.powermock.tests.utils.impl;

import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.reflect.Whitebox;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for the {@link PrepareForTestExtractorImpl} class.
 * 
 */
public class PrepareForTestExtractorImplTest {

    /**
     * Makes sure that issue <a
     * href="http://code.google.com/p/powermock/issues/detail?id=81">81</a> is
     * solved.
     */
    @Test
    public void assertThatInterfacesWorks() throws Exception {
        final PrepareForTestExtractorImpl tested = new PrepareForTestExtractorImpl();
        final Set<String> hashSet = new HashSet<String>();
        Whitebox.invokeMethod(tested, "addClassHierarchy", hashSet, Serializable.class);
        assertEquals(1, hashSet.size());
        assertEquals(Serializable.class.getName(), hashSet.iterator().next());
    }

    /**
     * Makes sure that issue <a
     * href="http://code.google.com/p/powermock/issues/detail?id=111">111</a> is
     * solved.
     */
    @Test
    public void shouldFindClassesToPrepareForTestInTheWholeClassHierarchy() throws Exception {
        final PrepareForTestExtractorImpl tested = new PrepareForTestExtractorImpl();
        final String[] classesToPrepare = tested.getTestClasses(PrepareForTestDemoClass.class);
        assertEquals(8, classesToPrepare.length);
        final String classPrefix = "Class";
        final List<String> classesToPrepareList = Arrays.asList(classesToPrepare);
        final int noOfClassesExplicitlyPrepared = 5;
        for (int i = 0; i < noOfClassesExplicitlyPrepared; i++) {
            final String className = classPrefix + (i + 1);
            assertTrue("Should prepare " + className, classesToPrepareList.contains(className));
        }
        // We assert that the implicit classes (the test cases) are also added.
        assertTrue(classesToPrepareList.contains(PrepareForTestDemoClass.class.getName()));
        assertTrue(classesToPrepareList.contains(PrepareForTestDemoClassParent.class.getName()));
        assertTrue(classesToPrepareList.contains(PrepareForTestDemoClassGrandParent.class.getName()));
    }
}

@PrepareForTest(fullyQualifiedNames = { "Class1", "Class2" })
class PrepareForTestDemoClass extends PrepareForTestDemoClassParent {

}

@PrepareForTest(fullyQualifiedNames = { "Class3" })
class PrepareForTestDemoClassParent extends PrepareForTestDemoClassGrandParent {

}

@PrepareForTest(fullyQualifiedNames = { "Class4", "Class5" })
class PrepareForTestDemoClassGrandParent {

}