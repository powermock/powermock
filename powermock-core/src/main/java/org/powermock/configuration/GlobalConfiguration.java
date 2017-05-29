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
    
    private static final ThreadLocal<Configuration> GLOBAL_CONFIGURATION = new ThreadLocal<Configuration>();
    
    public static MockitoConfiguration mockitoConfiguration() {
        return (MockitoConfiguration) new GlobalConfiguration().get();
    }
    
    private GlobalConfiguration() {
        if (GLOBAL_CONFIGURATION.get() == null) {
            GLOBAL_CONFIGURATION.set(createConfig());
        }
    }
    
    public static void clear() {
        GLOBAL_CONFIGURATION.remove();
    }
    
    private Configuration get() {
        return GLOBAL_CONFIGURATION.get();
    }
    
    private Configuration createConfig() {
        return new ConfigurationFactory().create();
    }
}
