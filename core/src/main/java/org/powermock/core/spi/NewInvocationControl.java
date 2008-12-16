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
	 * Perform new instance substitution logic.
	 */
	T performSubstitutionLogic(Object... arguments) throws Exception;
}
