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
package samples.junit4.nativemocking;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import samples.nativemocking.NativeMockingSample;
import samples.nativemocking.NativeService;

/**
 * This test demonstrates that it's possible to mock native methods using plain
 * EasyMock class extensions.
 */
public class NativeMockingSampleTest {

	@Test
	public void testMockNative() throws Exception {
		NativeService nativeServiceMock = createMock(NativeService.class);
		NativeMockingSample tested = new NativeMockingSample(nativeServiceMock);

		final String expectedParameter = "question";
		final String expectedReturnValue = "answer";
		expect(nativeServiceMock.invokeNative(expectedParameter)).andReturn(expectedReturnValue);

		replay(nativeServiceMock);

		assertEquals(expectedReturnValue, tested.invokeNativeMethod(expectedParameter));

		verify(nativeServiceMock);
	}

}
