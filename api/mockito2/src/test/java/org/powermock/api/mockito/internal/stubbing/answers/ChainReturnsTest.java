package org.powermock.api.mockito.internal.stubbing.answers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.powermock.api.mockito.PowerMockito.mock;

/**
 * Created by gauee on 12/11/15.
 */
public class ChainReturnsTest {

    public static final String ANSWER_FIRST = "answerFirst";
    public static final String ANSWER_SECOND = "answerSecond";
    private InvocationOnMock invocationOnMock;

    @Before
    public void init() {
        this.invocationOnMock = mock(InvocationOnMock.class);
    }


    @Test
    public void returnsTwoDifferentAnswers() throws Throwable {
        ChainReturns chainReturns = new ChainReturns(ANSWER_FIRST, ANSWER_SECOND);

        assertThat((String) chainReturns.answer(invocationOnMock), is(equalTo(ANSWER_FIRST)));
        assertThat((String) chainReturns.answer(invocationOnMock), is(equalTo(ANSWER_SECOND)));
    }

    @Test
    public void returnsFirstAnswerAndNullValue() throws Throwable {
        ChainReturns chainReturns = new ChainReturns(ANSWER_FIRST, (Object)null);

        assertThat((String) chainReturns.answer(invocationOnMock), is(equalTo(ANSWER_FIRST)));
        assertThat(chainReturns.answer(invocationOnMock), is(nullValue()));
    }

    @Test
    public void returnsAlwaysLastDeclaredAnswer() throws Throwable {
        ChainReturns chainReturns = new ChainReturns(ANSWER_FIRST, ANSWER_SECOND);

        chainReturns.answer(invocationOnMock);
        chainReturns.answer(invocationOnMock);

        assertThat((String) chainReturns.answer(invocationOnMock), is(equalTo(ANSWER_SECOND)));
    }

}