package samples.powermockito.junit4;


import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;
/*
 * Copyright 2011 the original author or authors.
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

@RunWith(value = PowerMockRunner.class)
@PrepareForTest({FinalEqualsClass.class})
@SuppressStaticInitializationFor({"samples.powermockito.junit4.FinalClass"})
public class FinalEqualsClassTest {

    @Test
    public void callingEqualsDoesntCauseStackOverflow() throws Exception {
        FinalEqualsClass fc = new FinalEqualsClass();
        fc.foo();

        FinalEqualsClass mock = PowerMockito.mock(FinalEqualsClass.class);
        FinalEqualsClass mock2 = PowerMockito.mock(FinalEqualsClass.class);
        PowerMockito.when(mock.foo()).thenReturn("bar");
        fc = PowerMockito.spy(fc);
        PowerMockito.when(fc.foo()).thenReturn("bar");
        fc.equals(mock);
        assertEquals("bar", mock.foo());
        assertEquals("bar", fc.foo());

        assertEquals(mock, mock);
        assertFalse(mock.equals(mock2));

    }
}
