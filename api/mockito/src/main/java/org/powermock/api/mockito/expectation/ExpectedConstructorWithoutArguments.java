package org.powermock.api.mockito.expectation;

import org.mockito.stubbing.OngoingStubbing;

public interface ExpectedConstructorWithoutArguments<T> {

    public abstract OngoingStubbing<T> withNoArguments() throws Exception;

}