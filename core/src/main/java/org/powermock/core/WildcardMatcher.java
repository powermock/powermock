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
package org.powermock.core;

import java.util.Collection;

/**
 * Implementation borrowed from http://www.adarshr.com/papers/wildcard.
 */
public class WildcardMatcher {

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
        // Create the cards by splitting using a RegEx. If more speed
        // is desired, a simpler character based splitting can be done.
        String[] cards = pattern.split("\\*");

        // Iterate over the cards.
        for (String card : cards) {
            int idx = text.indexOf(card);

            // Card not detected in the text.
            if (idx == -1) {
                return false;
            }

            // Move ahead, towards the right of the text.
            text = text.substring(idx + card.length());
        }
        return true;
    }

    public static boolean matchesAny(Collection<String> collectionOfTextToMatch, String pattern) {
        for (String string : collectionOfTextToMatch) {
            if (matches(pattern, string)) {
                return true;
            }
        }
        return false;
    }

    public static boolean matchesAny(Iterable<String> collectionOfTextToMatch, String pattern) {
        for (String string : collectionOfTextToMatch) {
            if (matches(pattern, string)) {
                return true;
            }
        }
        return false;
    }

    public static boolean matchesAny(String[] collectionOfTextToMatch, String pattern) {
        for (String string : collectionOfTextToMatch) {
            if (matches(pattern, string)) {
                return true;
            }
        }
        return false;
    }
}
