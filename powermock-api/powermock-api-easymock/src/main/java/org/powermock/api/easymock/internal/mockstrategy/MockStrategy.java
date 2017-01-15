package org.powermock.api.easymock.internal.mockstrategy;

import org.easymock.IMocksControl;

public interface MockStrategy {

	IMocksControl createMockControl(Class<?> type);

}
