package org.powermock.api.mockito.internal.verification;

import org.mockito.Mockito;
import org.powermock.api.mockito.internal.invocationcontrol.MockitoMethodInvocationControl;
import org.powermock.api.mockito.internal.invocationcontrol.MockitoNewInvocationControl;
import org.powermock.core.MockRepository;

/**
 * Verifies no more interactions, delegates to Mockito if PowerMockito doesn't
 * find a supplied mock.
 */
public class VerifyNoMoreInteractions {

    public static void verifyNoMoreInteractions(Object... objects) {
        for (Object mock : objects) {
            if (mock instanceof Class<?>) {
                verifyNoMoreInteractions((Class<?>) mock);
            } else {
                MockitoMethodInvocationControl invocationControl = (MockitoMethodInvocationControl) MockRepository
                        .getInstanceMethodInvocationControl(mock);
                if (invocationControl != null) {
                    invocationControl.verifyNoMoreInteractions();
                } else {
                    /*
                     * Delegate to Mockito if we have no handler registered for
                     * this object.
                     */
                    Mockito.verifyNoMoreInteractions(mock);
                }
            }
        }
    }

    private static void verifyNoMoreInteractions(Class<?>... types) {
        for (Class<?> type : types) {
            final MockitoMethodInvocationControl invocationHandler = (MockitoMethodInvocationControl) MockRepository
                    .getStaticMethodInvocationControl(type);
            if (invocationHandler != null) {
                invocationHandler.verifyNoMoreInteractions();
            }
            MockitoNewInvocationControl<?> newInvocationControl = (MockitoNewInvocationControl<?>) MockRepository.getNewInstanceControl(type);
            if (newInvocationControl != null) {
                newInvocationControl.verifyNoMoreInteractions();
            }
        }
    }
}
