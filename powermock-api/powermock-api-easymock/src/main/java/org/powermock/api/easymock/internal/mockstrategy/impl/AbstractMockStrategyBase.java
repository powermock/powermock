package org.powermock.api.easymock.internal.mockstrategy.impl;

import org.easymock.IMocksControl;
import org.easymock.internal.MocksControl;
import org.easymock.MockType;
import org.powermock.api.easymock.internal.mockstrategy.MockStrategy;

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

	@Override
	public IMocksControl createMockControl(Class<?> type) {
		return new MocksControl(mockType);
	}
}
