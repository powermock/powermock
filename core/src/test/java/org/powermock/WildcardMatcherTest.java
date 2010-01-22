/*
 * Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.powermock;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.powermock.core.WildcardMatcher;

public class WildcardMatcherTest {

    @Test
    public void matchesWildcardOnBothEnds() throws Exception {
        assertTrue(WildcardMatcher.matches("org.mytest.java", "*.java*"));
    }

    @Test
    public void matchesWildcardSuffix() throws Exception {
        assertTrue(WildcardMatcher.matches("org.mytest.java", "*.java"));
    }

    @Test
    public void doesntMatchWildcardPrefix() throws Exception {
        assertFalse(WildcardMatcher.matches("org.mytest.java", ".java*"));
    }

    @Test
    public void noWildcardCardPrefix() throws Exception {
        assertFalse(WildcardMatcher.matches("org.mytest.java", ".java"));
    }
    @Test
    public void exactMatch() throws Exception {
        assertTrue(WildcardMatcher.matches("org.mytest.java", "org.mytest.java"));
    }
}
