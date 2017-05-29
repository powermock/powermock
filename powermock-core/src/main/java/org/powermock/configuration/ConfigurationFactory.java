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

import org.powermock.configuration.support.ConfigurationReaderImpl;

public class ConfigurationFactory {
    
    private static final String USER_CONFIGURATION = "org/powermock/configuration.properties";
    private static final String DEFAULT_CONFIGURATION = "org/powermock/default.properties";
    
    public Configuration create() {
        Configuration configuration = readUserConfiguration();
        if (configuration == null){
            configuration = readDefault();
        }
        return configuration;
    }
    
    private Configuration readDefault() {
        
        System.out.print("Reading default configuration.");
        
        final Configuration configuration = new ConfigurationReaderImpl(DEFAULT_CONFIGURATION).read();
        if (configuration == null){
            throw new RuntimeException("It should never happen. If you see this exception, it means that something wrong with build." +
                                           " Please report to PowerMock issues tracker.");
        }
        return configuration;
    }
    
    private Configuration readUserConfiguration() {
        return new ConfigurationReaderImpl(USER_CONFIGURATION).read();
    }
}
