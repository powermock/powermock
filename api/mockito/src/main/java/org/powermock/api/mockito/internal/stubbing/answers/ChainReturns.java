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
