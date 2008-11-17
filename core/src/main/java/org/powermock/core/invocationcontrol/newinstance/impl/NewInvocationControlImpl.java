package org.powermock.core.invocationcontrol.newinstance.impl;

import org.easymock.EasyMock;
import org.easymock.IExpectationSetters;
import org.powermock.core.invocationcontrol.newinstance.NewInvocationControl;
import org.powermock.core.invocationcontrol.newinstance.NewInvocationSubstitute;

public class NewInvocationControlImpl<T> implements NewInvocationControl<T> {
	private final NewInvocationSubstitute<T> substitute;
	private final Class<T> type;

	public NewInvocationControlImpl(NewInvocationSubstitute<T> substitute, Class<T> type) {
		if (substitute == null) {
			throw new IllegalArgumentException("Internal error: substitute cannot be null.");
		}
		this.type = type;
		this.substitute = substitute;
	}

	public NewInvocationSubstitute<T> getNewInvocationSubstitute() {
		return substitute;
	}

	public IExpectationSetters<T> invoke(Object... arguments) throws Exception {
		return EasyMock.expect(substitute.createInstance(arguments));
	}

	public Class<T> getType() {
		return type;
	}
}
