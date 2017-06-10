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

import org.powermock.configuration.support.PropertiesFinder.ConfigurationSource;
import org.powermock.utils.StringJoiner;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

class PropertiesLoader {
    Properties load(final String propertiesFile) {
        if (propertiesFile == null) {
            return null;
        }
        
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
            final List<ConfigurationSource> configurations = new PropertiesFinder(classLoader).find(propertiesFile);
            return loadProperties(configurations, propertiesFile);
        } catch (Exception e) {
            return null;
        }
    }
    
    private Properties loadProperties(final List<ConfigurationSource> configurations, final String propertiesFile) throws IOException {
        if (configurations.size() == 0) {
            return null;
        } else {
            if (configurations.size() > 1) {
                printWarning(configurations, propertiesFile);
            }
            return loadPropertiesFromFile(configurations.get(0));
        }
    }
    
    private void printWarning(final List<ConfigurationSource> configurations, final String propertiesFile) {
        System.err.printf(
            "Properties file %s is found in %s places: %s. Which one will be used is undefined. " +
                "Please, remove duplicated configuration file (or second PowerMock jar file)" +
                " from class path to have stable tests.",
            propertiesFile,
            configurations.size(),
            StringJoiner.join(configurations)
        );
    }
    
    private Properties loadPropertiesFromFile(final ConfigurationSource configurationSource) throws IOException {
        final Properties properties = new Properties();
        properties.load(configurationSource.inputStream());
        return properties;
    }
    
}
