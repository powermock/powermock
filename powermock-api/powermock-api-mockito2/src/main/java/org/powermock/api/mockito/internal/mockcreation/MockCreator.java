package org.powermock.api.mockito.internal.mockcreation;

import java.lang.reflect.Method;

/**
 * An implementer of interface is reasonable for creating of an mocked instance of specific type.
 */
public interface MockCreator {
    <T> T createMock(Class<T> type, boolean isStatic, boolean isSpy, Object delegator, org.mockito.MockSettings mockSettings, Method... methods);
}
