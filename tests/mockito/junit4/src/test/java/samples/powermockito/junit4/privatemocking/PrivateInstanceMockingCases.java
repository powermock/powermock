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

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoAssertionError;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.reflect.Whitebox;
import samples.privateandfinal.PrivateFinal;
import samples.privatemocking.PrivateMethodDemo;

import java.io.File;
import java.io.StringReader;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.never;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.support.membermodification.MemberMatcher.method;

public class PrivateInstanceMockingCases {
    
    @Test
    public void should_call_method_that_best_match_the_given_parameters_during_verification() throws Exception {
        final String stubbedValue = "another";
        final PrivateMethodDemo tested = mock(PrivateMethodDemo.class);
    
        when(tested.sayYear(anyString(), anyInt())).thenCallRealMethod();
        when(tested, "doSayYear", 12, "test").thenReturn(stubbedValue);
        
        assertThat(tested.sayYear("test", 12))
            .as("Private method is called")
            .isEqualTo(stubbedValue);
    
        verifyPrivate(tested).invoke(12, "test");
    }
    
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
    public void erroneousVerificationOnPrivateMethodGivesFilteredErrorMessage() throws Exception {
        PrivateMethodDemo tested = spy(new PrivateMethodDemo());
        assertEquals("Hello Temp, you are 50 old.", tested.sayYear("Temp", 50));

        when(tested, "doSayYear", Mockito.anyInt(), Mockito.anyString()).thenReturn("another");

        assertEquals("another", tested.sayYear("Johan", 29));
        assertEquals("another", tested.sayYear("test", 12));

        try {
            verifyPrivate(tested, never()).invoke("doSayYear", 50, "Temp");
            fail("Should throw assertion error");
        } catch (MockitoAssertionError e) {
            assertThat(e.getMessage())
                      .as("Never wanted  but invoked")
                      .contains("Never wanted  but invoked");
        }
    }
    
    @Test
    public void expectationsWorkWhenSpyingOnPrivateMethodsUsingDoReturn() throws Exception {
        PrivateMethodDemo tested = spy(new PrivateMethodDemo());
        assertEquals("Hello Temp, you are 50 old.", tested.sayYear("Temp", 50));
        
        doReturn("another").when(tested, "doSayYear", 12, "test");
        
        assertEquals("Hello Johan, you are 29 old.", tested.sayYear("Johan", 29));
        assertEquals("another", tested.sayYear("test", 12));
        
        verifyPrivate(tested).invoke("doSayYear", 12, "test");
    }
    
    @Test
    public void expectationsWorkWhenSpyingOnPrivateMethodsUsingDoReturnWhenMethodDoesntHaveAnyArguments() throws Exception {
        PrivateMethodDemo tested = spy(new PrivateMethodDemo());
        
        doReturn("another").when(tested, "sayIt");
        
        assertEquals("another", Whitebox.invokeMethod(tested, "sayIt"));
        
        verifyPrivate(tested).invoke("sayIt");
    }
    
    @Test
    public void verifyPrivateMethodWhenNoExpectationForTheMethodHasBeenMade() throws Exception {
        PrivateMethodDemo tested = spy(new PrivateMethodDemo());
        
        assertEquals("Hello Johan, you are 29 old.", tested.sayYear("Johan", 29));
        
        verifyPrivate(tested).invoke("doSayYear", 29, "Johan");
    }
    
    @Test(expected = ArrayStoreException.class)
    public void expectationsWorkWhenSpyingOnPrivateVoidMethods() throws Exception {
        PrivateMethodDemo tested = spy(new PrivateMethodDemo());
        
        tested.doObjectStuff(new Object());
        
        when(tested, "doObjectInternal", isA(Object.class)).thenThrow(new ArrayStoreException());
        
        tested.doObjectStuff(new Object());
    }
    
    @Test
    public void usingMultipleArgumentsOnPrivateMethodWorks() throws Exception {
        File file = mock(File.class);
        StringReader expected = new StringReader("Some string");
        
        PrivateMethodDemo tested = mock(PrivateMethodDemo.class);
        doReturn(expected).when(tested, method(PrivateMethodDemo.class, "createReader", File.class)).withArguments(file);
        
        StringReader actual = Whitebox.invokeMethod(tested, "createReader", file);
        
        assertSame(expected, actual);
    }
}
