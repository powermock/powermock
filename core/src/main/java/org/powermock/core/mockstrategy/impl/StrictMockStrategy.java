package org.powermock.core.mockstrategy.impl;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.powermock.core.mockstrategy.MockStrategy;

public class StrictMockStrategy implements MockStrategy {

	public IMocksControl createMockControl(Class<?> type) {
		IMocksControl control = null;
		if (type.isInterface()) {
			control = EasyMock.createStrictControl();
		} else {
			control = org.easymock.classextension.EasyMock
					.createStrictControl();
		}
		return control;
	}

}
