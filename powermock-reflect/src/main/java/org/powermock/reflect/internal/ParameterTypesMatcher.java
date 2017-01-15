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

package org.powermock.reflect.internal;

/**
 *
 */
class ParameterTypesMatcher {
    private boolean isVarArgs;
    private Class<?>[] expectedParameterTypes;
    private Class<?>[] actualParameterTypes;

    public ParameterTypesMatcher(boolean isVarArgs, Class<?>[] expectedParameterTypes, Class<?>... actualParameterTypes) {
        this.isVarArgs = isVarArgs;
        this.expectedParameterTypes = expectedParameterTypes;
        this.actualParameterTypes = actualParameterTypes;
    }

    private boolean isRemainParamsVarArgs(int index, Class<?> actualParameterType) {
        return isVarArgs && index == expectedParameterTypes.length - 1
                       && actualParameterType.getComponentType().isAssignableFrom(expectedParameterTypes[index]);
    }

    private boolean isParameterTypesNotMatch(Class<?> actualParameterType, Class<?> expectedParameterType) {
        if (actualParameterType == null){
            return false;
        }
        if (expectedParameterType == null){
           return false;
        }
        return !actualParameterType.isAssignableFrom(expectedParameterType);
    }

    public boolean match() {
        assertParametersTypesNotNull();
        if (isParametersLengthMatch()) {
            return false;
        } else {
            return isParametersMatch();
        }
    }

    private boolean isParametersLengthMatch() {return expectedParameterTypes.length != actualParameterTypes.length;}

    private void assertParametersTypesNotNull() {
        if (expectedParameterTypes == null || actualParameterTypes == null) {
            throw new IllegalArgumentException("parameter types cannot be null");
        }
    }

    private Boolean isParametersMatch() {
        for (int index = 0; index < expectedParameterTypes.length; index++) {
            final Class<?> actualParameterType = actualParameterTypes[index];
            if (isRemainParamsVarArgs(index, actualParameterType)) {
                return true;
            } else {
                final Class<?> expectedParameterType = expectedParameterTypes[index];
                if (isParameterTypesNotMatch(actualParameterType, expectedParameterType)) {
                    return false;
                }
            }
        }
        return true;
    }
}
