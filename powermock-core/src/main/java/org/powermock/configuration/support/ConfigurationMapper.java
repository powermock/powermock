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
import org.powermock.configuration.ConfigurationType;
import org.powermock.core.PowerMockInternalException;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

class ConfigurationMapper<T extends Configuration> {
    private final Class<T> configurationClass;
    private final T configuration;
    private final ValueAliases aliases;
    
    ConfigurationMapper(final Class<T> configurationClass, final T configuration, final ValueAliases aliases) {
        this.configurationClass = configurationClass;
        this.configuration = configuration;
        this.aliases = aliases;
    }
    
    public void map(final Properties properties) {
        try {
            
            BeanInfo info = Introspector.getBeanInfo(configurationClass, Object.class);
            PropertyDescriptor[] propertyDescriptors = info.getPropertyDescriptors();
            
            for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                if (propertyDescriptor.getWriteMethod() != null) {
                    mapProperty(propertyDescriptor, properties);
                }
            }
            
        } catch (Exception e) {
            throw new PowerMockInternalException(e);
        }
    }
    
    private void mapProperty(final PropertyDescriptor propertyDescriptor, final Properties properties)
        throws IllegalAccessException, InvocationTargetException {
        
        final ConfigurationKey key = new ConfigurationKey(ConfigurationType.forClass(configurationClass), propertyDescriptor.getName());
        final String value = aliases.findValue((String) properties.get(key.toString()));
        
        PropertyWriter.forProperty(propertyDescriptor)
                      .writeProperty(propertyDescriptor, this.configuration, value);
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
    
    private enum PropertyWriter {
        ArrayWriter {
            @Override
            public void writeProperty(final PropertyDescriptor propertyDescriptor, final Object target, final String value) {
                try {
                    if (value != null) {
                        String[] array = value.split(",");
                        propertyDescriptor.getWriteMethod().invoke(target, (Object) array);
                    }
                } catch (Exception e) {
                    throw new PowerMockInternalException(e);
                }
            }
        },
        StringWriter {
            @Override
            public void writeProperty(final PropertyDescriptor propertyDescriptor, final Object target, final String value) {
                try {
                    if (value != null) {
                        propertyDescriptor.getWriteMethod().invoke(target, value);
                    }
                } catch (Exception e) {
                    throw new PowerMockInternalException(e);
                }
            }
        };
        
        public static PropertyWriter forProperty(final PropertyDescriptor propertyDescriptor) {
            if (String[].class.isAssignableFrom(propertyDescriptor.getPropertyType())) {
                return ArrayWriter;
            } else {
                return StringWriter;
            }
        }
        
        public abstract void writeProperty(final PropertyDescriptor propertyDescriptor, final Object target, final String value);
    }
}
