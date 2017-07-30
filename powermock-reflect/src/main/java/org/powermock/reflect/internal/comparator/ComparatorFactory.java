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

package org.powermock.reflect.internal.comparator;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Comparator;

/**
 *  This comparator factory is used to create Comparators for
 *  {@link org.powermock.reflect.Whitebox} which are used to find best candidates
 *  for constructor and method invocation.
 *  @see org.powermock.reflect.internal.WhiteboxImpl#getBestMethodCandidate(Class, String, Class[], boolean)
 *  @see org.powermock.reflect.internal.WhiteboxImpl#getBestCandidateConstructor(Class, Class[], Object[])
 */
public class ComparatorFactory {

    private ComparatorFactory() {
    }

    public static Comparator<Constructor> createConstructorComparator(){
        return new ConstructorComparator(new ParametersComparator());
    }

    public static Comparator<Method> createMethodComparator(){
        return new MethodComparator(new ParametersComparator());
    }


    public static class ConstructorComparator implements Comparator<Constructor> {
        private final ParametersComparator parametersComparator;

        private ConstructorComparator(ParametersComparator parametersComparator) {

            this.parametersComparator = parametersComparator;
        }

        @Override
        public int compare(Constructor constructor1, Constructor constructor2) {
            final Class<?>[] parameters1 = constructor1.getParameterTypes();
            final Class<?>[] parameters2 = constructor2.getParameterTypes();
            return parametersComparator.compare(parameters1,parameters2);
        }
    }

    /**
     *
     */
    public static class MethodComparator implements Comparator<Method> {
        private final ParametersComparator parametersComparator;

        private MethodComparator(ParametersComparator parametersComparator) {

            this.parametersComparator = parametersComparator;
        }

        @Override
        public int compare(Method m1, Method m2) {
            final Class<?>[] typesMethod1 = m1.getParameterTypes();
            final Class<?>[] typesMethod2 = m2.getParameterTypes();
            return parametersComparator.compare(typesMethod1, typesMethod2);

        }
    }

    private static class ParametersComparator implements Comparator<Class[]>{

        @Override
        public int compare(Class[] params1, Class[] params2) {
            final int size = params1.length;
            for (int i = 0; i < size; i++) {
                Class<?> type1 = params1[i];
                Class<?> type2 = params2[i];
                if (!type1.equals(type2)) {
                    if (type1.isAssignableFrom(type2)) {
                        if (!type1.isArray() && type2.isArray() && !type1.equals(Object.class)) {
                            return -1;
                        }
                        return 1;
                    } else {
                        if (type1.isArray() && !type2.isArray() && !type2.equals(Object.class)) {
                            return 1;
                        }
                        return -1;
                    }
                }
            }
            return 0;
        }
    }
}
