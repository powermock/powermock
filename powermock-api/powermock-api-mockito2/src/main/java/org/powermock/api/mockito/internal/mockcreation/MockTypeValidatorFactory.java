package org.powermock.api.mockito.internal.mockcreation;

import org.powermock.api.mockito.expectation.reporter.MockitoPowerMockReporter;
import org.powermock.core.agent.JavaAgentClassRegister;
import org.powermock.core.classloader.PowerMockModified;
import org.powermock.core.reporter.PowerMockReporter;

/**
 *
 */
public class MockTypeValidatorFactory {

    public static <T> MockTypeValidator<T> createValidator(Class<T> type, boolean isStatic, boolean isSpy, JavaAgentClassRegister agentClassRegister) {
        if (!isStatic || isSpy || isLoadedByBootstrap(type)) {
            return new NullMockTypeValidator<T>();
        } else if (agentClassRegister == null) {
            return new DefaultMockTypeValidator<T>(type);
        } else {
            return new JavaAgentMockTypeValidator<T>(type, agentClassRegister);
        }

    }

    private static boolean isLoadedByBootstrap(Class type) {
        return type.getClassLoader() == null;
    }

    private static class DefaultMockTypeValidator<T> extends AbstractMockTypeValidator<T> {

        DefaultMockTypeValidator(Class<T> type) {
            super(type);
        }

        @Override
        public void validate() {
            if (!isModifiedByPowerMock()) {
                reporter.classNotPrepared(type);
            }
        }

        private boolean isModifiedByPowerMock() {
            return PowerMockModified.class.isAssignableFrom(type);
        }
    }

    private static class JavaAgentMockTypeValidator<T> extends AbstractMockTypeValidator<T> {

        private final JavaAgentClassRegister agentClassRegister;

        private JavaAgentMockTypeValidator(Class<T> type, JavaAgentClassRegister agentClassRegister) {
            super(type);
            this.agentClassRegister = agentClassRegister;
        }

        @Override
        public void validate() {
            if (!isModifiedByAgent()) {
                reporter.classNotPrepared(type);
            }
        }

        private boolean isModifiedByAgent() {
            return agentClassRegister.isModifiedByAgent(type.getClassLoader(), type.getName());
        }
    }

    private abstract static class AbstractMockTypeValidator<T> implements MockTypeValidator<T> {
        final PowerMockReporter reporter;
        final Class<T> type;

        private AbstractMockTypeValidator(Class<T> type) {
            this.type = type;
            this.reporter = new MockitoPowerMockReporter();
        }

        @Override
        public abstract void validate();

    }

    private static class NullMockTypeValidator<T> implements MockTypeValidator<T> {
        @Override
        public void validate() {
            // NUll validator validates nothing
        }
    }
}
