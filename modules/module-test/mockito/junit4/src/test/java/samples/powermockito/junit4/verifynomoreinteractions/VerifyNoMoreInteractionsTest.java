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
package samples.powermockito.junit4.verifynomoreinteractions;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.times;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.verifyNoMoreInteractions;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static org.powermock.api.support.membermodification.MemberMatcher.constructor;

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

		verifyStatic();
		StaticService.say("hello");
		verifyNoMoreInteractions(StaticService.class);
	}

	@Test
	public void verifyNoMoreInteractionsOnMethodThrowsAssertionErrorWhenMoreInteractionsTookPlace() throws Exception {
		mockStatic(StaticService.class);
		assertNull(StaticService.say("hello"));

		try {
			verifyNoMoreInteractions(StaticService.class);
			fail("Should throw exception!");
		} catch (MockitoAssertionError e) {
			assertTrue(e
					.getMessage()
					.startsWith(
							"\nNo interactions wanted here:\n-> at samples.powermockito.junit4.verifynomoreinteractions.VerifyNoMoreInteractionsTest.verifyNoMoreInteractionsOnMethodThrowsAssertionErrorWhenMoreInteractionsTookPlace(VerifyNoMoreInteractionsTest.java"));
		}
	}

	@Test
	public void verifyNoMoreInteractionsOnNewInstancesThrowsAssertionErrorWhenMoreInteractionsTookPlace() throws Exception {
		ExpectNewDemo tested = new ExpectNewDemo();

		MyClass myClassMock = mock(MyClass.class);

		whenNew(MyClass.class).withNoArguments().thenReturn(myClassMock);

		tested.simpleMultipleNew();

		try {
			verifyNoMoreInteractions(MyClass.class);
			fail("Should throw exception!");
		} catch (MockitoAssertionError e) {
			assertTrue(e
					.getMessage()
					.startsWith(
							"\nNo interactions wanted here:\n-> at samples.powermockito.junit4.verifynomoreinteractions.VerifyNoMoreInteractionsTest.verifyNoMoreInteractionsOnNewInstancesThrowsAssertionErrorWhenMoreInteractionsTookPlace(VerifyNoMoreInteractionsTest.java:"));
		}
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
		MyClass myClassMock = Mockito.mock(MyClass.class);
		myClassMock.getMessage();

		try {
			verifyNoMoreInteractions(myClassMock);
			fail("Should throw exception!");
		} catch (AssertionError e) {
			/*
			 * This string would have been deleted by PowerMockito but should
			 * exists if delegation took place.
			 */
			final String expectedTextThatProvesDelegation = "But found this interaction";
			assertTrue(e.getMessage().contains(expectedTextThatProvesDelegation));
		}
	}
}
