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

package org.powermock.configuration;

public class MockitoConfiguration implements Configuration<MockitoConfiguration> {
    
    private String mockMakerClass;
    
    public MockitoConfiguration() {
        // Used by configuration reader to create an instance
    }
    
    private MockitoConfiguration(final String mockMakerClass) {
        this.mockMakerClass = mockMakerClass;
    }
    
    public String getMockMakerClass() {
        return mockMakerClass;
    }
    
    public void setMockMakerClass(final String mockMakerClass) {
        this.mockMakerClass = mockMakerClass;
    }
    
    @Override
    public MockitoConfiguration merge(final MockitoConfiguration configuration) {
        if (configuration != null && configuration.getMockMakerClass() != null) {
            return new MockitoConfiguration(configuration.getMockMakerClass());
        } else {
            return this;
        }
    }
}
