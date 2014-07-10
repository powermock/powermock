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
@PrepareForTest({FinalClass.class})
@SuppressStaticInitializationFor({"samples.powermockito.junit4.FinalClass"})
public class FinalClassTest {

    @Test
    public void test() throws Exception {

        FinalClass fc = new FinalClass();
        fc.foo();

        FinalClass mock = PowerMockito.mock(FinalClass.class);
        FinalClass mock2 = PowerMockito.mock(FinalClass.class);
        PowerMockito.when(mock.foo()).thenReturn("bar");
        fc = PowerMockito.spy(fc);
        PowerMockito.when(fc.foo()).thenReturn("bar");
        System.out.println("-a----");
        fc.equals(mock);
        System.out.println("-b----");
        assertEquals("bar", mock.foo());
        System.out.println("-c----");
        assertEquals("bar", fc.foo());

        System.out.println("-d----");
        assertEquals(mock, mock);
        System.out.println("-e----");
        assertFalse(mock.equals(mock2));

    }
}
