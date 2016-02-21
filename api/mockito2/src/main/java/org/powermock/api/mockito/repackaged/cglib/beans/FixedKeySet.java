/*
 *  Copyright (c) 2007 Mockito contributors
 *  This program is made available under the terms of the MIT License.
 */
package org.powermock.api.mockito.repackaged.cglib.beans;

import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public /* need it for class loading  */ class FixedKeySet extends AbstractSet {
    private Set set;
    private int size;

    public FixedKeySet(String[] keys) {
        size = keys.length;
        set = Collections.unmodifiableSet(new HashSet(Arrays.asList(keys)));
    }

    public Iterator iterator() {
        return set.iterator();
    }

    public int size() {
        return size;
    }
}
