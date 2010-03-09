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
package org.powermock.api.mockito.internal.expectation;

import java.util.List;

import org.mockito.internal.MockHandler;
import org.mockito.internal.MockitoInvocationHandler;
import org.mockito.internal.stubbing.StubberImpl;
import org.mockito.stubbing.Stubber;
import org.powermock.api.mockito.expectation.PowerMockitoStubber;
import org.powermock.api.mockito.internal.invocationcontrol.MockitoMethodInvocationControl;
import org.powermock.core.MockRepository;
import org.powermock.reflect.Whitebox;

/**
 * Extension of the standard Mocktio stubber implementation that also support
 * PowerMockito created mocks.
 */
public class PowerMockitoStubberImpl extends StubberImpl implements PowerMockitoStubber {

	/**
	 * {@inheritDoc}
	 */
	public void when(Class<?> classMock) {
		MockitoMethodInvocationControl invocationControl = (MockitoMethodInvocationControl) MockRepository
				.getStaticMethodInvocationControl(classMock);
		addAnswersForStubbing(invocationControl);
	}

	/**
	 * Supports PowerMockito mocks. If <code>mock</code> is not a PowerMockito
	 * mock it will delegate to Mockito.
	 * 
	 * @see Stubber#when(Object)
	 */
	@Override
	public <T> T when(T instanceMock) {
		MockitoMethodInvocationControl invocationControl = (MockitoMethodInvocationControl) MockRepository
				.getInstanceMethodInvocationControl(instanceMock);
		final T returnValue;
		if (invocationControl == null) {
			returnValue = super.when(instanceMock);
		} else {
			addAnswersForStubbing(invocationControl);
			returnValue = instanceMock;
		}
		return returnValue;
	}

	@SuppressWarnings("unchecked")
	private void addAnswersForStubbing(MockitoMethodInvocationControl invocationControl) {
		final MockitoInvocationHandler mockHandler = invocationControl.getInvocationHandler().getHandler();
		if (!(mockHandler instanceof MockHandler<?>)) {
			throw new RuntimeException("Cannot perform \"when\" because of unknown mockhandler type " + mockHandler.getClass());
		}
		((MockHandler<?>) mockHandler).setAnswersForStubbing(Whitebox.getInternalState(this, List.class));
	}
}
