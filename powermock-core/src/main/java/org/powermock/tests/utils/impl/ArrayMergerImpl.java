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

import org.powermock.tests.utils.ArrayMerger;

import java.lang.reflect.Array;

/**
 * The default implementation of the {@link ArrayMerger} interface.
 */
public class ArrayMergerImpl implements ArrayMerger {

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] mergeArrays(Class<T> type, T[]... arraysToMerge) {
		if (arraysToMerge == null || arraysToMerge.length == 0) {
			return (T[]) Array.newInstance(type, 0);
		}

		int size = 0;
		for (T[] array : arraysToMerge) {
			if (array != null) {
				size += array.length;
			}
		}

		final T[] finalArray = (T[]) Array.newInstance(type, size);

		int lastIndex = 0;
		for (final T[] currentArray : arraysToMerge) {
			if (currentArray != null) {
				final int currentArrayLength = currentArray.length;
				System.arraycopy(currentArray, 0, finalArray, lastIndex, currentArrayLength);
				lastIndex += currentArrayLength;
			}
		}

		return finalArray;
	}

}
