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

package org.powermock.api.mockito.expectation;

import org.mockito.ArgumentMatchers;
import org.mockito.stubbing.OngoingStubbing;
import org.powermock.api.mockito.internal.expectation.DelegatingToConstructorsOngoingStubbing;
import org.powermock.api.mockito.internal.invocation.MockitoNewInvocationControl;
import org.powermock.api.mockito.internal.mockcreation.DefaultMockCreator;
import org.powermock.core.MockRepository;
import org.powermock.core.spi.NewInvocationControl;
import org.powermock.core.spi.support.InvocationSubstitute;
import org.powermock.reflect.internal.WhiteboxImpl;
import org.powermock.tests.utils.ArrayMerger;
import org.powermock.tests.utils.impl.ArrayMergerImpl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class DefaultConstructorExpectationSetup<T> implements ConstructorExpectationSetup<T> {
    
    private final Class<T> mockType;
    private final ArrayMerger arrayMerger;
    private final DefaultMockCreator mockCreator;
    private Class<?>[] parameterTypes = null;
    private final InvocationSubstitute mock;
    
    public DefaultConstructorExpectationSetup(Class<T> mockType) {
        this.arrayMerger = new ArrayMergerImpl();
        this.mockType = mockType;
        this.mockCreator = new DefaultMockCreator();
        this.mock = getMockCreator().createMock(InvocationSubstitute.class, false, false, null, null, (Method[]) null);
    }
    
    @Override
    public OngoingStubbing<T> withArguments(Object firstArgument, Object... additionalArguments) throws Exception {
        return createNewSubstituteMock(mockType, parameterTypes, arrayMerger.mergeArrays(Object.class, new Object[]{firstArgument},
                                                                                         additionalArguments));
    }
    
    @SuppressWarnings({"unchecked", "rawtypes"})
    private OngoingStubbing<T> createNewSubstituteMock(Class<T> type, Class<?>[] parameterTypes, Object... arguments) throws Exception {
        if (type == null) {
            throw new IllegalArgumentException("type cannot be null");
        }
        
        final Class<T> unmockedType = (Class<T>) WhiteboxImpl.getOriginalUnmockedType(type);
        if (parameterTypes == null) {
            WhiteboxImpl.findUniqueConstructorOrThrowException(type, arguments);
        } else {
            WhiteboxImpl.getConstructor(unmockedType, parameterTypes);
        }
        
        NewInvocationControl<OngoingStubbing<T>> newInvocationControl = createNewInvocationControl(type, unmockedType);
        
        return newInvocationControl.expectSubstitutionLogic(arguments);
    }
    
    private NewInvocationControl<OngoingStubbing<T>> createNewInvocationControl(final Class<T> type, final Class<T> unmockedType) {
        /*
        * Check if this type has been mocked before
        */
        NewInvocationControl<OngoingStubbing<T>> newInvocationControl =
            (NewInvocationControl<OngoingStubbing<T>>) MockRepository.getNewInstanceControl(unmockedType);
        if (newInvocationControl == null) {
            newInvocationControl = createNewInvocationControl(mock);
            MockRepository.putNewInstanceControl(type, newInvocationControl);
            MockRepository.addObjectsToAutomaticallyReplayAndVerify(WhiteboxImpl.getOriginalUnmockedType(type));
        }
        return newInvocationControl;
    }
    
    @Override
    public OngoingStubbing<T> withAnyArguments() throws Exception {
        if (mockType == null) {
            throw new IllegalArgumentException("Class to expected cannot be null");
        }
        final Class<T> unmockedType = (Class<T>) WhiteboxImpl.getOriginalUnmockedType(mockType);
        final Constructor<?>[] allConstructors = WhiteboxImpl.getAllConstructors(unmockedType);
        final Constructor<?> constructor = allConstructors[0];
        final Class<?>[] parameterTypes = constructor.getParameterTypes();
        Object[] paramArgs = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> paramType = parameterTypes[i];
            paramArgs[i] = createParamArgMatcher(paramType);
        }
        Constructor<?>[] otherCtors = new Constructor<?>[allConstructors.length - 1];
        System.arraycopy(allConstructors, 1, otherCtors, 0, allConstructors.length - 1);
        
        final OngoingStubbing<T> ongoingStubbing = createNewSubstituteMock(mockType, parameterTypes, paramArgs);
        return new DelegatingToConstructorsOngoingStubbing<T>(otherCtors, ongoingStubbing);
    }
    
    private Object createParamArgMatcher(Class<?> paramType) {
        return ArgumentMatchers.nullable(paramType);
    }
    
    @Override
    public OngoingStubbing<T> withNoArguments() throws Exception {
        return createNewSubstituteMock(mockType, parameterTypes);
    }
    
    @Override
    public WithExpectedArguments<T> withParameterTypes(Class<?> parameterType, Class<?>... additionalParameterTypes) {
        this.parameterTypes = arrayMerger.mergeArrays(Class.class, new Class<?>[]{parameterType}, additionalParameterTypes);
        return this;
    }
    
    private DefaultMockCreator getMockCreator() {return mockCreator;}
    
    private NewInvocationControl<OngoingStubbing<T>> createNewInvocationControl(InvocationSubstitute<T> mock) {
        return new MockitoNewInvocationControl<T>(mock);
    }
    
    void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }
}
