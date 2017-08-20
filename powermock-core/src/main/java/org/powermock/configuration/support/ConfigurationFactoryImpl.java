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

import static org.powermock.configuration.support.ConfigurationReaderBuilder.newBuilder;

public class ConfigurationFactoryImpl implements ConfigurationFactory {
    
    private static final String USER_CONFIGURATION = "org/powermock/extensions/configuration.properties";
    private static final String DEFAULT_CONFIGURATION = "org/powermock/default.properties";
    
    @Override public <T extends Configuration<T>> T create(final Class<T> configurationType) {
        T configuration = readUserConfiguration(configurationType);
        T defaultConfiguration = readDefault(configurationType);
        return defaultConfiguration.merge(configuration);
    }
    
    private <T extends Configuration> T  readDefault(final Class<T> configurationType) {
    
        final T configuration = newBuilder()
                                    .forConfigurationFile(DEFAULT_CONFIGURATION)
                                    .build()
                                    .read(configurationType);
        if (configuration == null){
            throw new RuntimeException("Default configuration is null. It should never happen. If you see this exception, it means that something wrong with build." +
                                           " Please report to PowerMock issues tracker.");
        }
        return configuration;
    }
    
    private <T extends Configuration> T  readUserConfiguration(final Class<T> configurationType) {
        return newBuilder()
                   .forConfigurationFile(USER_CONFIGURATION)
                   .build()
                   .read(configurationType);
    }
}
