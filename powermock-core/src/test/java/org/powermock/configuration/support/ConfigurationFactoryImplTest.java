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

package org.powermock.configuration.support;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.powermock.configuration.ConfigurationFactory;
import org.powermock.configuration.PowerMockConfiguration;
import org.powermock.core.classloader.ByteCodeFramework;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(Enclosed.class)
public class ConfigurationFactoryImplTest {
    
    public static class SystemPropertiesCases {
        
        @Rule
        public final EnvironmentVariables environmentVariables = new EnvironmentVariables();
        
        private ConfigurationFactory configurationFactory;
    
        @Before
        public void setUp() {
            configurationFactory = new ConfigurationFactoryImpl(
                "org/powermock/extensions/test.properties",
                "org/powermock/test_default_configuration.properties"
            );
        }
        
        @Test
        public void should_read_byte_code_framework_from_environment_variable_if_defined() {
            environmentVariables.set("powermock.byte-code-framework", ByteCodeFramework.Javassist.name());
            
            PowerMockConfiguration configuration = configurationFactory.create(PowerMockConfiguration.class);
            
            assertThat(configuration)
                .as("Configuration is created")
                .isNotNull();
            
            assertThat(configuration.getByteCodeFramework())
                .as("Enum from configuration is read correctly")
                .isEqualTo(ByteCodeFramework.Javassist);
        }
    }
    
    
    public static class FileCases {
        
        private ConfigurationFactory configurationFactory;
        
        @Before
        public void setUp() {
            configurationFactory = new ConfigurationFactoryImpl(
                "org/powermock/extensions/test_configuration.properties",
                "org/powermock/test_default_configuration.properties"
            );
        }
        
        @Test
        public void should_return_configuration_from_file_if_configuration_file_exist() {
            PowerMockConfiguration configuration = configurationFactory.create(PowerMockConfiguration.class);
            
            assertThat(configuration)
                .as("Configuration is created")
                .isNotNull();
            
            assertThat(configuration.getGlobalIgnore())
                .as("Configuration is read correctly")
                .contains("org.somepackage");
            assertThat(configuration.getByteCodeFramework())
                .as("Enum from configuration is read correctly")
                .isEqualTo(ByteCodeFramework.Javassist);
        }
        
        @Test
        public void should_return_default_configuration_if_configuration_file_not_exist() {
            configurationFactory = new ConfigurationFactoryImpl(
                "org/powermock/test_default_configuration.properties"
            );
            PowerMockConfiguration configuration = configurationFactory.create(PowerMockConfiguration.class);
            
            assertThat(configuration)
                .as("Configuration is created")
                .isNotNull();
            
            assertThat(configuration.getGlobalIgnore())
                .as("Configuration is read correctly")
                .contains("org.powermock.core*");
        }
        
        @Test
        public void should_return_default_value_for_configuration_if_value_in_user_configuration_is_not_defined() {
            configurationFactory = new ConfigurationFactoryImpl(
                "org/powermock/extensions/test.properties",
                "org/powermock/test_default_configuration.properties"
            );
            
            PowerMockConfiguration configuration = configurationFactory.create(PowerMockConfiguration.class);
            
            assertThat(configuration)
                .as("Configuration is created")
                .isNotNull();
            
            assertThat(configuration.getByteCodeFramework())
                .as("Enum from configuration is read correctly")
                .isEqualTo(ByteCodeFramework.Javassist);
        }
    }
}
