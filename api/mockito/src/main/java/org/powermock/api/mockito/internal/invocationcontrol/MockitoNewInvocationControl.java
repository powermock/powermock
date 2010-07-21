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
package org.powermock.api.mockito.internal.invocationcontrol;

import static org.mockito.Mockito.times;

import java.lang.reflect.Constructor;

import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoAssertionError;
import org.mockito.stubbing.OngoingStubbing;
import org.mockito.verification.VerificationMode;
import org.powermock.core.MockRepository;
import org.powermock.core.spi.NewInvocationControl;
import org.powermock.core.spi.support.InvocationSubstitute;
import org.powermock.reflect.internal.WhiteboxImpl;

public class MockitoNewInvocationControl<T> implements NewInvocationControl<OngoingStubbing<T>> {
	private final InvocationSubstitute<T> substitute;

	public MockitoNewInvocationControl(InvocationSubstitute<T> substitute) {
		if (substitute == null) {
			throw new IllegalArgumentException("Internal error: substitute cannot be null.");
		}
		this.substitute = substitute;
	}

	public Object invoke(Class<?> type, Object[] args, Class<?>[] sig) throws Exception {
		Constructor<?> constructor = WhiteboxImpl.getConstructor(type, sig);
		if (constructor.isVarArgs()) {
			/*
			 * Get the first argument because this contains the actual varargs
			 * arguments.
			 */
			args = (Object[]) args[args.length - 1];
		}
		try {
			return substitute.performSubstitutionLogic(args);
		} catch (MockitoAssertionError e) {
			InvocationControlAssertionError.throwAssertionErrorForNewSubstitutionFailure(e, type);
		}

		// Won't happen
		return null;
	}

	public OngoingStubbing<T> expectSubstitutionLogic(Object... arguments) throws Exception {
		return Mockito.when(substitute.performSubstitutionLogic(arguments));
	}

	public InvocationSubstitute<T> getSubstitute() {
		return substitute;
	}

	/**
	 * {@inheritDoc}
	 */
	public synchronized Object replay(Object... mocks) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public synchronized Object verify(Object... mocks) {
		final VerificationMode verificationMode;
		Object mode = MockRepository.getAdditionalState("VerificationMode");
		if (mode != null) {
			if (mode instanceof VerificationMode) {
				verificationMode = (VerificationMode) mode;
			} else {
				throw new IllegalStateException("Internal error. VerificationMode in MockRepository was not of type "
						+ VerificationMode.class.getName() + ".");
			}
		} else {
			verificationMode = times(1);
		}
		Mockito.verify(substitute, verificationMode);
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public synchronized Object reset(Object... mocks) {
		Mockito.<InvocationSubstitute<T>> reset(substitute);
		return null;
	}

	public void verifyNoMoreInteractions() {
		try {
			Mockito.verifyNoMoreInteractions(substitute);
		} catch (MockitoAssertionError e) {
			InvocationControlAssertionError.updateErrorMessageForVerifyNoMoreInteractions(e);
			throw e;
		}
	}
}
