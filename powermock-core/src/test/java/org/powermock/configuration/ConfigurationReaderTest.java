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
import org.powermock.configuration.support.ConfigurationReaderBuilder;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfigurationReaderTest {
    
    private static final String CONF_PATH = "org/powermock/configuration";
    private static final String CONFIGURATION_FILE = CONF_PATH + "/test.properties";
    private ConfigurationReader reader;
    
    @Before
    public void setUp() throws Exception {
        reader = ConfigurationReaderBuilder.newBuilder()
                                           .forConfigurationFile(CONFIGURATION_FILE)
                                           .build();
    }
    
    @Test
    public void should_read_configuration_from_properties() {
        
        Configuration configuration =  reader.read(MockitoConfiguration.class);
        
        assertThat(configuration)
            .as("Configuration is read")
            .isNotNull();
    }
    
    @Test
    public void should_read_mock_maker_class_from_configuration() {
        MockitoConfiguration configuration = reader.read(MockitoConfiguration.class);
    
        assertThat(configuration.getMockMakerClass())
            .as("Configuration is read")
            .isEqualTo("TestMockMaker");
    }
    
    @Test
    public void should_not_read_mock_maker_class_from_configuration_without_prefix() {
        reader = ConfigurationReaderBuilder.newBuilder()
                                           .forConfigurationFile(CONF_PATH + "/test_without_prefix.properties")
                                           .build();
    
        MockitoConfiguration configuration = reader.read(MockitoConfiguration.class);
    
        assertThat(configuration.getMockMakerClass())
            .as("Configuration is read")
            .isNull();
    }
    
    @Test
    public void should_return_null_when_configuration_file_non_exist() {
        
        assertThat(ConfigurationReaderBuilder.newBuilder()
                                             .forConfigurationFile("test.properties")
                                             .build()
                                             .read(MockitoConfiguration.class))
            .as("Null is returned")
            .isNull();
    }
    
    @Test
    public void should_return_real_value_instead_alias() {
    
        String value = "value";
        reader = ConfigurationReaderBuilder.newBuilder()
                                           .forConfigurationFile(CONF_PATH + "/test_with_alias.properties")
                                           .withValueAlias("alias", value)
                                           .build();
    
        MockitoConfiguration configuration = reader.read(MockitoConfiguration.class);
    
        assertThat(configuration.getMockMakerClass())
            .as("Configuration is read")
            .isEqualTo(value);
    }
}
