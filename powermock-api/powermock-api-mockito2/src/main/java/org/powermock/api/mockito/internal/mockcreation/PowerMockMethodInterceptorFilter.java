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

package org.powermock.api.mockito.internal.mockcreation;

import org.mockito.internal.InternalMockHandler;
import org.mockito.mock.MockCreationSettings;
import org.powermock.api.mockito.repackaged.MethodInterceptorFilter;
import org.powermock.api.mockito.repackaged.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

class PowerMockMethodInterceptorFilter extends MethodInterceptorFilter {

    public PowerMockMethodInterceptorFilter(InternalMockHandler handler,
            MockCreationSettings mockSettings) {
        super(handler, mockSettings);
    }

    @Override
    public Object intercept(Object proxy, Method method, Object[] args,
            MethodProxy methodProxy) throws Throwable {
        Object intercept;
        try {
            intercept = super.intercept(proxy, method, args, methodProxy);
        } catch (RuntimeExceptionProxy p) {
            throw p.getCause();
        }
        if ("finalize".equals(method.getName())) {
            MockitoStateCleaner cleaner = new MockitoStateCleaner();
            cleaner.clearConfiguration();
            cleaner.clearMockProgress();
        }
        return intercept;
    }

}
