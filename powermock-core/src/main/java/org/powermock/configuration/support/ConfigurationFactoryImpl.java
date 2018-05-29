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
import org.powermock.configuration.ConfigurationFactory;
import org.powermock.utils.Asserts;

import java.util.Properties;

import static org.powermock.configuration.support.ConfigurationBuilder.createConfigurationFor;

public class ConfigurationFactoryImpl implements ConfigurationFactory {
    
    private static final String USER_CONFIGURATION = "org/powermock/extensions/configuration.properties";
    private static final String DEFAULT_CONFIGURATION = "org/powermock/default.properties";
    
    private final String userConfigurationLocation;
    private final String defaultConfigurationLocation;
    
    public ConfigurationFactoryImpl() {
        this(USER_CONFIGURATION, DEFAULT_CONFIGURATION);
    }
    
    ConfigurationFactoryImpl(final String userConfigurationLocation, final String defaultConfigurationLocation) {
        this.userConfigurationLocation = userConfigurationLocation;
        this.defaultConfigurationLocation = defaultConfigurationLocation;
    }
    
    ConfigurationFactoryImpl(final String defaultConfigurationLocation) {
        this(USER_CONFIGURATION, defaultConfigurationLocation);
    }
    
    @Override
    public <T extends Configuration<T>> T create(final Class<T> configurationType) {
        T environmentConfiguration = readEnvironmentConfiguration(configurationType);
        T configuration = readUserConfiguration(configurationType);
        T defaultConfiguration = readDefault(configurationType);
        return defaultConfiguration.merge(configuration.merge(environmentConfiguration));
    }
    
    private <T extends Configuration<T>> T readEnvironmentConfiguration(final Class<T> configurationType) {
        final Properties properties = new Properties();
        properties.putAll(System.getenv());
        return createConfigurationFor(configurationType)
                   .fromProperties(properties);
        
    }
    
    private <T extends Configuration> T  readDefault(final Class<T> configurationType) {
    
        final T configuration = createConfigurationFor(configurationType)
                                    .fromFile(getDefaultConfigurationLocation());
    
        Asserts.internalAssertNotNull(configuration, "Default configuration is null.");
        return configuration;
    }
    
    private <T extends Configuration> T  readUserConfiguration(final Class<T> configurationType) {
        return createConfigurationFor(configurationType)
                   .fromFile(getUserConfigurationLocation());
    }
    
    private String getDefaultConfigurationLocation() {
        return defaultConfigurationLocation;
    }
    
    private String getUserConfigurationLocation() {
        return userConfigurationLocation;
    }
}
