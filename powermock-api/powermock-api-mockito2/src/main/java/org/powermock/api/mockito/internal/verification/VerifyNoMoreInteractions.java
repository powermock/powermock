/*
 *   Copyright 2016 the original author or authors.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package org.powermock.api.mockito.internal.verification;

import org.mockito.Mockito;
import org.powermock.api.mockito.invocation.MockitoMethodInvocationControl;
import org.powermock.api.mockito.internal.invocation.MockitoNewInvocationControl;
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
