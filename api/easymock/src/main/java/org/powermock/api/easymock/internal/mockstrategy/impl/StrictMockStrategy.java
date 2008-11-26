package org.powermock.api.easymock.internal.mockstrategy.impl;

import org.easymock.internal.MocksControl.MockType;

public class StrictMockStrategy extends AbstractMockStrategyBase {

	public StrictMockStrategy() {
		super(MockType.STRICT);
	}
}
