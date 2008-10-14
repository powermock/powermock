package org.powermock.core.mockstrategy.impl;

import org.easymock.IMocksControl;
import org.easymock.internal.MocksControl;
import org.easymock.internal.MocksControl.MockType;
import org.powermock.core.mockstrategy.MockStrategy;
import org.powermock.core.signedsupport.SignedSupportingMocksClassControl;

/**
 * Base class that should be used by all mock strategies. Enables mocking of
 * signed classes.
 */
public abstract class AbstractMockStrategyBase implements MockStrategy {

	private final MockType mockType;

	public AbstractMockStrategyBase(MockType mockType) {
		if (mockType == null) {
			throw new IllegalArgumentException("Internal error: mockType cannot be null");
		}
		this.mockType = mockType;
	}

	public IMocksControl createMockControl(Class<?> type) {
		IMocksControl control = null;
		if (type.isInterface()) {
			control = new MocksControl(mockType);
		} else {
			control = new SignedSupportingMocksClassControl(mockType);
		}
		return control;
	}
}
