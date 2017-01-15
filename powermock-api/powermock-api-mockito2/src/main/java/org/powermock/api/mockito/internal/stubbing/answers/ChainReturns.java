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

package org.powermock.api.mockito.internal.stubbing.answers;

import org.mockito.internal.stubbing.answers.ReturnsElementsOf;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by gauee on 12/11/15.
 */
public class ChainReturns implements Answer<Object> {
    private final ReturnsElementsOf returnsElementsOf;

    public ChainReturns(Object toBeReturn, Object... toBeReturnedOthers) {
        List<Object> elements = new LinkedList<Object>();
        elements.add(toBeReturn);
        addOtherElementToBeReturned(elements, toBeReturnedOthers);

        this.returnsElementsOf = new ReturnsElementsOf(elements);

    }

    private void addOtherElementToBeReturned(List<Object> elements, Object[] toBeReturnedOthers) {
        if (toBeReturnedOthers == null) {
            elements.add(toBeReturnedOthers);
            return;
        }
        Collections.addAll(elements, toBeReturnedOthers);
    }

    @Override
    public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
        return returnsElementsOf.answer(invocationOnMock);
    }
}
