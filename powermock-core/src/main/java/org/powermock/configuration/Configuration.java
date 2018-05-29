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

/**
 * The general interface for all types configurations that could be obtained via {@link GlobalConfiguration}.
 * <p>
 * All user defined configurations are read from the properties file:
 * </p><pre>org/powermock/extensions/configuration.properties</pre>
 * <p>
 * By default the file is not exist and default values are used.
 * </p>
 *
 * @param <T> configuration implementer class.
 * @since 1.7.0
 */
public interface Configuration<T extends Configuration> {
    
    /**
     * Merge values of the configuration with values of <code>configuration</code>.
     * Values with the same keys from the <code>configuration</code>
     * overwrite value in the current configuration.
     *
     * @param configuration source configurations.
     * @return a new instance of {@link Configuration} with merged values.
     */
    T merge(T configuration);
}
