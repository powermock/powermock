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
package samples.packageprivate;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.easymock.PowerMock.createPartialMock;
import static org.powermock.api.easymock.PowerMock.expectPrivate;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Verifies that the issue at
 * http://code.google.com/p/powermock/issues/detail?id=32 is solved.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(PackagePrivateClass.class)
public class PackagePrivateClassTest {

	@Test
	public void testMockAPackagePrivateClass() throws Exception {
		final String returnAValueMethodName = "returnAValue";
		final int expected = 23;

		PackagePrivateClass tested = createPartialMock(PackagePrivateClass.class, returnAValueMethodName);
		expectPrivate(tested, returnAValueMethodName).andReturn(expected);

		replay(tested);

		assertEquals(expected, tested.getValue());

		verify(tested);
	}

}
