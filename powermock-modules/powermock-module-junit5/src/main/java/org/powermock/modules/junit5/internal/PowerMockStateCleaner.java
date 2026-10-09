package org.powermock.modules.junit5.internal;

import org.powermock.core.MockRepository;

import java.lang.reflect.InvocationTargetException;

/** Clears PowerMock's global state (MockRepository) after each test, in the MockClassLoader and outside it. */
public class PowerMockStateCleaner {

    public static void clear(ClassLoader mockClassLoader) {
        try {
            Class.forName(MockRepository.class.getName(), true, mockClassLoader).getMethod("clear").invoke(null);
        } catch (InvocationTargetException e) {
            // e.g. Mockito's UnfinishedStubbingException from the after-method runner: report the real cause
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            if (cause instanceof Error) {
                throw (Error) cause;
            }
            throw new IllegalStateException("Cannot clear PowerMock's MockRepository", cause);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Cannot clear PowerMock's MockRepository", e);
        } finally {
            MockRepository.clear();
        }
    }
}
