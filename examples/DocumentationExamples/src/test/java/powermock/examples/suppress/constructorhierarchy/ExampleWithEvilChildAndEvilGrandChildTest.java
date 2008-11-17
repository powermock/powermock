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
package powermock.examples.suppress.constructorhierarchy;

import static org.junit.Assert.assertEquals;
import static org.powermock.PowerMock.suppressConstructor;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Example that demonstrates PowerMock's ability to suppress constructor
 * hierarchies.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(ExampleWithEvilChildAndEvilGrandChild.class)
public class ExampleWithEvilChildAndEvilGrandChildTest {

	@Test
	public void testSuppressConstructorHierarchy() throws Exception {
		suppressConstructor(EvilChild.class);
		final String message = "myMessage";
		ExampleWithEvilChildAndEvilGrandChild tested = new ExampleWithEvilChildAndEvilGrandChild(message);
		assertEquals(message, tested.getMessage());
	}

	@Ignore("Should suppress constructor code really suppress the full hierarchy?")
	@Test(expected = UnsatisfiedLinkError.class)
	public void testSuppressConstructorOfEvilChild() throws Exception {
		suppressConstructor(EvilChild.class);
		final String message = "myMessage";
		new ExampleWithEvilChildAndEvilGrandChild(message);
	}

	@Test(expected = UnsatisfiedLinkError.class)
	public void testNotSuppressConstructorOfEvilChild() throws Exception {
		final String message = "myMessage";
		new ExampleWithEvilChildAndEvilGrandChild(message);
	}
}
