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
import org.powermock.core.reporter.MockingFrameworkReporterFactory;
import org.powermock.reflect.Whitebox;

/**
 *  Abstract implementation of the {@link MockingFrameworkReporterFactory}, contains common code for EasyMock and
 *  Mockito.
 */
@SuppressWarnings("WeakerAccess")
public abstract class AbstractMockingFrameworkReporterFactory implements MockingFrameworkReporterFactory {
    @Override
    public MockingFrameworkReporter create() {

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        return getInstanceForClassLoader(classLoader);

    }

    private MockingFrameworkReporter getInstanceForClassLoader(ClassLoader classLoader) {
        Class<MockingFrameworkReporter> frameworkReporterClass = getMockingFrameworkReporterClass(classLoader);
        return Whitebox.newInstance(frameworkReporterClass);
    }

    @SuppressWarnings("unchecked")
    private Class<MockingFrameworkReporter> getMockingFrameworkReporterClass(ClassLoader classLoader) {
        Class<MockingFrameworkReporter> frameworkReporterClass;
        try {
            frameworkReporterClass = (Class<MockingFrameworkReporter>) classLoader.loadClass(getImplementerClassName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return frameworkReporterClass;
    }

    protected abstract String getImplementerClassName();
}
