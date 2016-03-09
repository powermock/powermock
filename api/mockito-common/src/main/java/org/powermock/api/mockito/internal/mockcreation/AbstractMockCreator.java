package org.powermock.api.mockito.internal.mockcreation;

import org.mockito.MockSettings;
import org.powermock.core.agent.JavaAgentClassRegister;

import java.lang.reflect.Method;

/**
 *
 */
public abstract class AbstractMockCreator {

    private JavaAgentClassRegister agentClassRegister;

    protected abstract <T> T createMock(Class<T> type, boolean isStatic, boolean isSpy, Object delegator,
                                     MockSettings mockSettings, Method... methods);

    <T> void validateType(Class<T> type, boolean isStatic, boolean isSpy) {
        createTypeValidator(type, isStatic, isSpy).validate();
    }

    private <T> MockTypeValidator<T> createTypeValidator(Class<T> type, boolean isStatic, boolean isSpy) {
        return MockTypeValidatorFactory.createValidator(type, isStatic, isSpy, agentClassRegister);
    }
}
