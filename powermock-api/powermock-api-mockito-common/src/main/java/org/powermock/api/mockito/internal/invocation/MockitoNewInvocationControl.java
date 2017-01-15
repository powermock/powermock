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
package org.powermock.api.mockito.internal.invocation;

import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoAssertionError;
import org.mockito.internal.progress.MockingProgress;
import org.mockito.internal.progress.ThreadSafeMockingProgress;
import org.mockito.stubbing.OngoingStubbing;
import org.mockito.verification.VerificationMode;
import org.powermock.core.MockRepository;
import org.powermock.core.spi.NewInvocationControl;
import org.powermock.core.spi.support.InvocationSubstitute;
import org.powermock.reflect.internal.WhiteboxImpl;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;

import static org.mockito.Mockito.times;

public class MockitoNewInvocationControl<T> implements NewInvocationControl<OngoingStubbing<T>> {
	private final InvocationSubstitute<T> substitute;
	private final MockingProgress mockingProgress;

	public MockitoNewInvocationControl(InvocationSubstitute<T> substitute) {
		if (substitute == null) {
			throw new IllegalArgumentException("Internal error: substitute cannot be null.");
		}
		this.substitute = substitute;
        this.mockingProgress = new ThreadSafeMockingProgress();
    }

    @Override
	public Object invoke(Class<?> type, Object[] args, Class<?>[] sig) throws Exception {
		Constructor<?> constructor = WhiteboxImpl.getConstructor(type, sig);
		if (constructor.isVarArgs()) {
			Object varArgs =  args[args.length - 1];
            final int varArgsLength = Array.getLength(varArgs);
            Object[] oldArgs = args;
            args = new Object[args.length + varArgsLength - 1];
            System.arraycopy(oldArgs, 0, args, 0, oldArgs.length - 1);
            for (int i = oldArgs.length - 1, j=0; i < args.length; i++, j++) {
                args[i] = Array.get(varArgs, j);                                     
            }
		}
		try {
			return substitute.performSubstitutionLogic(args);
		} catch (MockitoAssertionError e) {
			InvocationControlAssertionError.throwAssertionErrorForNewSubstitutionFailure(e, type);
		}

		// Won't happen
		return null;
	}

	@Override
	public OngoingStubbing<T> expectSubstitutionLogic(Object... arguments) throws Exception {
        return Mockito.when(substitute.performSubstitutionLogic(arguments));
	}

	public InvocationSubstitute<T> getSubstitute() {
		return substitute;
	}

	@Override
	public synchronized Object replay(Object... mocks) {
		return null;
	}

	@Override
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

	@SuppressWarnings("unchecked")
	@Override
	public synchronized Object reset(Object... mocks) {
		Mockito.reset(substitute);
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
