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

import org.powermock.core.classloader.ByteCodeFramework;
import org.powermock.utils.ArrayUtil;

/**
 * The class provides list of setting for PowerMock.
 * The properties with `powermock` prefix are mapped to the class.
 *
 * @since 1.7.0
 * @see Configuration
 */
public class PowerMockConfiguration implements Configuration<PowerMockConfiguration> {
    private String[] globalIgnore;
    private ByteCodeFramework byteCodeFramework;
    
    public String[] getGlobalIgnore() {
        return globalIgnore;
    }
    
    public void setGlobalIgnore(final String[] globalIgnore) {
        this.globalIgnore = globalIgnore;
    }
    
    public ByteCodeFramework getByteCodeFramework() {
        return byteCodeFramework;
    }
    
    public void setByteCodeFramework(final ByteCodeFramework byteCodeFramework) {
        this.byteCodeFramework = byteCodeFramework;
    }
    
    @Override
    public PowerMockConfiguration merge(final PowerMockConfiguration configuration) {
        if (configuration == null) {
            return this;
        } else {
            PowerMockConfiguration powerMockConfiguration = new PowerMockConfiguration();
    
            String[] globalIgnore = ArrayUtil.mergeArrays(this.globalIgnore, configuration.globalIgnore);
            
            powerMockConfiguration.setGlobalIgnore(globalIgnore);
            if (byteCodeFramework == null) {
                powerMockConfiguration.setByteCodeFramework(configuration.byteCodeFramework);
            }else {
                powerMockConfiguration.setByteCodeFramework(byteCodeFramework);
            }
            
            return powerMockConfiguration;
        }
    }
}
