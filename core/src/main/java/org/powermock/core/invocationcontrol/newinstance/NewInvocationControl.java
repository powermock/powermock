package org.powermock.core.invocationcontrol.newinstance;

import org.easymock.IExpectationSetters;

/**
 * A new invocation control pairs up a {@link NewInvocationSubstitute} with the
 * mock object created when invoking
 * {@link NewInvocationSubstitute#createInstance(Object...)} object.
 */
public interface NewInvocationControl<T> {

	NewInvocationSubstitute<T> getNewInvocationSubstitute();

	IExpectationSetters<T> invoke(Object... arguments) throws Exception;
	
	Class<T> getType();
}
