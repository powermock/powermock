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
package org.powermock.api.mockito.internal.expectation;

import org.mockito.Matchers;
import org.mockito.stubbing.OngoingStubbing;
import org.powermock.api.mockito.internal.invocation.MockitoNewInvocationControl;
import org.powermock.api.mockito.internal.mockcreation.DefaultMockCreator;
import org.powermock.api.mockito.internal.mockcreation.MockCreator;
import org.powermock.core.spi.NewInvocationControl;
import org.powermock.core.spi.support.InvocationSubstitute;

public class DefaultConstructorExpectationSetup<T> extends AbstractConstructorExpectationSetup<T> {

    public DefaultConstructorExpectationSetup(Class<T> mockType) {
        super(mockType);
    }

    MockCreator getMockCreator() {return new DefaultMockCreator();}

    <T> NewInvocationControl<OngoingStubbing<T>> createNewInvocationControl(InvocationSubstitute<T> mock) {
        return new MockitoNewInvocationControl(mock);
    }

    Object createParamArgMatcher(Class<?> paramType) {
        return Matchers.any(paramType);
    }
}
