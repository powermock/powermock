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

import org.mockito.MockSettings;
import org.mockito.Mockito;
import org.powermock.api.mockito.invocation.MockitoMethodInvocationControl;
import org.powermock.core.ClassReplicaCreator;
import org.powermock.core.DefaultFieldValueGenerator;
import org.powermock.core.MockRepository;
import org.powermock.core.classloader.MockClassLoader;
import org.powermock.reflect.Whitebox;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.powermock.utils.Asserts.assertNotNull;

public class DefaultMockCreator extends AbstractMockCreator {
    
    private static final DefaultMockCreator MOCK_CREATOR = new DefaultMockCreator();
    
    @SuppressWarnings("unchecked")
    public static <T> T mock(Class<T> type, boolean isStatic, boolean isSpy, Object delegator,
                             MockSettings mockSettings, Method... methods) {
        return MOCK_CREATOR.createMock(type, isStatic, isSpy, delegator, mockSettings, methods);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T createMock(Class<T> type, boolean isStatic, boolean isSpy, Object delegatorCandidate,
                            MockSettings mockSettings, Method... methods) {
        
        assertNotNull(type, "The class to mock cannot be null");
        
        validateType(type, isStatic, isSpy);
    
        registerAfterMethodRunner();
    
        return doCreateMock(type, isStatic, isSpy, delegatorCandidate, mockSettings, methods);
    }
    
    private <T> T doCreateMock(final Class<T> type, final boolean isStatic, final boolean isSpy, final Object delegatorCandidate,
                               final MockSettings mockSettings, final Method[] methods) {
        final Class<T> typeToMock = getMockType(type);
        
        final Object delegator = isSpy && delegatorCandidate == null ? new Object() : delegatorCandidate;
        
        final MockData<T> mockData = createMethodInvocationControl(typeToMock, methods, delegator, mockSettings);
        
        T mock = mockData.getMock();
        if (isFinalJavaSystemClass(type) && !isStatic) {
            mock = Whitebox.newInstance(type);
            DefaultFieldValueGenerator.fillWithDefaultValues(mock);
        }
        
        putMethodInvocationControlToRepository(type, isStatic, mockData, mock);
        
        return mock;
    }
    
    private void registerAfterMethodRunner() {
        MockRepository.addAfterMethodRunner(new Runnable() {
            @Override
            public void run() {
                Mockito.reset();
            }
        });
    }
    
    private <T> void putMethodInvocationControlToRepository(final Class<T> type, final boolean isStatic, final MockData<T> mockData, final T mock) {
        if (isStatic) {
            MockRepository.putStaticMethodInvocationControl(type, mockData.getMethodInvocationControl());
        } else {
            MockRepository.putInstanceMethodInvocationControl(mock, mockData.getMethodInvocationControl());
        }
    }
    
    private <T> Class<T> getMockType(final Class<T> type) {
        final Class<T> typeToMock;
        if (isFinalJavaSystemClass(type)) {
            typeToMock = new ClassReplicaCreator().createClassReplica(type);
        } else {
            typeToMock = type;
        }
        return typeToMock;
    }
    
    private static <T> boolean isFinalJavaSystemClass(Class<T> type) {
        return type.getName().startsWith("java.") && Modifier.isFinal(type.getModifiers());
    }
    
    @SuppressWarnings("unchecked")
    private <T> MockData<T> createMethodInvocationControl(Class<T> type, Method[] methods, Object delegator, MockSettings mockSettings) {
        final T mock = Mockito.mock(type, mockSettings != null ? mockSettings : Mockito.withSettings());
        
        cacheMockClass(mock.getClass());
        
        return new MockData<T>(new MockitoMethodInvocationControl(delegator, mock, methods), mock);
    }
    
    private void cacheMockClass(final Class<?> mockClass) {
        ClassLoader classLoader = mockClass.getClassLoader();
        if (classLoader instanceof MockClassLoader) {
            MockClassLoader mcl = (MockClassLoader) classLoader;
            mcl.cache(mockClass);
        }
    }
    
    /**
     * Class that encapsulate a mock and its corresponding invocation control.
     */
    private static class MockData<T> {
        private final MockitoMethodInvocationControl methodInvocationControl;
        
        private final T mock;
        
        private MockData(MockitoMethodInvocationControl methodInvocationControl, T mock) {
            this.methodInvocationControl = methodInvocationControl;
            this.mock = mock;
        }
        
        private MockitoMethodInvocationControl getMethodInvocationControl() {
            return methodInvocationControl;
        }
        
        private T getMock() {
            return mock;
        }
    }
}
