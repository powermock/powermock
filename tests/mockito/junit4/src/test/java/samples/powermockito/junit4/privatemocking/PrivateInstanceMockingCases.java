/*
 *
 *   Copyright 2017 the original author or authors.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package samples.powermockito.junit4.privatemocking;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoAssertionError;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import samples.privateandfinal.PrivateFinal;
import samples.privatemocking.PrivateMethodDemo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.never;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;
import static org.powermock.api.mockito.PowerMockito.when;

public class PrivateInstanceMockingCases {
    @Test
    public void expectationsWorkWhenSpyingOnPrivateMethods() throws Exception {
        PrivateMethodDemo tested = spy(new PrivateMethodDemo());
        assertEquals("Hello Temp, you are 50 old.", tested.sayYear("Temp", 50));

        when(tested, "doSayYear", 12, "test").thenReturn("another");

        assertEquals("Hello Johan, you are 29 old.", tested.sayYear("Johan", 29));
        assertEquals("another", tested.sayYear("test", 12));

        verifyPrivate(tested).invoke("doSayYear", 12, "test");
    }
    
    @Test
    public void expectationsWorkWithArgumentMatchersWhenSpyingOnPrivateMethods() throws Exception {
        PrivateMethodDemo tested = spy(new PrivateMethodDemo());
        assertEquals("Hello Temp, you are 50 old.", tested.sayYear("Temp", 50));

        when(tested, "doSayYear", Mockito.anyInt(), Mockito.anyString()).thenReturn("another");

        assertEquals("another", tested.sayYear("Johan", 29));
        assertEquals("another", tested.sayYear("test", 12));

        verifyPrivate(tested).invoke("doSayYear", 29, "Johan");
        verifyPrivate(tested).invoke("doSayYear", 12, "test");
        verifyPrivate(tested).invoke("doSayYear", 50, "Temp");
    }
    
    @Test
    public void answersWorkWhenSpyingOnPrivateVoidMethods() throws Exception {
        PrivateMethodDemo tested = spy(new PrivateMethodDemo());

        tested.doObjectStuff(new Object());

        when(tested, "doObjectInternal", isA(String.class)).thenAnswer(new Answer<Void>() {
            private static final long serialVersionUID = 20645008237481667L;

            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                assertEquals("Testing", invocation.getArguments()[0]);
                return null;
            }
        });
        tested.doObjectStuff(new Object());
        tested.doObjectStuff("Testing");
    }
    
    @Test
    public void spyingOnPrivateFinalMethodsWorksWhenClassIsNotFinal() throws Exception {
        PrivateFinal tested = spy(new PrivateFinal());

        final String name = "test";
        tested.say(name);
        assertEquals("Hello " + name, tested.say(name));

        when(tested, "sayIt", name).thenReturn("First", "Second");

        assertEquals("First", tested.say(name));
        assertEquals("Second", tested.say(name));
    }
    
    @Test
    public void errorousVerificationOnPrivateMethodGivesFilteredErrorMessage() throws Exception {
        PrivateMethodDemo tested = spy(new PrivateMethodDemo());
        assertEquals("Hello Temp, you are 50 old.", tested.sayYear("Temp", 50));

        when(tested, "doSayYear", Mockito.anyInt(), Mockito.anyString()).thenReturn("another");

        assertEquals("another", tested.sayYear("Johan", 29));
        assertEquals("another", tested.sayYear("test", 12));

        try {
            verifyPrivate(tested, never()).invoke("doSayYear", 50, "Temp");
            fail("Should throw assertion error");
        } catch (MockitoAssertionError e) {
            Assertions.assertThat(e.getMessage())
                      .as("Never wanted  but invoked")
                      .contains("Never wanted  but invoked");
        }
    }
}
