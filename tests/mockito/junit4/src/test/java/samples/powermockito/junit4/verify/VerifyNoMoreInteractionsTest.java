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
package samples.powermockito.junit4.verify;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoAssertionError;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.expectnew.ExpectNewDemo;
import samples.newmocking.MyClass;
import samples.singleton.StaticHelper;
import samples.singleton.StaticService;

import static org.assertj.core.api.Java6Assertions.assertThatThrownBy;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.times;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.verifyNoMoreInteractions;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static org.powermock.api.support.membermodification.MemberMatcher.constructor;

/**
 * Test class to demonstrate static mocking with PowerMockito.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest( { StaticService.class, StaticHelper.class, ExpectNewDemo.class })
public class VerifyNoMoreInteractionsTest {

	@Test
	public void verifyNoMoreInteractionsForStaticMethodsReturnsSilentlyWhenNoMoreInteractionsTookPlace() throws Exception {
		mockStatic(StaticService.class);
		assertNull(StaticService.say("hello"));

		verifyStatic(StaticService.class);
		StaticService.say("hello");
		verifyNoMoreInteractions(StaticService.class);
	}

	@Test
	public void verifyNoMoreInteractionsOnMethodThrowsAssertionErrorWhenMoreInteractionsTookPlace() throws Exception {
		mockStatic(StaticService.class);
		assertNull(StaticService.say("hello"));

        assertThatThrownBy(new ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                verifyNoMoreInteractions(StaticService.class);
            }
        }).hasMessageStartingWith(
            "\nNo interactions wanted here:\n-> at samples.powermockito.junit4.verify.VerifyNoMoreInteractionsTest$1.call");
	}

	@Test
	public void verifyNoMoreInteractionsOnNewInstancesThrowsAssertionErrorWhenMoreInteractionsTookPlace() throws Exception {
		ExpectNewDemo tested = new ExpectNewDemo();

		MyClass myClassMock = mock(MyClass.class);

		whenNew(MyClass.class).withNoArguments().thenReturn(myClassMock);

		tested.simpleMultipleNew();
        
        assertThatThrownBy(new ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                verifyNoMoreInteractions(MyClass.class);
            }
        }).hasMessageStartingWith(
            "\nNo interactions wanted here:\n-> at samples.powermockito.junit4.verify.VerifyNoMoreInteractionsTest$2.call");
	}

	@Test
	public void verifyNoMoreInteractionsOnNewInstancesWorks() throws Exception {
		ExpectNewDemo tested = new ExpectNewDemo();

		MyClass myClassMock = mock(MyClass.class);

		whenNew(MyClass.class).withNoArguments().thenReturn(myClassMock);

		tested.simpleMultipleNew();

		verifyNew(MyClass.class, times(3)).withNoArguments();
		verifyNoMoreInteractions(MyClass.class);
	}

	@Test
	public void verifyNoMoreInteractionsOnNewInstancesWorksWhenUsingConstructorToExpect() throws Exception {
		ExpectNewDemo tested = new ExpectNewDemo();

		MyClass myClassMock = mock(MyClass.class);

		whenNew(constructor(MyClass.class)).withNoArguments().thenReturn(myClassMock);

		tested.simpleMultipleNew();

		verifyNew(MyClass.class, times(3)).withNoArguments();
		verifyNoMoreInteractions(MyClass.class);
	}

	@Test
	public void verifyNoMoreInteractionsDelegatesToPlainMockitoWhenMockIsNotAPowerMockitoMock() throws Exception {
		final MyClass myClassMock = Mockito.mock(MyClass.class);
        myClassMock.getMessage();
        
        final String expectedTextThatProvesDelegation = "But found this interaction";
        assertThatThrownBy(new ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                verifyNoMoreInteractions(myClassMock);
            }
        }).hasMessageContaining(expectedTextThatProvesDelegation);
	}
}
