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

import org.mockito.internal.creation.DelegatingMethod;
import org.mockito.internal.debugging.LocationImpl;
import org.mockito.internal.invocation.InvocationImpl;
import org.mockito.internal.invocation.realmethod.CleanTraceRealMethod;
import org.mockito.internal.invocation.realmethod.RealMethod;
import org.mockito.internal.progress.SequenceNumber;
import org.mockito.invocation.Invocation;
import org.powermock.api.support.SafeExceptionRethrower;
import org.powermock.core.MockGateway;
import org.powermock.core.MockRepository;
import org.powermock.reflect.Whitebox;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

class InvocationFactory {
    
    Invocation createInvocation(final Object interceptionObject, final Method method, final Object... arguments) {
        final CleanTraceRealMethod cleanTraceRealMethod = createRealMethod(interceptionObject, method);
        
        return new InvocationImpl(
                                     interceptionObject,
                                     new DelegatingMethod(method),
                                     arguments,
                                     SequenceNumber.next(),
                                     cleanTraceRealMethod,
                                     new LocationImpl()
        );
    }
    
    private CleanTraceRealMethod createRealMethod(final Object interceptionObject, final Method method) {
        return new CleanTraceRealMethod(new RealMethod() {
            private static final long serialVersionUID = 4564320968038564170L;
            
            @Override
            public Object invoke(Object target, Object[] arguments) throws Throwable {
                    /*
                         * Instruct the MockGateway to don't intercept the next call.
                         * The reason is that when Mockito is spying on objects it
                         * should call the "real method" (which is proxied by Mockito
                         * anyways) so that we don't end up in here one more time which
                         * causes infinite recursion. This should not be done if the
                         * interceptionObject is a final system class because these are
                         * never caught by the Mockito proxy.
                         */
                final Class<?> type = Whitebox.getType(interceptionObject);
                final boolean isFinalSystemClass = type.getName().startsWith("java.") && Modifier.isFinal(type.getModifiers());
                if (!isFinalSystemClass) {
                    MockRepository.putAdditionalState(MockGateway.DONT_MOCK_NEXT_CALL, true);
                }
                try {
                    return method.invoke(target, arguments);
                } catch (InvocationTargetException e) {
                    SafeExceptionRethrower.safeRethrow(e.getCause());
                }
                return null;
            }
        });
    }
}
