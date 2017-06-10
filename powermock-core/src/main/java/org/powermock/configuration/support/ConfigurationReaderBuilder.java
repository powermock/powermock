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

import org.powermock.configuration.Configuration;
import org.powermock.configuration.ConfigurationReader;
import org.powermock.core.PowerMockInternalException;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ConfigurationReaderBuilder {
    
    public static ConfigurationReaderBuilder newBuilder() {
        return new ConfigurationReaderBuilder();
    }
    
    private final Map<String, String> alias;
    private String configurationFile;
    
    private ConfigurationReaderBuilder() {
        alias = new HashMap<String, String>();
    }
    
    public ConfigurationReaderBuilder forConfigurationFile(final String configurationFile) {
        this.configurationFile = configurationFile;
        return this;
    }
    
    public ConfigurationReaderBuilder withValueAlias(final String alias, final String value) {
        this.alias.put(alias, value);
        return this;
    }
    
    public ConfigurationReader build() {
        return new ConfigurationReaderImpl(configurationFile, alias);
    }
    
    private static class ConfigurationReaderImpl implements ConfigurationReader {
        
        private final String configurationFile;
        private final ValueAliases alias;
        
        
        private ConfigurationReaderImpl(final String configurationFile, final Map<String, String> alias) {
            this.configurationFile = configurationFile;
            this.alias = new ValueAliases(alias);
        }
        
        @Override
        public <T extends Configuration> T read(Class<T> configurationClass) {
            final Properties properties = new PropertiesLoader().load(configurationFile);
            if (properties != null) {
                return createConfiguration(configurationClass, properties);
            } else {
                return null;
            }
        }
        
        private <T extends Configuration> T createConfiguration(final Class<T> configurationClass, final Properties properties) {
            try {
                T configuration = configurationClass.newInstance();
                mapConfiguration(configurationClass, configuration, properties);
                return configuration;
            } catch (Exception e) {
                throw new PowerMockInternalException(e);
            }
        }
        
        private <T extends Configuration> void mapConfiguration(final Class<T> configurationClass, final T configuration,
                                                                final Properties properties) {
            new ConfigurationMapper<T>(configurationClass, configuration, alias).map(properties);
        }
    }
}
