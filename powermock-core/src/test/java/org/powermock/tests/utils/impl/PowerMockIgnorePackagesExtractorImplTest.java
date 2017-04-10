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
import org.powermock.core.classloader.annotations.PowerMockIgnore;

import static org.junit.Assert.assertEquals;

public class PowerMockIgnorePackagesExtractorImplTest {

    /**
     * Makes sure that issue <a
     * href="http://code.google.com/p/powermock/issues/detail?id=150">150</a> is
     * solved.
     */
    @Test
    public void shouldFindIgnorePackagesInTheWholeClassHierarchy() throws Exception {
        final PowerMockIgnorePackagesExtractorImpl tested = new PowerMockIgnorePackagesExtractorImpl();
        final String[] packagesToIgnore = tested.getPackagesToIgnore(IgnoreAnnotatedDemoClass.class);
        assertEquals(7, packagesToIgnore.length);
        assertEquals("ignore0", packagesToIgnore[0]);
        assertEquals("ignore1", packagesToIgnore[1]);
        assertEquals("ignore2", packagesToIgnore[2]);
        assertEquals("ignore3", packagesToIgnore[3]);
        assertEquals("ignore4", packagesToIgnore[4]);
        assertEquals("ignore5", packagesToIgnore[5]);
        assertEquals("ignore6", packagesToIgnore[6]);
    }

    @PowerMockIgnore( { "ignore0", "ignore1" })
    private class IgnoreAnnotatedDemoClass extends IgnoreAnnotatedDemoClassParent implements IgnoreAnnotatedDemoInterfaceParent2 {

    }

    @PowerMockIgnore("ignore2")
    private class IgnoreAnnotatedDemoClassParent extends IgnoreAnnotatedDemoClassGrandParent {

    }

    @PowerMockIgnore("ignore3")
    private class IgnoreAnnotatedDemoClassGrandParent implements IgnoreAnnotatedDemoInterfaceParent1 {

    }

    @PowerMockIgnore("ignore4")
    private interface IgnoreAnnotatedDemoInterfaceParent1 extends IgnoreAnnotatedDemoInterfaceGrandParent {

    }

    @PowerMockIgnore("ignore5")
    private interface IgnoreAnnotatedDemoInterfaceGrandParent {

    }

    @PowerMockIgnore("ignore6")
    private interface IgnoreAnnotatedDemoInterfaceParent2 extends IgnoreAnnotatedDemoInterfaceGrandParent {
        // Test diamond interface hierarchies
    }
}
