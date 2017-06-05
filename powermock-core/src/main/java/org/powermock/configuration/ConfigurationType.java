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

public enum ConfigurationType {
    Mockito("mockito", MockitoConfiguration.class),
    PowerMock("powermock", PowerMockConfiguration.class);
    
    private final String prefix;
    private final Class<? extends Configuration> configurationClass;
    
    ConfigurationType(final String prefix,
                      final Class<? extends Configuration> configurationClass) {
        this.prefix = prefix;
        this.configurationClass = configurationClass;
    }
    
    public String getPrefix() {
        return prefix;
    }
    
    public static <T extends Configuration> ConfigurationType forClass(final Class<T> configurationClass) {
        for (ConfigurationType configurationType : ConfigurationType.values()) {
            if (configurationType.configurationClass.isAssignableFrom(configurationClass)){
                return configurationType;
            }
        }
        return null;
    }
}
