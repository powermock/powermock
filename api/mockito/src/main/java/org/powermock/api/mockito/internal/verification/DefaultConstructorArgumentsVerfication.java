package org.powermock.api.mockito.internal.verification;

import org.powermock.api.mockito.internal.invocationcontrol.MockitoNewInvocationControl;
import org.powermock.api.mockito.internal.invocationcontrol.NewInvocationControlAssertionError;
import org.powermock.api.mockito.verification.ConstructorArgumentsVerification;
import org.powermock.core.spi.NewInvocationControl;

public class DefaultConstructorArgumentsVerfication<T> implements ConstructorArgumentsVerification {

    private final MockitoNewInvocationControl<T> invocationControl;
    private final Class<?> type;

    @SuppressWarnings("unchecked")
    public DefaultConstructorArgumentsVerfication(NewInvocationControl<T> invocationControl, Class<?> type) {
        this.type = type;
        this.invocationControl = (MockitoNewInvocationControl<T>) invocationControl;
    }

    public void withArguments(Object argument, Object... arguments) throws Exception {
        final Object[] realArguments;
        if (argument == null && arguments.length == 0) {
            realArguments = null;
        } else {
            realArguments = new Object[arguments.length + 1];
            realArguments[0] = argument;
            System.arraycopy(arguments, 0, realArguments, 1, arguments.length);
        }
        invokeSubstitute(realArguments);
    }

    private void invokeSubstitute(Object... arguments) throws Exception {
        try {
            invocationControl.getSubstitute().performSubstitutionLogic(arguments);
        } catch (AssertionError e) {
            NewInvocationControlAssertionError.throwAssertionErrorForNewSubstitutionFailure(e, type);
        }
    }

    public void withNoArguments() throws Exception {
        invokeSubstitute(new Object[0]);
    }

}
