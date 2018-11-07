/*
 * Copyright 2013 the original author or authors.
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
package org.powermock.modules.junit4.internal.impl;

import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.Parameterized;
import org.easymock.EasyMock;
import org.powermock.tests.utils.PowerMockTestNotifier;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

@RunWith(Parameterized.class)
public class PowerMockRunNotifierTest {

    /*
     * Dummy test data ...
     */
    private static final Object[][] testDataAlternatives;
    static {
        Description description =
                Description.createSuiteDescription(Description.class);
        testDataAlternatives = new Object[][] {
            {description},
            {new Failure(description, new Throwable())},
            {new RunListener()},
            {new Result()}
        };
    }

    /**
     * Parameter data
     */
    @Parameterized.Parameter(0) public Method method;

    @Parameterized.Parameters(name = "{0}")
    public static List<?> runNotifierMethods() {
        List<Object[]> methods = new ArrayList<Object[]>();
        for (Method m : RunNotifier.class.getMethods()) {
            if (Object.class != m.getDeclaringClass()) {
                methods.add(new Object[] {m});
            }
        }
        return methods;
    }

    @Test
    public void verifyBackendRunNotifierIsProperlyNotified() throws Exception {
        Object[] testData = retrieveSuitableTestData();
        RunNotifier backendRunNotifierMock = createMock(RunNotifier.class);
        method.invoke(backendRunNotifierMock, testData);
        replay(backendRunNotifierMock);
        method.invoke(new PowerMockRunNotifier(
                backendRunNotifierMock,
                EasyMock.<PowerMockTestNotifier, PowerMockTestNotifier>createNiceMock(PowerMockTestNotifier.class),
                new Method[0]),
                testData);
        verify(backendRunNotifierMock);
    }

    private Object[] retrieveSuitableTestData() {
        Class<?>[] paramTypes = method.getParameterTypes();
        if (0 == paramTypes.length) {
            return null;
        }
        for (Object[] testData : testDataAlternatives) {
            if (paramTypes[0] == testData[0].getClass()) {
                return testData;
            }
        }
        throw new Error("No test-data available for method " + method);
    }
}
