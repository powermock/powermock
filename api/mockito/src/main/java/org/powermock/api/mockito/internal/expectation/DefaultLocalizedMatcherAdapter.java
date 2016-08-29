package org.powermock.api.mockito.internal.expectation;

import org.mockito.internal.matchers.LocalizedMatcher;

final class DefaultLocalizedMatcherAdapter implements LocalizedMatcherAdapter {

    private final LocalizedMatcher localizedMatcher;

    DefaultLocalizedMatcherAdapter(LocalizedMatcher localizedMatcher) {
        this.localizedMatcher = localizedMatcher;
    }

    @Override
    public int hashCode() {
        return localizedMatcher.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return localizedMatcher.matches(obj);
    }
}
