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
package org.powermock.modules.junit4.expectnew;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.powermock.PowerMock.createMock;
import static org.powermock.PowerMock.expectNew;
import static org.powermock.PowerMock.replay;

import java.io.DataInputStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.Whitebox;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import samples.expectnew.ExpectNewDemo;
import samples.newmocking.MyClass;

/**
 * Defects for expectNew
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest( { MyClass.class, ExpectNewDemo.class })
public class ExpectNewDemoDefect {

	/**
	 * Issue reported at http://code.google.com/p/powermock/issues/detail?id=12. 
	 */
	@Test
	public void testSimpleMultipleNewPrivate_tooManyTimesExpected()
			throws Exception {
		ExpectNewDemo tested = new ExpectNewDemo();

		MyClass myClassMock1 = createMock(MyClass.class);

		expectNew(MyClass.class).andReturn(myClassMock1).times(4);

		replay(myClassMock1, MyClass.class);
		try {
			Whitebox.invokeMethod(tested, "simpleMultipleNewPrivate");
			fail("Should throw an exception!.");
		} catch (RuntimeException e) {
			System.out.println(e);
			assertTrue(e.getMessage().contains(
					"Expected a new instance call on class "
							+ MyClass.class.getName()
							+ " 2 times but was actually 2 (+1) times."));
		}
	}

}
