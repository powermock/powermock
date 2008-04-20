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
package org.powermock.modules.junit4.expectvoid;

import static org.powermock.PowerMock.expectLastCall;
import static org.powermock.PowerMock.expectVoid;
import static org.powermock.PowerMock.mockMethod;
import static org.powermock.PowerMock.replay;
import static org.powermock.PowerMock.verify;

import org.easymock.classextension.EasyMock;
import org.powermock.Whitebox;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import samples.expectvoid.ExpectVoidDemo;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ExpectVoidDemo.class)
public class ExpectVoidDemoTest {

	@Test
	public void testInvokeAPrivateVoidMethod() throws Exception {
		final String methodToMock = "privateInvoke";
		ExpectVoidDemo tested = mockMethod(ExpectVoidDemo.class, methodToMock);

		final int expected = 112;

		expectVoid(tested, methodToMock, expected).times(1);

		replay(tested);

		tested.invokeAPrivateVoidMethod(expected);

		verify(tested);
	}

	@Test
	public void testInvokeAPrivateVoidMethod_usingPowerMockExpectLastCall()
			throws Exception {
		final String methodToMock = "privateInvoke";
		ExpectVoidDemo tested = mockMethod(ExpectVoidDemo.class, methodToMock);

		final int expected = 112;

		Whitebox.invokeMethod(tested, methodToMock, expected);
		expectLastCall().times(1);

		replay(tested);

		tested.invokeAPrivateVoidMethod(expected);

		verify(tested);
	}

	@Test
	public void testInvokeAPrivateVoidMethod_usingEasyMockExpectLastCall()
			throws Exception {
		final String methodToMock = "privateInvoke";
		ExpectVoidDemo tested = mockMethod(ExpectVoidDemo.class, methodToMock);

		final int expected = 112;

		Whitebox.invokeMethod(tested, methodToMock, expected);
		EasyMock.expectLastCall();

		replay(tested);

		tested.invokeAPrivateVoidMethod(expected);

		verify(tested);
	}

}
