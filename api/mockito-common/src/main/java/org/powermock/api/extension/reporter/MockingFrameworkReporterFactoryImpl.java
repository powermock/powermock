package org.powermock.api.extension.reporter;

import org.mockito.exceptions.Reporter;
import org.mockito.internal.MockitoCore;
import org.powermock.api.mockito.expectation.reporter.PowerMockitoReporter;
import org.powermock.core.reporter.MockingFrameworkReporter;
import org.powermock.reflect.Whitebox;

/**
 *  The MockingFrameworkReporterFactory which create a new instance of MockingFrameworkReporter
 *  which is loaded by current context class loader.
 */
@SuppressWarnings("WeakerAccess")
public class MockingFrameworkReporterFactoryImpl extends AbstractMockingFrameworkReporterFactory {


    @Override
    protected String getImplementerClassName() {
        return "org.powermock.api.extension.reporter.MockingFrameworkReporterFactoryImpl$MockitoMockingFrameworkReporter";
    }

    @SuppressWarnings("unused")
    private static class MockitoMockingFrameworkReporter implements MockingFrameworkReporter {

        private Reporter mockitoReporter;
        private MockitoCore mockitoCore;

        private Reporter getMockitoReporter(Object mockitoCore) {
            
            return Whitebox.getInternalState(mockitoCore, "reporter");
        }

        private void setMockitoReporter(Reporter reporter, MockitoCore mockitoCore) {
            Whitebox.setInternalState(mockitoCore, "reporter", reporter);
        }

        @Override
        public void enable() {
            mockitoCore = getMockitoCoreForCurrentClassLoader();
            mockitoReporter = getMockitoReporter(mockitoCore);
            
            PowerMockitoReporter powerMockitoReporter = new PowerMockitoReporter();
            setMockitoReporter(powerMockitoReporter, mockitoCore);
        }

        private MockitoCore getMockitoCoreForCurrentClassLoader() {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            try {
                return Whitebox.getInternalState(classLoader.loadClass("org.mockito.Mockito"), "MOCKITO_CORE");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void disable() {
            setMockitoReporter(mockitoReporter, mockitoCore);
        }
    }

}
