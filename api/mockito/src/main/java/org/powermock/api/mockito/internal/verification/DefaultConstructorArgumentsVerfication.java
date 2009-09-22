/*
 * Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.powermock.api.mockito.internal.verification;

import org.mockito.exceptions.base.MockitoAssertionError;
import org.powermock.api.mockito.internal.invocationcontrol.MockitoNewInvocationControl;
import org.powermock.api.mockito.internal.invocationcontrol.InvocationControlAssertionError;
import org.powermock.api.mockito.verification.ConstructorArgumentsVerification;
import org.powermock.core.spi.NewInvocationControl;

public class DefaultConstructorArgumentsVerfication<T> implements ConstructorArgumentsVerification {

    private final MockitoNewInvocationControl<T> invocationControl;
    private final Class<?> type;

    @SuppressWarnings("unchecked")
    public DefaultConstructorArgumentsVerfication(NewInvocationControl<T> invocationControl, Class<?> type) {
        this.type = type;
        this.invocationControl = (MockitoNewInvocationControl<T>) invocationControl;
    }

    public void withArguments(Object argument, Object... arguments) throws Exception {
        final Object[] realArguments;
        if (argument == null && arguments.length == 0) {
            realArguments = null;
        } else {
            realArguments = new Object[arguments.length + 1];
            realArguments[0] = argument;
            System.arraycopy(arguments, 0, realArguments, 1, arguments.length);
        }
        invokeSubstitute(realArguments);
    }

    private void invokeSubstitute(Object... arguments) throws Exception {
        try {
            invocationControl.getSubstitute().performSubstitutionLogic(arguments);
        } catch (MockitoAssertionError e) {
            InvocationControlAssertionError.throwAssertionErrorForNewSubstitutionFailure(e, type);
        }
    }

    public void withNoArguments() throws Exception {
        invokeSubstitute(new Object[0]);
    }

}
