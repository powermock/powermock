package org.powermock.api.mockito.expectation;

public interface ConstructorExpectationSetup<T> extends ExpectedConstructorArguments<T> {

    ExpectedConstructorWithArguments<T> withParameterTypes(Class<?> parameterType, Class<?>... additionalParameterTypes);

}
