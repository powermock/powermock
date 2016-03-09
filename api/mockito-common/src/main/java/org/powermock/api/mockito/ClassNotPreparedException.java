package org.powermock.api.mockito;

/**
 * The exception is thrown when a user tries to mock class which is't prepared, but should be. For example it could
 * be case mocking static call.
 */
public class ClassNotPreparedException extends RuntimeException {

    public ClassNotPreparedException(String message) {
        super(message);
    }
}
