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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.mockito.ConfigurationTestUtils;
import org.powermock.configuration.support.ConfigurationFactoryImpl;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfigurationFactoryImplTest {
    
    private ConfigurationFactory configurationFactory;
    private ConfigurationTestUtils util;
    
    @Before
    public void setUp() throws Exception {
        configurationFactory = new ConfigurationFactoryImpl();
        util = new ConfigurationTestUtils();
    }
    
    @After
    public void tearDown() throws Exception {
        util.clear();
    }
    
    @Test
    public void should_return_configuration_from_file_if_configuration_file_exist() throws Exception {
    
        util.copyTemplateToPropertiesFile();
    
        PowerMockConfiguration configuration = configurationFactory.create(PowerMockConfiguration.class);
        
        assertThat(configuration)
            .as("Configuration is created")
            .isNotNull();
        
        assertThat(configuration.getGlobalIgnore())
            .as("Configuration is read correctly")
            .containsExactly("org.somepackage");
    }
    
    @Test
    public void should_return_default_configuration_if_configuration_file_not_exist() {
        MockitoConfiguration configuration = configurationFactory.create(MockitoConfiguration.class);
    
        assertThat(configuration)
            .as("Configuration is created")
            .isNotNull();
    
        assertThat(configuration.getMockMakerClass())
            .as("Configuration is read correctly")
            .isEqualTo("org.mockito.internal.creation.bytebuddy.SubclassByteBuddyMockMaker");
    }
    
    
    @Test
    public void should_return_default_value_for_configuration_if_user_not_defined() throws Exception {
        util.copyTemplateToPropertiesFile();
        
        MockitoConfiguration configuration = configurationFactory.create(MockitoConfiguration.class);
    
        assertThat(configuration)
            .as("Configuration is created")
            .isNotNull();
    
        assertThat(configuration.getMockMakerClass())
            .as("Configuration is read correctly")
            .isEqualTo("org.mockito.internal.creation.bytebuddy.SubclassByteBuddyMockMaker");
    }
}
