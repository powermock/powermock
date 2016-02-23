/*
 *   Copyright 2016 the original author or authors.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package org.powermock.api.mockito.internal.expectation;

import org.mockito.stubbing.OngoingStubbing;
import org.powermock.api.mockito.expectation.WithOrWithoutExpectedArguments;

import java.lang.reflect.Constructor;

public class ConstructorAwareExpectationSetup<T> implements WithOrWithoutExpectedArguments<T> {

	private final Constructor<T> ctor;

	public ConstructorAwareExpectationSetup(Constructor<T> ctor) {
		if (ctor == null) {
			throw new IllegalArgumentException("Constructor to expect cannot be null");
		}
		this.ctor = ctor;
	}

	@Override
	public OngoingStubbing<T> withArguments(Object firstArgument, Object... additionalArguments) throws Exception {
		return setupExpectation().withArguments(firstArgument, additionalArguments);
	}

	@Override
	public OngoingStubbing<T> withNoArguments() throws Exception {
		return setupExpectation().withNoArguments();
	}

	private DefaultConstructorExpectationSetup<T> setupExpectation() {
		DefaultConstructorExpectationSetup<T> setup = new DefaultConstructorExpectationSetup<T>(ctor.getDeclaringClass());
		setup.setParameterTypes(ctor.getParameterTypes());
		return setup;
	}

}
