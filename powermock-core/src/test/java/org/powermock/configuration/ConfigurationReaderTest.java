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

import org.junit.Before;
import org.junit.Test;
import org.powermock.configuration.support.ConfigurationReaderImpl;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfigurationReaderTest {
    
    private ConfigurationReader reader;
    
    @Before
    public void setUp() throws Exception {
        reader = new ConfigurationReaderImpl("org/powermock/configuration/test.properties");
    }
    
    @Test
    public void should_read_configuration_from_properties() {
        
        Configuration configuration =  reader.read();
        
        assertThat(configuration)
            .as("Configuration is read")
            .isNotNull();
    }
    
    @Test
    public void should_read_mock_maker_class_from_configuration() {
        MockitoConfiguration configuration = (MockitoConfiguration) reader.read();
    
        assertThat(configuration.getMockMakerClass())
            .as("Configuration is read")
            .isEqualTo("TestMockMaker");
    }
    
    @Test
    public void should_return_null_when_configuration_file_non_exist() {
        
        assertThat(new ConfigurationReaderImpl("test.properties").read())
            .as("Null is returned")
            .isNull();
    }
}
