package org.powermock.core.mockstrategy;

import org.easymock.IMocksControl;

public interface MockStrategy {

	IMocksControl createMockControl(Class<?> type);

}
