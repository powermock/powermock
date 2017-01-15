/*
 *   Copyright 2016 the original author or authors.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package org.powermock.utils;

import java.lang.reflect.Array;

/**
 *
 */
public class ArrayUtil {

    public static <T> T[] addAll(T[] array1, T[] array2) {
        if (isEmpty(array1)) {
            return clone(array2);
        } else if (isEmpty(array2)) {
            return clone(array1);
        }
        int newLength = array1.length + array2.length;
        T[] joinedArray = createNewArrayWithSameType(array1, newLength);
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    private static <T> boolean isEmpty(T[] a) {
        return a == null || a.length == 0;
    }

    private static <T> T[] clone(T[] array) {
        if (array == null) {
            return null;
        }
        return array.clone();
    }

    @SuppressWarnings("unchecked")
    private static <T> T[] createNewArrayWithSameType(T[] arrayPrototype, int newLength) {
        return (T[]) Array.newInstance(arrayPrototype[0].getClass(), newLength);
    }
}
