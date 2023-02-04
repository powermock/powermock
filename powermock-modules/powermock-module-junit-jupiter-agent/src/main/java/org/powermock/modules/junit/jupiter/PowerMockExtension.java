package org.powermock.modules.junit.jupiter;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.powermock.core.MockRepository;
import org.powermock.core.agent.JavaAgentClassRegister;
import org.powermock.core.agent.JavaAgentFrameworkRegister;
import org.powermock.core.agent.JavaAgentFrameworkRegisterFactory;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.agent.support.JavaAgentClassRegisterImpl;
import org.powermock.modules.agent.support.PowerMockAgentTestInitializer;
import org.powermock.reflect.Whitebox;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

public class PowerMockExtension implements BeforeEachCallback, AfterEachCallback {

    static {
        if (PowerMockExtension.class.getClassLoader() != ClassLoader.getSystemClassLoader()) {
            throw new IllegalStateException("PowerMockExtension can only be used with the system classloader but was loaded by " + PowerMockExtension.class.getClassLoader());
        }
        PowerMockAgent.initializeIfPossible();
    }

    private static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(PowerMockExtension.class.getName());
    private static final String KEY = "state";
    private static final String ANNOTATION_ENABLER = "org.powermock.api.extension.listener.AnnotationEnabler";

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        JavaAgentClassRegister agentClassRegister = new JavaAgentClassRegisterImpl();
        PowerMockAgentTestInitializer.initialize(context.getRequiredTestClass(), agentClassRegister);

        Object annotationEnabler = loadAnnotationEnableIfPresent();
        State state = new State(annotationEnabler, agentClassRegister);

        injectMocksUsingAnnotationEnabler(context.getTestInstance(), annotationEnabler);
        state.setFrameworkAgentClassRegister();

        context.getStore(NAMESPACE).put(KEY, state);
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        // Clear the mock repository after each test
        MockRepository.clear();
        State state = context.getStore(NAMESPACE).get(KEY, State.class);
        clearMockFields(context.getTestInstance(), state.annotationEnabler);
        state.clearFrameworkAgentClassRegister();
    }

    private Object loadAnnotationEnableIfPresent() {
        boolean hasAnnotationEnabler = hasAnnotationEnablerClass();
        if (!hasAnnotationEnabler) {
            return null;
        }

        try {
            return Whitebox.invokeConstructor(Class.forName(ANNOTATION_ENABLER, true, Thread.currentThread().getContextClassLoader()));
        } catch (Exception e) {
            throw new RuntimeException("PowerMock internal error, failed to load annotation enabler.");
        }
    }

    private boolean hasAnnotationEnablerClass() {
        try {
            Class.forName(ANNOTATION_ENABLER, false, Thread.currentThread().getContextClassLoader());
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private void clearMockFields(Object target, Object annotationEnabler) throws Exception {
        if (annotationEnabler != null) {
            Class<? extends Annotation>[] mockAnnotations = Whitebox.invokeMethod(annotationEnabler, "getMockAnnotations");
            Set<Field> mockFields = Whitebox.getFieldsAnnotatedWith(target, mockAnnotations);
            for (Field field : mockFields) {
                field.set(target, null);
            }
        }
    }

    private void injectMocksUsingAnnotationEnabler(Object target, Object annotationEnabler) throws Exception {
        if (annotationEnabler != null) {
            Whitebox.invokeMethod(annotationEnabler, "beforeTestMethod", new Class<?>[]{Object.class, Method.class,
                    Object[].class}, target, null, null);
        }
    }

    private static class State {

        private final Object annotationEnabler;
        private final JavaAgentClassRegister agentClassRegister;
        private final JavaAgentFrameworkRegister javaAgentFrameworkRegister;

        public State(Object annotationEnabler, JavaAgentClassRegister agentClassRegister) {
            this.annotationEnabler = annotationEnabler;
            this.agentClassRegister = agentClassRegister;
            this.javaAgentFrameworkRegister = JavaAgentFrameworkRegisterFactory.create();
        }

        private void clearFrameworkAgentClassRegister() {
            agentClassRegister.clear();
            javaAgentFrameworkRegister.clear();
        }

        private void setFrameworkAgentClassRegister() {
            javaAgentFrameworkRegister.set(agentClassRegister);
        }
    }
}
