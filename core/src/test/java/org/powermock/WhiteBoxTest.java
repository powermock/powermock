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
package org.powermock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;

import org.powermock.Whitebox;
import org.junit.Test;


/**
 * TODO Should be a functional or integration test.
 * 
 * @author Johan Haleby
 */
public class WhiteBoxTest {

	@Test
	public void testFindMethod_classContainingMethodWithNoParameters()
			throws Exception {
		Method expected = ClassWithSeveralMethodsWithSameNameOneWithoutParameters.class
				.getMethod("getDouble");
		Method actual = Whitebox.findMethodOrThrowException(
				ClassWithSeveralMethodsWithSameNameOneWithoutParameters.class,
				"getDouble");
		assertEquals(expected, actual);
	}

	@Test
	public void testFindMethod_classContainingOnlyMethodsWithParameters()
			throws Exception {
		try {
			Whitebox.findMethodOrThrowException(
					ClassWithSeveralMethodsWithSameName.class, "getDouble");
			fail("Should throw runtime exception!");
		} catch (RuntimeException e) {
			assertTrue(
					"Error message did not match",
					e.getMessage().contains("Several matching methods found, please specify the argument parameter types"));
		}
	}

	@Test
	public void testFindMethod_noMethodFound() throws Exception {
		try {
			Whitebox.findMethodOrThrowException(
					ClassWithSeveralMethodsWithSameName.class, "getDouble2");
			fail("Should throw runtime exception!");
		} catch (RuntimeException e) {
			assertEquals(
					"Error message did not match",
					"No method found with name 'getDouble2' with argument types: [ ] in class class org.powermock.ClassWithSeveralMethodsWithSameName",
					e.getMessage());
		}
	}

	@Test
	public void testGetInternalState_object() throws Exception {
		ClassWithInternalState tested = new ClassWithInternalState();
		tested.increaseInteralState();
		Object internalState = Whitebox.getInternalState(tested,
				"internalState");
		assertTrue("InternalState should be instanceof Integer",
				internalState instanceof Integer);
		assertEquals(1, internalState);
	}

	@Test
	public void testGetInternalState_parmaterizedType() throws Exception {
		ClassWithInternalState tested = new ClassWithInternalState();
		tested.increaseInteralState();
		int internalState = Whitebox.getInternalState(tested, "internalState",
				tested.getClass(), 0);
		assertEquals(1, internalState);
	}

	@Test
	public void testSetInternalState() throws Exception {
		ClassWithInternalState tested = new ClassWithInternalState();
		tested.increaseInteralState();
		Whitebox.setInternalState(tested, "anotherInternalState", 2);
		assertEquals(2, tested.getAnotherInternalState());
	}

	@Test
	public void testSetInternalState_superClass() throws Exception {
		ClassWithSubclassThatHasInternalState tested = new ClassWithSubclassThatHasInternalState();
		tested.increaseInteralState();
		Whitebox.setInternalState(tested, "anotherInternalState", 2,
				ClassWithInternalState.class);
		assertEquals(2, tested.getAnotherInternalState());
	}

	@Test
	public void testGetInternalState_superClass_object() throws Exception {
		ClassWithSubclassThatHasInternalState tested = new ClassWithSubclassThatHasInternalState();
		Object internalState = Whitebox.getInternalState(tested,
				"internalState", ClassWithInternalState.class);
		assertEquals(0, internalState);
	}

	@Test
	public void testGetInternalState_superClass_parameterized()
			throws Exception {
		ClassWithSubclassThatHasInternalState tested = new ClassWithSubclassThatHasInternalState();
		int internalState = Whitebox.getInternalState(tested, "internalState",
				ClassWithInternalState.class, 100);
		assertEquals(0, internalState);
	}
}
