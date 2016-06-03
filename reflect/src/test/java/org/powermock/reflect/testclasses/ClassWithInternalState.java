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

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

public class ClassWithInternalState {

    private static int staticState = 5;

    private static final int staticFinalState = 15;

    private static final Integer staticFinalStateInteger = 15;

    private int internalState = 0;

    private int anotherInternalState = -1;

    private final String finalString = "hello";

    private long internalLongState = 17;
    
	private String[] stringArray = new String[0];

    private Set<String> genericState = new HashSet<String>();

    private ClassWithPrivateMethods classWithPrivateMethods;

    public String getFinalString() {
        return finalString;
    }

    public void increaseInteralState() {
        internalState++;
    }

    public void decreaseInteralState() {
        internalState--;
    }

    public int getAnotherInternalState() {
        return anotherInternalState;
    }

    public static int getStaticState() {
        return staticState;
    }

    public static int getStaticFinalState() {
        return staticFinalState;
    }

    public static Integer getStaticFinalStateInteger() {
        return staticFinalStateInteger;
    }

    public ClassWithPrivateMethods getClassWithPrivateMethods() {
        return classWithPrivateMethods;
    }

    public long getInternalLongState() {
        return internalLongState;
    }

    public Set<String> getGenericState() {
        return genericState;
    }

    public static String methodWithArgument(InputStream inputStream) {
        return "";
    }
    
    public String[] getStringArray() {
		return stringArray;
	}
}
