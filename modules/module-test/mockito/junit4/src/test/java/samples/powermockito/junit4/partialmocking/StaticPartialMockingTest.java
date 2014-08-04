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
package samples.powermockito.junit4.partialmocking;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.partialmocking.MockSelfDemo;
import samples.singleton.StaticExample;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.powermock.api.support.membermodification.MemberMatcher.method;

@RunWith(PowerMockRunner.class)
@PrepareForTest({StaticExample.class, MockSelfDemo.class})
public class StaticPartialMockingTest {

    @Test
    public void spyingOnStaticMethodReturningObjectWorks() throws Exception {
        spy(StaticExample.class);

        assertTrue(Object.class.equals(StaticExample.objectMethod().getClass()));
        when(StaticExample.class, "privateObjectMethod").thenReturn("Hello static");

        assertEquals("Hello static", StaticExample.objectMethod());
        /*
		 * privateObjectMethod should be invoked twice, once at "assertTrue" and
		 * once above.
		 */
        verifyPrivate(StaticExample.class, times(2)).invoke("privateObjectMethod");
    }

    @Test
    public void partialMockingOfStaticMethodReturningObjectWorks() throws Exception {
        spy(StaticExample.class);

        assertTrue(Object.class.equals(StaticExample.objectMethod().getClass()));
        doReturn("Hello static").when(StaticExample.class, "privateObjectMethod");

        assertEquals("Hello static", StaticExample.objectMethod());
		/*
		 * privateObjectMethod should be invoked twice, once at "assertTrue" and
		 * once above.
		 */
        verifyPrivate(StaticExample.class, times(2)).invoke("privateObjectMethod");
    }

    @Test
    public void partialPrivateMockingWithAnswerOfStaticMethodReturningObjectWorks() throws Exception {
        spy(StaticExample.class);

        assertTrue(Object.class.equals(StaticExample.objectMethod().getClass()));
        doAnswer(new Answer<String>() {
            public String answer(InvocationOnMock invocation) throws Throwable {
                return "Hello static";
            }
        }).when(StaticExample.class, "privateObjectMethod");

        assertEquals("Hello static", StaticExample.objectMethod());
		/*
		 * privateObjectMethod should be invoked twice, once at "assertTrue" and
		 * once above.
		 */
        verifyPrivate(StaticExample.class, times(2)).invoke("privateObjectMethod");
    }

    @Test
    public void spyingOnStaticFinalMethodReturningObjectWorks() throws Exception {
        spy(StaticExample.class);

        assertTrue(Object.class.equals(StaticExample.objectFinalMethod().getClass()));

        when(StaticExample.class, "privateObjectFinalMethod").thenReturn("Hello static");

        assertEquals("Hello static", StaticExample.objectFinalMethod());

        verifyPrivate(StaticExample.class, times(2)).invoke("privateObjectFinalMethod");
    }

    @Test
    public void partialMockingOfStaticFinalMethodReturningObjectWorks() throws Exception {
        spy(StaticExample.class);

        assertTrue(Object.class.equals(StaticExample.objectFinalMethod().getClass()));

        doReturn("Hello static").when(StaticExample.class, "privateObjectFinalMethod");

        assertEquals("Hello static", StaticExample.objectFinalMethod());

        verifyPrivate(StaticExample.class, times(2)).invoke("privateObjectFinalMethod");
    }

    @Test(expected = ArrayStoreException.class)
    public void spyingOnStaticVoidMethodReturningObjectWorks() throws Exception {
        spy(StaticExample.class);

        StaticExample.voidMethod();

        when(StaticExample.class, "privateVoidMethod").thenThrow(new ArrayStoreException());
        StaticExample.voidMethod();
    }

    @Test(expected = ArrayStoreException.class)
    public void partialMockingOfStaticVoidMethodReturningObjectWorks() throws Exception {
        spy(StaticExample.class);

        StaticExample.voidMethod();

        doThrow(new ArrayStoreException()).when(StaticExample.class, "privateVoidMethod");
        StaticExample.voidMethod();
    }

    @Test(expected = ArrayStoreException.class)
    public void spyingOnStaticFinalVoidMethodReturningObjectWorks() throws Exception {
        spy(StaticExample.class);

        StaticExample.voidFinalMethod();

        when(StaticExample.class, "privateVoidFinalMethod").thenThrow(new ArrayStoreException());
        StaticExample.voidFinalMethod();
    }

    @Test(expected = ArrayStoreException.class)
    public void partialMockingOfStaticFinalVoidMethodReturningObjectWorks() throws Exception {
        spy(StaticExample.class);

        StaticExample.voidFinalMethod();

        doThrow(new ArrayStoreException()).when(StaticExample.class, "privateVoidFinalMethod");
        StaticExample.voidFinalMethod();
    }

    @Test
    public void partialMockingOfPublicStaticVoidWorks() throws Exception {
        spy(StaticExample.class);

        // Given
        doNothing().when(StaticExample.class);
        StaticExample.staticVoidMethod();

        // When
        StaticExample.staticVoidMethod();

        // Then
        verifyStatic(times(1));
        StaticExample.staticVoidMethod();
    }

    @Test
    public void partialMockingOfPublicStaticFinalVoidWorks() throws Exception {
        spy(StaticExample.class);

        doNothing().when(StaticExample.class);
        StaticExample.staticFinalVoidMethod();

        StaticExample.staticFinalVoidMethod();
    }

    @Test
    public void partialMockingOfNonVoidPublicStaticMethodsWorks() throws Exception {
        spy(StaticExample.class);

        doReturn("something").when(StaticExample.class);
        StaticExample.staticMethodReturningString();

        assertEquals("something", StaticExample.staticMethodReturningString());
    }

    @Test
    public void partialMockingWithAnswerOfNonVoidPublicStaticMethodsWorks() throws Exception {
        spy(StaticExample.class);

        doAnswer(new Answer<String>() {
            public String answer(InvocationOnMock invocation) throws Throwable {
                return "something";
            }
        }).when(StaticExample.class);
        StaticExample.staticMethodReturningString();

        assertEquals("something", StaticExample.staticMethodReturningString());
    }

    @Test
    public void partialMockingOfPublicStaticMethodsWorks() throws Exception {
        spy(MockSelfDemo.class);
        when(MockSelfDemo.class, method(MockSelfDemo.class, "methodToBeStubbed")).withNoArguments().thenReturn(2);

        int result = MockSelfDemo.getSomething();
        assertEquals(4, result);
    }

    @Test
    public void partialMockingOfPublicStaticMethodsWorksWhenUsingDoReturn() throws Exception {
        spy(MockSelfDemo.class);

        doReturn(2).when(MockSelfDemo.class);
        MockSelfDemo.methodToBeStubbed();

        int result = MockSelfDemo.getSomething();
        assertEquals(4, result);
    }

    @Test
    public void partialMockingOfPublicStaticMethodsWorksWhenUsingDoReturnAndMethodNameAsString() throws Exception {
        spy(MockSelfDemo.class);

        doReturn(3).when(MockSelfDemo.class, "methodToBeStubbed");

        int result = MockSelfDemo.getSomething();
        assertEquals(6, result);
    }
}
