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

package org.powermock.api.mockito.mockmaker;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.creation.MockSettingsImpl;
import org.mockito.invocation.Invocation;
import org.mockito.invocation.MockHandler;
import org.mockito.mock.MockCreationSettings;
import org.mockito.plugins.MockMaker;
import org.powermock.api.mockito.ConfigurationTestUtils;
import org.powermock.api.mockito.mockmaker.PowerMockMaker;
import org.powermock.configuration.GlobalConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

public class PowerMockMakerTest {
    
    private ConfigurationTestUtils util;
    
    @Before
    public void setUp() throws Exception {
        util = new ConfigurationTestUtils();
        util.copyTemplateToPropertiesFile();
        GlobalConfiguration.clear();
    }
    
    @After
    public void tearDown() throws Exception {
        util.clear();
        GlobalConfiguration.clear();
    }
    
    @Test
    public void should_delegate_calls_to_mock_maker_from_configuration() {
        
        PowerMockMaker powerMockMaker = new PowerMockMaker();
        Object mock = powerMockMaker.createMock(new MockSettingsImpl<Object>(), new MockHandler() {
            @Override
            public Object handle(final Invocation invocation) throws Throwable {
                return null;
            }
        });
        
        MockMaker mockMaker = powerMockMaker.getMockMaker();
        
        assertThat(mockMaker)
            .as("Mock maker instance of configuration")
            .isInstanceOf(DelegateMockMakerStub.class);
        
        assertThat(((DelegateMockMakerStub) mockMaker).getMock())
            .as("Mock is created by delegated mock maker")
            .isSameAs(mock);
        
    }
    
    public static class DelegateMockMakerStub implements MockMaker {
        
        private final Object mock;
        
        public DelegateMockMakerStub() {
            this.mock = new Object();
        }
        
        @Override
        public <T> T createMock(final MockCreationSettings<T> settings, final MockHandler handler) {
            return (T) mock;
        }
        
        @Override
        public MockHandler getHandler(final Object mock) {
            return null;
        }
        
        @Override
        public void resetMock(final Object mock, final MockHandler newHandler, final MockCreationSettings settings) {
        
        }
        
        @Override
        public TypeMockability isTypeMockable(final Class<?> type) {
            return null;
        }
        
        private Object getMock() {
            return mock;
        }
    }
    
}