package org.powermock.api.mockito.expectation;

import org.mockito.stubbing.OngoingStubbing;

public interface ExpectedConstructorWithArguments<T> {

    public abstract OngoingStubbing<T> withArguments(Object firstArgument, Object... additionalArguments) throws Exception;

}