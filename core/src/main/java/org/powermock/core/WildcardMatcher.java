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
package org.powermock.core;

import java.util.Collection;

/**
 * Wildcard matcher.
 */
public class WildcardMatcher {

    private static final char WILDCARD = '*';

    /**
     * Performs a wildcard matching for the text and pattern provided.
     * 
     * @param text
     *            the text to be tested for matches.
     * 
     * @param pattern
     *            the pattern to be matched for. This can contain the wildcard
     *            character '*' (asterisk).
     * 
     * @return <tt>true</tt> if a match is found, <tt>false</tt> otherwise.
     */
    public static boolean matches(String text, String pattern) {
        if (text == null) {
            throw new IllegalArgumentException("text cannot be null");
        }

        text += '\0';
        pattern += '\0';

        int N = pattern.length();

        boolean[] states = new boolean[N + 1];
        boolean[] old = new boolean[N + 1];
        old[0] = true;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            states = new boolean[N + 1]; // initialized to false
            for (int j = 0; j < N; j++) {
                char p = pattern.charAt(j);

                // hack to handle *'s that match 0 characters
                if (old[j] && (p == WILDCARD))
                    old[j + 1] = true;

                if (old[j] && (p == c))
                    states[j + 1] = true;
                if (old[j] && (p == WILDCARD))
                    states[j] = true;
                if (old[j] && (p == WILDCARD))
                    states[j + 1] = true;
            }
            old = states;
        }
        return states[N];

    }

    public static boolean matchesAny(Collection<String> patterns, String text) {
        for (String pattern : patterns) {
            if (matches(text, pattern)) {
                return true;
            }
        }
        return false;
    }

    public static boolean matchesAny(Iterable<String> patterns, String text) {
        for (String string : patterns) {
            if (matches(text, string)) {
                return true;
            }
        }
        return false;
    }

    public static boolean matchesAny(String[] patterns, String text) {
        for (String string : patterns) {
            if (matches(text, string)) {
                return true;
            }
        }
        return false;
    }
}
