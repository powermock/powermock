/*
 * Copyright 2008 the original author or authors.
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

package org.powermock.reflect.testclasses;

@SuppressWarnings("unused")
public class ClassWithPrivateMethods {

	private boolean primitiveMethod(double value) {
		return true;
	}

	private boolean wrappedMethod(Double value) {
		return true;
	}

	private String methodWithPrimitiveIntAndString(int myInt, String aString) {
		return aString + Integer.toString(myInt);
	}

	private int methodWithPrimitiveAndWrappedInt(int myInt, Integer myInt2) {
		return myInt + myInt2;
	}

	private String evilConcatOfStrings(String[] strings) {
		String returnValue = "";
		for (String string : strings) {
			returnValue += string;
		}
		return returnValue;
	}

	private int varArgsMethod(int... ints) {
		int sum = 0;
		for (int i : ints) {
			sum += i;
		}
		return sum;
	}

	private ClassWithInternalState methodWithObjectArgument(ClassWithInternalState c) {
		return c;
	}

	private Class<? super ClassWithInternalState> methodWithClassArgument(Class<? super ClassWithInternalState> c) {
		return c;
	}

	private int varArgsMethod(int value) {
		return value * 2;
	}

	private int varArgsMethod2(int value, int... moreValues) {
		return value * 2 + moreValues.length;
	}
}
