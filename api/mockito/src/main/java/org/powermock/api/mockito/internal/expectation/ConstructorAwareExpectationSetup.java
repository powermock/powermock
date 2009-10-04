package org.powermock.api.mockito.internal.expectation;

import java.lang.reflect.Constructor;

import org.mockito.stubbing.OngoingStubbing;
import org.powermock.api.mockito.expectation.WithOrWithoutExpectedArguments;

public class ConstructorAwareExpectationSetup<T> implements WithOrWithoutExpectedArguments<T> {

	private final Constructor<T> ctor;

	public ConstructorAwareExpectationSetup(Constructor<T> ctor) {
		if (ctor == null) {
			throw new IllegalArgumentException("Constructor to expect cannot be null");
		}
		this.ctor = ctor;
	}

	public OngoingStubbing<T> withArguments(Object firstArgument, Object... additionalArguments) throws Exception {
		return setupExpectation().withArguments(firstArgument, additionalArguments);
	}

	public OngoingStubbing<T> withNoArguments() throws Exception {
		return setupExpectation().withNoArguments();
	}

	private DefaultConstructorExpectationSetup<T> setupExpectation() {
		DefaultConstructorExpectationSetup<T> setup = new DefaultConstructorExpectationSetup<T>(ctor.getDeclaringClass());
		setup.setParameterTypes(ctor.getParameterTypes());
		return setup;
	}

}
