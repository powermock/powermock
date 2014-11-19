/*
 * Copyright 2013 the original author or authors.
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
package org.powermock.core.testlisteners;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.AfterClass;
import org.junit.Test;
import org.powermock.core.testlisteners.GlobalNotificationBuildSupport.Callback;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * The design of this test-class does only allow it to be run once per JVM
 * (or more accurately "per classloader", in case the test-class is redefined
 * by different classloaders in some sort of test suite),
 * because it will test some class instanciation, which can only occur one per
 * class.
 */
public class GlobalNotificationBuildSupportTest {

    static boolean initiationOfNormalClassIsUnderWay;

    static final Callback mockCallback = mock(Callback.class);

    static class NormalClass {
        static {
            initiationOfNormalClassIsUnderWay = true;
            GlobalNotificationBuildSupport.testClassInitiated(NormalClass.class);
        }

        NormalClass(String dummy) {
            GlobalNotificationBuildSupport.testInstanceCreated(this);
        }

        public NormalClass() {
            this("dummy");
            GlobalNotificationBuildSupport.testInstanceCreated(this);
        }
    }

    static class SubClass extends NormalClass {
        public SubClass() {
            super("dummy");
        }
        public SubClass(String dummy) {
        }
    }

    private String nestedClassName(String localName) {
        return GlobalNotificationBuildSupportTest.class.getName() + "$" + localName;
    }

    private void assertNotificationOf(NormalClass normalInstance) {
        verify(mockCallback).testInstanceCreated(normalInstance);
        verifyNoMoreInteractions(mockCallback);
    }

    @Test
    public void normalClassCreation() {

        // Given
        assertFalse("Initiation of NormalClass must not yet have commenced",
                initiationOfNormalClassIsUnderWay);
        GlobalNotificationBuildSupport.prepareTestSuite(
                nestedClassName("NormalClass"), mockCallback);

        /* Nothing must have happened so far ... */
        verifyNoMoreInteractions(mockCallback);

        // When
        final NormalClass normalInstance = new NormalClass();

        // Then verify life-cycle callbacks on NormalClass
        verify(mockCallback).suiteClassInitiated(NormalClass.class);

        // Then notifications of created instances are expected ...
        assertNotificationOf(normalInstance);
        assertNotificationOf(new NormalClass());
        assertNotificationOf(new NormalClass());
        assertNotificationOf(new NormalClass("dummy"));
        assertNotificationOf(new SubClass("dummy"));
        assertNotificationOf(new NormalClass("dummy"));
        assertNotificationOf(new SubClass("dummy"));
        assertNotificationOf(new NormalClass());

        // Tear-down
        GlobalNotificationBuildSupport.closeTestSuite(NormalClass.class);
        new NormalClass("dummy").toString();
        new SubClass().hashCode();
        verifyNoMoreInteractions(mockCallback); // Creation should no longer have any affect
    }

    /**
     * Tests some ConcurrentHashMap functionality that
     * {@link GlobalNotificationBuildSupport#closePendingTestSuites(java.lang.String)}
     * depends on.
     */
    @Test
    public void removeAllFromConcurrentHashMap() {
        ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<String, Object>();
        final Object value = new Object();
        map.put("foo", value);
        map.put("bar", value);
        assertEquals("Size of concurrent hashmap", 2, map.size());
        Collection<?> valueToRemove = java.util.Collections.singleton(value);
        map.values().removeAll(valueToRemove);
        assertEquals("Size of concurrent hashmap after removal of values",
                0, map.size());
    }

    @AfterClass
    public static void closeTestSuite() {
        GlobalNotificationBuildSupport.closeTestSuite(NormalClass.class);
        GlobalNotificationBuildSupport.closePendingTestSuites(mockCallback);
    }
}
