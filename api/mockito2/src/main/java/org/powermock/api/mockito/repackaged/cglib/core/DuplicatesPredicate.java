/*
 *  Copyright (c) 2007 Mockito contributors
 *  This program is made available under the terms of the MIT License.
 */
package org.powermock.api.mockito.repackaged.cglib.core;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class DuplicatesPredicate implements Predicate {
    private Set unique = new HashSet();

    public boolean evaluate(Object arg) {
        return unique.add(MethodWrapper.create((Method)arg));
    }
}
