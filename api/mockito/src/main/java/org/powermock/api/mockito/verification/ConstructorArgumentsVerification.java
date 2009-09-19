package org.powermock.api.mockito.verification;

public interface ConstructorArgumentsVerification {

    void withArguments(Object argument, Object... additionalArguments) throws Exception;

    void withNoArguments() throws Exception;
}
