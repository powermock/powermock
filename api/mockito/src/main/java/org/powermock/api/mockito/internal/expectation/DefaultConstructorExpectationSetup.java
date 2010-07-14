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

import java.lang.reflect.Method;

import org.mockito.stubbing.OngoingStubbing;
import org.powermock.api.mockito.expectation.ConstructorExpectationSetup;
import org.powermock.api.mockito.expectation.WithExpectedArguments;
import org.powermock.api.mockito.internal.invocationcontrol.MockitoNewInvocationControl;
import org.powermock.api.mockito.internal.mockcreation.MockCreator;
import org.powermock.core.MockRepository;
import org.powermock.core.spi.NewInvocationControl;
import org.powermock.core.spi.support.InvocationSubstitute;
import org.powermock.reflect.internal.WhiteboxImpl;
import org.powermock.tests.utils.ArrayMerger;
import org.powermock.tests.utils.impl.ArrayMergerImpl;

public class DefaultConstructorExpectationSetup<T> implements ConstructorExpectationSetup<T> {

	private Class<?>[] parameterTypes = null;
	private final Class<T> mockType;
	private final ArrayMerger arrayMerger;

	public DefaultConstructorExpectationSetup(Class<T> mockType) {
		this.mockType = mockType;
		arrayMerger = new ArrayMergerImpl();
	}

	public void setParameterTypes(Class<?>[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}

	public OngoingStubbing<T> withArguments(Object firstArgument, Object... additionalArguments) throws Exception {
		return createNewSubsituteMock(mockType, parameterTypes, arrayMerger.mergeArrays(Object.class, new Object[] { firstArgument },
				additionalArguments));
	}

	public OngoingStubbing<T> withNoArguments() throws Exception {
		return createNewSubsituteMock(mockType, parameterTypes, new Object[0]);
	}

	public WithExpectedArguments<T> withParameterTypes(Class<?> parameterType, Class<?>... additionalParameterTypes) {
		this.parameterTypes = arrayMerger.mergeArrays(Class.class, new Class<?>[] { parameterType }, additionalParameterTypes);
		return this;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static <T> OngoingStubbing<T> createNewSubsituteMock(Class<T> type, Class<?>[] parameterTypes, Object... arguments) throws Exception {
		if (type == null) {
			throw new IllegalArgumentException("type cannot be null");
		}

		final Class<T> unmockedType = (Class<T>) WhiteboxImpl.getUnmockedType(type);
		if (parameterTypes == null) {
			WhiteboxImpl.findUniqueConstructorOrThrowException(type, arguments);
		} else {
			WhiteboxImpl.getConstructor(unmockedType, parameterTypes);
		}

		/*
		 * Check if this type has been mocked before
		 */
		NewInvocationControl<OngoingStubbing<T>> newInvocationControl = (NewInvocationControl<OngoingStubbing<T>>) MockRepository
				.getNewInstanceControl(unmockedType);
		if (newInvocationControl == null) {
			InvocationSubstitute<T> mock = MockCreator.mock(InvocationSubstitute.class, false, false, null, null, (Method[]) null);
			newInvocationControl = new MockitoNewInvocationControl(mock);
			MockRepository.putNewInstanceControl(type, newInvocationControl);
			MockRepository.addObjectsToAutomaticallyReplayAndVerify(WhiteboxImpl.getUnmockedType(type));
		}

		return newInvocationControl.expectSubstitutionLogic(arguments);
	}

}
