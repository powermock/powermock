/*
 * Copyright 2014 the original author or authors.
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
package powermock.modules.test.mockito.junit4.delegate.parameterized;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.powermock.reflect.Whitebox;
import samples.finalmocking.FinalDemo;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;

/**
 * Test class to demonstrate non-static final mocking with Mockito and PowerMock
 * annotations.
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Parameterized.class)
@PrepareForTest(FinalDemo.class)
public class MockFinalUsingAnnotationsTest {

    @Mock
    private FinalDemo usingMockitoMockAnnotation;

    @SuppressWarnings("deprecation")
    @org.mockito.MockitoAnnotations.Mock
    private FinalDemo usingDeprecatedMockitoMockAnnotation;

    @SuppressWarnings("deprecation")
    @org.powermock.core.classloader.annotations.Mock
    private FinalDemo usingPowerMockMockAnnotation;

    @Parameterized.Parameter(0)
    public MockField field2test;

    @Test
    public void testMockFinal() throws Exception {
        final String argument = "hello";
        System.out.println(field2test);
        FinalDemo mockedFinal = field2test.inTest(this);
        assertNull(mockedFinal.say(argument));
        verify(mockedFinal).say(argument);
    }

    @Parameterized.Parameters(name = "{0}")
    public static List<?> vals() {
        MockField[] mockFields = MockField.values();
        List<Object[]> vals = new ArrayList<Object[]>(mockFields.length);
        for (MockField each : mockFields) {
            vals.add(new Object[]{each});
        }
        return vals;
    }

    enum MockField {

        usingMockitoMockAnnotation,
        usingDeprecatedMockitoMockAnnotation,
        usingPowerMockMockAnnotation;

        <T> T inTest(Object test) {
            try {
                return (T) Whitebox.getInternalState(test, name());
            } catch (Exception ex) {
                throw new Error(ex);
            }
        }
    }
}
