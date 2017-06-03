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
import org.powermock.configuration.ConfigurationType;
import org.powermock.core.PowerMockInternalException;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
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
        private final Map<String, String> alias;
        
        private ConfigurationReaderImpl(final String configurationFile, final Map<String, String> alias) {
            this.configurationFile = configurationFile;
            this.alias = alias;
        }
        
        @Override
        public <T extends Configuration> T read(Class<T> configurationClass) {
            final Properties properties = new PropertiesLoader(configurationFile).load();
            if (properties != null) {
                return createConfiguration(configurationClass, properties);
            } else {
                return null;
            }
        }
    
        private  <T extends Configuration> T createConfiguration(final Class<T> configurationClass, final Properties properties) {
            try {
                T configuration = configurationClass.newInstance();
                mapConfiguration(configurationClass, configuration, properties);
                return configuration;
            } catch (Exception e) {
                throw new PowerMockInternalException(e);
            }
        }
    
        private <T extends Configuration> void mapConfiguration(final Class<T> configurationClass,
                                                                final T configuration,
                                                                final Properties properties) {
            try {
                ConfigurationType configurationType = ConfigurationType.forClass(configurationClass);
                
                BeanInfo info = Introspector.getBeanInfo(configurationClass, Object.class);
                PropertyDescriptor[] all = info.getPropertyDescriptors();
                
                for (PropertyDescriptor propertyDescriptor : all) {
                    if (propertyDescriptor.getWriteMethod() != null) {
                        mapProperty(configuration, properties, configurationType, propertyDescriptor);
                    }
                }
                
            } catch (Exception e) {
                throw new PowerMockInternalException(e);
            }
            
        }
        
        private <T extends Configuration> void mapProperty(final T configuration,
                                                           final Properties properties,
                                                           final ConfigurationType configurationType,
                                                           final PropertyDescriptor propertyDescriptor) throws IllegalAccessException, InvocationTargetException {
            String key = new ConfigurationKey(configurationType, propertyDescriptor.getName()).toString();
            writeProperty(configuration, propertyDescriptor, findValue(properties, key));
        }
    
        private String findValue(final Properties properties, final String key) {
            String value = (String) properties.get(key);
            if (alias.containsKey(value)){
                value = alias.get(value);
            }
            return value;
        }
    
        private <T extends Configuration> void writeProperty(final T configuration,
                                                             final PropertyDescriptor propertyDescriptor,
                                                             final String value) throws IllegalAccessException, InvocationTargetException {
            if (value != null) {
                propertyDescriptor.getWriteMethod().invoke(configuration, value);
            }
        }
    
    
        private static class ConfigurationKey {
            private final ConfigurationType configurationType;
            private final String name;
            
            private ConfigurationKey(final ConfigurationType configurationType, final String name) {
                this.configurationType = configurationType;
                this.name = name;
            }
            
            @Override
            public String toString() {
                StringBuilder key = new StringBuilder();
                
                if (configurationType.getPrefix() != null) {
                    key.append(configurationType.getPrefix());
                    key.append(".");
                }
                
                for (int i = 0; i < name.length(); i++) {
                    char c = name.charAt(i);
                    if (Character.isUpperCase(c)) {
                        key.append('-');
                        key.append(Character.toLowerCase(c));
                    } else {
                        key.append(c);
                    }
                }
                return key.toString();
            }
        }
        
        private static class PropertiesLoader {
            private String configurationFile;
            
            private PropertiesLoader(final String configurationFile) {
                this.configurationFile = configurationFile;
            }
            
            private Properties load() {
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
        }
    }
}
