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

import org.powermock.reflect.internal.comparator.ComparatorFactory;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *  This class search the best candidate in the given class to invoke constructor with given parameters.
 */
class CandidateConstructorSearcher<T> {
    private final Class<T> classThatContainsTheConstructorToTest;
    private final Class<?>[] argumentTypes;

    public CandidateConstructorSearcher(Class<T> classThatContainsTheConstructorToTest, Class<?>[] argumentTypes) {

        this.classThatContainsTheConstructorToTest = classThatContainsTheConstructorToTest;
        this.argumentTypes = argumentTypes;
    }
    
    public Constructor<T> findConstructor() {
        final Constructor<T>[] constructors = getConstructors();
        if (constructors.length == 0) {
            return null;
        }
        if (constructors.length == 1) {
            return constructors[0];
        } else {
            return findBestCandidate(constructors);

        }
    }

    private Constructor<T> findBestCandidate(Constructor<T>[] constructors) {
        //We've found overloaded constructor, we need to find the best one to invoke.
        Arrays.sort(constructors, ComparatorFactory.createConstructorComparator());
        return constructors[0];
    }

    @SuppressWarnings("unchecked")
    private Constructor<T>[] getConstructors() {

        try {
            Constructor<?>[] declaredConstructors = classThatContainsTheConstructorToTest.getDeclaredConstructors();
            List<Constructor<?>> constructors = new ArrayList<Constructor<?>>();
            for (Constructor<?> constructor : declaredConstructors) {
                if (argumentsApplied(constructor)) {
                    constructors.add(constructor);
                }
            }
            return constructors.toArray(new Constructor[constructors.size()]);
        } catch (Exception e) {
            return new Constructor[0];
        }

    }

    private boolean argumentsApplied(Constructor<?> constructor) {
        Class<?>[] constructorArgumentTypes = constructor.getParameterTypes();
        if (constructorArgumentTypes.length != argumentTypes.length) {
            return false;
        }

        for (int index = 0; index < argumentTypes.length; index++) {
            if (!constructorArgumentTypes[index].isAssignableFrom(argumentTypes[index])) {
                return false;
            }
        }

        return true;
    }


}
