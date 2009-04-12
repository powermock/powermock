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
package org.powermock.api.easymock.internal.invocationcontrol;

import java.lang.reflect.Constructor;

import org.easymock.classextension.EasyMock;
import org.easymock.IExpectationSetters;
import org.easymock.internal.MocksControl.MockType;
import org.powermock.core.MockRepository;
import org.powermock.core.PowerMockUtils;
import org.powermock.core.spi.NewInvocationControl;
import org.powermock.core.spi.support.InvocationSubstitute;
import org.powermock.reflect.internal.WhiteboxImpl;

public class NewInvocationControlImpl<T> implements NewInvocationControl<IExpectationSetters<T>> {
	private final InvocationSubstitute<T> substitute;
	private final Class<T> subsitutionType;

	public NewInvocationControlImpl(InvocationSubstitute<T> substitute, Class<T> type) {
		if (substitute == null) {
			throw new IllegalArgumentException("Internal error: substitute cannot be null.");
		}
		this.subsitutionType = type;
		this.substitute = substitute;
	}

	public Object invoke(Class<?> type, Object[] args, Class<?>[] sig) throws Exception {
		Constructor<?> constructor = WhiteboxImpl.getConstructor(type, sig);
		if (constructor.isVarArgs()) {
			/*
			 * Get the first argument because this contains the actual varargs
			 * arguments.
			 */
			args = (Object[]) args[0];
		}
		try {
			final MockType mockType = ((EasyMockMethodInvocationControl<?>) MockRepository
					.getInstanceMethodInvocationControl(substitute)).getMockType();
			Object result = substitute.performSubstitutionLogic(args);

			if (result == null) {
				if (mockType == MockType.NICE) {
					result = EasyMock.createNiceMock(subsitutionType);
				} else {
					throw new IllegalStateException("Must replay class " + type.getName()
							+ " to get configured expectation.");
				}
			}
			return result;
		} catch (AssertionError e) {
			PowerMockUtils.throwAssertionErrorForNewSubstitutionFailure(e, type);
		}

		// Won't happen
		return null;
	}

	public IExpectationSetters<T> performSubstitutionLogic(Object... arguments) throws Exception {
		return EasyMock.expect(substitute.performSubstitutionLogic(arguments));
	}

	/**
	 * {@inheritDoc}
	 */
	public Object replay(Object... mocks) {
		EasyMock.replay(substitute);
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object verify(Object... mocks) {
		EasyMock.verify(substitute);
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object reset(Object... mocks) {
		EasyMock.reset(substitute);
		return null;
	}
}
