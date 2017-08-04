/*
 *
 *   Copyright 2017 the original author or authors.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package org.powermock.api.mockito.invocation;

import org.mockito.Mockito;
import org.mockito.invocation.Invocation;
import org.mockito.mock.MockCreationSettings;
import org.powermock.api.support.SafeExceptionRethrower;
import org.powermock.core.MockGateway;
import org.powermock.core.MockRepository;
import org.powermock.reflect.Whitebox;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.Callable;

class InvocationFactory {
    
    Invocation createInvocation(final Object mock, final Method method, final MockCreationSettings settings,
                                final Object... arguments) {
        final Callable realMethod = createRealMethod(mock, method, arguments);
        return Mockito.framework().createInvocation(mock, settings, method, realMethod, arguments);
    }
    
    private Callable createRealMethod(final Object delegator, final Method method,
                                      final Object... arguments) {
        return new Callable() {
            @Override
            public Object call() throws Exception {
                final Class<?> type = Whitebox.getType(delegator);
                final boolean isFinalSystemClass = type.getName().startsWith("java.") && Modifier.isFinal(type.getModifiers());
                if (!isFinalSystemClass) {
                    MockRepository.putAdditionalState(MockGateway.DONT_MOCK_NEXT_CALL, true);
                }
                try {
                    return method.invoke(delegator, arguments);
                } catch (InvocationTargetException e) {
                    SafeExceptionRethrower.safeRethrow(e.getCause());
                }
                return null;
            }
        };
    }
}
