package org.powermock.api.mockito.expectation;

public interface ConstructorArgumentsVerification {

    void withArguments(Object argument, Object... additionalArguments) throws Exception;

    void withNoArguments() throws Exception;
}
