/*
 * Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.powermock.api.mockito.internal.expectation;

import org.mockito.internal.matchers.LocalizedMatcher;
import org.powermock.api.mockito.internal.mockcreation.DefaultMockCreator;
import org.powermock.api.mockito.internal.mockcreation.MockCreator;

import java.util.ArrayList;
import java.util.List;

public class DefaultConstructorExpectationSetup<T> extends AbstractConstructorExpectationSetup<T> {

    public DefaultConstructorExpectationSetup(Class<T> mockType) {
        super(mockType);
    }

     MockCreator getMockCreator() {return new DefaultMockCreator();}

    @Override
    protected List<LocalizedMatcherAdapter> getMatcherAdapters(List<LocalizedMatcher> matchers) {
        List<LocalizedMatcherAdapter> matcherAdapters = new ArrayList<LocalizedMatcherAdapter>();
        for (LocalizedMatcher matcher : matchers) {
            matcherAdapters.add(new DefaultLocalizedMatcherAdapter(matcher));
        }
        return matcherAdapters;
    }
}
