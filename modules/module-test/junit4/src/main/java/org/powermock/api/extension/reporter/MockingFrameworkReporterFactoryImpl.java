package org.powermock.api.extension.reporter;

import org.powermock.core.reporter.MockingFrameworkReporter;
import org.powermock.core.reporter.MockingFrameworkReporterFactory;

public class MockingFrameworkReporterFactoryImpl implements MockingFrameworkReporterFactory {
    @Override
    public MockingFrameworkReporter create() {
        return new MockingFrameworkReporter() {
            @Override
            public void enable() {

            }

            @Override
            public void disable() {

            }
        };
    }
}
