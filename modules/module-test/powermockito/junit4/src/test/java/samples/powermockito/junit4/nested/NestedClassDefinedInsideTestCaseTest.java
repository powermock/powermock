/*
 * Copyright 2010 the original author or authors.
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
package samples.powermockito.junit4.nested;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest( { NestedClassDefinedInsideTestCaseTest.class })
public class NestedClassDefinedInsideTestCaseTest {

    @Test
    public void mocksNestedPrivateClassDefinedInsideTestCase() throws Exception {
        NestedPrivateClassDefinedInsideTestCase tested = mock(NestedPrivateClassDefinedInsideTestCase.class);
        when(tested.getValue()).thenReturn("something");

        assertEquals("something", tested.getValue());
    }

    @Test
    @Ignore("See issue 95")
    public void mocksNestedPrivateFinalClassDefinedInsideTestCase() throws Exception {
        NestedPrivateFinalClassDefinedInsideTestCase tested = mock(NestedPrivateFinalClassDefinedInsideTestCase.class);
        when(tested.getValue()).thenReturn("something");

        assertEquals("something", tested.getValue());
    }

    private class NestedPrivateClassDefinedInsideTestCase {
        public String getValue() {
            return "value";
        }
    }

    private final class NestedPrivateFinalClassDefinedInsideTestCase {
        public String getValue() {
            return "value";
        }
    }
}
