package org.powermock.api.mockito.internal.exceptions;

import org.mockito.exceptions.stacktrace.StackTraceCleaner;

public class StackTraceCleanerProvider implements org.mockito.plugins.StackTraceCleanerProvider {
    @Override
    public StackTraceCleaner getStackTraceCleaner(final StackTraceCleaner defaultCleaner) {
        return new StackTraceCleaner() {
            @Override
            public boolean isOut(StackTraceElement candidate) {
                return defaultCleaner.isOut(candidate) || candidate.getClassName().startsWith("org.powermock.api.mockito");
            }
        };
    }
}
