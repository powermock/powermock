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
package org.powermock.modules.junit4.suppressconstructor;

import static org.powermock.PowerMock.expectPrivate;
import static org.powermock.PowerMock.mockMethod;
import static org.powermock.PowerMock.replay;
import static org.powermock.PowerMock.suppressConstructorCode;
import static org.powermock.PowerMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.powermock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import samples.suppressconstructor.SuppressConstructorDemo;
import samples.suppressconstructor.SuppressConstructorSubclassDemo;


/**
 * This test demonstrates how to tell PowerMock to avoid executing constructor
 * code for a certain class. This is crucial in certain tests where the
 * constructor or a subclass's constructor performs operations that are of no
 * concern to the unit test of the actual class or if the constructor performs
 * operations, such as getting services from a runtime environment that has not
 * been initialized. For example in an SWT application, a parent class may
 * depend on that the SWT runtime environment has been started and if not you'll
 * get an exception. This makes it impossible to unit test such classes without
 * creating an instance of the class that doesn't call the actual constructor
 * code (which is not possible without byte-code manipulation). In normal
 * situations you're forced to create an integration or function test for the
 * class instead (and thus the runtime environment is available). This is not
 * particularly good when it comes to testing method logic. PowerMock solves
 * these problems by letting you specify the
 * {@link PowerMock#suppressConstructorCode(Class...)} method
 * <p>
 * Nedanstående ska in till SuppressStaticInitializer annoteringen, därför står
 * det kvar: Note: The reason that an annotation is needed is because we need to
 * know at <strong>load-time</strong> if the constructor execution for this
 * class should be skipped or not. Unfortunately we cannot pass the class as the
 * value parameter to the <code>SkipExecutingConstructorCodeFor</code>
 * annotation (and thus get type-safe values) because in that case the class is
 * loaded when the PowerMock class loader checks if the
 * <code>SkipExecutingConstructorCodeFor</code> is present making it
 * impossible to know in advance if the class should skip executing constructor
 * code or not.
 * 
 * @author Johan Haleby
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(SuppressConstructorDemo.class)
public class SuppressConstructorDemoTest {

	/**
	 * This test makes sure that the real parent constructor has never been
	 * called.
	 */
	@Test
	public void testGetMessage() throws Exception {
		suppressConstructorCode(SuppressConstructorSubclassDemo.class);
		final SuppressConstructorDemo tested = new SuppressConstructorDemo(
				"a message");
		assertNull(
				"Message should have been null since we're skipping the execution of the constructor code.",
				tested.getMessage());
	}

	/**
	 * This test makes sure that it's possible to also mock methods from the
	 * class under test at the same time as skipping constructor execution.
	 */
	@Test
	public void testGetMyOwnMessageAndGetMessage() throws Exception {
		suppressConstructorCode(SuppressConstructorSubclassDemo.class);
		final SuppressConstructorDemo tested = mockMethod(
				SuppressConstructorDemo.class, "returnAMessage");
		final String expected = "Hello world!";
		expectPrivate(tested, "returnAMessage").andReturn(expected);
		replay(tested);

		final String actual = tested.getMyOwnMessage();

		verify(tested);

		assertEquals(expected, actual);

		assertNull(
				"Message should have been null since we're skipping the execution of the constructor code.",
				tested.getMessage());
	}
}
