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
package org.powermock.api.mockito.mockmaker;

import org.mockito.Mockito;
import org.mockito.invocation.Invocation;
import org.mockito.invocation.InvocationContainer;
import org.mockito.invocation.MockHandler;
import org.mockito.mock.MockCreationSettings;
import org.mockito.plugins.MockMaker;
import org.powermock.api.mockito.invocation.MockitoMethodInvocationControl;
import org.powermock.configuration.GlobalConfiguration;
import org.powermock.core.MockRepository;

/**
 * A PowerMock implementation of the MockMaker.
 */
public class PowerMockMaker implements MockMaker {
    private final MockMaker mockMaker;
    
    public PowerMockMaker() {
        mockMaker = new MockMakerLoader().load(GlobalConfiguration.mockitoConfiguration());
    }
    
    @Override
    public <T> T createMock(MockCreationSettings<T> settings, MockHandler handler) {
        return mockMaker.createMock(settings, handler);
    }
    
    @Override
    public MockHandler getHandler(Object mock) {
        // Return a fake mock handler for static method mocks
        if (mock instanceof Class) {
            return new StaticMockHandler(createStaticMockSettings((Class) mock));
        } else {
            final MockitoMethodInvocationControl invocationControl = (MockitoMethodInvocationControl) MockRepository.getInstanceMethodInvocationControl(mock);
            final Object realMock;
            if (invocationControl == null){
                realMock = mock;
            }else{
                realMock = invocationControl.getMockHandlerAdaptor().getMock();
    
            }
            return mockMaker.getHandler(realMock);
        }
    }
    
    @Override
    public void resetMock(Object mock, MockHandler newHandler, MockCreationSettings settings) {
        mockMaker.resetMock(mock, newHandler, settings);
    }
    
    @Override
    public TypeMockability isTypeMockable(Class<?> type) {
        return mockMaker.isTypeMockable(type);
    }
    
    MockMaker getMockMaker() {
        return mockMaker;
    }
    
    @SuppressWarnings("unchecked")
    private MockCreationSettings<Class> createStaticMockSettings(final Class mock) {
        return Mockito.withSettings()
                      .name(mock.getName())
                      .build((Class<Class>) mock);
    }
    
    private static class StaticMockHandler implements MockHandler<Class> {
        private final MockCreationSettings<Class> mockSettings;
        
        private StaticMockHandler(final MockCreationSettings<Class> mockSettings) {
            this.mockSettings = mockSettings;
        }
        
        @Override
        public MockCreationSettings<Class> getMockSettings() {
            return mockSettings;
        }
        
        @Override
        public InvocationContainer getInvocationContainer() {
            return null;
        }
        
        @Override
        public Object handle(Invocation invocation) throws Throwable {
            return null;
        }
    }
}
