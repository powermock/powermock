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

public final class GlobalConfiguration {
    
    private static final ThreadLocal<MockitoConfiguration> MOCKITO_CONFIGURATION = new ThreadLocal<MockitoConfiguration>();
    
    public static MockitoConfiguration mockitoConfiguration() {
        return new GlobalConfiguration().getMockitoConfiguration();
    }
    
    private GlobalConfiguration() {
        if (MOCKITO_CONFIGURATION.get() == null) {
            MOCKITO_CONFIGURATION.set(createConfig(MockitoConfiguration.class));
        }
    }
    
    public static void clear() {
        MOCKITO_CONFIGURATION.remove();
    }
    
    private MockitoConfiguration getMockitoConfiguration() {
        return MOCKITO_CONFIGURATION.get();
    }
    
    private <T extends Configuration> T createConfig(Class<T> configurationClass) {
        return new ConfigurationFactory().create(configurationClass);
    }
}
