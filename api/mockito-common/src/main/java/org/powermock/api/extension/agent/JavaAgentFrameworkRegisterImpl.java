package org.powermock.api.extension.agent;

import org.powermock.api.mockito.internal.mockcreation.AbstractMockCreator;
import org.powermock.core.agent.JavaAgentClassRegister;
import org.powermock.core.agent.JavaAgentFrameworkRegister;
import org.powermock.reflect.Whitebox;

/**
 * Implementation of JavaAgentFrameworkRegister for Mockito framework.
 */
public class JavaAgentFrameworkRegisterImpl implements JavaAgentFrameworkRegister {

    private AbstractMockCreator mockCreator;

    @Override
    public void set(JavaAgentClassRegister javaAgentClassRegister) {
        setToPowerMockito(javaAgentClassRegister);
    }

    private void setToPowerMockito(JavaAgentClassRegister javaAgentClassRegister) {

        mockCreator = getPowerMockCoreForCurrentClassLoader();
        Whitebox.setInternalState(mockCreator, "agentClassRegister", javaAgentClassRegister);

    }

    private AbstractMockCreator getPowerMockCoreForCurrentClassLoader() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
            return Whitebox.getInternalState(classLoader.loadClass("org.powermock.api.mockito.internal.mockcreation.MockCreator"), "MOCK_CREATOR");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clear() {
        if (mockCreator == null) {
            throw new IllegalStateException("Cannot clear JavaAgentClassRegister. Set method has not been called.");
        }
        Whitebox.setInternalState(mockCreator, "agentClassRegister", (Object) null);
    }
}
