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

package org.powermock.api.extension.reporter;

import org.powermock.core.reporter.MockingFrameworkReporter;

/**
 *
 */
public class MockingFrameworkReporterFactoryImpl extends AbstractMockingFrameworkReporterFactory {

    @Override
    protected String getImplementerClassName() {
        return "org.powermock.api.extension.reporter" +
                       ".MockingFrameworkReporterFactoryImpl$EasyMockMockingFrameworkReporter";
    }

    private static class EasyMockMockingFrameworkReporter implements MockingFrameworkReporter {

        public EasyMockMockingFrameworkReporter() {
            // Easymock uses static methods and cglib, so call cannot be intercept and exception cannot be changes
            // util ByteBuddy is not used. This class is create for capability.
        }

        @Override
        public void enable() {
        }

        @Override
        public void disable() {
        }
    }
}
