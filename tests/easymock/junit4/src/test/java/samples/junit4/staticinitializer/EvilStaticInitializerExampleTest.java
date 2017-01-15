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
package samples.junit4.staticinitializer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.staticinitializer.EvilStaticInitializerExample;

import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.*;

/**
 * Test that demonstrates a (naive) example of when chunking may be handy.
 */
@RunWith(PowerMockRunner.class)
public class EvilStaticInitializerExampleTest {

	@Test
	@SuppressStaticInitializationFor("samples.staticinitializer.EvilStaticInitializerExample")
	public void assertNativeCodeInvocationWorks() throws Exception {
		EvilStaticInitializerExample tested = new EvilStaticInitializerExample();
		assertThat(tested.doSomeNativeStuffUsingTheLoadedSystemLibrary(), instanceOf(String.class));
	}

	@Test
	public void assertCorrectErrorMessageIfLibraryNotFound() throws Exception {
		try {
			new EvilStaticInitializerExample();
			fail("Should throw unsatisfied link error!");
		} catch (UnsatisfiedLinkError error) {
			assertEquals(error.getMessage(), EvilStaticInitializerExample.FAILED_TO_LOAD_LIBRARY_MESSAGE);
		}
	}
}
