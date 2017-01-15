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
package powermock.modules.test.mockito.junit4.delegate.parameterized;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.model.Statement;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import powermock.modules.test.mockito.junit4.delegate.SystemClassUserCases;
import powermock.modules.test.mockito.junit4.delegate.SystemClassUserMethod;
import samples.system.SystemClassUser;

/**
 * Demonstrates PowerMockito's ability to mock non-final and final system
 * classes. To mock a system class you need to prepare the calling class for
 * testing. I.e. let's say you're testing class A which interacts with
 * URLEncoder then you would do:
 *
 * <pre>
 *
 * &#064;PrepareForTest({A.class})
 *
 * </pre>
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Parameterized.class)
@PrepareForTest({SystemClassUserCases.class, SystemClassUser.class})
public class SystemClassUserTest {

    final Statement test;

    public SystemClassUserTest(final SystemClassUserMethod testCase) {
        test = new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try {
                    testCase.getMethod().invoke(new SystemClassUserCases());
                } catch (InvocationTargetException ex) {
                    throw ex.getTargetException();
                }
            }
        };
    }

    @Parameterized.Parameters(name = "{0}")
    public static List<?> paramValues() {
        List<Object[]> values = new ArrayList<Object[]>();
        for (SystemClassUserMethod tstCase : SystemClassUserMethod.values()) {
            values.add(new Object[]{tstCase});
        }
        return values;
    }

    @Test
    public void __() throws Throwable {
        test.evaluate();
    }
}
