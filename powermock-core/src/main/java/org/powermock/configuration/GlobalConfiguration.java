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

import org.powermock.configuration.support.ConfigurationFactoryImpl;

/**
 * <p>
 * The class provides static access to {@link Configuration}.
 * The class uses {@link ThreadLocal} for storing of each type of configuration.
 * In result a one instance of each configuration type is created per thread.
 * </p>
 *
 * @since 1.7.0
 */
public final class GlobalConfiguration {
    
    private static ConfigurationFactory configurationFactory = new ConfigurationFactoryImpl();
    
    private static final ThreadLocal<MockitoConfiguration> MOCKITO_CONFIGURATION = new ThreadLocal<MockitoConfiguration>();
    private static final ThreadLocal<PowerMockConfiguration> POWER_MOCK_CONFIGURATION = new ThreadLocal<PowerMockConfiguration>();
    
    public static MockitoConfiguration mockitoConfiguration() {
        return new GlobalConfiguration().getMockitoConfiguration();
    }
    
    public static PowerMockConfiguration powerMockConfiguration() {
        return new GlobalConfiguration().getPowerMockConfiguration();
    }
    
    public static void clear() {
        configurationFactory = new ConfigurationFactoryImpl();
        MOCKITO_CONFIGURATION.remove();
        POWER_MOCK_CONFIGURATION.remove();
    }
    
    public static void setConfigurationFactory(final ConfigurationFactory configurationFactory) {
        GlobalConfiguration.configurationFactory = configurationFactory;
    }
    
    private GlobalConfiguration() {
        if (MOCKITO_CONFIGURATION.get() == null) {
            MOCKITO_CONFIGURATION.set(createConfig(MockitoConfiguration.class));
        }
        if (POWER_MOCK_CONFIGURATION.get() == null) {
            POWER_MOCK_CONFIGURATION.set(createConfig(PowerMockConfiguration.class));
        }
    }
    
    private PowerMockConfiguration getPowerMockConfiguration() {
        return POWER_MOCK_CONFIGURATION.get();
    }
    
    private MockitoConfiguration getMockitoConfiguration() {
        return MOCKITO_CONFIGURATION.get();
    }
    
    private <T extends Configuration<T>> T createConfig(Class<T> configurationClass) {
        return configurationFactory.create(configurationClass);
    }
}
