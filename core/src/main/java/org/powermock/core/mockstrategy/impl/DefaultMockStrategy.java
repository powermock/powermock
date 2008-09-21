package org.powermock.core.mockstrategy.impl;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.powermock.core.mockstrategy.MockStrategy;

public class DefaultMockStrategy implements MockStrategy {

	public IMocksControl createMockControl(Class<?> type) {
		IMocksControl control = null;
		if (type.isInterface()) {
			control = EasyMock.createControl();
		} else {
			control = org.easymock.classextension.EasyMock.createControl();
		}
		return control;
	}

}
