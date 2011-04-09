/*
 * Copyright 2009 the original author or authors.
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
package samples.junit4.equalswithgetclass;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.MockGateway;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.equalswithgetclass.EqualsWithGetClass;

import static org.junit.Assert.assertTrue;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;

/**
 * Demonstrates that PowerMock ignores the call to getClass by default.
 * Demonstrates that <a
 * href="http://code.google.com/p/powermock/issues/detail?id=190">issue 190</a>
 * is resolved.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(EqualsWithGetClass.class)
public class EqualsWithGetClassTest {

    @Test
    public void callingGetClassOnAMockWorksWhenTheCallWasUnexpected() throws Exception {
        EqualsWithGetClass mock1 = createMock(EqualsWithGetClass.class);
        replayAll();
        assertTrue(mock1.getClass().getName().startsWith(EqualsWithGetClass.class.getName()));
    }

    @Test(expected = AssertionError.class)
    public void callingGetClassOnAMockFailsWhenTheCallWasUnexpectedAndMockStandardMethodsIsSet() throws Exception {
        MockGateway.MOCK_GET_CLASS_METHOD = true;
        try {
            EqualsWithGetClass mock1 = createMock(EqualsWithGetClass.class);
            replayAll();
            mock1.getClass();
        } finally {
            MockGateway.MOCK_GET_CLASS_METHOD = false;
        }
    }
}
