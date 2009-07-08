package demo.org.powermock.examples;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.powermock.api.easymock.PowerMock.createPartialMockAndInvokeDefaultConstructor;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.mockpolicies.Log4jMockPolicy;
import org.powermock.core.classloader.annotations.MockPolicy;
import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

@RunWith(PowerMockRunner.class)
@PrepareOnlyThisForTest(Log4jUser.class)
@MockPolicy(Log4jMockPolicy.class)
public class Log4jUserTest {

    @Test
    public void assertThatLog4jMockPolicyWorks() throws Exception {
        final Log4jUser tested = createPartialMockAndInvokeDefaultConstructor(Log4jUser.class, "getMessage");
        final String otherMessage = "other message";
        final String firstMessage = "first message and ";

        expect(tested.getMessage()).andReturn(firstMessage);

        replayAll();

        /*
         * Since 1.2.5 getClass() invocations are mockable as well so this
         * assertion makes sure that getClass() returns null because the Logger
         * has been replaced with a nice mock (which thus is returning null for
         * all unexpected calls).
         */
        assertNull(Whitebox.getInternalState(Log4jUserParent.class, Logger.class).getClass());
        final String actual = tested.mergeMessageWith(otherMessage);

        verifyAll();

        assertEquals(firstMessage + otherMessage, actual);
    }
}
