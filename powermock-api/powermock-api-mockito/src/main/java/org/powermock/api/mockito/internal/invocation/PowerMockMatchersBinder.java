package org.powermock.api.mockito.internal.invocation;

import org.hamcrest.Matcher;
import org.mockito.exceptions.Reporter;
import org.mockito.internal.invocation.InvocationMatcher;
import org.mockito.internal.invocation.MatchersBinder;
import org.mockito.internal.matchers.LocalizedMatcher;
import org.mockito.internal.progress.ArgumentMatcherStorage;
import org.mockito.invocation.Invocation;

import java.util.List;

/**
 * This class is essentially a copy of {@link org.mockito.internal.invocation.MatchersBinder} with the exception that
 * the InvocationMatcher is replaced and its toString method is overwritten to avoid exceptions. For why these exceptions happen
 * refer to ToStringGenerator in this package.
 */
public class PowerMockMatchersBinder extends MatchersBinder {

    public InvocationMatcher bindMatchers(ArgumentMatcherStorage argumentMatcherStorage, final Invocation invocation) {
        List<LocalizedMatcher> lastMatchers = argumentMatcherStorage.pullLocalizedMatchers();
        validateMatchers(invocation, lastMatchers);

        final InvocationMatcher invocationWithMatchers = new InvocationMatcher(invocation, (List<Matcher>)(List) lastMatchers) {
            @Override
            public String toString() {
                return invocation.toString();
            }
        };
        return invocationWithMatchers;
    }

    private void validateMatchers(Invocation invocation, List<LocalizedMatcher> lastMatchers) {
        if (!lastMatchers.isEmpty()) {
            int recordedMatchersSize = lastMatchers.size();
            int expectedMatchersSize = invocation.getArguments().length;
            if (expectedMatchersSize != recordedMatchersSize) {
                new Reporter().invalidUseOfMatchers(expectedMatchersSize, lastMatchers);
            }
        }
    }
}