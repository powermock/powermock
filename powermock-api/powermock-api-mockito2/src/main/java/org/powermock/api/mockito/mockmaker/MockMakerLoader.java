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

package org.powermock.api.mockito.mockmaker;

import org.mockito.Mockito;
import org.mockito.plugins.MockMaker;
import org.powermock.configuration.MockitoConfiguration;

class MockMakerLoader {
    MockMaker load(final MockitoConfiguration mockitoConfiguration) {
        
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null) {
            loader = ClassLoader.getSystemClassLoader();
        }
        
        String mockMakerClassName = mockitoConfiguration.getMockMakerClass();
        
        try {
            return doLoad(loader, mockMakerClassName);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load MockMaker implementation: " + mockMakerClassName, e);
        }
    }
    
    private MockMaker doLoad(final ClassLoader loader, final String mockMakerClassName)
        throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        if (mockMakerClassName == null) {
            return Mockito.framework().getPlugins().getDefaultPlugin(MockMaker.class);
        } else if ("mock-maker-inline".equals(mockMakerClassName)) {
            return Mockito.framework().getPlugins().getInlineMockMaker();
        } else {
            Class<?> mockMakerClass = loader.loadClass(mockMakerClassName);
            Object mockMaker = mockMakerClass.newInstance();
            return MockMaker.class.cast(mockMaker);
        }
    }
}
