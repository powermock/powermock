/*
 * Copyright 2008 the original author or authors.
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
package samples.powermockito.junit4.abstractmocking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import samples.abstractmocking.AbstractMethodMocking;

@RunWith(PowerMockRunner.class)
@PrepareForTest( { AbstractMethodMocking.class })
public class AbstractMethodMockingTest {

    @Test
    public void mocksAbstractClasses() throws Exception {
        assertNotNull(mock(AbstractMethodMocking.class));
    }

    @Test
    public void canSpyOnAnonymousClasses() throws Exception {
        AbstractMethodMocking tested = new AbstractMethodMocking() {
            @Override
            protected String getIt() {
                return null;
            }
        };

        assertNull(tested.getValue());
        AbstractMethodMocking spy = spy(tested);
        when(spy.getValue()).thenReturn("something");

        assertEquals("something", spy.getValue());
    }
}