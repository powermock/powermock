package org.powermock.core.spi;

import org.powermock.core.spi.support.InvocationSubstitute;

/**
 * A new invocation control pairs up a {@link InvocationSubstitute} with the
 * mock object created when invoking
 * {@link InvocationSubstitute#performSubstitutionLogic(Object...)} object.
 * 
 */
public interface NewInvocationControl<T> extends DefaultBehavior {

    /**
     * Invoke the invocation control
     */
    Object invoke(Class<?> type, Object[] args, Class<?>[] sig) throws Exception;

    /**
     * Expect a call to the new instance substitution logic.
     */
    T expectSubstitutionLogic(Object... arguments) throws Exception;
}
