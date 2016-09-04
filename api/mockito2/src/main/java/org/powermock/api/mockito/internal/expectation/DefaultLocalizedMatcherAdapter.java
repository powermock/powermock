package org.powermock.api.mockito.internal.expectation;

import org.mockito.ArgumentMatcher;
import org.mockito.internal.matchers.LocalizedMatcher;

final class DefaultLocalizedMatcherAdapter implements LocalizedMatcherAdapter {

    private final ArgumentMatcher matcher;

    DefaultLocalizedMatcherAdapter(LocalizedMatcher localizedMatcher) {
        this.matcher = localizedMatcher.getMatcher();
    }

    @Override
    public int hashCode() {
        return matcher.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return matcher.matches(obj);
    }
}
