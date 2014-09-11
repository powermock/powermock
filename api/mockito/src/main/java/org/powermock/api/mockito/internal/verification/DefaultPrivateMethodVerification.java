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
package org.powermock.api.mockito.internal.verification;

import org.powermock.api.mockito.verification.PrivateMethodVerification;
import org.powermock.api.mockito.verification.WithOrWithoutVerifiedArguments;
import org.powermock.reflect.Whitebox;
import org.powermock.tests.utils.impl.ArrayMergerImpl;

import java.lang.reflect.Method;

public class DefaultPrivateMethodVerification implements PrivateMethodVerification {

	private final Object objectToVerify;

	public DefaultPrivateMethodVerification(Object objectToVerify) {
		this.objectToVerify = objectToVerify;
	}

	public void invoke(Object... arguments) throws Exception {
		Whitebox.invokeMethod(objectToVerify, arguments);

	}

	public void invoke(String methodToExecute, Object... arguments) throws Exception {
		Whitebox.invokeMethod(objectToVerify, methodToExecute, (Object[]) arguments);
	}

	public WithOrWithoutVerifiedArguments invoke(Method method) throws Exception {
		return new VerificationArguments(method);
	}

	private class VerificationArguments implements WithOrWithoutVerifiedArguments {
		private final Method method;

		public VerificationArguments(Method method) {
			if (method == null) {
				throw new IllegalArgumentException("method cannot be null");
			}
			this.method = method;
			this.method.setAccessible(true);
		}

		public void withArguments(Object firstArgument, Object... additionalArguments) throws Exception {
			if (additionalArguments == null || additionalArguments.length == 0) {
				method.invoke(objectToVerify, firstArgument);
			} else {
                Object[] arguments = new ArrayMergerImpl().mergeArrays(Object.class, new Object[]{firstArgument}, additionalArguments);
                method.invoke(objectToVerify, arguments);
			}
		}

		public void withNoArguments() throws Exception {
			method.invoke(objectToVerify);
		}
	}
}
