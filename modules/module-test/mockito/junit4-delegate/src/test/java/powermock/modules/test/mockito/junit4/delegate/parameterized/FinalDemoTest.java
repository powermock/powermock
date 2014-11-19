/*
 * Copyright 2011-2014 the original author or authors.
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


import java.lang.reflect.Method;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import samples.finalmocking.FinalDemo;
import samples.privateandfinal.PrivateFinal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.powermock.api.support.membermodification.MemberMatcher.method;

/**
 * Test class to demonstrate non-static final mocking with Mockito.
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Parameterized.class)
@PrepareForTest({FinalDemo.class, PrivateFinal.class})
public class FinalDemoTest {

    @Parameterized.Parameter(0)
    public String expected;

    @Parameterized.Parameters(name = "expected={0}")
    public static Collection<?> expections() {
        return java.util.Arrays.asList(new Object[][]{
            {"Hello altered World"}, {"something"}, {"test"}
        });
    }

    @Test
    public void assertMockFinalWithNoExpectationsWorks() throws Exception {
        final String argument = "hello";

        FinalDemo tested = mock(FinalDemo.class);

        assertNull(tested.say(argument));

        verify(tested).say(argument);
    }

    @Test
    public void assertMockFinalWithExpectationsWorks() throws Exception {
        final String argument = "hello";

        FinalDemo tested = mock(FinalDemo.class);

        when(tested.say(argument)).thenReturn(expected);

        final String actual = "" + tested.say(argument);

        verify(tested).say(argument);

        assertEquals("Expected and actual did not match", expected, actual);
    }

    @Test
    public void assertFinalNativeWithExpectationsWorks() throws Exception {
        final String argument = "hello";

        FinalDemo tested = mock(FinalDemo.class);

        when(tested.sayFinalNative(argument)).thenReturn(expected);

        String actual = "" + tested.sayFinalNative(argument);

        verify(tested).sayFinalNative(argument);
        assertEquals("Expected and actual did not match", expected, actual);
    }

    @Test
    public void assertSpyingOnFinalInstanceMethodWorks() throws Exception {
        FinalDemo tested = new FinalDemo();
        FinalDemo spy = spy(tested);

        final String argument = "PowerMock";

        assertEquals("Hello " + argument, spy.say(argument));
        when(spy.say(argument)).thenReturn(expected);
        assertEquals(expected, "" + spy.say(argument));
    }

    @Test(expected = ArrayStoreException.class)
    public void assertSpyingOnFinalVoidInstanceMethodWorks() throws Exception {
        FinalDemo tested = new FinalDemo();
        FinalDemo spy = spy(tested);

        doThrow(new ArrayStoreException()).when(spy).finalVoidCallee();

        spy.finalVoidCaller();
    }

    @Test
    public void assertSpyingOnPrivateFinalInstanceMethodWorks() throws Exception {
        PrivateFinal spy = spy(new PrivateFinal());

        assertEquals("Hello " + expected, spy.say(expected));

        when(spy, "sayIt", isA(String.class)).thenReturn(expected);

        assertEquals(expected, "" + spy.say(expected));

        verifyPrivate(spy, times(2)).invoke("sayIt", expected);
    }

    @Test
    public void assertSpyingOnPrivateFinalInstanceMethodWorksWhenUsingJavaLangReflectMethod() throws Exception {
        PrivateFinal spy = spy(new PrivateFinal());

        assertEquals("Hello " + expected, spy.say(expected));

        final Method methodToExpect = method(PrivateFinal.class, "sayIt");
        when(spy, methodToExpect).withArguments(isA(String.class)).thenReturn(expected);

        assertEquals(expected, "" + spy.say(expected));

        verifyPrivate(spy, times(2)).invoke(methodToExpect).withArguments(expected);
    }
}
