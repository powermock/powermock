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
package samples.powermockito.junit4.getclass;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.powermock.api.mockito.PowerMockito.mock;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.MockGateway;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import samples.expectnew.ExpectNewDemo;

/**
 * Assert that "getClass" on an object works correctly on objects
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(ExpectNewDemo.class)
public class GetClassTest {

    @Test
    public void getClassReturnsTheCorrectClassForNewInstancesOfClassesPrepareForTest() throws Exception {
        ExpectNewDemo instance = new ExpectNewDemo();
        assertEquals(ExpectNewDemo.class, instance.getClass());
    }

    @Test
    public void getClassReturnsTheCorrectClassForMocksPrepareForTest() throws Exception {
        ExpectNewDemo instance = mock(ExpectNewDemo.class);
        assertNotNull(instance.getClass());
    }

    @Test
    public void getClassReturnsNullForMocksPreparedForTestWhenMockingOfGetClassAllowed() throws Exception {
        MockGateway.MOCK_GET_CLASS_METHOD = true;
        ExpectNewDemo instance = mock(ExpectNewDemo.class);
        try {
            assertNull(instance.getClass());
        } finally {
            // Make sure we reset to the default for subsequent tests.
            MockGateway.MOCK_GET_CLASS_METHOD = false;
        }
    }
}
