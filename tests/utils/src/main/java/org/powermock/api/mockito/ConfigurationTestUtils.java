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

package org.powermock.api.mockito;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import static org.powermock.utils.IOUtils.copyFileUsingStream;

public final class ConfigurationTestUtils {
    
    private static final String CONFIG_FILE = "configuration.properties";
    
    private File config;
    
    public void copyTemplateToPropertiesFile() throws URISyntaxException, IOException {
        
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = classLoader.getResource("org/powermock/configuration.template");
        
        File file = new File(resource.toURI());
        
        File parentFile = file.getParentFile();
        
        config = new File(parentFile.getAbsolutePath() + File.separator + CONFIG_FILE);
        
        if (!config.createNewFile()) {
            throw new AssertionError("Test data not created: cannot create " + CONFIG_FILE);
        }
        
        System.out.printf("Copying template %s to %s", file, config);
        
        copyFileUsingStream(file, config);
    }
    
    public void clear(){
        if (config != null && !config.delete()){
            throw new RuntimeException("Cannot delete temporary configuration.");
        }
    }
    
}
