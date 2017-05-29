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
import org.powermock.configuration.MockitoConfiguration;

import java.io.InputStream;
import java.util.Properties;

public class ConfigurationReaderImpl implements ConfigurationReader {
    private final String configurationFile;
    
    public ConfigurationReaderImpl(final String configurationFile) {
        this.configurationFile = configurationFile;
    }
    
    @Override
    public Configuration read() {
        final Properties properties = loadProperties();
        if (properties != null) {
            return new ConfigurationImpl((String) properties.get("mockito.mock-maker-class"));
        } else {
            return null;
        }
    }
    
    private Properties loadProperties() {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        final Properties properties = new Properties();
        
        try {
            InputStream configFile = classLoader.getResourceAsStream(configurationFile);
            properties.load(configFile);
            return properties;
        } catch (Exception e) {
            return null;
        }
        
    }
    
    private static class ConfigurationImpl implements MockitoConfiguration {
        
        private final String mockMakerClass;
        
        private ConfigurationImpl(final String mockMakerClass) {this.mockMakerClass = mockMakerClass;}
        
        @Override
        public String getMockMakerClass() {
            return mockMakerClass;
        }
    }
}
