package org.powermock.api.mockito.internal.expectation;

import java.lang.reflect.Method;

import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;
import org.powermock.api.mockito.expectation.WithOrWithoutExpectedArguments;

public class DefaultMethodExpectationSetup<T> implements WithOrWithoutExpectedArguments<T> {

	private final Object object;
	private final Method method;

	public DefaultMethodExpectationSetup(Object object, Method method) {
		if (object == null) {
			throw new IllegalArgumentException("object to expect cannot be null");
		} else if (method == null) {
			throw new IllegalArgumentException("method to expect cannot be null");
		}
		this.object = object;
		this.method = method;
		this.method.setAccessible(true);
	}

	@SuppressWarnings("unchecked")
	public OngoingStubbing<T> withArguments(Object firstArgument, Object... additionalArguments) throws Exception {
		if (additionalArguments == null || additionalArguments.length == 0) {
			return (OngoingStubbing<T>) Mockito.when(method.invoke(object, firstArgument));
		} else {
			return (OngoingStubbing<T>) Mockito.when(method.invoke(object, firstArgument, additionalArguments));
		}
	}

	@SuppressWarnings("unchecked")
	public OngoingStubbing<T> withNoArguments() throws Exception {
		return (OngoingStubbing<T>) Mockito.when(method.invoke(object));
	}
}
