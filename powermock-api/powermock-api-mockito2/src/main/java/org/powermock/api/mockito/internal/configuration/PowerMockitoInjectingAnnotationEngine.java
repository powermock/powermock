/*
 *   Copyright 2016 the original author or authors.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package org.powermock.api.mockito.internal.configuration;

import org.mockito.Mock;
import org.mockito.internal.MockitoCore;
import org.mockito.internal.configuration.InjectingAnnotationEngine;
import org.mockito.internal.configuration.plugins.Plugins;
import org.powermock.api.mockito.internal.mockcreation.DefaultMockCreator;

/**
 * The same as {@link InjectingAnnotationEngine} with the exception that it
 * doesn't create/injects mocks annotated with the standard annotations such as
 * {@link Mock}.
 */
public class PowerMockitoInjectingAnnotationEngine extends InjectingAnnotationEngine {

    @SuppressWarnings("deprecation")
    @Override
    public AutoCloseable process(Class<?> context, Object testClass) {
        // this will create @Spies:
        new PowerMockitoSpyAnnotationEngine().process(context, testClass);
        
        preLoadPluginLoader();
        
        // this injects mocks
        injectMocks(testClass);
        return null;
    }
    
    private void preLoadPluginLoader() {
        final ClassLoader originalCL = Thread.currentThread().getContextClassLoader();

        Thread.currentThread().setContextClassLoader(DefaultMockCreator.class.getClassLoader());
        
        try {
            MockitoCore mc = new MockitoCore();
            Plugins.getMockMaker();
        } finally {
            Thread.currentThread().setContextClassLoader(originalCL);
        }
    }
}
