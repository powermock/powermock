package org.powermock.api.extension.reporter;

import org.powermock.core.reporter.MockingFrameworkReporter;

/**
 * The MockingFrameworkReporterFactory which create a new instance of MockingFrameworkReporter
 * which is loaded by current context class loader.
 */
@SuppressWarnings("WeakerAccess")
public class MockingFrameworkReporterFactoryImpl extends AbstractMockingFrameworkReporterFactory {

    @Override
    protected String getImplementerClassName() {
        return "org.powermock.api.extension.reporter.MockingFrameworkReporterFactoryImpl$MockitoMockingFrameworkReporter";
    }

    @SuppressWarnings("unused")
    private static class MockitoMockingFrameworkReporter implements MockingFrameworkReporter {
// TODO: mockito2 reporter is internal now
//        private org.mockito.internal.exceptions.Reporter mockitoReporter;
//        private org.mockito.internal.MockitoCore mockitoCore;

//        private org.mockito.internal.exceptions.Reporter getMockitoReporter(Object mockitoCore) {

//            return Whitebox.getInternalState(mockitoCore, "reporter");
//        }

//        private void setMockitoReporter(org.mockito.internal.exceptions.Reporter reporter, org.mockito.internal.MockitoCore mockitoCore) {
//            Whitebox.setInternalState(mockitoCore, "reporter", reporter);
//        }

        @Override
        public void enable() {
//            mockitoCore = getMockitoCoreForCurrentClassLoader();
//            mockitoReporter = getMockitoReporter(mockitoCore);
//
//            PowerMockitoReporter powerMockitoReporter = new PowerMockitoReporter();
//            setMockitoReporter(powerMockitoReporter, mockitoCore);
        }

//        private org.mockito.internal.MockitoCore getMockitoCoreForCurrentClassLoader() {
//            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
//            try {
//                return Whitebox.getInternalState(classLoader.loadClass("org.mockito.Mockito"), "MOCKITO_CORE");
//            } catch (ClassNotFoundException e) {
//                throw new RuntimeException(e);
//            }
//        }

        @Override
        public void disable() {
//            setMockitoReporter(mockitoReporter, mockitoCore);
        }
    }

    /**
     * PowerMock reported for Mockito, which replace standard mockito message
     * to specific message for cases when PowerMock is used.
     */
//    private static class PowerMockitoReporter extends org.mockito.internal.exceptions.Reporter {
//
//        public void missingMethodInvocation() {
//            throw new org.mockito.exceptions.misusing.MissingMethodInvocationException(join(
//                    "when() requires an argument which has to be 'a method call on a mock'.",
//                    "For example:",
//                    "    when(mock.getArticles()).thenReturn(articles);",
//                    "Or 'a static method call on a prepared class`",
//                    "For example:",
//                    "    @PrepareForTest( { StaticService.class }) ",
//                    "    TestClass{",
//                    "       public void testMethod(){",
//                    "           PowerMockito.mockStatic(StaticService.class);",
//                    "           when(StaticService.say()).thenReturn(expected);",
//                    "       }",
//                    "    }",
//                    "",
//                    "Also, this error might show up because:",
//                    "1. inside when() you don't call method on mock but on some other object.",
//                    "2. inside when() you don't call static method, but class has not been prepared.",
//                    ""
//            ));
//        }
//
//    }
}
