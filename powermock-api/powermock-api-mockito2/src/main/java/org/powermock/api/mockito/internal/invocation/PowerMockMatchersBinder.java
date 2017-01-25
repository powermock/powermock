/*
 *   Copyright 2016 the original author or authors.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package org.powermock.api.mockito.internal.invocation;

import org.mockito.ArgumentMatcher;
import org.mockito.internal.exceptions.Reporter;
import org.mockito.internal.invocation.InvocationMatcher;
import org.mockito.internal.invocation.MatchersBinder;
import org.mockito.internal.matchers.LocalizedMatcher;
import org.mockito.internal.progress.ArgumentMatcherStorage;
import org.mockito.invocation.Invocation;

import java.util.ArrayList;
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

        // In Mockito 2.0 LocalizedMatcher no more extend ArgumentMatcher, so new list should be created.

        final List<ArgumentMatcher> argumentMatchers = extractArgumentMatchers(lastMatchers);

        final InvocationMatcher invocationWithMatchers = new InvocationMatcher(invocation, argumentMatchers) {
            @Override
            public String toString() {
                return invocation.toString();
            }
        };

        return invocationWithMatchers;
    }

    private List<ArgumentMatcher> extractArgumentMatchers(List<LocalizedMatcher> lastMatchers) {
        final List<ArgumentMatcher> argumentMatchers = new ArrayList<ArgumentMatcher>(lastMatchers.size());

        for (LocalizedMatcher localizedMatcher: lastMatchers){
            argumentMatchers.add(localizedMatcher.getMatcher());
        }
        return argumentMatchers;
    }

    private void validateMatchers(Invocation invocation, List<LocalizedMatcher> lastMatchers) {
        if (!lastMatchers.isEmpty()) {
            int recordedMatchersSize = lastMatchers.size();
            int expectedMatchersSize = invocation.getArguments().length;
            if (expectedMatchersSize != recordedMatchersSize) {
                Reporter.invalidUseOfMatchers(expectedMatchersSize, lastMatchers);
            }
        }
    }
}