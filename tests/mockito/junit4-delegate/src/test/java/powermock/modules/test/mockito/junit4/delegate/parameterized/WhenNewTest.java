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

import org.powermock.core.classloader.annotations.PrepareForTest;
import samples.classwithinnermembers.ClassWithInnerMembers;
import samples.expectnew.ExpectNewDemo;
import samples.newmocking.MyClass;

import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import powermock.modules.test.mockito.junit4.delegate.WhenNewCaseMethod;
import samples.powermockito.junit4.whennew.WhenNewCases;

@PrepareForTest({MyClass.class, ExpectNewDemo.class, ClassWithInnerMembers.class, DataInputStream.class,
        WhenNewCases.class})
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Parameterized.class)
public class WhenNewTest {

    @Rule
    public final ExpectedException ee = ExpectedException.none();

    @Parameterized.Parameter(0)
    public WhenNewCaseMethod whenNewCase;

    @Test
    public void test() throws Throwable {
        if (whenNewCase.nullPointerIsExpected()) {
            ee.expect(NullPointerException.class);
        }
        whenNewCase.runTest();
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> whenNewCases() {
        WhenNewCaseMethod[] cases = WhenNewCaseMethod.values();
        List<Object[]> paramValues = new ArrayList<Object[]>(cases.length);
        for (WhenNewCaseMethod each : cases) {
            paramValues.add(new Object[]{each});
        }
        return paramValues;
    }
}
