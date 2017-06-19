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
import org.mockito.internal.configuration.plugins.Plugins;
import org.mockito.internal.handler.MockHandlerFactory;
import org.mockito.internal.util.reflection.LenientCopyTool;
import org.mockito.invocation.MockHandler;
import org.mockito.mock.MockCreationSettings;
import org.mockito.plugins.MockMaker;
import org.powermock.api.mockito.internal.invocation.MockitoMethodInvocationControl;
import org.powermock.core.ClassReplicaCreator;
import org.powermock.core.DefaultFieldValueGenerator;
import org.powermock.core.MockRepository;
import org.powermock.core.classloader.MockClassLoader;
import org.powermock.reflect.Whitebox;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class DefaultMockCreator extends AbstractMockCreator {
    
    
    private static final DefaultMockCreator MOCK_CREATOR = new DefaultMockCreator();
    
    @SuppressWarnings("unchecked")
    public static <T> T mock(Class<T> type, boolean isStatic, boolean isSpy, Object delegator,
                             MockSettings mockSettings, Method... methods) {
        return MOCK_CREATOR.createMock(type, isStatic, isSpy, delegator, mockSettings, methods);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T createMock(Class<T> type, boolean isStatic, boolean isSpy, Object delegator,
                            MockSettings mockSettings, Method... methods) {
        if (type == null) {
            throw new IllegalArgumentException("The class to mock cannot be null");
        }
        
        validateType(type, isStatic, isSpy);
        
        MockRepository.addAfterMethodRunner(new MockitoStateCleanerRunnable());
        
        final Class<T> typeToMock;
        if (isFinalJavaSystemClass(type)) {
            typeToMock = (Class<T>) new ClassReplicaCreator().createClassReplica(type);
        } else {
            typeToMock = type;
        }
        
        final MockData<T> mockData = createMethodInvocationControl(typeToMock, methods, isSpy, delegator, mockSettings);
        
        T mock = mockData.getMock();
        if (isFinalJavaSystemClass(type) && !isStatic) {
            mock = Whitebox.newInstance(type);
            DefaultFieldValueGenerator.fillWithDefaultValues(mock);
        }
        
        if (isStatic) {
            MockRepository.putStaticMethodInvocationControl(type, mockData.getMethodInvocationControl());
        } else {
            MockRepository.putInstanceMethodInvocationControl(mock, mockData.getMethodInvocationControl());
        }
        
        if (isSpy) {
            new LenientCopyTool().copyToMock(delegator, mock);
        }
        
        return mock;
    }
    
    private static <T> boolean isFinalJavaSystemClass(Class<T> type) {
        return type.getName().startsWith("java.") && Modifier.isFinal(type.getModifiers());
    }
    
    @SuppressWarnings("unchecked")
    private <T> MockData<T> createMethodInvocationControl(Class<T> type, Method[] methods, boolean isSpy, Object delegator,
                                                          MockSettings mockSettings) {
        final MockMaker mockMaker = getMockMaker();
        
        final MockCreationSettings<T> settings = getMockSettings(type, mockSettings);
        
        MockHandler mockHandler = MockHandlerFactory.createMockHandler(settings);
        
        T mock = mockMaker.createMock(settings, mockHandler);
        
        ClassLoader classLoader = mock.getClass().getClassLoader();
        if (classLoader instanceof MockClassLoader) {
            MockClassLoader mcl = (MockClassLoader) classLoader;
            mcl.cache(mock.getClass());
        }
        final MockitoMethodInvocationControl invocationControl =
            new MockitoMethodInvocationControl(
                                                  mockHandler,
                                                  isSpy && delegator == null ? new Object() : delegator,
                                                  mock,
                                                  methods
            );
        
        return new MockData<T>(invocationControl, mock);
    }
    
    private static <T> MockCreationSettings<T> getMockSettings(final Class<T> type, final MockSettings mockSettings) {
        MockSettings settings = mockSettings;
        if (mockSettings == null) {
            settings = Mockito.withSettings();
        }
        return settings.build(type);
    }
    
    private static MockMaker getMockMaker() {
        final ClassLoader originalCL = Thread.currentThread().getContextClassLoader();
        
        Thread.currentThread().setContextClassLoader(DefaultMockCreator.class.getClassLoader());
        
        try {
            return Plugins.getMockMaker();
        } finally {
            Thread.currentThread().setContextClassLoader(originalCL);
        }
    }
    
    /**
     * Class that encapsulate a mock and its corresponding invocation control.
     */
    private static class MockData<T> {
        private final MockitoMethodInvocationControl methodInvocationControl;
        
        private final T mock;
        
        MockData(MockitoMethodInvocationControl methodInvocationControl, T mock) {
            this.methodInvocationControl = methodInvocationControl;
            this.mock = mock;
        }
        
        public MockitoMethodInvocationControl getMethodInvocationControl() {
            return methodInvocationControl;
        }
        
        public T getMock() {
            return mock;
        }
    }
    
    /**
     * Clear state in Mockito that retains memory between tests
     */
    private static class MockitoStateCleanerRunnable implements Runnable {
        public void run() {
            MockitoStateCleaner cleaner = new MockitoStateCleaner();
            cleaner.clearConfiguration();
            cleaner.clearMockProgress();
        }
        
    }
}
