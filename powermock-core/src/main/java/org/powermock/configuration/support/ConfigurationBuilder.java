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

import org.powermock.PowerMockInternalException;
import org.powermock.configuration.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

class ConfigurationBuilder<T extends Configuration<?>> {
    
    static <C extends Configuration<?>> ConfigurationBuilder<C> createConfigurationFor(final Class<C> configurationType) {
        return new ConfigurationBuilder<C>(configurationType);
    }
    
    private final Map<String, String> alias;
    private final Class<T> configurationType;
    
    private ConfigurationBuilder(final Class<T> configurationType) {
        this.configurationType = configurationType;
        alias = new HashMap<String, String>();
    }
    
    ConfigurationBuilder<T> withValueAlias(final String alias, final String value) {
        this.alias.put(alias, value);
        return this;
    }
    
    T fromFile(final String configurationLocation) {
        final Properties properties = new PropertiesLoader().load(configurationLocation);
        return fromProperties(properties);
    }
    
    T fromProperties(final Properties properties) {
        final ConfigurationCreator configurationCreator = new ConfigurationCreator(alias);
        return configurationCreator.create(configurationType, properties);
    }
    
    private static class ConfigurationCreator {
    
        private final ValueAliases alias;
        
        private ConfigurationCreator(final Map<String, String> alias) {
            this.alias = new ValueAliases(alias);
        }
        
        public <T extends Configuration> T create(Class<T> configurationClass, final Properties properties) {
            try {
                T configuration = configurationClass.newInstance();
                if (properties != null) {
                    mapConfiguration(configurationClass, configuration, properties);
                }
                return configuration;
            } catch (Exception e) {
                throw new PowerMockInternalException(e);
            }
        }
        
        private <T extends Configuration> void mapConfiguration(final Class<T> configurationClass,
                                                                final T configuration,
                                                                final Properties properties) {
            new ConfigurationMapper<T>(configurationClass, configuration, alias).map(properties);
        }
    }
}
