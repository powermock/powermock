/*
 * Copyright 2014 the original author or authors.
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
package powermock.modules.test.mockito.junit4.delegate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import samples.powermockito.junit4.whennew.WhenNewCases;

public class WhenNewCaseMethod {

    final Method testMethod;

    public WhenNewCaseMethod(Method testMethod) {
        this.testMethod = testMethod;
    }

    @Override
    public String toString() {
        return testMethod.getName();
    }

    public void runTest() throws Throwable {
        try {
            testMethod.invoke(new WhenNewCases());
        } catch (InvocationTargetException ex) {
            throw ex.getTargetException();
        }
    }

    public boolean nullPointerIsExpected() {
        return NullPointerException.class
                == testMethod.getAnnotation(Test.class).expected();
    }

    public static WhenNewCaseMethod[] values() {
        Method[] methods = WhenNewCases.class.getDeclaredMethods();
        List<WhenNewCaseMethod> values
                = new ArrayList<WhenNewCaseMethod>(methods.length);
        for (Method m : methods) {
            if (m.isAnnotationPresent(Test.class)) {
                values.add(new WhenNewCaseMethod(m));
            }
        }
        return values.toArray(new WhenNewCaseMethod[values.size()]);
    }
}
